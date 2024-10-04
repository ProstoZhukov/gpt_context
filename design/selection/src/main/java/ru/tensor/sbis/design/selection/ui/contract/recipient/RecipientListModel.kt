package ru.tensor.sbis.design.selection.ui.contract.recipient

import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel

/**
 * Модель для передачи информации о загруженных данных. Содержит список данных, и флаг можно ли грузить еще
 *
 * @author ma.kolpakov
 */
data class RecipientListModel<out DATA : RecipientSelectorItemModel>(
    /**
     * Список моделей для отображения в компоненте выбора
     */
    val items: List<DATA>,
    /**
     * Флаг можно ли грузить еще данные. Если он `false` то визуальных признаков о том что есть еще данные не будет, и
     * не будут запрашиваться новые данные
     */
    val hasMore: Boolean
)