package ru.tensor.sbis.swipeablelayout.util

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.Px

/**
 * Реализация [ViewOutlineProvider] для скругления углов контента, принимающая во внимание внутренние отступы.
 *
 * @author us.bessonov
 */
internal class RoundedRectWithPaddingsOutlineProvider(@Px val radius: Float) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val left = view.paddingLeft
        val top = view.paddingTop
        val right = view.width - view.paddingRight
        val bottom = view.height - view.paddingBottom

        outline.setRoundRect(left, top, right, bottom, radius)
    }
}