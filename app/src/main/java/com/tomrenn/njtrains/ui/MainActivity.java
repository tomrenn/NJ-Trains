package com.tomrenn.njtrains.ui;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;

import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.Utils;
import com.tomrenn.njtrains.data.api.LastUpdated;
import com.tomrenn.njtrains.data.api.TripRequest;
import com.tomrenn.njtrains.data.api.TripResult;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.prefs.StringPreference;
import com.tomrenn.njtrains.ui.MainActivityModule;
import com.tomrenn.njtrains.ui.MainFragment;
import com.tomrenn.njtrains.data.StopLookup;
import com.tomrenn.njtrains.ui.WelcomeFragment;
import com.tomrenn.njtrains.ui.stationpicker.StationPickerFragment;

import javax.inject.Inject;

import dagger.ObjectGraph;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    @Inject @LastUpdated StringPreference lastUpdated;

    MainFragment mainFragment;
    ObjectGraph activityGraph;
    TripRequest tripResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.riseAndShine(this);
        Timber.plant(new Timber.DebugTree());

        ObjectGraph appGraph = Injector.obtain(getApplicationContext());
        appGraph.inject(this);

        tripResult = new TripRequest(null, null);
        activityGraph = appGraph.plus(new MainActivityModule(this, tripResult));

        FragmentManager fm = getSupportFragmentManager();
        Fragment startFragment = fm.findFragmentById(R.id.fragmentContainer);

        if (startFragment == null){
            if (lastUpdated.isSet()){
                mainFragment = MainFragment.getInstance();
                startFragment = mainFragment;
            } else {
                startFragment = WelcomeFragment.getInstance();
            }

//            ActivityOptionsCompa
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, startFragment)
                    .commit();
        } else {
            if (startFragment instanceof MainFragment){
                mainFragment = (MainFragment) startFragment;
            }
        }

    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)){
            return activityGraph;
        }
        return super.getSystemService(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void pickStationDeparture() {
       pickStation(StationPickerFragment.FROM_STATION);
    }

    void pickStation(int action){
        Fragment fragment = StationPickerFragment.getInstance(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            TransitionSet transitionSet = new TransitionSet();
            transitionSet.addTransition(new ChangeBounds());
            transitionSet.addTransition(new ChangeTransform());

//            transitionSet.addTransition(new )

//            fragment.setSharedElementEnterTransition(transitionSet);
//            fragment.setSharedElementReturnTransition(transitionSet);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                Transition explodeTransform = TransitionInflater.from(this).
//                        inflateTransition(android.R.transition.s);
//                fragment.setEnterTransition(explodeTransform);
            }
        }

        // replace is necessary for transitions? But it also messes with state
        // we need to readd the same mainFragment instead of new one?
        getSupportFragmentManager()
                .beginTransaction()
//                .addSharedElement(mainFragment.getFromStationButton(), "stopField")
                .addToBackStack(null)
                .add(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void selectedDeparture(Stop stop) {
        tripResult.setFromStation(stop);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void pickStationDestination() {
        pickStation(StationPickerFragment.TO_STATION);
    }

    @Override
    public void selectedDestination(Stop stop) {
        tripResult.setToStation(stop);
        getSupportFragmentManager().popBackStack();
    }

}
