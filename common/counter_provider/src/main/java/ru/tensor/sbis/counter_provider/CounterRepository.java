package ru.tensor.sbis.counter_provider;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import ru.tensor.sbis.crud.generated.DataRefreshCallback;
import ru.tensor.sbis.platform.generated.Subscription;

/**
 * Интерфейс источника данных для получения счетчика
 *
 * @param <COUNTER> модель счетчика
 *
 * @author mb.kruglova
 */
public interface CounterRepository<COUNTER> extends BaseCounterRepository<COUNTER, DataRefreshCallback> {

}
