package ru.tensor.sbis.communicator_support_consultation_list.mapper

import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import java.util.UUID

/**
 * Модель данных, представление ConsultationViewModel
 * Необходима для упрощения доступа к полям исходной ConsultationViewModel, а также для возможности расширения
 */
internal data class ConsultationViewModelBindingModel(
    val id: UUID,
    val name: String,
    val text: String?,
    val personData: PersonData?,
    val closedIcon: String,
    val closedIconSize: Int,
    val closedIconColor: Int,
    val isClosed: Boolean,
    val date: String?,
    val unreadCounter: Int?
) : ComparableItem<ConsultationViewModelBindingModel> {

    override fun areTheSame(otherItem: ConsultationViewModelBindingModel): Boolean = id == otherItem.id
}