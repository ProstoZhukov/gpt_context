package ru.tensor.sbis.design.buttons.group.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * @author ma.kolpakov
 */
internal class OvalOutlineProvider : ViewOutlineProvider() {

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