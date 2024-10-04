package ru.tensor.sbis.design.text_span.text.masked.phone

import android.annotation.SuppressLint
import androidx.annotation.IntDef

/**
 * Определение перечня флагов формата
 *
 * @author ma.kolpakov
 * @since 12/6/2019
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [COMMON_FORMAT, RU_LEN, RU_FORMAT], flag = true)
annotation class PhoneFormat

/**
 * Формат по умолчанию
 */
const val COMMON_FORMAT = 0
/**
 * Флаг для отметки ограничения длины как для российских номеров
 */
const val RU_LEN = 1
/**
 * Флаг для отметки форматирования как для российских номеров
 */
@SuppressLint("ShiftFlags")
const val RU_FORMAT = 2