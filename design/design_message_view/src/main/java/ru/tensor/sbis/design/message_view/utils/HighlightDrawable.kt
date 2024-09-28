package ru.tensor.sbis.design.message_view.utils

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.animation.DecelerateInterpolator
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA

/**
 * Фон для подсветки с fade анимациями.
 *
 * @author vv.chekurda
 */
class HighlightDrawable(highlightColor: Int) : ColorDrawable(highlightColor) {

    private var isAnimationRunning: Boolean = false
    private var lastFrameTime = 0L
    private var animationRunningMs = 0L
    private var highlightDurationMs = 0L
        set(value) {
            field = value
            fullAnimTime = value + SHOW_DURATION_MS * 2
        }
    private var fullAnimTime = 0L

    private val interpolator by lazy { DecelerateInterpolator() }

    var translationY: Float = 0f

    init {
        alpha = 0
    }

    /**
     * Подсветить продолжительностью [durationMs].
     * @param durationMs продолжительность статичной подсветки.
     * Общая продолжительность анимации [durationMs] статика + [SHOW_DURATION_MS] на появление
     * и то же время на исчезновение.
     */
    fun highlight(durationMs: Long = DEFAULT_HIGHLIGHT_DURATION_MS) {
        highlightDurationMs = durationMs
        lastFrameTime = System.currentTimeMillis()
        animationRunningMs = when {
            // Анимация закончилась или сейчас будет завершена - начинаем с 0
            !isAnimationRunning || animationRunningMs > fullAnimTime -> 0L
            // Находимся в фазе появления - ничего не меняем
            animationRunningMs < SHOW_DURATION_MS -> animationRunningMs
            // Находимся в фазе исчезновения -> инвертируем время для плавного показа из той же альфы
            animationRunningMs > SHOW_DURATION_MS + highlightDurationMs -> fullAnimTime - animationRunningMs
            // Находимся в фазе статичной подсветки -> продлеваем с конца появления
            else -> SHOW_DURATION_MS
        }
        isAnimationRunning = true
        invalidateSelf()
    }

    /**
     * Убрать подсветку.
     */
    fun hide() {
        isAnimationRunning = false
        alpha = 0
        lastFrameTime = 0L
        animationRunningMs = 0L
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (isAnimationRunning) {
            val dt = System.currentTimeMillis() - lastFrameTime
            animationRunningMs += dt
            alpha = when {
                // Анимация закончилась -> выключаем
                animationRunningMs > fullAnimTime -> {
                    isAnimationRunning = false
                    0
                }
                // Фаза анимации появления -> fade in
                animationRunningMs < SHOW_DURATION_MS -> {
                    val animTime = animationRunningMs
                    val interpolation = interpolator.getInterpolation(animTime / SHOW_DURATION_MS.toFloat())
                    (interpolation * PAINT_MAX_ALPHA).toInt()
                }
                // Фаза анимации исчезновения -> fade out
                animationRunningMs > SHOW_DURATION_MS + highlightDurationMs -> {
                    val animTime = animationRunningMs - highlightDurationMs - SHOW_DURATION_MS
                    val interpolation = interpolator.getInterpolation(1 - animTime / SHOW_DURATION_MS.toFloat())
                    (interpolation * PAINT_MAX_ALPHA).toInt()
                }
                // Фаза статичной подсветки
                else -> PAINT_MAX_ALPHA
            }
            lastFrameTime = System.currentTimeMillis()
        }
        canvas.withTranslation(y = translationY) {
            super.draw(canvas)
        }
        if (isAnimationRunning) invalidateSelf()
    }
}

const val DEFAULT_HIGHLIGHT_DURATION_MS = 800L
private const val SHOW_DURATION_MS = 200L