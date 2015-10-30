package com.concur.swbservice.service.component;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by mtalbot on 17/09/2015.
 */
public class ResponseMapObservableOperator<T> implements Observable.Operator<ResponseEntity<T>, T> {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger("ResponseMapper");

    private final HttpStatus validStatus;

    /**
     * Operation to lift an observable and wrap the value in a spring mvc response entity
     *
     * @param validStatus The status for valid requests
     */
    public ResponseMapObservableOperator(HttpStatus validStatus) {
        this.validStatus = validStatus;
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super ResponseEntity<T>> subscriber) {
        return new Subscriber<T>() {
            private int count = 0;

            @Override
            public void onCompleted() {
                if (count == 0) {
                    subscriber.
                            onNext(new ResponseEntity<T>(HttpStatus.NOT_FOUND));
                }
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("Error occurred during request", throwable);
                subscriber.onError(throwable);
            }

            @Override
            public void onNext(T t) {
                count++;
                subscriber.
                        onNext(ResponseEntity.status(validStatus).body(t));
            }
        };
    }
}
