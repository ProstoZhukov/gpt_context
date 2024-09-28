package ru.tensor.sbis.objectpool.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.objectpool.BuildConfig;
import ru.tensor.sbis.objectpool.InflatableObjectPoolController;
import ru.tensor.sbis.objectpool.exception.NegativeAdditionSizeException;
import ru.tensor.sbis.objectpool.exception.NegativeRequestedPoolSizeException;

/**
 * Реализация пула объектов с синхронизированным доступом и возможностью досоздавать объекты.
 * @param <T> - тип объектов в пуле
 *
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class InflatableConcurrentObjectPool<T> extends ConcurrentObjectPool<T> implements InflatableObjectPoolController {

    public InflatableConcurrentObjectPool() { }

    public InflatableConcurrentObjectPool(int capacity) {
        super(capacity);
    }

    // region InflatableObjectPoolController interface impl

    @NonNull
    @Override
    public T take() {
        T instance = super.take();
        if (instance == null) {
            instance = createInstance();
            log("Pool is empty. Create new instance and return it");
        }
        return instance;
    }

    @Override
    public void inflate(int requestSize) throws NegativeRequestedPoolSizeException {
        requestSize = validateReduceSize(requestSize);
        // Ограничиваем размер вместимостью пула
        requestSize = Math.min(getCapacity(), requestSize);
        if (mContainer.size() < requestSize) {
            int delta = requestSize - mContainer.size();
            // Добавляем объекты в пул
            for (int i = 0; i < delta; i++) {
                mContainer.offer(createInstance());
            }
            log("Pool size increased to %d", requestSize);
        }
    }

    @Override
    public void inflateBy(int additionSize) throws NegativeAdditionSizeException {
        additionSize = validateAdditionSize(additionSize);
        inflate(getPoolSize() + additionSize);
    }

    // endregion

    /**
     * Создать новый экземпляр объекта.
     * @return экземпляр объекта
     */
    @NonNull
    protected abstract T createInstance();

    // region Validations

    protected static int validateAdditionSize(int reduceSize) {
        if (reduceSize < 0) {
            // Некорректный параметр уменьшения пула
            if (BuildConfig.DEBUG) {
                // Выбрасываем исключение, если отладочная сборка
                throw new NegativeAdditionSizeException(reduceSize);
            }
            // Исправляем параметр
            return 0;
        }
        return reduceSize;
    }

    // endregion

}
