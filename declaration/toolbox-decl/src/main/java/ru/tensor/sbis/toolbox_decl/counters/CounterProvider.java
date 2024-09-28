package ru.tensor.sbis.toolbox_decl.counters;

import androidx.annotation.NonNull;

import io.reactivex.Observable;

/**
 * Интерфейс поставщика данных для счетчиков
 *
 * @param <MODEL> модель счетчика
 */
public interface CounterProvider<MODEL> {

    /**
     * Синхронный запрос счетчика
     *
     * @return счетчик
     */
    @NonNull
    MODEL getCounter();

    /**
     * Подписка на события обновления счетчика
     *
     * @return счетчик
     */
    @NonNull
    Observable<MODEL> getCounterEventObservable();
}
