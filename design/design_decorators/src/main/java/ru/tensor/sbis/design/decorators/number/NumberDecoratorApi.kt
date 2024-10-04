package ru.tensor.sbis.design.decorators.number

import android.text.Spannable

/**
 * Api числового декоратора.
 *
 * @author ps.smirnyh
 */
interface NumberDecoratorApi {

    /** Отформатированное значение декоратора. */
    val formattedValue: Spannable

    /** @SelfDocumented */
    fun changeValue(newValue: String?)

    /** @SelfDocumented */
    fun changeValue(newValue: Double?)

    /** @SelfDocumented */
    fun changeValue(newValue: Int?)
}