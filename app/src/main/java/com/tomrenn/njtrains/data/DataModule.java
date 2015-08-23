package com.tomrenn.njtrains.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.squareup.okhttp.OkHttpClient;
import com.tomrenn.njtrains.NJTModule;
import com.tomrenn.njtrains.data.api.ApiModule;
import com.tomrenn.njtrains.data.db.DbOpenHelper;

import java.io.File;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module(
    library = true,
    complete = false,
    includes = ApiModule.class
)
public class DataModule {

    @Provides @RootDir File provideRootInternalDirectory(Application app){
        return app.getFilesDir();
    }

   @Provides SharedPreferences provideSharedPreferences(Application app){
       return app.getSharedPreferences(app.getPackageName() + "_preferences", Context.MODE_PRIVATE);
   }

    @Provides OkHttpClient provideOkHttpClient(){
        return new OkHttpClient();
    }

    @Provides SQLiteOpenHelper provideSQLiteHelper(Application app){
        return new DbOpenHelper(app);
    }
}
