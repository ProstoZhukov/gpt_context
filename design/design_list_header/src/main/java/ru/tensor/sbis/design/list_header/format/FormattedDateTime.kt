package ru.tensor.sbis.design.list_header.format

/**
 *
 * Модель даты и времени для отображения. Предоставляет разные варианты отображения: в одной view
 * и в независимых для даты и времени, могут располагаться в разных местах ячейки
 * (дата над облачком в переписке, время в облачке).
 *
 * @author ra.petrov
 */
data class FormattedDateTime(val date: String, val time: String)