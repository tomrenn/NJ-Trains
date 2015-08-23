package com.tomrenn.njtrains;

import android.app.Application;

import dagger.ObjectGraph;

/**
 *
 */
public class NJTApp extends Application {

    ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

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
