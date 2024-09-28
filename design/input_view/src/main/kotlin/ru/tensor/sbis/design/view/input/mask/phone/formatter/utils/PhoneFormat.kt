/**
 * Набор предикатов для подбора нужной маски во время ввода номера телефона.
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.view.input.mask.phone.formatter.utils

import ru.tensor.sbis.design.view.input.mask.phone.formatter.FormatterPredicate

/** Длина Российского мобильного телефона с учетом знака +. */
internal const val MAX_LENGTH_RUSSIAN_PHONE = 12

/** Длина иностранного мобильного телефона с 1 цифрой кода страны с учетом знака +. */
internal const val MAX_LENGTH_ONE_FOREIGN_PHONE = MAX_LENGTH_RUSSIAN_PHONE

/** Длина иностранного мобильного телефона с 2 цифрами кода страны с учетом знака +. */
internal const val MAX_LENGTH_TWO_FOREIGN_PHONE = 13

/** Длина иностранного мобильного телефона с 3 цифрами кода страны с учетом знака +. */
internal const val MAX_LENGTH_THREE_FOREIGN_PHONE = 14

/**
 * Маска, которая покрывает пользовательский ввод (без форматирования) телефонного номера.
 */
internal const val COMMON_PHONE_MASK = "*000000000000000000000000000000"

// region иностранные номера

/** Номер начинается с + и имеет одну цифру кода страны. */
internal val START_PLUS_ONE_FOREIGN_CODE: FormatterPredicate = { s ->
    s.length > 1 && s.firstPlus
}

/** Номер начинается с + и имеет одну цифру кода страны и код региона. */
internal val START_PLUS_ONE_FOREIGN_CODE_AND_REGION: FormatterPredicate = { s ->
    START_PLUS_ONE_FOREIGN_CODE(s) && s.length == 5
}

/** Номер начинается с +, имеет одну цифру кода страны и длину более 10 цифр, не считая + и код страны. */
internal val START_PLUS_ONE_FOREIGN_CODE_AND_MORE_THAN_10: FormatterPredicate = { s ->
    START_PLUS_ONE_FOREIGN_CODE(s) && s.length > 12
}

/** Номер начинается с + и имеет две цифры кода страны. */
internal val START_PLUS_TWO_FOREIGN_CODE: FormatterPredicate = { s ->
    s.length > 2 && s.firstPlus && (FOREIGN_CODES[s[1].toString()]?.any { it.startsWith(s[2]) } == true)
}

/** Номер начинается с + и имеет две цифры кода страны и код региона. */
internal val START_PLUS_TWO_FOREIGN_CODE_AND_REGION: FormatterPredicate = { s ->
    START_PLUS_TWO_FOREIGN_CODE(s) && s.length == 6
}

/** Номер начинается с +, имеет две цифры кода страны и длину более 10 цифр, не считая + код страны. */
internal val START_PLUS_TWO_FOREIGN_CODE_AND_MORE_THAN_10: FormatterPredicate = { s ->
    START_PLUS_TWO_FOREIGN_CODE(s) && s.length > 13
}

/** Номер начинается с + и имеет три цифры кода страны. */
internal val START_PLUS_THREE_FOREIGN_CODE: FormatterPredicate = { s ->
    s.length > 3 && s.firstPlus && (FOREIGN_CODES[s[1].toString()]?.contains(s.substring(2..3)) == true)
}

/** Номер начинается с + и имеет три цифры кода страны и код региона. */
internal val START_PLUS_THREE_FOREIGN_CODE_AND_REGION: FormatterPredicate = { s ->
    START_PLUS_THREE_FOREIGN_CODE(s) && s.length == 7
}

/** Номер начинается с +, имеет три цифры кода страны и длину более 10 цифр, не считая + и код страны. */
internal val START_PLUS_THREE_FOREIGN_CODE_AND_MORE_THAN_10: FormatterPredicate = { s ->
    START_PLUS_THREE_FOREIGN_CODE(s) && (s.length in 4..6 || s.length > 14)
}
// endregion

// region мобильные номера России

/** Номер начинается с +7 и имеет три цифры кода региона. */
internal val PLUS_SEVEN_THREE_REGION_CODE: FormatterPredicate = { s ->
    s.plusSeven && s.length > 4
}

/** Номер начинается с 8 и имеет три цифры кода региона. */
internal val EIGHT_THREE_REGION_CODE: FormatterPredicate = { s ->
    s.firstEight && s.length > 3
}

/** Номер начинается с +7 и имеет только три цифры кода региона. */
internal val PLUS_SEVEN_THREE_REGION_CODE_SHORT: FormatterPredicate = { s ->
    PLUS_SEVEN_THREE_REGION_CODE(s) && s.length == 5
}

/** Номер начинается с 8 и имеет только три цифры кода региона. */
internal val EIGHT_THREE_REGION_CODE_SHORT: FormatterPredicate = { s ->
    EIGHT_THREE_REGION_CODE(s) && s.length == 4
}

/** Номер начинается с +7 и имеет четыре цифры кода региона. */
internal val PLUS_SEVEN_FOUR_REGION_CODE: FormatterPredicate = { s ->
    s.plusSeven && s.length > 5 && (REGION[s.substring(2..4)]?.contains(s[5].toString()) == true)
}

