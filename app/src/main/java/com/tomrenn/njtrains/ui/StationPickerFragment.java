package com.tomrenn.njtrains.ui;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.db.Db;
import com.tomrenn.njtrains.data.db.DbOpenHelper;
import com.tomrenn.njtrains.data.db.Stop;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 *
 */
public class StationPickerFragment extends Fragment {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    DbOpenHelper dbHelper;

    private static final String LIST_QUERY = "SELECT * FROM "
            + Stop.TABLE
            + " ORDER BY "
            + Stop.NAME
            + " ASC";

    public static StationPickerFragment getInstance(){
        return new StationPickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.station_picker, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper = new DbOpenHelper(getActivity());
        SqlBrite sqlBrite = SqlBrite.create();
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(dbHelper);

        db.createQuery(Stop.TABLE, LIST_QUERY)
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
            .subscribe(new Action1<List<Stop>>() {
                @Override
                public void call(List<Stop> stops) {
                    StopAdapter adapter = new StopAdapter(stops);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                }
            });
    }

    static class StopAdapter extends RecyclerView.Adapter<StopViewHolder> {
        private List<Stop> stops;

        public StopAdapter(List<Stop> stops) {
            this.stops = stops;
        }

        @Override
        public StopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.station_stop_item, parent, false);
            return new StopViewHolder(view);
        }

        @Override
        public void onBindViewHolder(StopViewHolder holder, int position) {
            Stop stop = stops.get(position);
            holder.stopName.setText(stop.getName());
        }

        @Override
        public int getItemCount() {
            return stops.size();
        }
    }

    static class StopViewHolder extends RecyclerView.ViewHolder {
        TextView stopName;

        public StopViewHolder(View itemView) {
            super(itemView);
            stopName = ButterKnife.findById(itemView, R.id.stopName);
        }
    }
}
