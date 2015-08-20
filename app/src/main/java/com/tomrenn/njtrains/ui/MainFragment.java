package com.tomrenn.njtrains.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tomrenn.njtrains.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 *
 */
public class MainFragment extends Fragment {

    @Bind(R.id.fromStation) Button fromStation;

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
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, new StationPickerFragment())
                .commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
