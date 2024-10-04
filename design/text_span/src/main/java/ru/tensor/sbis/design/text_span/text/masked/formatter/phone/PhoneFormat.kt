/**
 * @author us.bessonov
 */
package ru.tensor.sbis.design.text_span.text.masked.formatter.phone

import ru.tensor.sbis.design.text_span.text.masked.formatter.FormatterPredicate
import ru.tensor.sbis.design.text_span.text.masked.phone.firstEight
import ru.tensor.sbis.design.text_span.text.masked.phone.firstSeven
import ru.tensor.sbis.design.text_span.text.masked.phone.plusSeven

internal const val MAX_LENGTH_RUSSIAN_PHONE = 11

/**
 * Маска, которая покрывает пользовательский ввод (без форматирования) телефонного номера
 */
internal const val COMMON_PHONE_MASK = "*##############################"

internal val START_WITH_PLUS_SEVEN_AND_LESS_THAN_4: FormatterPredicate = { s -> s.plusSeven && s.length < 5 }
internal val START_WITH_PLUS_SEVEN_AND_LESS_THAN_5: FormatterPredicate = { s -> s.plusSeven && s.length < 6 }
internal val START_WITH_PLUS_SEVEN_AND_LESS_THAN_12: FormatterPredicate = { s -> s.plusSeven && s.length < 13 }
internal val START_WITH_PLUS_SEVEN_AND_11_DIGITS: FormatterPredicate = { s -> s.plusSeven && s.length == 11 }
internal val START_WITH_PLUS_SEVEN: FormatterPredicate = { s -> s.plusSeven }

internal val START_WITH_PLUS: FormatterPredicate = { s -> s.startsWith('+') }

internal val START_WITH_EIGHT_AND_LESS_THAN_4: FormatterPredicate = { s -> s.firstEight && s.length < 4 }
internal val START_WITH_EIGHT_AND_LESS_THAN_5: FormatterPredicate = { s -> s.firstEight && s.length < 5 }
internal val START_WITH_EIGHT_AND_LESS_THAN_12: FormatterPredicate = { s -> s.firstEight && s.length < 12 }
internal val START_WITH_EIGHT_AND_11_DIGITS: FormatterPredicate = { s -> s.firstEight && s.length == 11 }
internal val START_WITH_EIGHT: FormatterPredicate = { s -> s.firstEight }

internal val START_WITH_SEVEN_AND_LESS_THAN_4: FormatterPredicate = { s -> s.firstSeven && s.length < 4 }
internal val START_WITH_SEVEN_AND_LESS_THAN_5: FormatterPredicate = { s -> s.firstSeven && s.length < 5 }
internal val START_WITH_SEVEN_AND_LESS_THAN_12: FormatterPredicate = { s -> s.firstSeven && s.length < 12 }
internal val START_WITH_SEVEN_AND_11_DIGITS: FormatterPredicate = { s -> s.firstSeven && s.length == 11 }
internal val START_WITH_SEVEN: FormatterPredicate = { s -> s.firstSeven }

internal val LESS_THAN_FIVE: FormatterPredicate = { s -> s.length < 5 }
internal val FIVE_DIGIT: FormatterPredicate = { s -> s.length == 5 }
internal val SIX_DIGIT: FormatterPredicate = { s -> s.length == 6 }
internal val SEVEN_DIGIT: FormatterPredicate = { s -> s.length == 7 }
internal val UP_TO_TEN_DIGIT: FormatterPredicate = { s -> s.length < 11 }
internal val MORE_THAN_TEN: FormatterPredicate = { s -> s.length > 10 }