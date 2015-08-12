package com.tomrenn.njtrains.data.db;

/**
 *
 */
public class StopTime {
    // trip_id,arrival_time,departure_time,stop_id,stop_sequence,pickup_type,drop_off_type,shape_dist_traveled
    // 1,25:53:00,25:53:00,10,1,0,0,0.0000

    public static final String TABLE = "stop_times";

    public static final String TRIP_ID = "trip_id";
    public static final String STOP_ID = "stop_id";
    public static final String ARRIVAL = "arrival_time";
    public static final String DEPARTURE = "departure_time";
    public static final String SEQUENCE = "stop_sequence";
    public static final String PICKUP_TYPE = "pickup_type";
    public static final String DROPOFF_TYPE = "drop_off_type";
    public static final String SHAPE_TRAVELED = "shape_dist_traveled";
}
