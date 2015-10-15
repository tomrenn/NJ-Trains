package com.tomrenn.njtrains.ui;

import android.os.Bundle;

import com.tomrenn.njtrains.BuildConfig;
import com.tomrenn.njtrains.data.api.models.TripResult;
import com.tomrenn.njtrains.data.db.Stop;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.threeten.bp.LocalDate;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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
public class TestMainFragment {
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
    public void restoreFromPreferences() {
        startFragment(mainFragment, MainActivity.class);
        mainFragment.stopFinder = spy(mainFragment.stopFinder);
        mainFragment.sharedPreferences.edit()
            .putLong(MainFragment.STATION_FROM_ID, 1)
            .putLong(MainFragment.STATION_TO_ID, 2l)
            .commit();
        doReturn(Observable.just(rahway)).when(mainFragment.stopFinder).findStop(1);
        doReturn(Observable.just(penn)).when(mainFragment.stopFinder).findStop(2);
        TripResult result = new TripResult(LocalDate.now(), "08:27", "09:08", "4420");
        mainFragment.tripFinder = spy(mainFragment.tripFinder);
        LocalDate localDate = LocalDate.now();
        Observable<List<TripResult>> tripResults = Observable.just(singletonList(result));
        doReturn(tripResults).when(mainFragment.tripFinder).findTrips(localDate, rahway, penn);


        mainFragment.restoreFromPreferences();

        // verify stops were set
        assertEquals(rahway, mainFragment.fromStation);
        assertEquals(penn, mainFragment.toStation);
        // verify fragment displays results
        verify(mainFragment.tripFinder).findTrips(localDate, rahway, penn);
        assertEquals(1, mainFragment.results.getAdapter().getItemCount());
    }
}
