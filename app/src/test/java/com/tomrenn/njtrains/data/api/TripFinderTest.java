package com.tomrenn.njtrains.data.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.collect.Lists;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomrenn.njtrains.BuildConfig;
import com.tomrenn.njtrains.data.db.DbOpenHelper;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.db.StopTime;
import com.tomrenn.njtrains.data.db.Trip;

import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.observers.TestObserver;

import static org.assertj.android.api.Assertions.assertThat;

import static com.squareup.sqlbrite.SqlBrite.Query;
import static com.tomrenn.njtrains.data.db.Utils.*;
import static org.junit.Assert.*;

/**
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class TripFinderTest {

    NJTripFinder tripFinder;
    BriteDatabase db;

    int RAHWAY_STOP_ID = 127;
    int NYP_STOP_ID = 105;

    int TRIP1 = 44;
    int TRIP2 = 55;
    int TRIP3 = 77;


    @Before public void setup(){
        SQLiteOpenHelper sqLiteOpenHelper = new DbOpenHelper(RuntimeEnvironment.application);
        SqlBrite sqlBrite = SqlBrite.create();
        db = sqlBrite.wrapDatabaseHelper(sqLiteOpenHelper);
        tripFinder = new NJTripFinder(db);
        sampleData();
    }

    void sampleData(){
        int NE_CORRIDER = 8;

        insertStop(db, RAHWAY_STOP_ID, "RAHWAY");
        insertStop(db, NYP_STOP_ID, "NYP");

        // to NYC
        insertStopTime(db, RAHWAY_STOP_ID, TRIP1, 2, "7:27am");
        insertStopTime(db, NYP_STOP_ID, TRIP1, 8, "8:08am");
        // to RAHWAY
        insertStopTime(db, NYP_STOP_ID, TRIP2, 0, "5:32pm");
        insertStopTime(db, RAHWAY_STOP_ID, TRIP2, 6, "6:12pm");

        insertTrip(db, TRIP1, NE_CORRIDER, 0);
        insertTrip(db, TRIP2, NE_CORRIDER, 1);
        insertTrip(db, TRIP3, NE_CORRIDER, 2);
    }

    /** Only Trip 1 goes from Rahway _to_ NYP */
    @Test
    public void testFindTrips(){
        Stop from = new Stop(RAHWAY_STOP_ID, 0, "RAHWAY", "", 0, 0, 0);
        Stop to = new Stop(NYP_STOP_ID, 0, "NYP", "", 0, 0, 0);

        TripRequest request = new TripRequest(from, to);
        List<TripResult> trips = tripFinder.findTrips(request).toBlocking().first();
        assertEquals(1, trips.size());
    }

    /** Trip 1 and 2 stop at Rahway */
    @Test public void subtripsTest(){
        List<String> tables = Lists.asList(StopTime.TABLE, new String[]{Trip.TABLE});
        Observable<Query> query = db.createQuery(
                tables, NJTripFinder.subTrips,
                String.valueOf(RAHWAY_STOP_ID));

        Cursor cursor = query.toBlocking().first().run();

        assertThat(cursor).hasCount(2);
    }

}
