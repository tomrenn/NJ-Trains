package com.tomrenn.njtrains.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.api.StopFinder;
import com.tomrenn.njtrains.data.api.TripFinder;
import com.tomrenn.njtrains.data.api.TripResult;
import com.tomrenn.njtrains.data.db.Stop;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 *
 */
public class MainFragment extends Fragment {
    public static final String STATION_FROM_ID = "fromStationId";
    public static final String STATION_FROM = "depaturingStation";
    public static final String STATION_TO_ID = "toStationId";
    public static final String STATION_TO = "destinationStation";

    public static Calendar calendar;

    static {
        calendar = Calendar.getInstance();
    }

    @Inject Clock clock;
    @Inject SharedPreferences sharedPreferences;
    @Inject MainCallbacks mainCallbacks;
    @Inject StopFinder stopFinder;
    @Inject TripFinder tripFinder;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.results) RecyclerView results;
    @Bind(R.id.fromStation) Button fromStationBtn;
    @Bind(R.id.toStation) Button toStationBtn;

    Stop fromStation;
    Stop toStation;


    Action1<Stop> toStationSelection = new Action1<Stop>() {
        @Override
        public void call(Stop stop) {
            if (toStation != stop){ // request b/c of stop change
                requestIfPossible(fromStation, stop);
            }
            toStation = stop;
            toStationBtn.setText(stop.prettyName());

        }
    };

    Action1<Stop> fromStationSelection = new Action1<Stop>() {
        @Override
        public void call(Stop stop) {
            if (fromStation != stop){ // request b/c of stop change
                requestIfPossible(stop, toStation);
            }
            fromStation = stop;
            fromStationBtn.setText(stop.prettyName());
        }
    };
    private TripResultAdapter adapter;

    void requestIfPossible(Stop from, Stop to){
        if (from != null
                && to != null){
            tripFinder.findTrips(LocalDate.now(clock), from, to)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(handleResults);
        }
    }

    public static MainFragment getInstance(){
        return new MainFragment();
    }


    @OnClick(R.id.dateButton)
    void promptDateDialog(){
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                null,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        dpd.show(getActivity().getFragmentManager(), "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @OnClick({R.id.fromStation, R.id.toStation}) void stationClick(Button button){
        if (button.getId() == R.id.fromStation){
            mainCallbacks.pickStationDeparture(fromStationSelection);
        } else {
            mainCallbacks.pickStationDestination(toStationSelection);
        }
    }

    public View getFromStationButton(){
        return fromStationBtn;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATION_TO, toStation);
        outState.putParcelable(STATION_FROM, fromStation);
        super.onSaveInstanceState(outState);
    }

    void restoreStops(@NonNull Bundle savedInstanceState){
        fromStation = savedInstanceState.getParcelable(STATION_FROM);
        if (fromStation != null){
            fromStationSelection.call(fromStation);
        }
        toStation = savedInstanceState.getParcelable(STATION_TO);
        if (toStation != null) {
            toStationSelection.call(null);
        }
    }

    void restoreStop(String prefKey, Action1<Stop> action) {
        long fromId = sharedPreferences.getLong(prefKey, -1);
        if (fromId >= 0){
            stopFinder.findStop(fromId)
                    .subscribe(action);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.obtain(getActivity()).inject(this);

        // todo: don't leave this here.
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        results.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (adapter != null){
            results.setAdapter(adapter);
        }

        if (savedInstanceState != null){
            restoreStops(savedInstanceState);
        } else {
            if (fromStation != null){
                fromStationSelection.call(fromStation);
            }
            if (toStation != null){
                toStationSelection.call(toStation);
            }
        }
        // restore from preferences
        if (fromStation == null){
            restoreStop(STATION_FROM_ID, fromStationSelection);
        }
        if (toStation == null){
            restoreStop(STATION_TO_ID, toStationSelection);
        }
    }


    @Override
    public void onStop() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (fromStation != null){
            editor.putLong(STATION_FROM_ID, fromStation.id());
        }
        if (toStation != null) {
            editor.putLong(STATION_TO_ID, toStation.id());
        }
        editor.apply();
        super.onStop();
    }


    Action1<List<TripResult>> handleResults = new Action1<List<TripResult>>() {
        @Override
        public void call(List<TripResult> tripResults) {
            // nothing
            adapter = new TripResultAdapter(tripResults);
            results.setAdapter(adapter);
        }
    };

}
