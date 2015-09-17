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
    MainActivity mainActivity;
    TripRequest tripRequest;

    public MainActivityModule(MainActivity mainActivity, TripRequest tripRequest){
        this.tripRequest = tripRequest;
        this.mainActivity = mainActivity;
    }

    @Provides MainCallbacks providesMainCallbacks(){
        return mainActivity;
    }

    @Provides
    TripRequest provideStopLookup(){
        return tripRequest;
    }
}
