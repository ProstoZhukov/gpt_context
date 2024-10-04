package ru.tensor.sbis.design.buttons.round.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.graphics.Shader
import androidx.annotation.Dimension
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonComponentDrawer
import ru.tensor.sbis.design.theme.Direction
import ru.tensor.sbis.design.theme.global_variables.BorderColor

/**
 * Объект для рисования фона круглой кнопки.
 * ShapeDrawable не учитывает внутренние отступы из коробки.
 *
 * @author ma.kolpakov
 */
internal class CircleBackgroundDrawer(
    context: Context,
    @Dimension private val buttonSize: Float,
    private val cornerRadius: Float,
    private val borderWidthDisabled: Float,
    private val state: SbisButtonState,
    private val gradient: Pair<Direction, Int>? = null
) : ButtonComponentDrawer {

    private val borderColor by lazy { BorderColor.READ_ONLY.getValue(context) }

    private val defaultRadius = buttonSize / 2F
    private var borderRadius = defaultRadius - borderWidthDisabled / 2

    private val backgroundPaint = Paint(ANTI_ALIAS_FLAG)
    private var borderPaint: Paint? = null

    private val smallRect = RectF().apply {
        left = borderWidthDisabled
        right = buttonSize - borderWidthDisabled
        top = borderWidthDisabled
        bottom = buttonSize - borderWidthDisabled
    }
    private val defaultRect = RectF().apply {
        right = buttonSize
        bottom = buttonSize
    }
    private var buttonRect = defaultRect

    private val gradientCoordinate = gradient?.first?.toOrientation()

    override var isVisible: Boolean = true

    override val width: Float = buttonSize

    override val height: Float = buttonSize

    init {
        setupBorderPaint(state)
    }

    override fun setTint(color: Int): Boolean {
        val changed = backgroundPaint.color != color
        if (changed) {
            backgroundPaint.color = color

            gradientCoordinate?.let { coordinate ->
                backgroundPaint.style = Paint.Style.FILL

                backgroundPaint.shader = LinearGradient(
                    coordinate.x0,
                    coordinate.y0,
                    coordinate.x1,
                    coordinate.y1,
                    if (state == SbisButtonState.DISABLED) color else gradient?.second ?: color,
                    color,
                    Shader.TileMode.CLAMP
                )
            }
        }
        return changed
    }

    override fun changeState(state: SbisButtonState) {
        if (!setupBorderPaint(state)) {
            borderPaint = null
            buttonRect = defaultRect
        }
    }

    private fun setupBorderPaint(state: SbisButtonState): Boolean {
        return if (state == SbisButtonState.DISABLED && borderWidthDisabled != 0F) {
            borderPaint = Paint(ANTI_ALIAS_FLAG).apply {
                color = borderColor
                strokeWidth = borderWidthDisabled
                style = Paint.Style.STROKE
            }
            buttonRect = smallRect
            true
        } else {
            false
        }
    }

    override fun draw(canvas: Canvas) {
        if (isVisible) {
            if (cornerRadius.isNaN()) {
                canvas.drawCircle(defaultRadius, defaultRadius, defaultRadius, backgroundPaint)
                borderPaint?.let {
                    canvas.drawCircle(defaultRadius, defaultRadius, borderRadius, it)
                }
            } else {
                canvas.drawRoundRect(buttonRect, cornerRadius, cornerRadius, backgroundPaint)
                borderPaint?.let {
                    canvas.drawRoundRect(smallRect, cornerRadius, cornerRadius, it)
                }
            }
        }
    }

    private fun Direction.toOrientation() = when (this) {
        Direction.LEFT_TO_RIGHT ->
            GradientCoordinate(0F, 0F, defaultRect.width(), 0F)

        Direction.RIGHT_TO_LEFT ->
            GradientCoordinate(defaultRect.width(), defaultRect.height(), 0F, defaultRect.height())

        Direction.TOP_TO_BOTTOM ->
            GradientCoordinate(0F, 0F, 0F, defaultRect.height())

        Direction.BOTTOM_TO_TOP ->
            GradientCoordinate(defaultRect.width(), defaultRect.height(), defaultRect.width(), 0F)
    }

    data class GradientCoordinate(val x0: Float, val y0: Float, val x1: Float, val y1: Float)
}