package com.tomrenn.njtrains.data.api;

import com.squareup.okhttp.Request;
import com.tomrenn.njtrains.data.db.Stop;

/**
 *
 */
public class TripRequest {
    private Stop fromStation;
    private Stop toStation;
    // additional fields like date / time

    public TripRequest(Stop fromStation, Stop toStation) {
        this.fromStation = fromStation;
        this.toStation = toStation;
    }


    public Stop getToStation() {
        return toStation;
    }

    public void setToStation(Stop toStation) {
        this.toStation = toStation;
    }

    public Stop getFromStation() {
        return fromStation;
    }

    public void setFromStation(Stop fromStation) {
        this.fromStation = fromStation;
    }
}
