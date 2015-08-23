package com.tomrenn.njtrains.data.api;

import com.squareup.sqlbrite.BriteDatabase;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.db.StopTime;
import com.tomrenn.njtrains.data.db.Trip;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

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

    @Inject
    public NJTripFinder(BriteDatabase db) {
        this.db = db;
    }


    

    @Override
    public Observable<List<Trip>> findTrips(Stop from, Stop destination) {
        final String subTrips = "SELECT * FROM " + Trip.TABLE
                                + " JOIN " + StopTime.TABLE
                                + " ON ("
                                    + Trip.TABLE+"."+Trip.SERVICE_ID
                                        + " = " + StopTime.TABLE+"."+StopTime.TRIP_ID
                                    + ")"
                                + " WHERE " + StopTime.TABLE + "." + StopTime.STOP_ID
                                    + "= ?";

        return Observable.create(new Observable.OnSubscribe<List<Trip>>() {
            @Override
            public void call(Subscriber<? super List<Trip>> subscriber) {

            }
        });
    }
}
