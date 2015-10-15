package com.tomrenn.njtrains.data.api;

import com.tomrenn.njtrains.data.api.models.TripResult;
import com.tomrenn.njtrains.data.db.Stop;

import org.threeten.bp.LocalDate;

import java.util.List;

import rx.Observable;

/**
 *
 */
public interface TripFinder {

    Observable<List<TripResult>> findTrips(LocalDate localDate, Stop from, Stop to);
}
