package ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers

import ru.tensor.sbis.design.retail_views.numberic_keyboard.NumericKeyboard

/** Слушатель момента окончания отрисовки и определения размеров виртуальной клавиатуры. */
fun interface OnInflateNumericKeyboardListener {
    /** Событие "Виртуальная клавиатура [NumericKeyboard] закончила отрисовку". */
    fun onKeyboardInflated()
}