package ru.tensor.sbis.counter_provider;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import ru.tensor.sbis.platform.generated.Subscription;

/**
 * Интерфейс источника данных для получения счетчика
 *
 * @param <COUNTER> модель счетчика
 *
 * @author mb.kruglova
 */
public interface BaseCounterRepository<COUNTER, DATA_REFRESH_CALLBACK> {

    /**
     * Синхронный запрос счетчика, рекомендуется вызывать в рабочем потоке
     *
     * @return локальный счетчик
     */
    @WorkerThread
    @NonNull
    COUNTER getCounter();

    /**
     * Метод для формирования подписки на события от источника данных
     *
     * @param callback слушатель результата
     * @return подписка
     */
    @NonNull
    Subscription subscribe(@NonNull DATA_REFRESH_CALLBACK callback);
}
