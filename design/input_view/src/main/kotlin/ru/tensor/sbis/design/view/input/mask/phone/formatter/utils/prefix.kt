/**
 * Набор инструментов для определения специфичных префиксов.
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.view.input.mask.phone.formatter.utils

internal val CharSequence.plusSeven: Boolean get() = startsWith("+7")
internal val CharSequence.firstEight: Boolean get() = startsWith('8')
internal val CharSequence.firstSeven: Boolean get() = startsWith('7')
internal val CharSequence.firstPlus: Boolean get() = startsWith('+')
