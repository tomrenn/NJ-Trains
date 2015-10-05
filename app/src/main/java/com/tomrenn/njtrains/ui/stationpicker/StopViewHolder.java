package com.tomrenn.njtrains.ui.stationpicker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.db.Route;

import butterknife.ButterKnife;

import static butterknife.ButterKnife.findById;

/**
 *
 */
class StopViewHolder extends RecyclerView.ViewHolder {
    TextView stopName;
    LinearLayout routeContainer;

    public StopViewHolder(View itemView) {
        super(itemView);
        stopName = findById(itemView, R.id.stopName);
        routeContainer = findById(itemView, R.id.routeContainer);
    }

    public void reset(){
        routeContainer.removeAllViews();
    }

    public void addRouteView(Route route){
        TextView textView = new TextView(itemView.getContext());
        textView.setText(route.getName());
        routeContainer.addView(textView);
    }
}
