package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.collapsingtext

import android.graphics.Color
import android.view.animation.Interpolator
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.AnimationUtils
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.AnimationUtils.lerp
import ru.tensor.sbis.design.toolbar.util.collapsingimage.MEDIATE_APPBAR_OFFSET_THRESHOLD

private val POSITION_INTERPOLATOR: Interpolator? = null
private val TEXT_SIZE_INTERPOLATOR = AnimationUtils.DECELERATE_INTERPOLATOR
private val TEXT_BLEND_INTERPOLATOR = AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR
private val SHADOW_INTERPOLATOR: Interpolator? = null
private const val MEDIATE_STATE_OFFSET = 1 - MEDIATE_APPBAR_OFFSET_THRESHOLD

internal fun interpolateBounds(
    currentState: CollapsingTextState,
    expandedState: CollapsingTextState,
    collapsedState: CollapsingTextState,
    fraction: Float
) {
    currentState.bounds.left = lerp(
        expandedState.bounds.left, collapsedState.bounds.left,
        fraction, POSITION_INTERPOLATOR
    )
    currentState.bounds.top = lerp(
        expandedState.titleState.y, collapsedState.titleState.y,
        fraction, POSITION_INTERPOLATOR
    )
    currentState.bounds.right = lerp(
        expandedState.bounds.right, collapsedState.bounds.right,
        fraction, POSITION_INTERPOLATOR
    )
    currentState.bounds.bottom = lerp(
        expandedState.bounds.bottom, collapsedState.bounds.bottom,
        fraction, POSITION_INTERPOLATOR
    )
}

internal fun getLerpPositionX(titleStates: TitleStates, fraction: Float) = with(titleStates) {
    lerp(expanded.x, collapsed.x, fraction, POSITION_INTERPOLATOR)
}

internal fun lerpPositionX(titleStates: TitleStates, fraction: Float) = with(titleStates) {
    current.x = getLerpPositionX(titleStates, fraction)
}

internal fun getLerpPositionY(titleStates: TitleStates, fraction: Float) = with(titleStates) {
    lerp(expanded.y, collapsed.y, fraction, POSITION_INTERPOLATOR)
}

internal fun lerpPositionY(titleStates: TitleStates, fraction: Float) = with(titleStates) {
    current.y = getLerpPositionY(titleStates, fraction)
}

internal fun lerpTitleSize(titleStates: TitleStates, fraction: Float) = with(titleStates) {
    lerp(expanded.size, mediate?.size, collapsed.size, fraction, TEXT_SIZE_INTERPOLATOR)
}

internal fun lerpCollapsedTextBlend(hasMediateState: Boolean, fraction: Float) = lerp(
    0f,
    if (hasMediateState) 1f else null,
    1f,
    fraction,
    TEXT_BLEND_INTERPOLATOR
)

internal fun lerpExpandedTextBlend(hasMediateState: Boolean, fraction: Float) = lerp(
    1f,
    if (hasMediateState) 0f else null,
    0f,
    fraction,
    TEXT_BLEND_INTERPOLATOR
)

internal fun lerpShadowValues(
    titleStates: TitleStates,
    fraction: Float
) = with(titleStates) {
    val collapsedShadow = collapsed.shadow
    val mediateShadow = mediate?.shadow
    val expandedShadow = expanded.shadow
    val currentShadow = current.shadow
    current.shadow.radius = lerp(
        expandedShadow.radius,
        mediateShadow?.radius,
        collapsedShadow.radius,
        fraction,
        SHADOW_INTERPOLATOR
    )
    currentShadow.dx = lerp(
        expandedShadow.dx,
        mediateShadow?.dx,
        collapsedShadow.dx,
        fraction,
        SHADOW_INTERPOLATOR
    )
    currentShadow.dy = lerp(
        expandedShadow.dy,
        mediateShadow?.dy,
        collapsedShadow.dy,
        fraction,
        SHADOW_INTERPOLATOR
    )
    currentShadow.color = blendColors(expanded.shadow.color, mediateShadow?.color, collapsedShadow.color, fraction)
}

internal fun blendColors(color1: Int, color2: Int?, color3: Int, ratio: Float): Int {
    return when {
        color2 == null -> blendColors(color1, color3, ratio)
        ratio < MEDIATE_STATE_OFFSET -> blendColors(color1, color2, getPartialFraction(ratio))
        else -> blendColors(color2, color3, getPartialFraction(ratio))
    }
}

internal fun lerp(
    startValue: Float,
    endValue: Float,
    fraction: Float,
    interpolator: Interpolator?
): Float {
    val interpolated = interpolator?.getInterpolation(fraction) ?: fraction
    return lerp(startValue, endValue, interpolated)
}

/**
 * Blend `color1` and `color2` using the given ratio.
 *
 * @param ratio of which to blend. 0.0 will return `color1`, 0.5 will give an even blend,
 * 1.0 will return `color2`.
 */
private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
    val inverseRatio = 1f - ratio
    val a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio
    val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
    val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
    val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

private fun lerp(
    startValue: Float,
    mediateValue: Float?,
    endValue: Float,
    fraction: Float,
    interpolator: Interpolator?
) = when {
    mediateValue == null -> lerp(startValue, endValue, fraction, interpolator)
    fraction < MEDIATE_STATE_OFFSET -> lerp(startValue, mediateValue, getPartialFraction(fraction), interpolator)
    else -> lerp(mediateValue, endValue, getPartialFraction(fraction), interpolator)
}

private fun getPartialFraction(fraction: Float) = if (fraction < MEDIATE_STATE_OFFSET) {
    fraction / MEDIATE_STATE_OFFSET
} else {
    (fraction - MEDIATE_STATE_OFFSET) / (1 - MEDIATE_STATE_OFFSET)
}
