package com.tomrenn.njtrains.data.db.util;

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

import rx.functions.Action1;
import timber.log.Timber;

/**
 * Goal of receiving csv files to be inserted into the given database.
 */
public class CSVTableImport implements Action1<File> {
    public static final List<String> TABLES =
            Arrays.asList(Stop.TABLE, StopTime.TABLE, Trip.TABLE, ServiceDate.TABLE, Route.TABLE);

    private SQLiteDatabase db;

    public CSVTableImport(SQLiteDatabase db) {
        this.db = db;
    }

    boolean tableExists(String tableName){
        return TABLES.contains(tableName);
    }

    @Override
    public void call(File file) {
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
}
