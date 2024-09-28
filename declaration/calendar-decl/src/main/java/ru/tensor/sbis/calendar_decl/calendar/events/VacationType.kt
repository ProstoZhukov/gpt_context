package ru.tensor.sbis.calendar_decl.calendar.events

/** Тип фактического отпуска */
enum class VacationType
{
    /** по КД оплачиваемый */
    COLL_AGREEMENT_PAID,
    /** по КД не оплачиваемый */
    COLL_AGREEMENT_NOT_PAID,
    /** Ежегодный */
    ANNUAL,
    /** За свой счет */
    WITHOUT_PAY,
    /** Учебный */
    EDU,
    /** Гос. обязанности */
    STATE_DUTIES,
    /** Дни донора */
    DONOR,
    /** Доп. отпуск пострадавшим от ЧАЭС */
    CHNPP,
    /** Компенсация */
    COMPENSATION,
    /** Уход за ребенком-инвалидом */
    CARE_INV,
    /** Диспансеризация */
    DISPENSARY,
    /** Отзыв из отпуска */
    RECALL,
    /** Дополнительный */
    ADDITIONAL,
    /** Курортное лечение */
    RESORT_TREATMENT,
    /** Мобилизация */
    MOBILIZATION,
    /** Неизвестный тип */
    UNKNOWN,
}
