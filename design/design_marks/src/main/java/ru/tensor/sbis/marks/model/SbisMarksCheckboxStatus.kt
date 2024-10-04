package ru.tensor.sbis.marks.model

import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue

/**
 * Обёртка вокруг enum класса [SbisCheckboxValue].
 * Позволяет прикладному разработчику не добавлять зависимость для чекбоксов в свой модуль.
 *
 * @author ra.geraskin
 */

enum class SbisMarksCheckboxStatus(val value: SbisCheckboxValue) {
    CHECKED(SbisCheckboxValue.CHECKED),
    UNCHECKED(SbisCheckboxValue.UNCHECKED),
    UNDEFINED(SbisCheckboxValue.UNDEFINED);

    companion object {

        /**
         * Получение значения текущего enam класса по значению enum класса из модуля чекбоксов.
         */
        fun getByValue(value: SbisCheckboxValue): SbisMarksCheckboxStatus = when (value) {
            SbisCheckboxValue.CHECKED -> CHECKED
            SbisCheckboxValue.UNCHECKED -> UNCHECKED
            SbisCheckboxValue.UNDEFINED -> UNDEFINED
        }
    }
}