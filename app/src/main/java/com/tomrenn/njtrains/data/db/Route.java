package com.tomrenn.njtrains.data.db;

import android.database.Cursor;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 *
 */
public class Route {
    public static final String TABLE = "routes";

    public static final String ID = "route_id";
    public static final String AGENCY_ID = "agency_id";
    public static final String SHORT_NAME = "route_short_name";
    public static final String LONG_NAME = "route_long_name";
    public static final String ROUTE_TYPE = "route_type";
    public static final String URL = "route_url";
    public static final String COLOR = "route_color";

    private static SparseArray<Route> routeCache = new SparseArray<>(12);

    public static final String LOOKUP_QUERY = "SELECT *"
            + " FROM " + TABLE
            + " GROUP BY " + LONG_NAME // removes duplicates w/ same name
            + " ORDER BY " + LONG_NAME;

    int id;
    String agency;
    String name;
    int type;

    public static Route create(int id, String agencyId, String name, int type){
        Route route = routeCache.get(id);
        if (route == null){
            route = new Route(id, agencyId, name, type);
            routeCache.put(id, route);
        }
        return route;
    }

    Route(int id, String agency, String name, int type) {
        this.id = id;
        this.agency = agency;
        this.name = name;
        this.type = type;
    }

    public static final Func1<Cursor, List<Route>> cursorToValues = new Func1<Cursor, List<Route>>() {
        @Override
        public List<Route> call(Cursor cursor) {
            try {
                List<Route> values = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    int id = Db.getInt(cursor, Route.ID);
                    String agency = Db.getString(cursor, Route.AGENCY_ID);
                    String name = Db.getString(cursor, Route.LONG_NAME);
                    int type = Db.getInt(cursor, Route.ROUTE_TYPE);

                    values.add(Route.create(id, agency, name, type));
                }
                return values;
            } finally {
                cursor.close();
            }
        }
    };


    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getAgency() {
        return agency;
    }

    public String getName() {
        return name;
    }
}
