package ru.tensor.sbis.communication_decl.communicator.event

import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewData

/**
 * Событие обновления тулбара переписки с одним участником.
 *
 * @author vv.chekurda
 */
data class SinglePersonTitleEvent(
    val viewData: List<SbisPersonViewData>?,
    val name: String?,
    val status: String?
) : ConversationToolbarEvent(EventType.SINGLE_PERSON)
