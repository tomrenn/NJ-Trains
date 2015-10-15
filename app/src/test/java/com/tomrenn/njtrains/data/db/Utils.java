package com.tomrenn.njtrains.data.db;

import android.content.ContentValues;

import com.squareup.sqlbrite.BriteDatabase;

/**
 *
 */
public class Utils {

    public static void insertStop(BriteDatabase db, int id, String name){
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

    public static void insertTrip(BriteDatabase db, int tripId, int routeId, int serviceId, String blockId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Trip.ID, tripId);
        contentValues.put(Trip.ROUTE_ID, routeId);
        contentValues.put(Trip.SERVICE_ID, serviceId);
        contentValues.put(Trip.BLOCK_ID, blockId);
        contentValues.put(Trip.HEADSIGN, "");

        db.insert(Trip.TABLE, contentValues);
    }

    public static void insertStopTime(BriteDatabase db, int stopId, int tripId, int stopSequence, String stopTime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(StopTime.STOP_ID, stopId);
        contentValues.put(StopTime.TRIP_ID, tripId);
        contentValues.put(StopTime.SEQUENCE, stopSequence);
        contentValues.put(StopTime.ARRIVAL, stopTime);
        contentValues.put(StopTime.DEPARTURE, stopTime);
        contentValues.put(StopTime.SHAPE_TRAVELED, 0f);

        db.insert(StopTime.TABLE, contentValues);
    }
}
