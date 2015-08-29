package com.tomrenn.njtrains.data.api;

import com.tomrenn.njtrains.data.db.Stop;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 *
 */
public class TripRequest {
    private Stop fromStation;
    private Stop toStation;
    // additional fields like date / time

    ChangeListener changeListener;
    Observable<TripRequest> changes;

    interface ChangeListener{
        void changed();
    }

    public TripRequest(Stop fromStation, Stop toStation) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        // we could probably just keep the subscriber object, but that may be bad practice.
        changes = rx.Observable.create(new Observable.OnSubscribe<TripRequest>() {
            @Override
            public void call(final Subscriber<? super TripRequest> subscriber) {
                changeListener = new ChangeListener() {
                    @Override
                    public void changed() {
                        subscriber.onNext(TripRequest.this);
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

    public Observable<TripRequest> onChanges(){
        return changes;
    }

    void notifyChange(){
        if (changeListener != null){
            changeListener.changed();
        }
    }

    public Stop getToStation() {
        return toStation;
    }

    public void setToStation(Stop toStation) {
        this.toStation = toStation;
        notifyChange();
    }

    public Stop getFromStation() {
        return fromStation;
    }

    public void setFromStation(Stop fromStation) {
        this.fromStation = fromStation;
        notifyChange();
    }
}
