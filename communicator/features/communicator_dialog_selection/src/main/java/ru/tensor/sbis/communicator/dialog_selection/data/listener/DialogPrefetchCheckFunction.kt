package ru.tensor.sbis.communicator.dialog_selection.data.listener

import ru.tensor.sbis.communicator.dialog_selection.presentation.RECIPIENT_LIST_SIZE
import ru.tensor.sbis.communicator.dialog_selection.presentation.RECIPIENT_TITLE_ITEMS_COUNT
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel

/**
 * Реализация функции, которая используется для распознавания необходимости дозагрузки элементов через [PrefetchMode]
 *
 * @author vv.chekurda
 */
internal class DialogPrefetchCheckFunction : PrefetchCheckFunction<SelectorItemModel> {

    @Transient
    private var lastSelectionSize = 0

    override fun needToPrefetch(
        selectedItems: List<SelectorItemModel>,
        availableItems: List<SelectorItemModel>
    ): PrefetchMode? {
        //в списке доступных элементов, помимо получателей, содержатся еще заголовки и диалоги
        val availableRecipientsCount = availableItems.take(RECIPIENT_LIST_SIZE + RECIPIENT_TITLE_ITEMS_COUNT)
            .filterIsInstance<RecipientSelectorItemModel>()
            .size
        return when {
            availableRecipientsCount < RECIPIENT_LIST_SIZE -> PrefetchMode.RELOAD
            /*
            Список получателей загружается с excludeList, поэтому перемещение из списка выбранных в общий список требует
            перезагрузки для удаления элемента из excludeList и восстановления его оригинальной позиции
             */
            selectedItems.size < lastSelectionSize         -> PrefetchMode.RELOAD
            else                                           -> null
        }.also {
            lastSelectionSize = selectedItems.size
        }
    }
}