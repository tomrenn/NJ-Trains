package com.tomrenn.njtrains.data.api;

import android.database.Cursor;

import com.tomrenn.njtrains.data.db.Db;
import com.tomrenn.njtrains.data.db.Route;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.util.Strings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rx.functions.Func1;

/**
 *
 */
public class Station {
    public static String LOOKUP_QUERY = "SELECT DISTINCT"
            + " stop_times.stop_id, stops.stop_name, trips.route_id, routes.route_long_name, routes.route_type"
        + " FROM stop_times "
        + " JOIN stops ON (stop_times.stop_id=stops.stop_id AND stops.stop_name like ?)"
        + " JOIN trips ON (stop_times.trip_id=trips.trip_id)"
        + " JOIN routes ON (trips.route_id=routes.route_id)"
        + " ORDER BY stop_times.stop_id";

    private String name;
    private long stopId;
    private Route[] routes;

    public Station(String name, long stopId, Route[] routes) {
        this.name = name;
        this.stopId = stopId;
        this.routes = routes;
    }

    public String prettyName(){
        return Strings.capitalizeString(name);
    }

    public String getName() {
        return name;
    }

    public long getStopId() {
        return stopId;
    }

    public Route[] getRoutes() {
        return routes;
    }

    public static final Func1<Cursor, List<Station>> cursorToValue = new Func1<Cursor, List<Station>>() {
        @Override
        public List<Station> call(Cursor cursor) {
            try {
                List<Station> values = new ArrayList<>(cursor.getCount());

                List<Route> stationRoutes = new LinkedList<>();
                String currentStopName = "";
                long currentStopId = Long.MIN_VALUE;

                while (cursor.moveToNext()) {
                    long stationId = Db.getLong(cursor, "stop_id");
                    if (currentStopId == Long.MIN_VALUE){
                        currentStopName = Db.getString(cursor, "stop_name");
                        currentStopId = stationId;
                        stationRoutes.add(getRoute(cursor));
                        continue;
                    }
                    if (stationId == currentStopId){ // same stop, additional route
                        stationRoutes.add(getRoute(cursor));
                    } else { // new stop
                        // create station with current routes and stop info
                        Route[] routes = new Route[stationRoutes.size()];
                        routes = stationRoutes.toArray(routes);
                        values.add(new Station(currentStopName, currentStopId, routes));
                        // clear routes for this stop
                        stationRoutes.clear();

                        // update current stop info with new stop
                        currentStopName = Db.getString(cursor, "stop_name");
                        currentStopId = stationId;
                        stationRoutes.add(getRoute(cursor));
                    }
                }
                // last stop/routes will never be inserted to values
                // _because_ the stopId will not change triggering the insert
                if (currentStopId != Long.MIN_VALUE){
                    Route[] routes = new Route[stationRoutes.size()];
                    routes = stationRoutes.toArray(routes);
                    values.add(new Station(currentStopName, currentStopId, routes));
                }
                return values;
            } finally {
                cursor.close();
            }
        }
    };

    static Route getRoute(Cursor cursor){
        int routeId = Db.getInt(cursor, "route_id");
        int type = Db.getInt(cursor, "route_type");
        String name = Db.getString(cursor, "route_long_name");
        return Route.create(routeId, "", name, type);
    }

}
