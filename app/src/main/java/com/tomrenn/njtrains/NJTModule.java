package com.tomrenn.njtrains;

/**
 *
 */

import android.app.Application;

import com.tomrenn.njtrains.data.DataModule;
import com.tomrenn.njtrains.data.TaskService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (
    includes = {
        DataModule.class,
//        UiModule.class
    },
    injects = {
//        WSJ_App.class
            TaskService.class
    }
)
public class NJTModule {
    private final Application app;

    public NJTModule(Application app) {
        this.app = app;
    }

    @Provides @Singleton
    public Application provideApplication(){
        return app;
    }
}
