package ru.tensor.sbis.design.buttons.round.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.Px

/**
 * @author ma.kolpakov
 */
internal class CircleOutlineProvider(
    @Px private val size: Int,
    private val cornerRadius: Float
) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) = with(view) {
        if (cornerRadius.isNaN()) {
            outline.setOval(paddingStart, paddingTop, paddingStart + size, paddingTop + size)
        } else {
            outline.setRoundRect(paddingStart, paddingTop, paddingStart + size, paddingTop + size, cornerRadius)
        }
    }
}