package com.tomrenn.njtrains.ui;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
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
import rx.functions.Action1;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    @Inject @LastUpdated StringPreference lastUpdated;

    MainFragment mainFragment;
    ObjectGraph activityGraph;
    @Nullable Action1<Stop> pendingStopSelection;
//    TripRequest tripResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.riseAndShine(this);

        ObjectGraph appGraph = Injector.obtain(getApplicationContext());
        appGraph.inject(this);

        activityGraph = appGraph.plus(new MainActivityModule(this));

        FragmentManager fm = getSupportFragmentManager();
        Fragment startFragment = fm.findFragmentById(R.id.fragmentContainer);

        if (startFragment == null){
            if (lastUpdated.isSet()){
                mainFragment = MainFragment.getInstance();
                startFragment = mainFragment;
                mainFragment.setRetainInstance(true);
            } else {
                startFragment = WelcomeFragment.getInstance();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, startFragment)
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
    public void finishedWelcome() {
        this.mainFragment = MainFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, mainFragment)
                .setCustomAnimations(R.anim.abc_slide_out_bottom, R.anim.abc_slide_out_bottom)
                .commit();
    }

    @Override
    public void pickStationDeparture(Action1<Stop> selectAction) {
        pendingStopSelection = selectAction;
        pickStation(StationPickerFragment.FROM_STATION);
    }

    void pickStation(int action){
        Fragment fragment = StationPickerFragment.getInstance(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            TransitionSet transitionSet = new TransitionSet();
            transitionSet.addTransition(new ChangeBounds());

//            transitionSet.addTransition(new )

//            fragment.setSharedElementEnterTransition(transitionSet);
//            fragment.setSharedElementReturnTransition(transitionSet);
//            Transition slideLeft = TransitionInflater.from(this).
//                    inflateTransition(android.R.transition.fade);

//            fragment.setEnterTransition(new Fade());
//            fragment.setExitTransition(new Fade());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                transitionSet.addTransition(new ChangeTransform());

//                Transition explodeTransform = TransitionInflater.from(this).
//                        inflateTransition(android.R.transition.explode);
//                fragment.setEnterTransition(explodeTransform);
            }
        }

        // replace is necessary for transitions? But it also messes with state
        // we need to readd the same mainFragment instead of new one?
        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(mainFragment.getFromStationButton(), "stopField")
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void selectedDeparture(Stop stop) {
        getSupportFragmentManager().popBackStack();
        if (pendingStopSelection != null){
            pendingStopSelection.call(stop);
        }
    }

    @Override
    public void pickStationDestination(Action1<Stop> selectAction) {
        pendingStopSelection = selectAction;
        pickStation(StationPickerFragment.TO_STATION);
    }

    @Override
    public void selectedDestination(Stop stop) {
        getSupportFragmentManager().popBackStack();
        if (pendingStopSelection != null){
            pendingStopSelection.call(stop);
        }
    }

}
