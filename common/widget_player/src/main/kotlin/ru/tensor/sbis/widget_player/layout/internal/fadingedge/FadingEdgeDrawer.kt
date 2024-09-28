package ru.tensor.sbis.widget_player.layout.internal.fadingedge

import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px

/**
 * Интерфейс для отрисовки эффекта Fading Edge на View.
 * Отрисовывает по краям View угасающую тень в виде градиента, начиная от [color] и заканчивая [Color.TRANSPARENT].
 *
 * @property color основной цвет градиента
 * @property length размер тени в пикселях
 *
 * @author am.boldinov
 */
internal interface FadingEdgeDrawer {

    @get:ColorInt
    @setparam:ColorInt
    var color: Int

    @get:Px
    @setparam:Px
    var length: Int

    /**
     * Выполняет отрисовку Fading Edge.
     *
     * @param view view на которой необходимо произвести отрисовку.
     * @param canvas canvas на котором необходимо произвести отрисовку.
     * Для [android.view.ViewGroup] необходимо вызывать после отрисовки чайлдов.
     * @param startStrength интенсивность начальной тени.
     * Можно использовать готовую реализацию [View.getLeftFadingEdgeStrength] или [View.getTopFadingEdgeStrength].
     * @param endStrength интенсивность конечной тени.
     * Можно использовать готовую реализацию [View.getRightFadingEdgeStrength] или [View.getBottomFadingEdgeStrength].
     */
    fun draw(view: View, canvas: Canvas, startStrength: Float, endStrength: Float)
}

