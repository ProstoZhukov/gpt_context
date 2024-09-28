package ru.tensor.sbis.message_panel.recorder.util

import android.view.ViewGroup
import timber.log.Timber

internal tailrec fun hasParentWithoutClippingChildren(viewGroup: ViewGroup?): Boolean {
    val parent = viewGroup?.parent as? ViewGroup
        ?: return false
    if (!parent.clipChildren) return true
    return hasParentWithoutClippingChildren(parent)
}

internal fun checkHasParentWithoutClippingChildren(recorderView: ViewGroup) {
    if (!hasParentWithoutClippingChildren(recorderView)) {
        Timber.w("Attribute 'android:clipChildren' must be set to false on one of the parents of ${recorderView::class.java.simpleName}")
    }
}