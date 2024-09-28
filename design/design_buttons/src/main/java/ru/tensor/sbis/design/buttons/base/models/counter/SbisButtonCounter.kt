package ru.tensor.sbis.design.buttons.base.models.counter

import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterStyle
import ru.tensor.sbis.design.counters.sbiscounter.PrimarySbisCounterStyle

/**
 * Модель счётчика в кнопке.
 *
 * @author ma.kolpakov
 */
data class SbisButtonCounter(
    val counter: Int,
    val style: SbisCounterStyle = PrimarySbisCounterStyle
)
