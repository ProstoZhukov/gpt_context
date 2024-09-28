package ru.tensor.sbis.business.common.ui.utils

import android.view.View
import androidx.databinding.ObservableField

/**@SelfDocumented */
class ViewActionObservable<T : View> : ObservableField<T.() -> Unit>() {

    fun perform(view: T) {
        val action = super.get()
        if (action != null) {
            action.invoke(view)
            set(null)
        }
    }

    override fun notifyChange() {
        if (get() != null) {
            super.notifyChange()
        }
    }
}