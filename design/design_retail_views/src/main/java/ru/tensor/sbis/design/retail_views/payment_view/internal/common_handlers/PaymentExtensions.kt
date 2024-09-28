@file:JvmName("PaymentExtensions")

package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/** Исправление перехвата фокуса системой Android. (Дропается cursorPosition) */
internal fun EditText.initializeCursorPositionAfterDraw() {
    post { requestFocus() }
}

/** Попытка найти [FragmentManager] путем поиска родительского контейнера. */
internal fun View.tryFindFragmentManager() = try {
    FragmentManager.findFragment<Fragment>(this).childFragmentManager
} catch (e: IllegalStateException) {
    context.getActivity().supportFragmentManager
}

private tailrec fun Context.getActivity(): FragmentActivity = this as? FragmentActivity
    ?: (this as ContextWrapper).baseContext.getActivity()