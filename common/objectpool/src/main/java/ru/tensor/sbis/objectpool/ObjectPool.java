package ru.tensor.sbis.objectpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.objectpool.exception.ObjectAlreadyExistsInPoolException;

/**
 * Базовый интерфейс пула объектов.
 * @param <T> - тип объектов в пуле
 *
 * @author am.boldinov
 */
public interface ObjectPool<T> {

    /**
     * Получить экземпляр объекта из пула.
     * @return доступный экземпляр объекта, либо null, если нет доступного
     * объекта и пул не может быть расширен
     */
    @Nullable T take();

    /**
     * Добавить экземпляр объекта в пул.
     * @param instance - экземпляр объекта
     * @return true если объект был успешно добавлен в пул, false - иначе
     */
    @SuppressWarnings("SameReturnValue")
    boolean put(@NonNull T instance) throws ObjectAlreadyExistsInPoolException;

    /**
     * Получить текущее количество объектов в пуле.
     * @return текущее количество элементов в пуле
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    int getPoolSize();

}
