package com.tomrenn.njtrains.ui;

import com.tomrenn.njtrains.data.db.Stop;

/**
 *
 */
public interface MainCallbacks {

    void pickStationDeparture();
    void selectedDeparture(Stop stop);
    void pickStationDestination();
    void selectedDestination(Stop stop);


}
