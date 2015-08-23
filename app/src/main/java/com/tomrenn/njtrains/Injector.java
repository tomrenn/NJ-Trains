package com.tomrenn.njtrains;

import android.content.Context;

import dagger.ObjectGraph;

/**
 *
 */
public class Injector {
    private static final String INJECT_SERVICE = "injection-service";

    public static ObjectGraph obtain(Context context){
        //noinspection ResourceType
        return (ObjectGraph) context.getSystemService(INJECT_SERVICE);
    }

    public static boolean matchesService(String name){
        return INJECT_SERVICE.equals(name);
    }
}
