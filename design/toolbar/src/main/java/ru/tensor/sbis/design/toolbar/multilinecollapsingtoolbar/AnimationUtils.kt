package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar

import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.math.MathUtils
import kotlin.math.roundToInt

/**
 * Вспомогательный класс для хранения параметров анимации.
 *
 * @author us.bessonov
 */
internal data class AnimationInfo(
    var startValue: Float = 0f,
    var targetValue: Float = 0f,
    var startFraction: Float = 0f
)

/**
 * Утилиты для анимаций
 *
 * @author us.bessonov
 */
internal object AnimationUtils {

    @JvmField
    val FAST_OUT_SLOW_IN_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()

    @JvmField
    val FAST_OUT_LINEAR_IN_INTERPOLATOR: Interpolator = FastOutLinearInInterpolator()

    @JvmField
    val LINEAR_OUT_SLOW_IN_INTERPOLATOR: Interpolator = LinearOutSlowInInterpolator()

    @JvmField
    val DECELERATE_INTERPOLATOR: Interpolator = DecelerateInterpolator()

    @JvmField
    val ACCELERATE_INTERPOLATOR: Interpolator = AccelerateInterpolator()

    /**
     * Linear interpolation between `startValue` and `endValue` by `fraction`.
     */
    @JvmStatic
    fun lerp(startValue: Float, endValue: Float, fraction: Float): Float {
        return startValue + fraction * (endValue - startValue)
    }

    @JvmStatic
    fun lerp(startValue: Int, endValue: Int, fraction: Float, interpolator: Interpolator?): Int {
        val actualFraction = interpolator
            ?.let { interpolator.getInterpolation(fraction) }
            ?: fraction
        return startValue + (actualFraction * (endValue - startValue)).roundToInt()
    }
}

internal fun updateValue(
    isAnimating: Boolean,
    isReverse: Boolean,
    lastOffset: Float,
    animatedFraction: Float,
    animationInfo: AnimationInfo,
    getTargetValue: (appBarOffset: Float) -> Float,
    getCurrentValue: () -> Float
): Float {
    return if (isAnimating) {
        val newTargetValue = getTargetValue(lastOffset)
        val fraction = if (!isReverse && newTargetValue != animationInfo.startValue) {
            animationInfo.startValue = getCurrentValue()
            animationInfo.targetValue = newTargetValue
            val startFraction = animationInfo.startFraction
            (animatedFraction - startFraction) / (1f - startFraction)
        } else {
            animatedFraction
        }
        MathUtils.lerp(animationInfo.startValue, animationInfo.targetValue, fraction)
    } else {
        getTargetValue(lastOffset)
    }
}
