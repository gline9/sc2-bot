package com.gline9.sc2.units;

import io.reactivex.subjects.PublishSubject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UnitPoolPublisher
{
    private final Map<Class<?>, PublishSubject<?>> publisherMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <U> PublishSubject<U> getSubjectForType(Class<U> clazz)
    {
        return (PublishSubject<U>)publisherMap.computeIfAbsent(clazz, (__) -> PublishSubject.create());
    }

    public <U extends IUnit> void subscribeToUnitType(Class<U> clazz, Consumer<? super U> consumer)
    {
        getSubjectForType(clazz).subscribe(consumer::accept);
    }

    public <U extends IUnit> void emitForType(Class<? super U> clazz, U value)
    {
        getSubjectForType(clazz).onNext(value);
    }
}
