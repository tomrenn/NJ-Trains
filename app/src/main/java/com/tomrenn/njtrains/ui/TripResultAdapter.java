package com.tomrenn.njtrains.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.api.TripResult;

import org.w3c.dom.Text;

import java.util.List;

import static butterknife.ButterKnife.findById;

/**
 *
 */
public class TripResultAdapter extends RecyclerView.Adapter<TripResultAdapter.ResultViewHolder> {
    private List<TripResult> results;

    public TripResultAdapter(List<TripResult> results) {
        this.results = results;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ResultViewHolder(inflater.inflate(R.layout.trip_result_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        TripResult result = results.get(position);
        holder.departureWarning.setText(result.getDuration() + " minutes");
        holder.time.setText(result.departureTime + " - " + result.arrivalTime);
        holder.serviceLine.setText(result.serviceNumber);
    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        public final TextView time;
        public final TextView departureWarning;
        public final TextView serviceLine;

        public ResultViewHolder(View itemView) {
            super(itemView);
            time = findById(itemView, R.id.time);
            departureWarning = findById(itemView, R.id.departWarning);
            serviceLine = findById(itemView, R.id.serviceLine);
        }
    }
}
