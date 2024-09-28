package ru.tensor.sbis.hallscheme.v2.util

/**
 * Вспомогательная функция для итерирования по диапазону чисел с автоматическим вычислением шага.
 * @author aa.gulevskiy
 */
internal infix fun Int.iterateTo(second: Int): IntProgression {
    return if (this <= second) {
        IntProgression.fromClosedRange(this, second, 1)
    } else {
        IntProgression.fromClosedRange(this, second, -1)
    }
}