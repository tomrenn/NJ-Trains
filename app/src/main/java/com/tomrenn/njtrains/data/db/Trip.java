package com.tomrenn.njtrains.data.db;

import android.database.Cursor;

import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 *
 */
public class Trip {
    // route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id
    // 1,1,1,"30TH ST. PHL.",0,"4652",1
    public static final String TABLE = "trips";

    public static final String ID = "trip_id";
    public static final String ROUTE_ID = "route_id";
    public static final String SERVICE_ID = "service_id";
    public static final String HEADSIGN = "trip_headsign";
    public static final String DIRECTION_ID = "direction_id";
    public static final String BLOCK_ID = "block_id";
    public static final String SHAPE_ID = "shape_id";

    int id;
    int routeId;
    int serviceId;
    String headsign;
    int directionId;
    int blockId;
    int shapeId;

    public static final Func1<SqlBrite.Query, List<Trip>> MAP = new Func1<SqlBrite.Query, List<Trip>>() {
        @Override public List<Trip> call(SqlBrite.Query query) {
            Cursor cursor = query.run();
            try {
                List<Trip> values = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    int id = Db.getInt(cursor, ID);
                    int routeId = Db.getInt(cursor, ROUTE_ID);
                    int serviceId = Db.getInt(cursor, SERVICE_ID);
                    String headsign = Db.getString(cursor, HEADSIGN);
                    int directionId = Db.getInt(cursor, DIRECTION_ID);
                    int blockId = Db.getInt(cursor, BLOCK_ID);
                    int shapeId = Db.getInt(cursor, SHAPE_ID);

                    values.add(new Trip(id, routeId, serviceId, headsign, directionId, blockId, shapeId));
                }
                return values;
            } finally {
                cursor.close();
            }
        }
    };

    public Trip(int id, int routeId, int serviceId, String headsign, int directionId, int blockId, int shapeId) {
        this.id = id;
        this.routeId = routeId;
        this.serviceId = serviceId;
        this.headsign = headsign;
        this.directionId = directionId;
        this.blockId = blockId;
        this.shapeId = shapeId;
    }
}
