package com.tomrenn.njtrains.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.HandlerThread;

import com.tomrenn.njtrains.data.db.Stop;
import com.tomrenn.njtrains.data.db.StopTime;
import com.tomrenn.njtrains.data.db.Trip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import rx.Observer;
import timber.log.Timber;

/**
 * Goal of receiving csv files to be inserted into the given database.
 */
public class CsvFileObserver implements Observer<File> {
    public static final List<String> TABLES =
            Arrays.asList(Stop.TABLE, StopTime.TABLE, Trip.TABLE);
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
        if (!tableExists(tableName) || tableName.equals(StopTime.TABLE)){
            Timber.v("Table for " + file.getName() + " does not exist");
            return;
        }

        BufferedSource fileSource;
        int numInserted = 0;

        try {
            fileSource = Okio.buffer(Okio.source(file));
            StringBuilder strBuilder = new StringBuilder("INSERT INTO " + tableName + " (");
            String str1 = null;

            db.beginTransaction();
            while (!fileSource.exhausted()){
                String line = fileSource.readUtf8Line();
                if (str1 == null){
                    strBuilder.append(line) // the column order
                            .append(") values(");
                    str1 = strBuilder.toString();
                    continue;
                } else {
                    strBuilder = new StringBuilder(str1);
                }
//                if (tableName.equals(StopTime.TABLE)){
                    // arrival time and departure time have colons (:) in them that must be quoted.
                    String[] values = line.split(",");
                    StringBuilder rowBuilder = new StringBuilder(line.length());
                    for (int i=0; i<values.length; i++){
                        String value = values[i];
                        if (i > 0){
                            rowBuilder.append(",");
                        }
                        if (value.contains(":")){
                            rowBuilder.append("\"").append(value).append("\"");
                        } else if (value.isEmpty()) {
                            rowBuilder.append("\"\"");
                        } else {
                            rowBuilder.append(value);
                        }
                    }
                    line = rowBuilder.toString();
//                }
                strBuilder.append(line).append(");");
                db.execSQL(strBuilder.toString());
                numInserted++;
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            Timber.d("Inserted " + numInserted + " rows into table " + tableName);
        } catch (IOException e){
            Timber.e(e, "Something bad happened");
        }
    }
}
