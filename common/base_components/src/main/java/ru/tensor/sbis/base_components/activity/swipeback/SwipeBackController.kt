package ru.tensor.sbis.base_components.activity.swipeback

import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import kotlin.math.min

/**
 * Контроллер свайпа.
 *
 * @author kv.martyshenko
 */
class SwipeBackController(
    val swipeBackLayout: SwipeBackLayout,
    val shadow: ImageView
) {
    private val swipeAnimationInterpolator: Interpolator by lazy { AccelerateInterpolator() }

    /** @SelfDocumented */
    fun animateSwipe(screenFraction: Float) {
        val fraction = min((screenFraction / SWIPE_ANIMATION_WIDTH_RANGE).toDouble(), 1.0)
                .toFloat()
        val interpolation = swipeAnimationInterpolator.getInterpolation(fraction)
        val alpha = 1 - interpolation
        swipeBackLayout.setAlpha(alpha)
        swipeBackLayout.setAlpha(alpha)
    }

    companion object {
        private const val SWIPE_ANIMATION_WIDTH_RANGE: Float = 0.75f
    }
}