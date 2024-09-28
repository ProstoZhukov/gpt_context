package ru.tensor.sbis.base_components.fragment.dialog

import androidx.fragment.app.DialogFragment

/**
 * Базовый класс для диалогов
 */
open class BaseDialogFragment : DialogFragment() {

    /**@SelfDocumented*/
    protected fun <T> getListener(listenerClass: Class<T>): T? {
        return if (activity == null && parentFragment == null) {
            null
        } else if (parentFragment != null && listenerClass.isInstance(parentFragment)) {
            listenerClass.cast(parentFragment)
        } else if (listenerClass.isInstance(activity)) {
            listenerClass.cast(activity)
        } else {
            null
        }
    }

    companion object {

        protected const val DIALOG_CODE = "dialog_code"
    }
}
