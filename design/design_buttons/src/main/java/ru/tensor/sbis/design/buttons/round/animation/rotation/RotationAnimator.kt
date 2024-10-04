package ru.tensor.sbis.design.buttons.round.animation.rotation

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import androidx.core.animation.addListener
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.style.NavigationButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.NavigationColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Аниматор вращения кнопки со сменой цвета фона кнопки.
 * [По задаче](https://dev.sbis.ru/opendoc.html?guid=55b779ca-ea4a-4efd-8eca-50bd87f34492&client=3)
 *
 * @author ra.geraskin
 */
internal class RotationAnimator(private val button: SbisRoundButton) {

    /**
     * Стартовые параметры кнопки перед анимацией по умолчанию.
     */
    private var startParams = RotationAnimationParams(
        backgroundColor = NavigationColor.BACKGROUND_BUTTON.getValue(button.context),
        rotation = 0f,
        elevation = button.elevation
    )

    /**
     * Конечные параметры кнопки после выполнения анимации по умолчанию.
     */
    private var endParams = RotationAnimationParams(
        backgroundColor = StyleColor.UNACCENTED.getColor(button.context),
        rotation = ANIMATION_ROTATION_ANGLE,
        elevation = 0f
    )

    private var rotationDuration = ANIMATION_DURATION_MS

    private val fractionStyle: SbisButtonCustomStyle by lazy {
        SbisButtonCustomStyle(
            iconStyle = SbisButtonIconStyle(ColorStateList.valueOf(IconColor.CONTRAST.getValue(button.context)))
        )
    }

    private var currentAnimation: ValueAnimator? = null

    /**
     * Установить стартовый и конечный параметры кнопки, а так же длительность анимации.
     * Если параметры не будут установлены, то будут использоваться параметры по умолчанию.
     */
    internal fun setParams(
        startParams: RotationAnimationParams? = this.startParams,
        endParams: RotationAnimationParams? = this.endParams,
        duration: Long? = ANIMATION_DURATION_MS
    ) {
        startParams?.let { this.startParams = it }
        endParams?.let { this.endParams = it }
        duration?.let { rotationDuration = it }
    }

    /**
     * Повернуть кнопку с анимацией.
     */
    internal fun rotateWithAnimFront() {
        currentAnimation?.cancel()
        currentAnimation = ValueAnimator.ofObject(RotationAnimationEvaluator(), startParams, endParams).apply {
            addUpdateListener { updateButton(it.animatedValue as RotationAnimationParams) }
            addListener(
                onEnd = { currentAnimation = null }
            )
            duration = rotationDuration
            start()
        }
    }

    /**
     * Вернуть кнопку в первоначальное состояние с анимацией.
     */
    internal fun rotateWithAnimBack() {
        currentAnimation?.cancel()
        currentAnimation = ValueAnimator.ofObject(RotationAnimationEvaluator(), endParams, startParams).apply {
            addUpdateListener { updateButton(it.animatedValue as RotationAnimationParams) }
            duration = rotationDuration
            addListener(
                onEnd = {
                    currentAnimation = null
                    reset()
                }
            )
            start()
        }
    }

    /**
     * Сбросить текущую анимацию и вернуть кнопку в первоначальное состояние.
     */
    internal fun reset() {
        currentAnimation?.cancel()
        currentAnimation = null

        button.style = NavigationButtonStyle
        button.rotation = 0f
        button.elevation = startParams.elevation
    }

    private fun updateButton(params: RotationAnimationParams) = with(params) {
        button.style = fractionStyle.copy(
            backgroundColors = ColorStateList(arrayOf(intArrayOf()), intArrayOf(backgroundColor))
        )
        button.rotation = rotation
        button.elevation = elevation
        button.invalidate()
    }

}

private const val ANIMATION_ROTATION_ANGLE = -180f * 3 / 4
private const val ANIMATION_DURATION_MS = 400L