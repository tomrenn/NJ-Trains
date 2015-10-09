package com.tomrenn.njtrains.data.db.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tomrenn.njtrains.data.db.Db;
import com.tomrenn.njtrains.data.db.Route;
import com.tomrenn.njtrains.data.db.Trip;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Tidy up data tables.
 */
public class TidyTable implements Observable.OnSubscribe<Void>{
    SQLiteDatabase db;

    TidyTable(SQLiteDatabase db) {
        this.db = db;
    }

    public static Observable<Void> asObservable(SQLiteDatabase db) {
        return Observable.create(new TidyTable(db));
    }

    void tidyTables(){
        tidyRoutes();
        Timber.d("Tables tidied");
    }

    void tidyRoutes(){
        String query = "select routes.route_id, duplicates.route_id as duplicate_id " +
            "FROM routes " +
            "JOIN ( " +
                "SELECT * " +
                "FROM routes " +
                "GROUP BY route_long_name " +
                "HAVING count(route_long_name) > 1)  as duplicates  " +
                "ON (routes.route_long_name=duplicates.route_long_name " +
                     "AND routes.route_id != duplicates.route_id)";

        Cursor cursor = db.rawQuery(query, new String[0]);
        try {
            db.beginTransaction();

            while (cursor.moveToNext()){
                long routeId = Db.getLong(cursor, "route_id");
                long duplicateId = Db.getLong(cursor, "duplicate_id");
                Timber.i("Removing duplicated route " + routeId + " dup = " + duplicateId);

                updateTrips(routeId, duplicateId);
                removeRoute(duplicateId);
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            Timber.e(e, "Failed cleaning up routes");
        } finally {
            db.endTransaction();
            cursor.close();
        }
    }

    void updateTrips(long routeId, long duplicateId) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("route_id", routeId);
        db.update(Trip.TABLE, contentValues, "route_id=?", new String[]{String.valueOf(duplicateId)});
    }

    void removeRoute(long routeId){
        db.delete(Route.TABLE, "route_id=?", new String[]{String.valueOf(routeId)});
    }

    @Override
    public void call(Subscriber<? super Void> subscriber) {
        try {
            tidyTables();
            subscriber.onCompleted();
        } catch (Exception e){
            subscriber.onError(e);
        }
    }
}
