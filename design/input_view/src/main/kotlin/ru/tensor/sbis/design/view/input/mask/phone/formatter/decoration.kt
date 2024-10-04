package ru.tensor.sbis.design.view.input.mask.phone.formatter

import android.annotation.SuppressLint
import androidx.annotation.IntDef

/**
 * Определение перечня флагов формата номера телефона.
 *
 * @author ps.smirnyh
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [COMMON_FORMAT, MOB_LEN, MOB_FORMAT], flag = true)
annotation class PhoneFormatDecoration

/**
 * Флаг для отметки форматирования немобильных (общих) номеров телефонов.
 */
@SuppressLint("ShiftFlags")
const val COMMON_FORMAT = 2

/**
 * Флаг для отметки ограничения длины мобильных номеров.
 */
const val MOB_LEN = 1

/**
 * Флаг для отметки форматирования мобильных номеров.
 */
const val MOB_FORMAT = 0