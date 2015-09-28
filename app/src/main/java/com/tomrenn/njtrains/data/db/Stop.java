package com.tomrenn.njtrains.data.db;

import android.database.Cursor;
import android.os.Parcelable;

import com.squareup.sqlbrite.SqlBrite;
import com.tomrenn.njtrains.data.util.Strings;

import java.util.ArrayList;
import java.util.List;

import auto.parcel.AutoParcel;
import rx.functions.Func1;

import static com.squareup.sqlbrite.SqlBrite.Query;

/**
 *
 */
@AutoParcel
public abstract class Stop implements Parcelable {
    // stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id
    // 1,95001,"30TH ST. PHL.",,39.956565,-75.182327,5961
    public static final String TABLE = "stops";

    public static final String ID = "stop_id";
    public static final String CODE = "stop_code";
    public static final String NAME = "stop_name";
    public static final String DESCRIPTION = "stop_desc";
    public static final String LATITUDE = "stop_lat";
    public static final String LONGITUDE = "stop_lon";
    public static final String ZONE_ID = "zone_id";

    public abstract long id();
    public abstract long code();
    public abstract String name();
    public abstract String desc();
    public abstract double latitude();
    public abstract double longitude();
    public abstract long zoneId();


    public static Stop create(long id, long code, String name, String desc, double latitude, double longitude, long zoneId) {
        return new AutoParcel_Stop(id, code, name, desc, latitude, longitude, zoneId);
    }


    public static final Func1<Cursor, List<Stop>> cursorToValues = new Func1<Cursor, List<Stop>>() {
        @Override
        public List<Stop> call(Cursor cursor) {
            try {
                List<Stop> values = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    long id = Db.getLong(cursor, Stop.ID);
                    String name = Db.getString(cursor, Stop.NAME);
                    values.add(Stop.create(id, 0l, name, "", 0, 0, 0));
                }
                return values;
            } finally {
                cursor.close();
            }
        }
    };

    public static final Func1<Cursor, Stop> cursorToValue = new Func1<Cursor, Stop>() {
        @Override
        public Stop call(Cursor cursor) {
            try {
                Stop station = null;
                while (cursor.moveToNext()) {
                    long id = Db.getLong(cursor, Stop.ID);
                    String name = Db.getString(cursor, Stop.NAME);
                    station = Stop.create(id, 0l, name, "", 0, 0, 0);
                }
                return station;
            } finally {
                cursor.close();
            }
        }
    };

    public String prettyName(){
        return Strings.capitalizeString(name());
    }

}
