package com.tomrenn.njtrains.data.api.models;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.ChronoUnit;

/**
 *
 */
public class TripResult {
    public final LocalDateTime departure;
    public final LocalDateTime arrival;
    public final String departureTime;
    public final String arrivalTime;
    public final String serviceNumber;


    public TripResult(LocalDate date, String departureTime, String arrivalTime, String serviceNumber) {
        this.departureTime = departureTime;
        this.departure = parse(date, departureTime);
        this.arrival = parse(date, arrivalTime);
        this.arrivalTime = arrivalTime;
        this.serviceNumber = serviceNumber;
    }

    public long getDuration(){
        return Duration.between(departure, arrival).toMinutes();
    }


    LocalDateTime parse(LocalDate date, String time){
        String[] slices = time.split(":");
        int hour = 0;
        if (slices.length > 0){
            hour = Integer.parseInt(slices[0]);
        }
        int minute = 0;
        if (slices.length > 1){
            minute = Integer.parseInt(slices[1]);
        }
        // roll over the day if need be
        if (hour > 23){
            date.plusDays(1);
            hour = hour - 24;
        }
        return date.atTime(hour, minute);
    }

}
