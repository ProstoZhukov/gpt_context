package ru.tensor.sbis.objectpool;

import ru.tensor.sbis.objectpool.exception.NegativeAdditionSizeException;
import ru.tensor.sbis.objectpool.exception.NegativeRequestedPoolSizeException;

/**
 * Интерфейс контроллера пула с возможностью увеличение количества экземпляров.
 *
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public interface InflatableObjectPoolController extends ObjectPoolController {

    /**
     * Увеличить количество объектов в пуле до указанного значения.
     * @param requestSize - требуемое количество объектов в пуле (должно быть положительным)
     */
    void inflate(int requestSize) throws NegativeRequestedPoolSizeException;

    /**
     * Увеличить количество объектов в пуле на указанное значение.
     * @param additionSize - количество дополнительных объектов (должно быть положительным)
     */
    void inflateBy(int additionSize) throws NegativeAdditionSizeException;

}
