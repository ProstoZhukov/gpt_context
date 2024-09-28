package ru.tensor.sbis.shift_hours.data

/** Тип блока - для раскраски */
enum class TimeBlockTypeUI{
    /** Рабочее время */
    WORK_TIME,
    /** Отгул */
    TIME_OFF,
    /** Прогул */
    TRUANCY,
    /** Простой */
    DOWNTIME,
    /** Обед */
    LUNCH,
    /** Рабочее время по приглашению */
    INVITATION_WORK_TIME,
}