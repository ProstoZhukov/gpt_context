package ru.tensor.sbis.segmented_control.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * @author ps.smirnyh
 */
internal class BackgroundOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) = with(view) {
        outline.setRoundRect(
            paddingStart,
            paddingTop,
            paddingStart + measuredWidth,
            paddingTop + measuredHeight,
            measuredHeight / 2f
        )
    }
}