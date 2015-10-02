package com.tomrenn.njtrains.data.db;

import android.util.SparseArray;

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
