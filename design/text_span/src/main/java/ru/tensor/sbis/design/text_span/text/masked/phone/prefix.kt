/**
 * Набор инструметов для определения специфичных префиксов
 *
 * @author ma.kolpakov
 * @since 12/6/2019
 */
package ru.tensor.sbis.design.text_span.text.masked.phone

internal val CharSequence.plusSeven: Boolean get() = startsWith("+7")
internal val CharSequence.firstEight: Boolean get() = startsWith('8')
internal val CharSequence.firstSeven: Boolean get() = startsWith('7')