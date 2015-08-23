package com.tomrenn.njtrains.data.api;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomrenn.njtrains.BuildConfig;
import com.tomrenn.njtrains.data.db.DbOpenHelper;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.db.StopTime;
import com.tomrenn.njtrains.data.db.Trip;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class TripFinderTest {

    NJTripFinder tripFinder;
    BriteDatabase db;


    @Before public void setup(){
        SQLiteOpenHelper sqLiteOpenHelper = new DbOpenHelper(RuntimeEnvironment.application);
        SqlBrite sqlBrite = SqlBrite.create();
        db = sqlBrite.wrapDatabaseHelper(sqLiteOpenHelper);
        tripFinder = new NJTripFinder(db);
        sampleData();
    }

    void sampleData(){
        int NE_CORRIDER = 8;

        int TRIP1 = 44;
        int TRIP2 = 55;

        insertStop(127, "RAHWAY");
        insertStop(105, "NYP");

        insertStopTime(127, TRIP1, 2);
        insertStopTime(127, TRIP2, 2);

        insertTrip(1, NE_CORRIDER, TRIP2);
        insertTrip(2, NE_CORRIDER, 77);
    }

    void insertStop(int id, String name){
        ContentValues values = new ContentValues();
        values.put(Stop.ID, id);
        values.put(Stop.NAME, name);
        values.put(Stop.CODE, 0);
        values.put(Stop.DESCRIPTION, "");
        values.put(Stop.LATITUDE, 0);
        values.put(Stop.LONGITUDE, 0);
        values.put(Stop.ZONE_ID, 0);

        db.insert(Stop.TABLE, values);
    }

    void insertTrip(int tripId, int routeId, int serviceId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Trip.ID, tripId);
        contentValues.put(Trip.ROUTE_ID, routeId);
        contentValues.put(Trip.SERVICE_ID, serviceId);
        contentValues.put(Trip.HEADSIGN, "");

        db.insert(Trip.TABLE, contentValues);
    }

    void insertStopTime(int stopId, int tripId, int stopSequence){
        ContentValues contentValues = new ContentValues();
        contentValues.put(StopTime.STOP_ID, stopId);
        contentValues.put(StopTime.TRIP_ID, tripId);
        contentValues.put(StopTime.SEQUENCE, stopSequence);
        contentValues.put(StopTime.ARRIVAL, "8am");
        contentValues.put(StopTime.DEPARTURE, "8am");
        contentValues.put(StopTime.SHAPE_TRAVELED, 0f);

        db.insert(StopTime.TABLE, contentValues);
    }


    @Test public void subtripsTest(){

    }

}
