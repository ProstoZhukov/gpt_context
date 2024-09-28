package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.data.ReadStatusModel
import ru.tensor.sbis.list.view.item.comparator.ComparableItem

/**
 * Вью-модель элемента списка статусов прочитанности сообщения.
 * @see [ReadStatusModel]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusVM : ComparableItem<ReadStatusVM> {

    /**
     * Модель статуса прочитанности
     */
    val model: ReadStatusModel
}