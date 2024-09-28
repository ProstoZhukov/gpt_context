package ru.tensor.sbis.communicator.sbis_conversation.preview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator

/**
 * Класс `ConversationPreviewScaleAlphaAnimation` представляет собой анимацию, которая
 * одновременно изменяет масштаб представления и его прозрачность. Анимация выполняется
 * с заданными параметрами, включая начальные и конечные значения масштаба, продолжительность
 * анимации и интерполятор для сглаживания анимации.
 *
 * Конструктор:
 * - `fromScale`: Начальный масштаб анимации. Значение по умолчанию — 0.8f.
 * - `toScale`: Конечный масштаб анимации. Значение по умолчанию — 1.0f.
 * - `duration`: Длительность анимации в миллисекундах. Значение по умолчанию — 300.
 * - `interpolator`: Интерполятор для анимации. По умолчанию используется
 *   `AccelerateDecelerateInterpolator()`, который делает анимацию плавной.
 *
 * Наследует:
 * - `AnimationSet`: Класс `AnimationSet` позволяет объединить несколько анимаций,
 *   которые будут выполняться одновременно.
 *
 * Поля:
 * - Анимация масштаба (`ScaleAnimation`): Изменяет размеры представления от `fromScale` до `toScale`.
 *   Центр масштабирования находится в середине представления (по оси X и Y).
 * - Анимация прозрачности (`AlphaAnimation`): Изменяет прозрачность представления от 0 (полностью
 *   невидимое) до 1 (полностью видимое).
 *
 * Методы:
 * - `addAnimation()`: Метод класса `AnimationSet`, который добавляет анимации в набор для их
 *   одновременного выполнения.
 *
 * Пример использования:
 * ```
 * val animation = ConversationPreviewScaleAlphaAnimation().apply {
 *     // Можно настроить анимацию, если это необходимо
 *     duration = 500
 * }
 * view.startAnimation(animation)
 * ```
 * @author da.zhukov
 */
internal object ConversationPreviewAnimatorUtils {

    fun createAnimator(
        view: View,
        durationMs: Long = 200,
        startScale: Float = 1f,
        middleScale: Float = 1.05f,
        endScale: Float = 1f
    ): Animator =
        AnimatorSet().apply {
            playSequentially(
                createUpscaleAnimator(
                    view = view,
                    durationMs = durationMs / 2L,
                    from = startScale,
                    to = middleScale
                ),
                createDownScaleAnimator(
                    view = view,
                    durationMs = durationMs / 2L,
                    from = middleScale,
                    to = endScale
                )
            )
        }

    private fun createUpscaleAnimator(
        view: View,
        durationMs: Long,
        from: Float,
        to: Float
    ): AnimatorSet {
        val interpolator = AccelerateInterpolator()
        val scaleXUp = ObjectAnimator.ofFloat(view, "scaleX", from, to).apply {
            duration = durationMs
            this.interpolator = interpolator
        }
        val scaleYUp = ObjectAnimator.ofFloat(view, "scaleY", from, to).apply {
            duration = durationMs
            this.interpolator = interpolator
        }
        return AnimatorSet().apply {
            playTogether(scaleXUp, scaleYUp)
        }
    }

    private fun createDownScaleAnimator(
        view: View,
        durationMs: Long,
        from: Float,
        to: Float
    ): AnimatorSet {
        val scaleXDown = ObjectAnimator.ofFloat(view, "scaleX", from, to).apply {
            duration = durationMs
            interpolator = AccelerateInterpolator()
        }
        val scaleYDown = ObjectAnimator.ofFloat(view, "scaleY", from, to).apply {
            duration = durationMs
            interpolator = AccelerateInterpolator()
        }
        return AnimatorSet().apply {
            playTogether(scaleXDown, scaleYDown)
        }
    }
}