package ru.tensor.sbis.design.toolbar.appbar.background

import android.util.DisplayMetrics
import android.view.View

private const val DEFAULT_ASPECT_RATIO = 1f

/**
 * Функция для обработки изменения соотношения сторон
 *
 * @author ma.kolpakov
 */
internal class AspectRatioChangeListener(
    private val view: View,
    private val backgroundView: View
) {

    private val displayMetrics = DisplayMetrics()
    private var latestRatio = DEFAULT_ASPECT_RATIO

    /**
     * Нужно ли обрабатывать изменения соотношения сторон
     */
    var isEnabled = true

    /**
     * Применяет соотношение сторон [aspectRatio] при изменении значения, если [isEnabled] истинно
     */
    fun onAspectRatioChanged(aspectRatio: Float) {
        latestRatio = aspectRatio
        if (isEnabled) {
            applyAspectRatio(aspectRatio)
        }
    }

    /**
     * Выполняет применение последнего доступного значения соотношения сторон, если [isEnabled] истинно
     */
    fun invokeWithCurrentAspectRatio() {
        if (isEnabled) {
            applyAspectRatio(latestRatio)
        }
    }

    private fun applyAspectRatio(aspectRatio: Float) {
        val layoutParams = view.layoutParams ?: return
        view.display?.getMetrics(displayMetrics) ?: return
        val height = (displayMetrics.widthPixels / aspectRatio).toInt()
            .coerceAtMost(displayMetrics.heightPixels)
        layoutParams.height = height
        backgroundView.layoutParams?.height = height
        view.requestLayout()
    }
}