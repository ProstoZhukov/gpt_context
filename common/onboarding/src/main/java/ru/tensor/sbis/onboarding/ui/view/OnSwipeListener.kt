package ru.tensor.sbis.onboarding.ui.view

import androidx.viewpager.widget.ViewPager

/**
 * Интерфейс слушателя для мониторинга событий свайпа по фичам на экране онбординга
 */
internal interface OnSwipeListener {

    /**
     * Событие свайпа вперед
     *
     * @param leavePosition текущая покидаемая позиция
     * @param deferredSwipeAction отложенное действие выполняемое для прерванного свайпа внешним потребителем
     * @return true если событие свайпа прерывается потребителем, иначе false
     */
    fun onSwipeForward(
        leavePosition: Int,
        deferredSwipeAction: ViewPager.() -> Unit
    ): Boolean

    /**
     * Событие свайпа вперед за область последней фичи
     *
     * @param deferredSwipeAction отложенное действие выполняемое для прерванного свайпа внешним потребителем
     * @return true если событие свайпа прерывается потребителем, иначе false
     */
    fun onSwipeOutAtEnd(deferredSwipeAction: () -> Unit)

    /** Событие свайпа вперед */
    fun onSwipeBack()

    /** Событие свайпа вперед за область первой фичи */
    fun onSwipeOutAtStart()
}