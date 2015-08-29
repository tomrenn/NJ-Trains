package com.tomrenn.njtrains.data.api;

import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.tomrenn.njtrains.data.db.Db;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.db.StopTime;
import com.tomrenn.njtrains.data.db.Trip;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 *   select FOO.service_id
 *   FROM (select * from trips JOIN stop_times on (trips.service_id = stop_times.trip_id) where stop_times.stop_id=127) as FOO
 *     INNER JOIN
 *       (select * from trips JOIN stop_times on (trips.service_id = stop_times.trip_id) where stop_times.stop_id=105) AS BAR
 *       on (FOO.service_id = BAR.service_id)
 *   WHERE FOO.stop_sequence < BAR.stop_sequence;
 */
@Singleton
public class NJTripFinder implements TripFinder {
    BriteDatabase db;

    // finds the sub
    static final String subTrips = "SELECT * FROM " + Trip.TABLE
            + " JOIN " + StopTime.TABLE
            + " ON ("
            + Trip.TABLE+"."+Trip.ID
            + " = " + StopTime.TABLE+"."+StopTime.TRIP_ID
            + ")"
            + " WHERE " + StopTime.TABLE + "." + StopTime.STOP_ID
            + "= ? "
            + "GROUP BY " + Trip.SERVICE_ID;

    @Inject
    public NJTripFinder(BriteDatabase db) {
        this.db = db;
    }

    String makeshiftSubquery(Stop stop){
        return subTrips.replace("?", Long.toString(stop.id()));
    }

    @Override
    public Observable<List<TripResult>> findTrips(final TripRequest tripRequest) {

        return Observable.create(new Observable.OnSubscribe<List<TripResult>>() {
            @Override
            public void call(Subscriber<? super List<TripResult>> subscriber) {
                String subquery = makeshiftSubquery(tripRequest.getToStation());
                String subqueryB = makeshiftSubquery(tripRequest.getFromStation());
                String query = "SELECT SUB1.departure_time as departure_time, SUB2.arrival_time as arrival_time "
                        + "FROM ("+subquery+") AS SUB1 "
                        + "INNER JOIN (" + subqueryB + ") AS SUB2 "
                         + "ON SUB1.service_id = SUB2.service_id "
                        + "WHERE SUB1.stop_sequence < SUB2.stop_sequence;";

//                Cursor one = db.query("select * from stops");
//                Cursor two = db.query("select * from stop_times");
//                Cursor three = db.query("select * from trips");
                Cursor cursor = db.query(query);
                Timber.d("ResultCount " + cursor.getCount());
                List<TripResult> trips = new LinkedList<>();
                try {
                    while(cursor.moveToNext()){
//                        int id = Db.getInt(cursor, Trip.ID);
//                        int routeId = Db.getInt(cursor, Trip.ROUTE_ID);
//                        int serviceId = Db.getInt(cursor, Trip.SERVICE_ID);
                        String arrival = Db.getString(cursor, "departure_time");
                        String depature = Db.getString(cursor, "arrival_time");
                        trips.add(new TripResult(depature, arrival, ""));
                    }
                } finally {
                    cursor.close();
                }
                subscriber.onNext(trips);
                subscriber.onCompleted();
            }
        });
    }
}
