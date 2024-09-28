package ru.tensor.sbis.recipient_selection.profile.data.listeners

import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.recipient_selection.profile.ui.RECIPIENT_SELECTION_LIST_SIZE

internal const val MINIMAL_RECIPIENT_COUNT = RECIPIENT_SELECTION_LIST_SIZE * 0.75F

/**
 * Реализация функции, которая используется для распознавания необходимости дозагрузки элементов через [PrefetchMode]
 *
 * @author vv.chekurda
 */
internal class RecipientsPrefetchCheckFunction : PrefetchCheckFunction<RecipientSelectorItemModel> {

    @Transient
    private var lastSelectionSize = 0

    override fun needToPrefetch(
        selectedItems: List<RecipientSelectorItemModel>,
        availableItems: List<RecipientSelectorItemModel>
    ): PrefetchMode? {
        val lastSize = lastSelectionSize
        lastSelectionSize = selectedItems.size
        return when {
            getUnselectedCount(availableItems, selectedItems) < MINIMAL_RECIPIENT_COUNT -> PrefetchMode.RELOAD
            /*
            Список получателей загружается с excludeList, поэтому перемещение из списка выбранных в общий список требует
            перезагрузки для удаления элемента из excludeList и восстановления его оригинальной позиции
             */
            selectedItems.size < lastSize                       -> PrefetchMode.RELOAD
            else                                                -> null
        }
    }

    private fun getUnselectedCount(
        availableItems: List<RecipientSelectorItemModel>,
        selectedItems: List<RecipientSelectorItemModel>
    ) = availableItems.map { it.id }.minus(selectedItems.map { it.id }).size
}