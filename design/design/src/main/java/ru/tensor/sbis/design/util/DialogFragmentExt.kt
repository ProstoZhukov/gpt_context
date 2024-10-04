package ru.tensor.sbis.design.util

import android.app.Activity
import androidx.fragment.app.DialogFragment

/**
 * Проверяет, нужно ли показать статус-бар.
 * @see [Activity.shouldShowStatusBar]
 */
fun DialogFragment.checkStatusBarShouldBeShown() {
    val window = dialog?.window ?: return
    val activity = this.activity ?: return
    window.showStatusBar(activity.shouldShowStatusBar)
}