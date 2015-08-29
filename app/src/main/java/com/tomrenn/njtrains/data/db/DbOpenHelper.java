package com.tomrenn.njtrains.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    private static final String CREATE_STOP = ""
            + "CREATE TABLE " + Stop.TABLE + "("
            + Stop.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Stop.CODE + " INTEGER NOT NULL,"
            + Stop.NAME + " TEXT NOT NULL,"
            + Stop.DESCRIPTION + " TEXT NOT NULL,"
            + Stop.LATITUDE + " REAL NOT NULL,"
            + Stop.LONGITUDE + " REAL NOT NULL,"
            + Stop.ZONE_ID + " INTEGER NOT NULL"
            + ")";

    public static final String CREATE_ROUTE = ""
            + "CREATE TABLE " + Route.TABLE + "("
            + Route.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Route.AGENCY_ID + " TEXT NOT NULL,"
            + Route.SHORT_NAME + " TEXT NOT NULL,"
            + Route.LONG_NAME + " TEXT NOT NULL,"
            + Route.ROUTE_TYPE + " INTEGER NOT NULL DEFAULT 0,"
            + Route.URL + " TEXT NOT NULL,"
            + Route.COLOR + " INTEGER"
            + ")";

    public static final String CREATE_SERVICE_DATE = ""
            + "CREATE TABLE " + ServiceDate.TABLE + "("
            + ServiceDate.ID + " INTEGER NOT NULL,"
            + ServiceDate.DATE + " TEXT NOT NULL,"
            + ServiceDate.EXCEPTION_TYPE + " INTEGER NOT NULL"
            + ")";

    private static final String CREATE_TRIP = ""
            + "CREATE TABLE " + Trip.TABLE + "("
            + Trip.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Trip.SERVICE_ID +" INTEGER NOT NULL REFERENCES " + ServiceDate.TABLE + "(" + ServiceDate.ID + "),"
            + Trip.ROUTE_ID + " INTEGER NOT NULL,"
            + Trip.HEADSIGN + " TEXT NOT NULL,"
            + Trip.DIRECTION_ID + " INTEGER NOT NULL DEFAULT 0,"
            + Trip.BLOCK_ID + " INTEGER NOT NULL DEFAULT 0,"
            + Trip.SHAPE_ID + " INTEGER NOT NULL DEFAULT 0"
            + ")";

    private static final String CREATE_STOP_TIME = ""
            + "CREATE TABLE " + StopTime.TABLE + "("
            + StopTime.STOP_ID + " INTEGER NOT NULL REFERENCES " + Stop.TABLE + "(" + Stop.ID + "),"
            + StopTime.TRIP_ID + " INTEGER NOT NULL REFERENCES " + Trip.TABLE + "(" + Trip.ID + "),"
            + StopTime.ARRIVAL + " TEXT NOT NULL,"
            + StopTime.DEPARTURE + " TEXT NOT NULL,"
            + StopTime.SEQUENCE + " INTEGER NOT NULL,"
            + StopTime.PICKUP_TYPE + " INTEGER NOT NULL DEFAULT 0,"
            + StopTime.DROPOFF_TYPE + " INTEGER NOT NULL DEFAULT 0,"
            + StopTime.SHAPE_TRAVELED + " REAL NOT NULL"
            + ")";

//
//    private static final String CREATE_ITEM_LIST_ID_INDEX =
//            "CREATE INDEX item_list_id ON " + TodoItem.TABLE + " (" + TodoItem.LIST_ID + ")";

    public DbOpenHelper(Context context) {
        super(context, "todo.db", null /* factory */, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STOP);
        db.execSQL(CREATE_ROUTE);
        db.execSQL(CREATE_SERVICE_DATE);
        db.execSQL(CREATE_TRIP);
        db.execSQL(CREATE_STOP_TIME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // todo: locate pitfalls of version comparison
    }
}
