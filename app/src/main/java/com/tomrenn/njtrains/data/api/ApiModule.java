package com.tomrenn.njtrains.data.api;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.okhttp.OkHttpClient;
import com.tomrenn.njtrains.ui.MainActivity;
import com.tomrenn.njtrains.data.RootDir;
import com.tomrenn.njtrains.data.prefs.StringPreference;
import com.tomrenn.njtrains.ui.MainFragment;
import com.tomrenn.njtrains.ui.WelcomeFragment;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module(
    library = true,
    complete = false,
    injects = {
            MainActivity.class, WelcomeFragment.class
    }
)
public class ApiModule {

    @Provides @LastUpdated
    StringPreference provideLastUpdated(SharedPreferences prefs){
        return new StringPreference(prefs, "data.api.lastUpdated");
    }

    @Provides @Singleton StopFinder provideStopFinder(NJTStopFinder stopFinder){
        return stopFinder;
    }

    @Provides TripFinder provideTripFinder(NJTripFinder tripFinder){
        return tripFinder;
    }

    @Provides
    TransitDataManager provideTransitDataManager(@LastUpdated StringPreference lastUpdated,
                                                 @RootDir File rootDir, OkHttpClient httpClient,
                                                 SQLiteOpenHelper sqLiteOpenHelper){
        return new NJTDataManager(sqLiteOpenHelper, lastUpdated, httpClient, rootDir);
    }
}
