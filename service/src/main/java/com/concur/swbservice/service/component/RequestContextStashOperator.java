package com.concur.swbservice.service.component;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import rx.Observable;
import rx.Subscriber;

/**
 * Spring hateoas requires the request context to be set but observables may happen on other threads
 */
public class RequestContextStashOperator<T> implements Observable.Operator<T, T> {

    private final RequestAttributes attributes;

    /**
     * Spring hateoas requires the request context to be set but observables may happen on other threads
     * This operator will reapply the context of the constructing thread on the execution thread of the subscriber
     */
    public RequestContextStashOperator() {
        attributes = RequestContextHolder.currentRequestAttributes();
    }
    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(T t) {
                RequestContextHolder.setRequestAttributes(attributes);
                subscriber.onNext(t);
            }
        };
    }
}
