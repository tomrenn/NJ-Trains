package com.tomrenn.njtrains.ui.stationpicker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.api.models.Station;
import com.tomrenn.njtrains.data.api.StopFinder;
import com.tomrenn.njtrains.data.db.Route;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.ui.MainCallbacks;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

import static com.tomrenn.njtrains.ui.stationpicker.StationAdapter.StopSelectedListener;

/**
 *
 */
public class StationPickerFragment extends Fragment {
    public static final String STATION_ACTION = "stationAction";
    public static final int FROM_STATION = 0;
    public static final int TO_STATION = 1;

    @Inject MainCallbacks mainCallbacks;
    @Inject StopFinder stopFinder;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.search) EditText searchField;
    @Bind(R.id.routeSpinner) Spinner routeSpinner;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @VisibleForTesting int stationAction;
    long routeId = Route.NON_SELECTABLE_ID;
    StationAdapter stationAdapter;
    Subscription textChangeSubscription;


    public static StationPickerFragment getInstance(int action){
        Bundle args = new Bundle();
        args.putInt(STATION_ACTION, action);
        StationPickerFragment frag = new StationPickerFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.station_picker, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Station> emptyList = Collections.emptyList();
        stationAdapter = new StationAdapter(emptyList);
        recyclerView.setAdapter(stationAdapter);

        textChangeSubscription = RxTextView.textChanges(searchField)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(findStationStops);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textChangeSubscription.unsubscribe();
    }

    Action1<List<Station>> receiveResults = new Action1<List<Station>>() {
        @Override
        public void call(List<Station> stops) {
            stationAdapter.update(stops);
        }
    };

    Action1<CharSequence> findStationStops = new Action1<CharSequence>() {
        @Override
        public void call(CharSequence query) {
            stopFinder.searchStations(query.toString(), routeId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(receiveResults);
        }
    };



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        stationAction = args.getInt(STATION_ACTION, -1);
        if (stationAction == -1){
            throw new IllegalStateException("Fragment created with correct action");
        }
        Injector.obtain(getActivity()).inject(this);

        stopFinder.allRoutes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Route>>() {
                    @Override
                    public void call(final List<Route> routes) {
                        routeSpinner.setAdapter(new RouteAdapter(getActivity(), routes));
                    }
                });
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Timber.d("Route id selected = " + id);
                StationPickerFragment.this.routeId = id;
                findStationStops.call(StationPickerFragment.this.searchField.getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        stationAdapter.setStopSelectedListener(new StopSelectedListener() {
            @Override
            public void onStopSelected(Stop stop) {
                if (stationAction == FROM_STATION) {
                    mainCallbacks.selectedDeparture(stop);
                } else {
                    mainCallbacks.selectedDestination(stop);
                }
            }
        });
        findStationStops.call("");
    }

}
