package ru.tensor.sbis.communication_decl.communicator.event

/**
 * Базовое событие для обновления тулбара переписки.
 *
 * @author vv.chekurda
 */

abstract class ConversationToolbarEvent(val type: EventType) {

    enum class EventType {
        SINGLE_PERSON,
        MULTI_PERSONS,
    }

}