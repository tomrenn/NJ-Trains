package com.tomrenn.njtrains.data.api;

import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.db.Trip;

import java.util.List;

import rx.Observable;

/**
 *
 */
public interface TripFinder {

    Observable<List<TripResult>> findTrips(TripRequest tripRequest);
}
