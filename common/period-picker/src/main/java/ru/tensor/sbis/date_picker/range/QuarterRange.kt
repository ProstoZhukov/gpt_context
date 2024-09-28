package ru.tensor.sbis.date_picker.range

import ru.tensor.sbis.date_picker.Period

/**
 * Список кварталов
 *
 * @author mb.kruglova
 */
fun quarterRange(year: Int) = (0..3).map { quarter -> Period.fromQuarter(year, quarter) }