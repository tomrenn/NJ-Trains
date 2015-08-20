package com.tomrenn.njtrains.data.db;

import android.database.Cursor;

import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

import static com.squareup.sqlbrite.SqlBrite.Query;

/**
 *
 */
public class Stop {
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

    long id;
    long code;
    String name;
    String desc;
    double latitude;
    double longitude;
    long zoneId;

    public String getName(){
        return name;
    }

    public static final Func1<Query, List<Stop>> MAP = new Func1<Query, List<Stop>>() {
        @Override public List<Stop> call(Query query) {
            Cursor cursor = query.run();
            try {
                List<Stop> values = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    long id = Db.getLong(cursor, ID);
                    long code = Db.getLong(cursor, CODE);
                    String name = Db.getString(cursor, NAME);
                    String desc = Db.getString(cursor, DESCRIPTION);
                    double lat = Db.getDouble(cursor, LATITUDE);
                    double lon = Db.getDouble(cursor, LONGITUDE);
                    long zoneId = Db.getLong(cursor, ZONE_ID);

                    values.add(new Stop(id, code, name, desc, lat, lon, zoneId));
                }
                return values;
            } finally {
                cursor.close();
            }
        }
    };


    public Stop(long id, long code, String name, String desc, double latitude, double longitude, long zoneId) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoneId = zoneId;
    }
}
