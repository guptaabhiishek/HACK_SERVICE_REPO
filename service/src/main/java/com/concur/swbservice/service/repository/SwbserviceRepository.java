package com.concur.swbservice.service.repository;

import com.concur.swbservice.api.model.v1_0.SwbserviceModel;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.ObservableResult;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import rx.Observable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Created by mtalbot on 17/08/2015.
 */
@Repository
public class SwbserviceRepository implements CrudRepository<UUID, SwbserviceModel> {

    private final Map<UUID, SwbserviceModel> data = LongStream.
            range(1, 20).
            mapToObj(c -> new SwbserviceModel(UUID.randomUUID(), "Test" + Long.toString(c), DateTime.now(), c)).
            collect(Collectors.toMap(SwbserviceModel::getId, model -> model));

    private <result> HystrixObservableCommand<result> getCommand(String commandType, Supplier<Observable<result>> generate) {
        return new HystrixObservableCommand<result>(
                HystrixObservableCommand.
                        Setter.
                        withGroupKey(
                                HystrixCommandGroupKey.
                                        Factory.
                                        asKey(this.getClass().getSimpleName())
                        ).andCommandKey(
                        HystrixCommandKey.
                                Factory.
                                asKey(commandType)
                ).andCommandPropertiesDefaults(HystrixCommandProperties.
                                Setter().
                                withCircuitBreakerEnabled(true).
                                withCircuitBreakerErrorThresholdPercentage(20).
                                withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE).
                                withExecutionIsolationSemaphoreMaxConcurrentRequests(1000000)
                )
        ) {
            @Override
            protected Observable<result> construct() {
                return generate.get();
            }
        };
    }

    @Override
    public Observable<SwbserviceModel> findAll() {
        return getCommand("findAll", () -> Observable.from(data.values())).observe();
    }

    @Override
    public Observable<SwbserviceModel> findById(UUID id) {
        return getCommand("findById", () -> data.containsKey(id) ?
                        Observable.just(data.get(id)) : Observable.empty()
        ).observe();
    }

    @Override
    public Observable<SwbserviceModel> save(UUID id, SwbserviceModel domain) {
        return getCommand("save", () -> {
                    data.put(id, domain);

                    return Observable.just(data.get(id));
                }
        ).observe();
    }

    @Override
    public Observable<Boolean> delete(UUID id) {
        return getCommand("delete", () -> {
                    if (data.containsKey(id)) {
                        data.remove(id);

                        return Observable.just(true);
                    } else {
                        return Observable.just(false);
                    }
                }
        ).observe();
    }
}
