package com.tomrenn.njtrains.ui;

import android.os.Bundle;

import com.tomrenn.njtrains.BuildConfig;
import com.tomrenn.njtrains.data.api.TripResult;
import com.tomrenn.njtrains.data.db.Stop;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;
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
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook(){
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
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
        TripResult result = new TripResult("08:27", "09:08", "4420");
        mainFragment.tripFinder = spy(mainFragment.tripFinder);
        Observable<List<TripResult>> tripResults = Observable.just(singletonList(result));
        doReturn(tripResults).when(mainFragment.tripFinder).findTrips(rahway, penn);

        mainFragment.restoreStops(null);

        // verify stops were set
        assertEquals(rahway, mainFragment.fromStation);
        assertEquals(penn, mainFragment.toStation);
        // verify fragment displays results
        verify(mainFragment.tripFinder).findTrips(rahway, penn);
        assertEquals(1, mainFragment.results.getAdapter().getItemCount());
    }
}
