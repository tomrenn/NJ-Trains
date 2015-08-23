package com.tomrenn.njtrains.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomrenn.njtrains.NJTModule;
import com.tomrenn.njtrains.data.api.ApiModule;
import com.tomrenn.njtrains.data.db.DbOpenHelper;

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
    includes = ApiModule.class
)
public class DataModule {

    @Provides @Singleton @RootDir File provideRootInternalDirectory(Application app){
        return app.getFilesDir();
    }

   @Provides @Singleton SharedPreferences provideSharedPreferences(Application app){
       return app.getSharedPreferences("nj_trains", Context.MODE_PRIVATE);
   }

    @Provides @Singleton OkHttpClient provideOkHttpClient(){
        return new OkHttpClient();
    }

    @Provides @Singleton SQLiteOpenHelper provideSQLiteHelper(Application app){
        return new DbOpenHelper(app);
    }

    @Provides @Singleton BriteDatabase provideBriteDatabase(SQLiteOpenHelper sqLiteOpenHelper){
        SqlBrite sqlBrite = SqlBrite.create();
        return sqlBrite.wrapDatabaseHelper(sqLiteOpenHelper);
    }
}
