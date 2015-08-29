package com.tomrenn.njtrains.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.StopLookup;
import com.tomrenn.njtrains.data.api.TripFinder;
import com.tomrenn.njtrains.data.api.TripRequest;
import com.tomrenn.njtrains.data.api.TripResult;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.ui.stationpicker.StationPickerFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 */
public class MainFragment extends Fragment {

    @Inject TripFinder tripFinder;
    @Inject TripRequest tripRequest;

    @Bind(R.id.results) RecyclerView results;
    @Bind(R.id.fromStation) Button fromStationBtn;
    @Bind(R.id.toStation) Button toStationBtn;

    Action1<TripRequest> updateTripViews = new Action1<TripRequest>() {
        @Override
        public void call(TripRequest tripRequest) {
            Stop fromStation = tripRequest.getFromStation();
            Stop toStation = tripRequest.getToStation();

            if (fromStation != null){
                fromStationBtn.setText(tripRequest.getFromStation().getName());
            }
            if (toStation != null){
                toStationBtn.setText(tripRequest.getToStation().getName());
            }
        }
    };

    Func1<TripRequest, Boolean> filterRequestable = new Func1<TripRequest, Boolean>() {
        @Override
        public Boolean call(TripRequest tripResult) {
            return tripResult.getFromStation() != null
                    && tripResult.getToStation() != null;
        }
    };

    public static MainFragment getInstance(){
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @OnClick({R.id.fromStation, R.id.toStation}) void stationClick(Button button){
        FragmentManager fm = getFragmentManager();

        int action;
        if (button.getId() == R.id.fromStation){
            action = StationPickerFragment.FROM_STATION;
        } else {
            action = StationPickerFragment.TO_STATION;
        }

        fm.beginTransaction()
                .addToBackStack("stationFragment")
                .setCustomAnimations(R.anim.slide_in_right, 0,
                        0, android.R.anim.slide_out_right)
                .add(R.id.fragmentContainer, StationPickerFragment.getInstance(action))
                .commit();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.obtain(getActivity()).inject(this);
        results.setLayoutManager(new LinearLayoutManager(getActivity()));


        Observable<TripRequest> changes = tripRequest.onChanges();

        changes
                .doOnNext(updateTripViews)
                .filter(filterRequestable)
                .subscribe(findResults);
    }

    Action1<List<TripResult>> handleResults = new Action1<List<TripResult>>() {
        @Override
        public void call(List<TripResult> tripResults) {
            // nothing
            TripResultAdapter adapter = new TripResultAdapter(tripResults);
            results.setAdapter(adapter);
        }
    };


    Action1<TripRequest> findResults = new Action1<TripRequest>() {
        @Override
        public void call(TripRequest tripRequest) {
            tripFinder.findTrips(tripRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(handleResults);
        }
    };
}
