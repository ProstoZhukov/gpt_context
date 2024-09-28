package ru.tensor.retail.compositions_decl

import androidx.fragment.app.DialogFragment

/**
 * Надстройка над системным [DialogFragment], которая умеет принимать со стороны
 * слушатель закрытия диалога.
 */
abstract class DialogFragmentWithDismissListener : DialogFragment() {

    /**@SelfDocumented*/
    abstract fun setOnDismissListener(listener: (() -> Unit)?)
}