package com.tomrenn.njtrains.ui;

import com.tomrenn.njtrains.data.db.Stop;

import rx.functions.Action1;

/**
 *
 */
public interface MainCallbacks {
    void finishedWelcome();

    void pickStationDeparture(Action1<Stop> selected);
    void selectedDeparture(Stop stop);
    void pickStationDestination(Action1<Stop> selected);
    void selectedDestination(Stop stop);


}
