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
            StationPickerFragment.class, WelcomeFragment.class
    }
)
public class MainActivityModule {
    MainActivity mainActivity;

    public MainActivityModule(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Provides MainCallbacks providesMainCallbacks(){
        return mainActivity;
    }


}
