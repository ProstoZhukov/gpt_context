package ru.tensor.sbis.communication_decl.communicator.event

import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewData

/**
 * Событие обновления тулбара переписки с несколькими участниками.
 *
 * @author vv.chekurda
 */
data class MultiPersonsTitleEvent(
    val viewData: List<SbisPersonViewData>?,
    val names: List<String>?,
    val hiddenCount: Int
) : ConversationToolbarEvent(EventType.MULTI_PERSONS) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MultiPersonsTitleEvent

        if (names != other.names) return false
        if (hiddenCount != other.hiddenCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = names?.hashCode() ?: 0
        result = 31 * result + hiddenCount
        return result
    }
}