package ru.tensor.sbis.design.design_menu.view.shadow

import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout

/**
 * Положение тени в [FrameLayout].
 *
 * @author ra.geraskin
 */
internal enum class ShadowPosition(
    val gravity: Int,
    val scrollDirection: Int,
    val gradientOrientation: GradientDrawable.Orientation
) {

    /**
     * Верхняя тень.
     */
    TOP(
        gravity = Gravity.TOP,
        scrollDirection = SCROLL_DIRECTION_UP,
        gradientOrientation = GradientDrawable.Orientation.TOP_BOTTOM,
    ),

    /**
     * Нижняя тень.
     */
    BOTTOM(
        gravity = Gravity.BOTTOM,
        scrollDirection = SCROLL_DIRECTION_DOWN,
        gradientOrientation = GradientDrawable.Orientation.BOTTOM_TOP,
    )
}

private const val SCROLL_DIRECTION_UP = -1
private const val SCROLL_DIRECTION_DOWN = 1