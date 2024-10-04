package ru.tensor.sbis.design.text_span.text.masked.formatter.phone

/**
 * Правила, накладываются по длине номера. Необходимы для отображения номеров в карточке сотрудника
 *
 * @author ra.temnikov
 */

private const val FIRST_MOBILE_PHONE_CHARACTER = '8'

internal val FIVE_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 5 }
internal val SIX_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 6 }
internal val SEVEN_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 7 }
internal val EIGHT_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 8 }
internal val NINE_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 9 }
internal val TEN_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 10 }
internal val ELEVEN_DIGITS_AND_CONTAINS_EIGHT: (CharSequence) -> Boolean =
    { s -> s.length == 11 && s.first() == FIRST_MOBILE_PHONE_CHARACTER }
internal val ELEVEN_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 11 }
internal val TWELVE_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 12 }
internal val THIRTEEN_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 13 }
internal val FOURTEEN_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 14 }
internal val FIFTEEN_DIGITS: (CharSequence) -> Boolean = { s -> s.length == 15 }
