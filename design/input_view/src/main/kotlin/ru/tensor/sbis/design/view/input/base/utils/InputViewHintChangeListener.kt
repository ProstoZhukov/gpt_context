package ru.tensor.sbis.design.view.input.base.utils

import android.view.View

/**
 * Класс для отложенного выполнения [action] после onLayout.
 *
 * @author ps.smirnyh
 */
internal class InputViewChangeListener(private val action: () -> Unit) : View.OnLayoutChangeListener {
    override fun onLayoutChange(
        v: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        v.removeOnLayoutChangeListener(this)
        action()
    }
}