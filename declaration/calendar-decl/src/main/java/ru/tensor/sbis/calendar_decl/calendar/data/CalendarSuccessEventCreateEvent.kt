package ru.tensor.sbis.calendar_decl.calendar.data

/**
 * Событие показать сообщение после создания события
 * @param shortMsg сообщение для режима день, когда нет необходимости показывать весь список календарей
 * @param msg сообщение для других режимов, показываем полный набор календарей
 */
data class CalendarShowSuccessEvent(val shortMsg: String, val msg: String)