package ru.tensor.sbis.design.selection.ui.contract.list

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import java.io.Serializable

/**
 * Функция для проверки необходимости дозагрузки элементов при выборе
 *
 * @author ma.kolpakov
 */
interface PrefetchCheckFunction<DATA : SelectorItemModel> : Serializable {

    /**
     * Проверяет необходимость дозагрузки на основе доступных для выбора элементов [availableItems] и уже выбранных
     * элементов [selectedItems]. Если данные загружаются c `excludeList` (без выбранных), при уменьшении размера
     * [selectedItems] (удалении из списка выбранных) нужно перезагрузить список, чтобы восстановить элементы в прежних
     * позициях
     *
     * @return `null`, если действия не требуется
     */
    fun needToPrefetch(selectedItems: List<DATA>, availableItems: List<DATA>): PrefetchMode?
}