/** Номер начинается с 8 и имеет четыре цифры кода региона. */
internal val EIGHT_FOUR_REGION_CODE: FormatterPredicate = { s ->
    s.firstEight && s.length > 4 && (REGION[s.substring(1..3)]?.contains(s[4].toString()) == true)
}

/** Номер начинается с +7 и имеет только четыре цифры кода региона. */
internal val PLUS_SEVEN_FOUR_REGION_CODE_SHORT: FormatterPredicate = { s ->
    PLUS_SEVEN_FOUR_REGION_CODE(s) && s.length == 6
}

/** Номер начинается с 8 и имеет только четыре цифры кода региона. */
internal val EIGHT_FOUR_REGION_CODE_SHORT: FormatterPredicate = { s ->
    EIGHT_FOUR_REGION_CODE(s) && s.length == 5
}

/** Номер начинается с +7 и имеет пять цифр кода региона. */
internal val PLUS_SEVEN_FIVE_REGION_CODE: FormatterPredicate = { s ->
    s.plusSeven && s.length > 6 &&
        (REGION[s.substring(2..4)]?.contains(s.substring(5..6)) == true)
}

/** Номер начинается с 8 и имеет пять цифр кода региона. */
internal val EIGHT_FIVE_REGION_CODE: FormatterPredicate = { s ->
    s.firstEight && s.length > 5 &&
        (REGION[s.substring(1..3)]?.contains(s.substring(4..5)) == true)
}

/** Номер начинается с +7 и имеет только пять цифр кода региона. */
internal val PLUS_SEVEN_FIVE_REGION_CODE_SHORT: FormatterPredicate = { s ->
    PLUS_SEVEN_FIVE_REGION_CODE(s) && s.length == 7
}

/** Номер начинается с 8 и имеет только пять цифр кода региона. */
internal val EIGHT_FIVE_REGION_CODE_SHORT: FormatterPredicate = { s ->
    EIGHT_FIVE_REGION_CODE(s) && s.length == 6
}

/** Номер начинается с +7 и содержит 12 цифр. */
internal val START_WITH_PLUS_SEVEN_AND_12_DIGITS: FormatterPredicate =
    { s -> s.plusSeven && s.length == 13 }

/** Номер начинается с 8 и содержит 12 цифр. */
internal val START_WITH_EIGHT_AND_12_DIGITS: FormatterPredicate =
    { s -> s.firstEight && s.length == 12 }

/** Номер начинается с +7, имеет 3 цифры кода региона и длину более 12 цифр. */
internal val PLUS_SEVEN_THREE_REGION_CODE_AND_MORE_THAN_12: FormatterPredicate =
    { s -> PLUS_SEVEN_THREE_REGION_CODE(s) && s.length > 13 }

/** Номер начинается с 8, имеет 3 цифры кода региона и длину более 12 цифр. */
internal val EIGHT_THREE_REGION_CODE_AND_MORE_THAN_12: FormatterPredicate =
    { s -> EIGHT_THREE_REGION_CODE(s) && s.length > 12 }

/** Номер начинается с +7, имеет 4 цифры кода региона и длину более 12 цифр. */
internal val PLUS_SEVEN_FOUR_REGION_CODE_AND_MORE_THAN_12: FormatterPredicate =
    { s -> PLUS_SEVEN_FOUR_REGION_CODE(s) && s.length > 13 }

/** Номер начинается с 8, имеет 4 цифры кода региона и длину более 12 цифр. */
internal val EIGHT_FOUR_REGION_CODE_AND_MORE_THAN_12: FormatterPredicate =
    { s -> EIGHT_FOUR_REGION_CODE(s) && s.length > 12 }

/** Номер начинается с +7, имеет 5 цифр кода региона и длину более 12 цифр. */
internal val PLUS_SEVEN_FIVE_REGION_CODE_AND_MORE_THAN_12: FormatterPredicate =
    { s -> PLUS_SEVEN_FIVE_REGION_CODE(s) && s.length > 13 }

/** Номер начинается с 8, имеет 5 цифр кода региона и длину более 12 цифр. */
internal val EIGHT_FIVE_REGION_CODE_AND_MORE_THAN_12: FormatterPredicate =
    { s -> EIGHT_FIVE_REGION_CODE(s) && s.length > 12 }

/** Номер начинается с +7. */
internal val START_WITH_PLUS_SEVEN: FormatterPredicate = { s -> s.plusSeven }

/** Номер начинается с 8. */
internal val START_WITH_EIGHT: FormatterPredicate = { s -> s.firstEight }
// endregion

// region общие номера

/** Длина номера меньше 5 знаков. */
internal val LESS_THAN_FIVE: FormatterPredicate = { s -> s.length < 5 }

/** Длина номера 5 знаков. */
internal val FIVE_DIGIT: FormatterPredicate = { s -> s.length == 5 }

/** Длина номера 6 знаков. */
internal val SIX_DIGIT: FormatterPredicate = { s -> s.length == 6 }

/** Длина номера 7 знаков. */
internal val SEVEN_DIGIT: FormatterPredicate = { s -> s.length == 7 }

/** Длина номера меньше 11 знаков. */
internal val UP_TO_TEN_DIGIT: FormatterPredicate = { s -> s.length < 11 }

/** Длина номера больше 10 знаков. */
internal val MORE_THAN_TEN: FormatterPredicate = { s -> s.length > 10 }
// endregion