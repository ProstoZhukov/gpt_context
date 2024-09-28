package ru.tensor.sbis.design.buttons.round.animation.rotation

import android.animation.ArgbEvaluator
import android.animation.TypeEvaluator

/**
 * Класс evaluator, выполняющий "пофрэймовое" вычисление значений анимируемых параметров.
 *
 * @author ra.geraskin
 */
internal class RotationAnimationEvaluator : TypeEvaluator<RotationAnimationParams> {

    private val colorEvaluator = ArgbEvaluator()

    /**
     * @SelfDocumented
     */
    override fun evaluate(
        fraction: Float,
        start: RotationAnimationParams,
        end: RotationAnimationParams
    ): RotationAnimationParams {
        val fractionColor = colorEvaluator.evaluate(fraction, start.backgroundColor, end.backgroundColor) as Int
        val fractionRotation = start.rotation + fraction * (end.rotation - start.rotation)
        val fractionElevation = start.elevation + fraction * (end.elevation - start.elevation)
        return RotationAnimationParams(fractionColor, fractionRotation, fractionElevation)
    }
}