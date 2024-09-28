package ru.tensor.sbis.date_picker.range

import ru.tensor.sbis.date_picker.Period

/**
 * Список полугодий
 *
 * @author mb.kruglova
 */
fun halfYearRange(year: Int) = (0..1).map { halfYear -> Period.fromHalfYear(year, halfYear) }
