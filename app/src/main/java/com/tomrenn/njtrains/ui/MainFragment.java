package com.tomrenn.njtrains.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.StopLookup;
import com.tomrenn.njtrains.data.db.Stop;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;
import timber.log.Timber;

/**
 *
 */
public class MainFragment extends Fragment {

    @Inject StopLookup stopLookup;

    @Bind(R.id.fromStation) Button fromStation;


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
        Timber.d("CLICK");
        FragmentManager fm = getFragmentManager();

        fm.beginTransaction()
                .addToBackStack("stationFragment")
                .setCustomAnimations(R.anim.slide_in_right, 0,
                        0, android.R.anim.slide_out_right)
                .add(R.id.fragmentContainer, new StationPickerFragment())
                .commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.obtain(getActivity()).inject(this);
        stopLookup.onChanges()
                .subscribe(new Action1<StopLookup>() {
                    @Override
                    public void call(StopLookup stopLookup) {
                        Stop fromStop = stopLookup.fromStation();
                        if (fromStop != null){
                            fromStation.setText(fromStop.getName());
                        }
                    }
                });
    }
}
