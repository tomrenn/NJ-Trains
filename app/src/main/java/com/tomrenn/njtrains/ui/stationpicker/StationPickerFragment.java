package com.tomrenn.njtrains.ui.stationpicker;


import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.api.TripRequest;
import com.tomrenn.njtrains.data.db.Db;
import com.tomrenn.njtrains.data.db.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.tomrenn.njtrains.ui.stationpicker.StopAdapter.StopSelectedListener;

/**
 *
 */
public class StationPickerFragment extends Fragment {
    public static final String STATION_ACTION = "stationAction";
    public static final int FROM_STATION = 0;
    public static final int TO_STATION = 1;

    @Inject TripRequest tripRequest;
    @Inject SQLiteOpenHelper sqLiteOpenHelper;
    @Bind(R.id.search) EditText searchField;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @VisibleForTesting int stationAction;
    BriteDatabase db;
    StopAdapter stopAdapter;
    StopSelectedListener stopSelectedListener;

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
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                query(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    Action1<List<Stop>> receiveResults = new Action1<List<Stop>>() {
        @Override
        public void call(List<Stop> stops) {
            stopAdapter.update(stops);
        }
    };

    void query(String query){
        query = "%" + query + "%";
        db.createQuery(Stop.TABLE, LIST_QUERY, query)
                .map(new Func1<SqlBrite.Query, List<Stop>>() {
                    @Override
                    public List<Stop> call(SqlBrite.Query query) {
                        Cursor cursor = query.run();
                        try {
                            List<Stop> values = new ArrayList<>(cursor.getCount());
                            while (cursor.moveToNext()) {
                                long id = Db.getLong(cursor, Stop.ID);
                                String name = Db.getString(cursor, Stop.NAME);
                                values.add(new Stop(id, 0l, name, "", 0, 0, 0));
                            }
                            return values;
                        } finally {
                            cursor.close();
                        }
                    }
                })
                .subscribe(receiveResults);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        stationAction = args.getInt(STATION_ACTION, -1);
        if (stationAction == -1){
            throw new IllegalStateException("Fragment created with correct action");
        }
        Injector.obtain(getActivity()).inject(this);
        SqlBrite brite = SqlBrite.create();
        db = brite.wrapDatabaseHelper(sqLiteOpenHelper);
        stopSelectedListener = new StopSelectedListener() {
            @Override
            public void onStopSelected(Stop stop) {
                if (stationAction == FROM_STATION){
                    tripRequest.setFromStation(stop);
                } else {
                    tripRequest.setToStation(stop);
                }
                getFragmentManager()
                        .beginTransaction()
                        .remove(StationPickerFragment.this)
                        .commit();
            }
        };
        List<Stop> emptyList = Collections.emptyList();
        stopAdapter = new StopAdapter(emptyList, stopSelectedListener);
        recyclerView.setAdapter(stopAdapter);
        query("");
    }


}
