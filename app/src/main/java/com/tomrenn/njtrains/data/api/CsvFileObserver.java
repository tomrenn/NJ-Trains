package com.tomrenn.njtrains.data.api;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.HandlerThread;

import com.tomrenn.njtrains.data.db.Route;
import com.tomrenn.njtrains.data.db.ServiceDate;
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

        BufferedSource fileSource;
        int numInserted = 0;

        try {
            fileSource = Okio.buffer(Okio.source(file));
            StringBuilder strBuilder = new StringBuilder("INSERT INTO " + tableName + " (");

            SQLiteStatement sqLiteStatement = null;

            db.beginTransaction();
            while (!fileSource.exhausted()){
                String line = fileSource.readUtf8Line();
                // first line
                if (sqLiteStatement == null){
                    int numArgs = line.split(",").length;
                    strBuilder.append(line) // the column order
                            .append(") values(");
                    for (int i = 0; i < numArgs; i++) {
                        strBuilder.append("?,");
                    }
                    // delete last comma ','
                    strBuilder.deleteCharAt(strBuilder.length() - 1);
                    strBuilder.append(")");
                    sqLiteStatement = db.compileStatement(strBuilder.toString());
                    continue;
                }
                String[] values = line.split(",");
                for (int i=0; i<values.length; i++){
                    String value = values[i];
                    // we could optimize this a little, only StopTime.arrival/StopTime.departure have colons
//                    if (value.contains(":")){
//                        value = "\"" + value + "\"";
//                    } else if (value.isEmpty()){
//                        value = "\"\"";
//                    }

                    // for Stop.stop_name which is enclosed in quotes
                    if (value.startsWith("\"") && value.endsWith("\"")){
                        value = value.substring(1, value.length()-1);
                    }
                    sqLiteStatement.bindString(i+1, value);
                }
                sqLiteStatement.executeInsert();
                numInserted++;
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            Timber.d("Inserted " + numInserted + " rows into table " + tableName);
        } catch (IOException e){
            Timber.e(e, "Something bad happened");
        }
    }

    @Override
    public void call(File file) {
        onNext(file);
    }
}
