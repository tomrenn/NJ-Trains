package com.tomrenn.njtrains.ui;

import com.tomrenn.njtrains.NJTModule;
import com.tomrenn.njtrains.data.api.TripRequest;
import com.tomrenn.njtrains.ui.stationpicker.StationPickerFragment;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module(
    addsTo = NJTModule.class,
    injects = {
            MainFragment.class,
            StationPickerFragment.class
    }
)
public class MainActivityModule {
    TripRequest tripRequest;

    public MainActivityModule(TripRequest tripRequest){
        this.tripRequest = tripRequest;
    }

    @Provides
    TripRequest provideStopLookup(){
        return tripRequest;
    }
}
