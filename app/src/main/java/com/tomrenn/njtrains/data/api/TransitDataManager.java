package com.tomrenn.njtrains.data.api;

import rx.Completable;
import rx.Observable;

/**
 * Managing the CSV zip files from NJ Transit.
 */
public interface TransitDataManager {

    interface StateListener {
        void update(String parodyDesc);
    }

    Completable fetchLatestData(String zipUrl, StateListener stateListener);

}
