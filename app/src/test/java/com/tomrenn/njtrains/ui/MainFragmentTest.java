package com.tomrenn.njtrains.ui;

import android.os.Bundle;

import com.tomrenn.njtrains.BuildConfig;
import com.tomrenn.njtrains.data.db.Stop;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;
import static org.junit.Assert.*;
/**
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)
public class MainFragmentTest {
    MainFragment mainFragment;
    Stop rahway;
    Stop penn;

    @Before
    public void setup() {
        mainFragment = MainFragment.getInstance();
        rahway = createStop("Rahway");
        penn = createStop("NYP");
    }


    public Stop createStop(String name) {
        return Stop.create(0, 0, name, "", 0, 0, 0);
    }


    @Test
    public void parcelableTest() {
        startFragment(mainFragment, MainActivity.class);
        mainFragment.stopFinder = spy(mainFragment.stopFinder);
        mainFragment.fromStation = rahway;
        mainFragment.toStation = penn;

        Bundle savedState = new Bundle();
        mainFragment.onSaveInstanceState(savedState);

        mainFragment.onCreate(savedState);
        verifyZeroInteractions(mainFragment.stopFinder);
    }

    @Test
    public void restoreTrips() {
        startFragment(mainFragment, MainActivity.class);
        mainFragment.stopFinder = spy(mainFragment.stopFinder);
        mainFragment.sharedPreferences = spy(mainFragment.sharedPreferences);
        doReturn(1l).when(mainFragment.sharedPreferences).getLong(MainFragment.STATION_FROM_ID, -1);
        doReturn(2l).when(mainFragment.sharedPreferences).getLong(MainFragment.STATION_TO_ID, -1);
        doReturn(Observable.just(rahway)).when(mainFragment.stopFinder).findStop(1);
        doReturn(Observable.just(penn)).when(mainFragment.stopFinder).findStop(2);

        mainFragment.restoreStops(null);
        assertEquals(rahway, mainFragment.fromStation);
        assertEquals(penn, mainFragment.toStation);
    }
}
