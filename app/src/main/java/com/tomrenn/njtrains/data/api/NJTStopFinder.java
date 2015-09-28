package com.tomrenn.njtrains.data.api;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomrenn.njtrains.data.db.Db;
import com.tomrenn.njtrains.data.db.Stop;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 */
@Singleton
public class NJTStopFinder implements StopFinder {
    private static final String LIST_QUERY = "SELECT * FROM "
            + Stop.TABLE
            + " WHERE " + Stop.NAME + " LIKE ?"
            + " ORDER BY "
            + Stop.NAME
            + " ASC";

    private static final String LOOKUP_QUERY = "SELECT *"
            + " FROM " + Stop.TABLE
            + " WHERE " + Stop.ID + "=?";

    SQLiteDatabase db;

    @Inject
    public NJTStopFinder(SQLiteDatabase db) {
        this.db = db;
    }


    public Observable<Cursor> execQuery(final String sql, final String... bindArgs){
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                subscriber.onNext(db.rawQuery(sql, bindArgs));
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Stop> findStop(long stopId) {
        return execQuery(LOOKUP_QUERY, Long.toString(stopId))
                .map(Stop.cursorToValue)
                .filter(new Func1<Stop, Boolean>() {
                    @Override
                    public Boolean call(Stop stop) {
                        return stop != null;
                    }
                })
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<List<Stop>> searchStops(String name) {
        name = "%" + name + "%";
        return execQuery(LIST_QUERY, name)
                .map(Stop.cursorToValues)
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<List<Stop>> allStops() {
        return null;
    }
}
