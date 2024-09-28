package ru.tensor.sbis.design.cloud_view.thread.data

import ru.tensor.sbis.person_decl.profile.model.Person
import java.util.Date
import java.util.UUID

/**
 * Данные треда.
 *
 * @author vv.chekurda
 */
data class ThreadData(
    val dialogUuid: UUID,
    val relevantMessageUuid: UUID?,
    val title: String?,
    val showDocumentIcon: Boolean,
    val date: Date,
    val recipients: List<Person>,
    val recipientCount: Int,
    val relevantMessageText: String = "",
    val isServiceText: Boolean = false,
    val messageCount: Int = 0,
    val unreadCount: Int = 0,
    val isOutgoing: Boolean = false,
    var isGroupConversation: Boolean = false
)