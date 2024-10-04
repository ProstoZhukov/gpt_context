package ru.tensor.sbis.design.buttons.button.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Looper
import android.view.View
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState.DISABLED
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState.ENABLED
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState.IN_PROGRESS
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground.*
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize.*
import ru.tensor.sbis.design.theme.Direction
import ru.tensor.sbis.design.theme.Direction.*

/**
 * Вспомогательнй объект, который отвечает за формирвоание и обновление фона кнопки.
 *
 * @author ma.kolpakov
 */
internal class BackgroundHolder(
    private val button: View
) {

    private companion object {
        const val VISIBLE_ALPHA = 255

        const val INVISIBLE_ALPHA = 0
    }

    private val context = button.context
    private val backgroundDrawable = ButtonBackgroundDrawable()
    private val progressDrawable = CircularProgressDrawable(context)

    val background: Drawable = LayerDrawable(
        arrayOf(backgroundDrawable, progressDrawable)
    )

    var size: SbisButtonSize = M
        set(value) {
            field = value

            with(context.resources) {
                progressDrawable.centerRadius = getDimension(field.progressSize) / 2F
                progressDrawable.strokeWidth = getDimension(field.progressWidth)
            }
        }

    fun updateCornerRadius(cornerRadius: Float) {
        backgroundDrawable.cornerRadius = cornerRadius
    }

    var state
        get() = backgroundDrawable.buttonState
        set(value) {
            backgroundDrawable.buttonState = value

            if (backgroundDrawable.buttonState == IN_PROGRESS) {
                if (Looper.getMainLooper().thread == Thread.currentThread()) {
                    progressDrawable.start()
                } else {
                    /*
                    Кнопка создаётся в фоне с начальным xml состоянием in_progress.
                    Запустим анимацию, когда кнопка будет видна
                     */
                    button.post { progressDrawable.start() }
                }
            } else if (progressDrawable.isRunning) {
                progressDrawable.stop()
            }
        }

    fun updateStyle(style: SbisButtonCustomStyle, buttonBackground: SbisButtonBackground) =
        with(style) {
            when (buttonBackground) {
                Default -> {
                    backgroundDrawable.color = backgroundColors
                    backgroundDrawable.setStroke(borderWidth, borderColors)
                    backgroundDrawable.alpha = VISIBLE_ALPHA

                    progressDrawable.setColorSchemeColors(progressColor)
                }
                // заливаем фон контрастным цветом
                Contrast -> {
                    if ((button.isEnabled && state == ENABLED) || isReadOnly()) {
                        backgroundDrawable.color = contrastBackgroundColors
                    }
                    if (isReadOnly()) {
                        backgroundDrawable.setStroke(borderWidth, borderColors)
                    } else {
                        backgroundDrawable.setStroke(0, borderColors)
                    }
                    backgroundDrawable.alpha = VISIBLE_ALPHA

                    progressDrawable.setColorSchemeColors(progressContrastColor)
                }
                // применяем состояния фона прозрачной кнопки
                BorderOnly -> {
                    backgroundDrawable.color = transparentBackgroundColors
                    backgroundDrawable.setStroke(borderWidth, borderColors)
                    backgroundDrawable.alpha = VISIBLE_ALPHA

                    progressDrawable.setColorSchemeColors(progressColor)
                }

                Transparent, InGroup -> {
                    backgroundDrawable.alpha = INVISIBLE_ALPHA

                    progressDrawable.setColorSchemeColors(progressColor)
                }

                is Gradient -> {
                    backgroundDrawable.orientation = buttonBackground.direction.toOrientation()
                    backgroundDrawable.color = gradientBackgroundColors
                    val pressedState = intArrayOf(android.R.attr.state_pressed)
                    backgroundDrawable.gradientColor = gradientBackgroundColors.getColorForState(
                        pressedState,
                        gradientBackgroundColors.defaultColor
                    )
                    backgroundDrawable.setStroke(0, borderColors)
                    backgroundDrawable.alpha = VISIBLE_ALPHA

                    progressDrawable.setColorSchemeColors(progressContrastColor)
                }
            }
        }

    private fun Direction.toOrientation() = when (this) {
        LEFT_TO_RIGHT -> GradientDrawable.Orientation.LEFT_RIGHT
        RIGHT_TO_LEFT -> GradientDrawable.Orientation.RIGHT_LEFT
        TOP_TO_BOTTOM -> GradientDrawable.Orientation.TOP_BOTTOM
        BOTTOM_TO_TOP -> GradientDrawable.Orientation.BOTTOM_TOP
    }

    private fun isReadOnly() = !button.isEnabled && state == DISABLED
}