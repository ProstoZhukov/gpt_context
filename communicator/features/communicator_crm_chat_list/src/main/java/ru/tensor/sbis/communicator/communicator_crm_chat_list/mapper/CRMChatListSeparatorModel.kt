package ru.tensor.sbis.communicator.communicator_crm_chat_list.mapper

import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import java.util.UUID

/**
 * Модель данных, представление ConsultationListSeparator
 * Необходима для упрощения доступа к полям исходной ConsultationListSeparator, а также для возможности расширения
 *
 * @author da.zhukov
 */
internal data class CRMChatListSeparatorModel(val id: UUID, val text: String) :
    ComparableItem<CRMChatListSeparatorModel> {
    override fun areTheSame(otherItem: CRMChatListSeparatorModel): Boolean = id == otherItem.id
}