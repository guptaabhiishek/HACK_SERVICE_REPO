package com.concur.swbservice.service.component;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import rx.Observable;

/**
 * Created by mtalbot on 12/08/2015.
 */
@Component
public class RxObservableHandlerMethodReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Observable.
                class.
                isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            return;
        }

        DeferredResult result = new DeferredResult();

        Observable<?> observable = Observable.class.cast(returnValue);

        observable.subscribe(result::setResult, result::setErrorResult);

        WebAsyncUtils.getAsyncManager(webRequest)
                .startDeferredResultProcessing(result, mavContainer);
    }

    @Override
    public boolean isAsyncReturnValue(Object val, MethodParameter returnType) {
        return val instanceof Observable;
    }

}
