package ru.tensor.sbis.design.selection.ui.contract.listeners

import androidx.fragment.app.FragmentActivity
import java.io.Serializable

/**
 * Подписка на нажатие кнопки "Новая группа"
 *
 * @author ma.kolpakov
 */
interface NewGroupClickListener<in ACTIVITY : FragmentActivity> : Serializable {

    fun onButtonClicked(activity: ACTIVITY)
}