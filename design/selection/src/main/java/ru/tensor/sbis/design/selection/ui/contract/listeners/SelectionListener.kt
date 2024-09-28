package ru.tensor.sbis.design.selection.ui.contract.listeners

import androidx.fragment.app.FragmentActivity
import java.io.Serializable

/**
 * Подписка на результат одиночного выбора
 *
 * @author ma.kolpakov
 */
interface SelectionListener<in DATA, in ACTIVITY : FragmentActivity> : Serializable {

    fun onComplete(activity: ACTIVITY, result: DATA)
}