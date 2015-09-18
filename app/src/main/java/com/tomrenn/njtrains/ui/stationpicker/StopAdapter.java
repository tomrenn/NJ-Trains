package com.tomrenn.njtrains.ui.stationpicker;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.db.Stop;

import java.util.List;

/**
 *
 */
class StopAdapter extends RecyclerView.Adapter<StopViewHolder> {
    private List<Stop> stops;
    private @Nullable StopSelectedListener stopListener;

    interface StopSelectedListener {
        void onStopSelected(Stop stop);
    }

    public StopAdapter(List<Stop> stops) {
        this.stops = stops;
    }

    public void update(List<Stop> stops){
        this.stops = stops;
        this.notifyDataSetChanged();
    }

    public void setStopSelectedListener(StopSelectedListener stopListener){
        this.stopListener = stopListener;
    }

    @Override
    public StopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.station_stop_item, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StopViewHolder holder, int position) {
        final Stop stop = stops.get(position);
        holder.stopName.setText(stop.prettyName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopListener != null){
                    stopListener.onStopSelected(stop);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }
}
