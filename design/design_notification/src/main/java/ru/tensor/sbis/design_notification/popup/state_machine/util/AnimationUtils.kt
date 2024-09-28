package ru.tensor.sbis.design_notification.popup.state_machine.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

private const val ANIMATION_DURATION = 150L
private val INTERPOLATOR = FastOutSlowInInterpolator()

/**
 * Предназначен для анимированного отображения панели, с возможностью указания события по окончании анимации
 *
 * @author us.bessonov
 */
internal class ShowViewAction(var onShown: () -> Unit) {

    /** @SelfDocumented */
    fun showViews(views: List<View>): Animator? = animateTranslation(views,
        getStartValue = { -it.height.toFloat() },
        getEndValue = { 0f }) {
        onShown()
    }
}

/**
 * Предназначен для анимированного скрытия панели, с возможностью указания события по окончании анимации
 *
 * @author us.bessonov
 */
internal class HideViewAction(var onHidden: () -> Unit) {

    /** @SelfDocumented */
    fun hideViews(views: List<View>, externalOnHiddenAction: () -> Unit): Animator? = animateTranslation(views,
        getStartValue = { it.translationY },
        getEndValue = { -it.height.toFloat() }) {
        onHidden()
        externalOnHiddenAction()
    }
}

private fun animateTranslation(
    views: List<View>,
    getStartValue: (View) -> Float,
    getEndValue: (View) -> Float,
    onEnd: () -> Unit
): Animator? {
    val firstView = views.firstOrNull() ?: return null
    val startValue = getStartValue(firstView)
    val endValue = getEndValue(firstView)
    views.forEach {
        it.translationY = startValue
    }
    return ValueAnimator.ofFloat(startValue, endValue)
        .apply {
            interpolator = INTERPOLATOR
            duration = ANIMATION_DURATION
            addUpdateListener { animator ->
                val translation = animator.animatedValue as Float
                views.forEach {
                    it.translationY = translation
                }
            }
            addListener(onEnd = {
                onEnd()
            })
            start()
        }
}

