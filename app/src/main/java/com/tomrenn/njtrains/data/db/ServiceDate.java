package com.tomrenn.njtrains.data.db;

/**
 *
 */
public class ServiceDate {
//    service_id,date,exception_type
//    1,20150807,1
    public static final String TABLE = "calendar_dates";

    public static final String ID = "service_id";
    public static final String DATE = "date"; // https://www.sqlite.org/lang_datefunc.html
    public static final String EXCEPTION_TYPE = "exception_type";
}
