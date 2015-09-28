package com.tomrenn.njtrains.data.api;

import com.tomrenn.njtrains.data.db.Stop;

import java.util.List;

import rx.Observable;

/**
 * Finds stops by id, name, or all stops.
 */
public interface StopFinder {

    Observable<Stop> findStop(long stopId);

    Observable<List<Stop>> searchStops(String name);

    Observable<List<Stop>> allStops();
}
