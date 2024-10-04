package ru.tensor.sbis.design.buttons.round.animation.rotation

/**
 * Класс парамеров для описания начального и конечного состояния кнопки (до поворота и после поворота).
 *
 * @author ra.geraskin
 */
data class RotationAnimationParams(

    /**
     * Цвет фона кнопки.
     */
    val backgroundColor: Int,

    /**
     * Угол поворота.
     */
    val rotation: Float,

    /**
     * Подъём кнопки над родителем (системный параметр).
     */
    val elevation: Float
)