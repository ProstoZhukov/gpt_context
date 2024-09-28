package ru.tensor.sbis.person_decl.profile.event

import ru.tensor.sbis.person_decl.profile.model.ProfileContactType

/**
 * Класс-ивент об изменении контакта
 */
class ContactChangedEvent constructor(val contactType: ProfileContactType, val eventType: EventType) {

    /** Пееречисление типов событий */
    enum class EventType {
        ADD,
        CONFIRM
    }
}