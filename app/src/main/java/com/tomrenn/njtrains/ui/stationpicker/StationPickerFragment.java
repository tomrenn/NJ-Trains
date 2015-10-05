package com.tomrenn.njtrains.ui.stationpicker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.api.Station;
import com.tomrenn.njtrains.data.api.StopFinder;
import com.tomrenn.njtrains.data.db.Route;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.ui.MainCallbacks;
import com.tomrenn.njtrains.ui.misc.BindableAdapter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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

    @Bind(R.id.search) EditText searchField;
    @Bind(R.id.routeSpinner) Spinner routeSpinner;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @VisibleForTesting int stationAction;
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
                .debounce(200, TimeUnit.MILLISECONDS)
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
            stopFinder.searchStations(query.toString())
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
                        BaseAdapter adapter = new BindableAdapter<Route>(getActivity()) {
                            @Override
                            public void bindView(Route item, int position, View view) {
                                TextView tv = ButterKnife.findById(view, android.R.id.text1);
                                tv.setText(item.getName());
                            }

                            @Override
                            public int getCount() {
                                return routes.size();
                            }

                            @Override
                            public Route getItem(int position) {
                                return routes.get(position);
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View newView(LayoutInflater inflater, int position, ViewGroup container) {
                                return inflater.inflate(android.R.layout.simple_spinner_item, container, false);
                            }
                        };
                        routeSpinner.setAdapter(adapter);
                    }
                });

        stationAdapter.setStopSelectedListener(new StopSelectedListener() {
            @Override
            public void onStopSelected(Stop stop) {
                if (stationAction == FROM_STATION){
                    mainCallbacks.selectedDeparture(stop);
                } else {
                    mainCallbacks.selectedDestination(stop);
                }
            }
        });
        findStationStops.call("");
    }


}
