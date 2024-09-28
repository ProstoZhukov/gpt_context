package ru.tensor.sbis.viper.arch.router

import ru.tensor.sbis.design.R as RDesign

/**
 * Перечисление типов анимаций открытия экранов
 */
enum class ChangeScreenAnimationType {

    /**
     * Анимация пролистывания вперед
     */
    FORWARD,

    /**
     * Анимация пролистывания назад
     */
    BACKWARD,

    /**
     * Без анимации
     */
    STATIC;

    /**
     * Метод, возвращающий анимацию по элементу enum
     */
    fun toAnimation(): FragmentTransactionCustomAnimations =
        when (this) {
            FORWARD -> FragmentTransactionCustomAnimations(
                RDesign.anim.slide_in_from_right,
                RDesign.anim.slide_out_to_left
            )

            BACKWARD -> FragmentTransactionCustomAnimations(
                RDesign.anim.slide_in_from_left,
                RDesign.anim.slide_out_to_right
            )

            STATIC -> FragmentTransactionCustomAnimations()
        }

    companion object {

        /**
         * Метод, возвращающий enum по текущей позиции и предыдущей
         */
        fun getAnimationByPosition(position: Int, previousPosition: Int): ChangeScreenAnimationType =
            when {
                position > previousPosition -> FORWARD
                position < previousPosition -> BACKWARD
                else -> STATIC
            }
    }
}