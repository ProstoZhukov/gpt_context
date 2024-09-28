package ru.tensor.sbis.calendar_decl.calendar.data

import java.util.Date

/**
 * Событие скролла после создания события
 * @param eventDate дата и время, к которым нужно подскроллить
 */
data class ScrollAfterEventCreationEvent(val eventDate: Date)