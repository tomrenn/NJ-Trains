package com.tomrenn.njtrains.ui;

import com.tomrenn.njtrains.data.db.Stop;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 *
 */
public class StopLookup {
    private Stop fromStation;
    private Stop toStation;

    LookupChangeListener changeListener;
    Observable<StopLookup> changes;

    interface LookupChangeListener{
        void changed(StopLookup stopLookup);
    }

    public StopLookup(){
        changes = Observable.create(new Observable.OnSubscribe<StopLookup>() {
            @Override
            public void call(final Subscriber<? super StopLookup> subscriber) {
                changeListener = new LookupChangeListener() {
                    @Override
                    public void changed(StopLookup stopLookup) {
                        subscriber.onNext(stopLookup);
                    }
                };

                Subscription subscription = Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        changeListener = null;
                    }
                });
                subscriber.add(subscription);
            }
        }).share();
    }

    public Observable<StopLookup> onChanges(){
        return changes.startWith(this);
    }

    void notifyChange(){
        if (changeListener != null){
            changeListener.changed(this);
        }
    }

    public void from(Stop stop){
        this.fromStation = stop;
        notifyChange();
    }

    public Stop fromStation(){
        return fromStation;
    }

    public void to(Stop stop){
        this.toStation = stop;
        notifyChange();
    }

    public Stop toStation(){
        return toStation;
    }
}
