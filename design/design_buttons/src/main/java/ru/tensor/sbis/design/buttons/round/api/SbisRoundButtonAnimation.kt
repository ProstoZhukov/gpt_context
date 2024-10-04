package ru.tensor.sbis.design.buttons.round.api

import ru.tensor.sbis.design.buttons.round.animation.rotation.RotationAnimationParams

/**
 * Контракт для вызова различных анимаций у круглой кнопки.
 *
 * @author ra.geraskin
 */
interface SbisRoundButtonAnimation {

    /**
     * Спрятать кнопку с плавным уменьшением фактического размера кнопки до 0.
     */
    fun hide(animated: Boolean = false)

    /**
     * Показать кнопку с плавным увеличением её размера до первоначального значения.
     */
    fun show(animated: Boolean = false)

    /**
     * Повернуть кнопку кнопки со сменой цвета фона.
     */
    fun rotateForward()

    /**
     * Повернуть кнопку в обратную сторону со сменой цвета фона кнопки в первоначальное значение.
     */
    fun rotateBack()

    /**
     * Вернуть кнопку в первоначальное состояние (до поворота) без анимации.
     */
    fun resetRotation()

    /**
     * Установить начальные и конченые параметры кнопки и длительность анимации.
     * Значение аргумента == null будет заменено параметром по умолчанию.
     */
    fun setRotationAnimationParams(
        startParams: RotationAnimationParams? = null,
        endParams: RotationAnimationParams? = null,
        duration: Long? = null
    )
}