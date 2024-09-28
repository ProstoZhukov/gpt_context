package ru.tensor.sbis.design.selection.ui.utils.fixed_button

import androidx.fragment.app.FragmentActivity

/**
 * Подписка на нажатие "фиксированной кнопки" в компоненте выбора
 *
 * @see FixedButtonType
 *
 * @author ma.kolpakov
 */
internal interface FixedButtonListener<in DATA, in ACTIVITY : FragmentActivity> {

    fun onButtonClicked(activity: ACTIVITY, result: DATA)
}