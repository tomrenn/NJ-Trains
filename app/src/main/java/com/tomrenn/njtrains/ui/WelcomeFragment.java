package com.tomrenn.njtrains.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.TransitDataManager;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Manage a preference whether initial data load is completed.
 */
public class WelcomeFragment extends Fragment {

    @Inject TransitDataManager transitDataManager;

    @Bind(R.id.progressText) TextView progressText;

    public static WelcomeFragment getInstance(){
        return new WelcomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.obtain(getActivity().getApplicationContext()).inject(this);
        transitDataManager.fetchLatestData(new TransitDataManager.StateListener() {
            @Override
            public void update(final String parodyDesc) {
                progressText.post(new Runnable() {
                    @Override
                    public void run() {
                        progressText.setText(parodyDesc);
                    }
                });
            }
        })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Void>() {
                @Override
                public void onCompleted() {
                    Timber.d("Files completed");
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, MainFragment.getInstance())
                            .setCustomAnimations(R.anim.abc_slide_out_bottom, R.anim.abc_slide_out_bottom)
                            .commit();
                }

                @Override
                public void onError(Throwable e) {
                    Timber.e(e, "failure");
                }

                @Override
                public void onNext(Void aVoid) {

                }
            });
        // transit manager with listen updates
    }
}
