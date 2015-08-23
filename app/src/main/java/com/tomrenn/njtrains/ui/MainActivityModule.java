package com.tomrenn.njtrains.ui;

import com.tomrenn.njtrains.NJTModule;
import com.tomrenn.njtrains.data.StopLookup;

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
    StopLookup stopLookup;

    public MainActivityModule(StopLookup stopLookup){
        this.stopLookup = stopLookup;
    }

    @Provides
    StopLookup provideStopLookup(){
        return stopLookup;
    }
}
