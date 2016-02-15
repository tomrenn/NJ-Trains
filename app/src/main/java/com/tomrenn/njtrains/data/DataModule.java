package com.tomrenn.njtrains.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomrenn.njtrains.data.api.ApiModule;
import com.tomrenn.njtrains.data.api.TransitService;
import com.tomrenn.njtrains.data.db.DbOpenHelper;

import org.threeten.bp.Clock;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

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

    @Provides @Singleton
    TransitService transitService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://njtrains-api.tomrenn.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TransitService service = retrofit.create(TransitService.class);
        return service;
    }

    @Provides @Singleton Clock providesClock(){
        return Clock.systemDefaultZone();
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(){
        return new OkHttpClient();
    }

    @Provides @Singleton SQLiteOpenHelper provideSQLiteHelper(Application app){
        return new DbOpenHelper(app);
    }

    @Provides @Singleton SQLiteDatabase providesSQLiteDB(SQLiteOpenHelper sqLiteOpenHelper){
        return sqLiteOpenHelper.getReadableDatabase();
    }

    @Provides @Singleton BriteDatabase provideBriteDatabase(SQLiteOpenHelper sqLiteOpenHelper){
        SqlBrite sqlBrite = SqlBrite.create();
        return sqlBrite.wrapDatabaseHelper(sqLiteOpenHelper);
    }
}
