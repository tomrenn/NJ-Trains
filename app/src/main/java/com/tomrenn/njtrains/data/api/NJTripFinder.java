package com.tomrenn.njtrains.data.api;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.database.DatabaseUtilsCompat;

import com.google.common.collect.Lists;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
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

import static android.database.DatabaseUtils.stringForQuery;

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
            + Trip.TABLE+"."+Trip.SERVICE_ID
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
    public Observable<List<Trip>> findTrips(final Stop from, final Stop destination) {

        return Observable.create(new Observable.OnSubscribe<List<Trip>>() {
            @Override
            public void call(Subscriber<? super List<Trip>> subscriber) {
                String subquery = makeshiftSubquery(from);
                String subqueryB = makeshiftSubquery(destination);
                String query = "SELECT * "
                        + "FROM ("+subquery+") AS SUB1 "
                        + "INNER JOIN (" + subqueryB + ") AS SUB2 "
                         + "ON SUB1.service_id = SUB2.service_id "
                        + "WHERE SUB1.stop_sequence < SUB2.stop_sequence;";

                Cursor cursor = db.query(query, new String[0]);
                List<Trip> trips = new LinkedList<Trip>();
                try {
                    while(cursor.moveToNext()){
                        int id = Db.getInt(cursor, Trip.ID);
                        int routeId = Db.getInt(cursor, Trip.ROUTE_ID);
                        int serviceId = Db.getInt(cursor, Trip.SERVICE_ID);
                        trips.add(new Trip(id, routeId, serviceId, "", 0, 0, 0));
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
