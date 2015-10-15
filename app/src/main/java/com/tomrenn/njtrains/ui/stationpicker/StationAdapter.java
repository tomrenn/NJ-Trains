package com.tomrenn.njtrains.ui.stationpicker;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.api.models.Station;
import com.tomrenn.njtrains.data.db.Route;
import com.tomrenn.njtrains.data.db.Stop;

import java.util.List;

/**
 *
 */
class StationAdapter extends RecyclerView.Adapter<StopViewHolder> {
    private List<Station> stations;
    private @Nullable StopSelectedListener stopListener;

    interface StopSelectedListener {
        void onStopSelected(Stop stop);
    }

    public StationAdapter(List<Station> stations) {
        this.stations = stations;
    }

    public void update(List<Station> stations){
        this.stations = stations;
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
        holder.reset();
        final Station station = stations.get(position);
        for (Route route : station.getRoutes()){
            holder.addRouteView(route);
        }
        holder.stopName.setText(station.prettyName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopListener != null){
                    Stop stop = Stop.create(station.getStopId(), 0l, station.getName(), "",
                            0l, 0l, 0l);
                    stopListener.onStopSelected(stop);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }
}
