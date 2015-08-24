package com.tomrenn.njtrains.data.api;

/**
 *
 */
public class TripResult {
    public final String departureTime;
    public final String arrivalTime;
    public final String serviceNumber;


    public TripResult(String departureTime, String arrivalTime, String serviceNumber) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.serviceNumber = serviceNumber;
    }
}
