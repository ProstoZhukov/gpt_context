package ru.tensor.sbis.design.buttons.round.animation

import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.round.api.SbisRoundButtonAnimation
import ru.tensor.sbis.design.buttons.round.animation.fade.FadeAnimator
import ru.tensor.sbis.design.buttons.round.animation.rotation.RotationAnimationParams
import ru.tensor.sbis.design.buttons.round.animation.rotation.RotationAnimator

/**
 * Класс контроллера для вызова различных анимаций у круглой кнопки.
 *
 * @author ra.geraskin
 */
internal class AnimationController : SbisRoundButtonAnimation {

    /**
     * Аниматор вращения кнопки (влияет на параметры: rotation, elevation, background).
     */
    private lateinit var rotationAnimator: RotationAnimator

    /**
     * Аниматор исчезновения и появления (влияет на параметры: scale, visibility).
     */
    private lateinit var fadeAnimator: FadeAnimator

    /**
     * @SelfDocumented
     */
    internal fun attach(button: SbisRoundButton) {
        rotationAnimator = RotationAnimator(button)
        fadeAnimator = FadeAnimator(button)
    }

    /**
     * @SelfDocumented
     */
    override fun show(animated: Boolean) = fadeAnimator.show(animated)

    /**
     * @SelfDocumented
     */
    override fun hide(animated: Boolean) = fadeAnimator.hide(animated)

    /**
     * @SelfDocumented
     */
    override fun rotateForward() = rotationAnimator.rotateWithAnimFront()

    /**
     * @SelfDocumented
     */
    override fun rotateBack() = rotationAnimator.rotateWithAnimBack()

    /**
     * @SelfDocumented
     */
    override fun resetRotation() = rotationAnimator.reset()

    /**
     * @SelfDocumented
     */
    override fun setRotationAnimationParams(
        startParams: RotationAnimationParams?,
        endParams: RotationAnimationParams?,
        duration: Long?
    ) = rotationAnimator.setParams(startParams, endParams, duration)
}