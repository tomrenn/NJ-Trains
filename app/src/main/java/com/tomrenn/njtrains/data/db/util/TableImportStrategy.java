package com.tomrenn.njtrains.data.db.util;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.tomrenn.njtrains.data.db.Stop;

import java.io.File;
import java.io.IOException;

import okio.BufferedSource;
import okio.Okio;

/**
 *strategy
 */
public class TableImportStrategy {
    SQLiteDatabase db;
    String tableName;
    File csvFile;

    TableImportStrategy(SQLiteDatabase db, String tableName, File csvFile) {
        this.db = db;
        this.csvFile = csvFile;
        this.tableName = tableName;
    }

    public static TableImportStrategy from(SQLiteDatabase db, File csvFile){
        String tableName = csvFile.getName().replace(".txt", "");
        switch (tableName){
            case Stop.TABLE:
                return new StopTableImportStrategy(db, tableName, csvFile);
            default:
                return new TableImportStrategy(db, tableName, csvFile);
        }
    }

    protected void handleColumnNames(String[] names){

    }

    protected String[] cleanUpValues(String[] values){
        return values;
    }

    public void importAll() throws IOException{
        BufferedSource fileSource = Okio.buffer(Okio.source(csvFile));
        StringBuilder strBuilder = new StringBuilder("INSERT INTO " + tableName + " (");

        SQLiteStatement sqLiteStatement = null;

        db.beginTransaction();
        try {
            while (!fileSource.exhausted()) {
                String line = fileSource.readUtf8Line();
                // first line
                if (sqLiteStatement == null) {
                    String[] columnNames = line.split(",");
                    handleColumnNames(columnNames);
                    int numArgs = columnNames.length;
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
                values = cleanUpValues(values);

                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    sqLiteStatement.bindString(i + 1, value);
                }
                sqLiteStatement.executeInsert();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    static class StopTableImportStrategy extends TableImportStrategy {
        int stopNameIndex = -1;

        StopTableImportStrategy(SQLiteDatabase db, String tableName, File csvFile) {
            super(db, tableName, csvFile);
        }

        @Override
        protected void handleColumnNames(String[] names) {
            for (int i=0; i<names.length; i++){
                if (Stop.NAME.equals(names[i])){
                    stopNameIndex = i;
                    break;
                }
            }
        }

        static String cleanLightRail(String stopName){
            String lowercase = stopName.toLowerCase();
            if (lowercase.matches(".*light rail sta(?:tion)?$")) {
                int index = lowercase.indexOf("light rail sta");
                if (index != -1){
                    return stopName.substring(0, index).trim();
                }
            }
            return stopName;
        }

        static String cleanSecaucus(String stopName){
            String prefix = "frank r lautenberg ";
            if (stopName.toLowerCase().startsWith(prefix)){
                return stopName.substring(prefix.length());
            }
            return stopName;
        }

        @Override
        protected String[] cleanUpValues(String[] values) {
            if (stopNameIndex != -1){
                String name = values[stopNameIndex];
                if (name.startsWith("\"") && name.endsWith("\"")){
                    name = name.substring(1, name.length()-1);
                    // we're also going to remove things like "Light Rail Station" from stop_name.
                    // also rename Frank R Lautenberg Secaucus to just Secaucus X
                }
                name = cleanLightRail(name);
                name = cleanSecaucus(name);

                values[stopNameIndex] = name;
            }
            return values;
        }
    }

    // also strip Route, long_name
}
