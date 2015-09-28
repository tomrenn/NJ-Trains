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
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.api.StopFinder;
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

import static com.tomrenn.njtrains.ui.stationpicker.StopAdapter.StopSelectedListener;

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
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @VisibleForTesting int stationAction;
    StopAdapter stopAdapter;
    Subscription textChangeSubscription;

    private static final String LIST_QUERY = "SELECT * FROM "
            + Stop.TABLE
            + " WHERE " + Stop.NAME + " LIKE ?"
            + " ORDER BY "
            + Stop.NAME
            + " ASC";


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
        List<Stop> emptyList = Collections.emptyList();
        stopAdapter = new StopAdapter(emptyList);
        recyclerView.setAdapter(stopAdapter);

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

    Action1<List<Stop>> receiveResults = new Action1<List<Stop>>() {
        @Override
        public void call(List<Stop> stops) {
            stopAdapter.update(stops);
        }
    };

    Action1<CharSequence> findStationStops = new Action1<CharSequence>() {
        @Override
        public void call(CharSequence query) {
            stopFinder.searchStops(query.toString())
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

        stopAdapter.setStopSelectedListener(new StopSelectedListener() {
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
