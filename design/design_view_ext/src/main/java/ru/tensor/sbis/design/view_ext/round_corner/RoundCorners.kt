package ru.tensor.sbis.design.view_ext.round_corner

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * Закруглить все углы [View] радиусом [roundRadius] с помощью [ViewOutlineProvider].
 */
fun View.setRoundedOutlineProvider(roundRadius: Int) {
    setRoundedSideOutlineProvider(roundRadius, top = true, bottom = true, left = true, right = true)
}

/**
 * Закруглить углы сторон [View] радиусом [roundRadius] с помощью [ViewOutlineProvider].
 */
fun View.setRoundedSideOutlineProvider(
    roundRadius: Int,
    top: Boolean = false,
    bottom: Boolean = false,
    left: Boolean = false,
    right: Boolean = false
) {
    setRoundedOutlineProvider(
        roundRadius,
        topLeft = top || left,
        topRight = top || right,
        bottomLeft = bottom || left,
        bottomRight = bottom || right
    )
}

/**
 * Закруглить углы [View] радиусом [roundRadius] с помощью [ViewOutlineProvider].
 * Поддерживаются закругления:
 * - Все углы
 * - Каждая сторона (верхняя ([topLeft] и [topRight]), нижняя ([bottomLeft] и [bottomRight]), левая ([topLeft] и [bottomLeft]), правая ([topRight] и [bottomRight]))
 * - Отдельно ОДИН угол ([topLeft], [topRight], [bottomLeft], [bottomRight])
 * Остальные закругления не поддерживаются.
 * Например, верхняя сторона + левый нижний угол - закругление применится только для верхней стороны
 */
fun View.setRoundedOutlineProvider(
    roundRadius: Int,
    topLeft: Boolean = false,
    topRight: Boolean = false,
    bottomLeft: Boolean = false,
    bottomRight: Boolean = false
) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            val left = 0
            val top = 0
            val right = view.width
            val bottom = view.height

            val topCorners = topLeft && topRight
            val bottomCorners = bottomLeft && bottomRight
            val leftCorners = topLeft && bottomLeft
            val rightCorners = topRight && bottomRight
            val allCorners = topCorners && bottomCorners

            val roundRadiusF = roundRadius.toFloat()
            when {
                allCorners -> outline.setRoundRect(left, top, right, bottom, roundRadiusF)
                topCorners -> outline.setRoundRect(left, top, right, bottom + roundRadius, roundRadiusF)
                bottomCorners -> outline.setRoundRect(left, top - roundRadius, right, bottom, roundRadiusF)
                leftCorners -> outline.setRoundRect(left, top, right + roundRadius, bottom, roundRadiusF)
                rightCorners -> outline.setRoundRect(left - roundRadius, top, right, bottom, roundRadiusF)
                topLeft -> outline.setRoundRect(
                    left, top, right + roundRadius, bottom + roundRadius, roundRadiusF
                )

                topRight -> outline.setRoundRect(
                    left - roundRadius, top, right, bottom + roundRadius, roundRadiusF
                )

                bottomLeft -> outline.setRoundRect(
                    left, top - roundRadius, right + roundRadius, bottom, roundRadiusF
                )

                bottomRight -> outline.setRoundRect(
                    left - roundRadius, top - roundRadius, right, bottom, roundRadiusF
                )
            }
        }
    }
    clipToOutline = listOf(topLeft, topRight, bottomLeft, bottomRight).any { it }
}