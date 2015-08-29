package com.tomrenn.njtrains.ui.stationpicker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tomrenn.njtrains.R;

import butterknife.ButterKnife;

/**
 *
 */
class StopViewHolder extends RecyclerView.ViewHolder {
    TextView stopName;

    public StopViewHolder(View itemView) {
        super(itemView);
        stopName = ButterKnife.findById(itemView, R.id.stopName);
    }
}
