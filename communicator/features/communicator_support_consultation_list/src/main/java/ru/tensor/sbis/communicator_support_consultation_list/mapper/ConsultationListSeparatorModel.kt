package ru.tensor.sbis.communicator_support_consultation_list.mapper

import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import java.util.UUID

/**
 * Модель данных, представление ConsultationListSeparator
 * Необходима для упрощения доступа к полям исходной ConsultationListSeparator, а также для возможности расширения
 */
internal data class ConsultationListSeparatorModel(val id: UUID, val text: String) :
    ComparableItem<ConsultationListSeparatorModel> {
    override fun areTheSame(otherItem: ConsultationListSeparatorModel): Boolean = id == otherItem.id
}