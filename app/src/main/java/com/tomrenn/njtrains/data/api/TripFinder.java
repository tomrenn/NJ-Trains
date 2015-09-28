package com.tomrenn.njtrains.data.api;

import com.tomrenn.njtrains.data.db.Stop;

import java.util.List;

import rx.Observable;

/**
 *
 */
public interface TripFinder {

    Observable<List<TripResult>> findTrips(Stop from, Stop to);
}
