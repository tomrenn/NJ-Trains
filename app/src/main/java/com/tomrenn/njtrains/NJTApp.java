package com.tomrenn.njtrains;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import dagger.ObjectGraph;

/**
 *
 */
public class NJTApp extends Application {

    ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        objectGraph = ObjectGraph.create(Modules.list(this));
    }

    @Override
    public Object getSystemService(String name) {
        if (Injector.matchesService(name)){
            return objectGraph;
        }
        return super.getSystemService(name);
    }
}
