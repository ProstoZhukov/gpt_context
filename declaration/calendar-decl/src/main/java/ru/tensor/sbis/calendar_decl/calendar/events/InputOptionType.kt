package ru.tensor.sbis.calendar_decl.calendar.events

/** Типы опции для создания события */
enum class InputOptionType {
    /** Тип отгула */
    TIME_OFF_TYPE,

    /** Тип отпуска */
    VACATION_TYPE,

    /** ИД организации (для создания работы на выезде) */
    COMPANY_ORIGINAL_ID,

    /** UUID регламента ддокумента */
    REGULATION_UUID,

    /** Выбор стратегии разрешения конфликта в события */
    INTERSECTION_RESOLVE_TYPE,
}