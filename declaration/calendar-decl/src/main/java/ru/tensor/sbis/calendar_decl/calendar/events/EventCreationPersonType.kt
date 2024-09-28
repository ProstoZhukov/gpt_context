package ru.tensor.sbis.calendar_decl.calendar.events

/** Тип персоны, для которой создается событие */
enum class EventCreationPersonType {
    /** Создание события для себя */
    FOR_ME,

    /** Создание события для другой персоны */
    FOR_ANOTHER_USER,
}