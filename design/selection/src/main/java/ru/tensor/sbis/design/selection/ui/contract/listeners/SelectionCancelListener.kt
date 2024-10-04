package ru.tensor.sbis.design.selection.ui.contract.listeners

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.Serializable

/**
 * Подписка на отмену выбора
 *
 * @author ma.kolpakov
 */
interface SelectionCancelListener<in ACTIVITY : FragmentActivity> : Serializable {

    fun onCancel(activity: ACTIVITY)

    @Suppress("UNCHECKED_CAST")
    fun onCancel(fragment: Fragment) {
        onCancel(fragment.requireActivity() as ACTIVITY)
    }
}