package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.data

import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import java.util.*

/**
 * Реализация модели статуса прочитанности сообщения
 * @see [ReadStatusModel]
 *
 * @author vv.chekurda
 */
internal data class ReadStatusModelImpl(
    override val personUuid: UUID,
    override val fullName: String,
    override val photoUrl: String?,
    override val departmentOrCompany: String,
    override val readDateTime: Date?,
    override val isRead: Boolean,
    override val initialsStubData: InitialsStubData?
) : ReadStatusModel