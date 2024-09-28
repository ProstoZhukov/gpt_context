package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.data.ReadStatusModel

/**
 * Реализация вью-модели элемента списка статусов прочитанности сообщения.
 * @see [ReadStatusVM]
 *
 * @property model модель статуса прочитанности.
 *
 * @author vv.chekurda
 */
internal data class ReadStatusVMImpl(
    override val model: ReadStatusModel
) : ReadStatusVM {

    override fun areTheSame(otherItem: ReadStatusVM): Boolean =
        model.personUuid == otherItem.model.personUuid

    override fun hasTheSameContent(otherItem: ReadStatusVM): Boolean =
        model == otherItem.model
}