package com.tomrenn.njtrains.data.api;

import android.database.sqlite.SQLiteDatabase;

import com.tomrenn.njtrains.data.db.Route;
import com.tomrenn.njtrains.data.db.ServiceDate;
import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.db.StopTime;
import com.tomrenn.njtrains.data.db.Trip;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import rx.Observer;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Goal of receiving csv files to be inserted into the given database.
 */
public class CsvFileObserver implements Observer<File>, Action1<File> {
    public static final List<String> TABLES =
            Arrays.asList(Stop.TABLE, StopTime.TABLE, Trip.TABLE, ServiceDate.TABLE, Route.TABLE);

    private SQLiteDatabase db;

    public CsvFileObserver(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void onCompleted() {
        Timber.d("Completed!");
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Something else bad happened");
    }

    boolean tableExists(String tableName){
        return TABLES.contains(tableName);
    }

    @Override
    public void onNext(File file) {
        String tableName = file.getName().replace(".txt", "");
        if (!tableExists(tableName)){
            Timber.v("Table for " + file.getName() + " does not exist");
            return;
        }

        try {
            TableImportStrategy importStrategy = TableImportStrategy.from(db, file);
            importStrategy.importAll();
        } catch (IOException e){
            Timber.e(e, "Something bad happened during " + file);
        }
    }

    @Override
    public void call(File file) {
        onNext(file);
    }
}
