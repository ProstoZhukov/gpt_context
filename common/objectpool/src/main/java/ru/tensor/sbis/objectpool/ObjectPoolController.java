package ru.tensor.sbis.objectpool;

import ru.tensor.sbis.objectpool.exception.NegativeReduceSizeException;
import ru.tensor.sbis.objectpool.exception.NegativeRequestedPoolSizeException;

/**
 * Базовый интерфейс пула объектов.
 *
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public interface ObjectPoolController {

    /**
     * Уменьшить количество объектов в пуле до указанного значения.
     * @param requestSize - требуемое количество объектов в пуле (должно быть положительным)
     */
    void reduce(int requestSize) throws NegativeRequestedPoolSizeException;

    /**
     * Уменьшить количество объектов в пуле на указанное значение.
     * @param reduceSize - количество лишних объектов (должно быть положительным)
     */
    void reduceBy(int reduceSize) throws NegativeReduceSizeException;

    /**
     * Удалить все объекты из пула.
     */
    void flush();

}
