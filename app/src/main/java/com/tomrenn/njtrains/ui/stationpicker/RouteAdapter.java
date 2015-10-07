package com.tomrenn.njtrains.ui.stationpicker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.db.Route;
import com.tomrenn.njtrains.ui.misc.BindableAdapter;

import java.util.List;

import butterknife.ButterKnife;

/**
 *
 */
public class RouteAdapter extends BindableAdapter<Route> {
    private List<Route> routes;

    public RouteAdapter(Context context, List<Route> routes) {
        super(context);
        this.routes = routes;
    }

    @Override
    public void bindView(Route item, int position, View view) {
        TextView tv = ButterKnife.findById(view, android.R.id.text1);

        if (position == 0){
            tv.setText(R.string.hint_all_routes);
        } else {
            tv.setText(item.getName());
        }
    }

    @Override
    public int getCount() {
        return routes.size() + 1;
    }

    @Override @Nullable
    public Route getItem(int position) {
        if (position == 0){
            return null;
        } else {
            return routes.get(position-1);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == 0){
            return Route.NON_SELECTABLE_ID;
        } else {
            return routes.get(position-1).getId();
        }
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(android.R.layout.simple_spinner_item, container, false);
    }
}
