package ru.tensor.sbis.communicator.communicator_crm_chat_list.mapper

import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVm
import java.util.UUID

/**
 * Модель данных, представление ConsultationViewModel.
 * Необходима для упрощения доступа к полям исходной ConsultationViewModel, а также для возможности расширения.
 *
 * @author da.zhukov
 */
internal data class CRMChatViewModelBindingModel(
    val id: UUID,
    val sourceName: String?,
    val sourceIcon: String?,
    val isSabyGetIcon: Boolean = false,
    val name: CharSequence,
    val text: CharSequence?,
    val authorName: CharSequence?,
    val authorPersonData: PersonData?,
    val subtitle: CharSequence?,
    val messagePersonData: PersonData?,
    val operatorPersonData: PersonData?,
    val isClosed: Boolean,
    val closedIcon: String,
    val closedIconSize: Int,
    val closedIconColor: Int,
    val date: String?,
    val unreadCounter: Int?,
    val needShowOperator: Boolean,
    val isHistoryMode: Boolean,
    val isExpired: Boolean
) : ComparableItem<CRMChatViewModelBindingModel> {

    /** @SelfDocumented */
    lateinit var swipeableVm: SwipeableVm

    override fun areTheSame(otherItem: CRMChatViewModelBindingModel): Boolean = id == otherItem.id
}
