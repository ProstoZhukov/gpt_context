package ru.tensor.sbis.design.files_picker.decl

import androidx.fragment.app.Fragment

/**
 * Действие при клике по разделу.
 *
 * @author ai.abramenko
 */
sealed class SbisFilesPickerTabClickAction {

    /** Показать фрагмент раздела. */
    class ShowFragment(
        val fragmentFactory: () -> Fragment
    ) : SbisFilesPickerTabClickAction()

    /** Выполнить произвольное действие. */
    class Custom(val action: (Fragment) -> Unit) : SbisFilesPickerTabClickAction()
}