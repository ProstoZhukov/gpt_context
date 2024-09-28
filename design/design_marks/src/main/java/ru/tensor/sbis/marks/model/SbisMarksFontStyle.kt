package ru.tensor.sbis.marks.model

import android.annotation.SuppressLint
import androidx.annotation.IntDef
import android.view.Gravity

/**
 * Структура стилизации текста заголовка пометки. Работает по аналогии с Int значениями констант класса [Gravity].
 *
 * @author ra.geraskin
 */

@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [BOLD, ITALIC, UNDERLINE, STRIKETHROUGH], flag = true)
annotation class SbisMarksFontStyle

/** Жирный */
const val BOLD = 1

/** Курсив */
@SuppressLint("ShiftFlags")
const val ITALIC = 2

/** Подчёркнутый */
@SuppressLint("ShiftFlags")
const val UNDERLINE = 4

/** Зачёркнутый */
@SuppressLint("ShiftFlags")
const val STRIKETHROUGH = 8
