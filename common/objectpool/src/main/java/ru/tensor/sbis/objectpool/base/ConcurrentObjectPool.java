package ru.tensor.sbis.objectpool.base;

import android.util.Log;

import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.objectpool.BuildConfig;
import ru.tensor.sbis.objectpool.ObjectPool;
import ru.tensor.sbis.objectpool.ObjectPoolController;
import ru.tensor.sbis.objectpool.exception.NegativePoolCapacityException;
import ru.tensor.sbis.objectpool.exception.NegativeReduceSizeException;
import ru.tensor.sbis.objectpool.exception.NegativeRequestedPoolSizeException;
import ru.tensor.sbis.objectpool.exception.ObjectAlreadyExistsInPoolException;

/**
 * Базовая реализация пула объектов с синхронизированным доступом.
 * @param <T> - тип объектов в пуле
 *
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ConcurrentObjectPool<T> implements ObjectPool<T>, ObjectPoolController {

    /**
     * Размер пула по умолчанию.
     */
    private static final int DEFAULT_POOL_CAPACITY = 10;

    /**
     * Включение/выключение логирования операций с пулом.
     */
    private static final boolean ENABLE_LOGGING_BY_DEFAULT = false;

    /**
     * Контейнер для объектов в пуле.
     */
    @NonNull
    protected final BlockingQueue<T> mContainer;

    /**
     * Максимальное количество объектов в пуле.
     */
    private final int mCapacity;

    public ConcurrentObjectPool() {
        this(DEFAULT_POOL_CAPACITY);
    }

    public ConcurrentObjectPool(int capacity) {
        if (capacity < 0) {
            if (BuildConfig.DEBUG) {
                // Выбрасываем исключение, если отладочная сборка
                throw new NegativePoolCapacityException(capacity);
            } else {
                // Исправляем вместимость, если релизная сборка
                capacity = 0;
            }
        }
        mCapacity = capacity;
        mContainer = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * Максимальное количество объектов в пуле.
     * @return максимальное количество объектов в пуле
     */
    public int getCapacity() {
        return mCapacity;
    }

    // region ObjectPool interface impl

    @Nullable
    @Override
    public T take() {
        if (mContainer.size() > 0) {
            log("Object polled");
            return mContainer.poll();
        } else {
            log("Pool is empty.");
        }
        return null;
    }

    @Override
    public boolean put(@NonNull T instance) throws ObjectAlreadyExistsInPoolException {
        if (BuildConfig.DEBUG) {
            if (mContainer.contains(instance)) {
                // Данный экземпляр уже есть в пуле
                throw new ObjectAlreadyExistsInPoolException(instance);
            }
        }
        if (mContainer.size() < mCapacity) {
            if (validate(instance)) {
                mContainer.offer(instance);
                log("Object added");
            } else {
                log("Object skipped due to invalid state and will be collected");
            }
        } else {
            log("Maximal capacity is already achieved. Object skipped and will be collected");
        }
        return false;
    }

    @Override
    public int getPoolSize() {
        return mContainer.size();
    }

    // endregion

    // region ObjectPoolController interface impl

    @Override
    public void reduce(int requestSize) {
        requestSize = validateRequestedSize(requestSize);
        if (mContainer.size() > requestSize) {
            // Вычисляем количество лишних объектов
            int delta = mContainer.size() - requestSize;
            // Удаляем лишние объекты из пула
            for (int i = 0; i < delta; i++) {
                mContainer.poll();
            }
            log("Pool size reduced to %d", requestSize);
        }
    }

    @Override
    public void reduceBy(int reduceSize) {
        reduceSize = validateReduceSize(reduceSize);
        if (reduceSize < mContainer.size()) {
            // Уменьшаем количество объектов в пуле
            reduce(mContainer.size() - reduceSize);
        } else {
            // Очищаем пул
            flush();
        }
    }

    @Override
    public void flush() {
        log("Pool flushed");
        mContainer.clear();
    }

    // endregion

    /**
     * Проверить, подходит ли объект для пула. Исправить, если возможно.
     * @param instance - объект для проверки
     * @return true - если объект подходит, false - иначе
     */
    @SuppressWarnings("SameReturnValue")
    protected boolean validate(@NonNull T instance) {
        return true;
    }

    // region Validations

    protected static int validateRequestedSize(int requestSize) {
        if (requestSize < 0) {
            // Некорректный размер пула
            if (BuildConfig.DEBUG) {
                // Выбрасываем исключение, если отладочная сборка
                throw new NegativeRequestedPoolSizeException(requestSize);
            }
            // Исправляем размер, если релизная сборка
            return 0;
        }
        return requestSize;
    }

    protected static int validateReduceSize(int reduceSize) {
        if (reduceSize < 0) {
            // Некорректный параметр уменьшения пула
            if (BuildConfig.DEBUG) {
                // Выбрасываем исключение, если отладочная сборка
                throw new NegativeReduceSizeException(reduceSize);
            }
            // Исправляем параметр
            return 0;
        }
        return reduceSize;
    }

    // endregion

    // region Logging

    private boolean mLoggingEnabled = ENABLE_LOGGING_BY_DEFAULT;

    @Nullable
    private String mLoggingTag;

    /** @SelfDocumented  */
    public boolean isLoggingEnabled() {
        return mLoggingEnabled;
    }

    /** @SelfDocumented  */
    public void setLoggingEnabled(boolean enable) {
        mLoggingEnabled = enable;
    }

    /** @SelfDocumented  */
    @NonNull
    protected String getLoggingTag() {
        return getClass().getSimpleName();
    }

    @NonNull
    private String getLoggingTagInternal() {
        if (mLoggingTag == null) {
            mLoggingTag = getLoggingTag();
            if (mLoggingTag.length() > 23) {
                mLoggingTag = mLoggingTag.substring(0, 23);
            }
        }
        return mLoggingTag;
    }

    protected void log(String format, Object... args) {
        if (mLoggingEnabled) {
            log(String.format(Locale.getDefault(), format, args));
        }
    }

    protected void log(String message) {
        if (mLoggingEnabled) {
            Log.i(getLoggingTagInternal(), message);
            //Timber.i("%s : %s", getLoggingTagInternal(), message);
        }
    }

    // endregion

}
