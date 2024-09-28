package ru.tensor.sbis.design_dialogs.movablepanel

import android.content.res.Resources
import android.view.View
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.FitToContent

/**
 * Интерфейс для взаимодействия с Behavior панели
 *
 * @author ga.malinskiy
 */
internal interface LockableBehavior {

    /**
     * Заблокированы ли движения панели (принудительно пользователем компонента)
     */
    var isBehaviorLocked: Boolean

    /**
     * Игнорировать анимацию подъема шторки, не работает с [FitToContent]
     */
    var ignoreOpenAnim: Boolean

    /**
     * Игнорирование автоблокировки скрола шторки
     */
    var ignoreLock: Boolean

    /**
     * Анимировать изменение родительского контейнера.
     */
    var animateParentHeightChanges: Boolean

    /**
     * Установка актуальной высоты панели
     */
    fun setPeekHeight(peekHeight: MovablePanelPeekHeight)

    /**
     * Получение актуальной высоты панели
     */
    fun getPeekHeight(): MovablePanelPeekHeight

    /**
     * Подсчитать значение slideOffset для конкретного peekHeight
     */
    fun calculateSlideOffsetByPeekHeight(peekHeight: MovablePanelPeekHeight, resources: Resources): Float

    /**
     * Установка возможных высот панели и инициализирующее значение (вызывается при инициализации)
     *
     * @throws IllegalArgumentException - если передать менььше 2х и больше 4х значений высоты
     */
    fun setPeekHeightList(peekHeightList: List<MovablePanelPeekHeight>, initPeekHeight: MovablePanelPeekHeight)

    /**
     * Установить колбэк на изменение состояния панели
     */
    fun setMovingCallback(callback: MovablePanelMovingCallback)

    /**
     * Аннулировать ссылку на предыдущую вью и найти новую
     */
    fun invalidateScrollingChildView()

    /**
     * Начать анимацию показа для высоты [peekHeight].
     */
    fun startShowingAnimation(peekHeight: MovablePanelPeekHeight)
}

/**
 * Коллбэк для отслеживания состояний
 *
 * @author ga.malinskiy
 */
internal interface MovablePanelMovingCallback {
    /**
     * Уведомить об обновлении высоты
     *
     * @see MovablePanelPeekHeight
     */
    fun onHeightChanged(view: View, state: MovablePanelPeekHeight)

    /**
     * Вызывается при STATE_DRAGGING
     */
    fun onSlide(view: View, slideOffset: Float)

    /**
     * Получить вью активной страницы пейджера
     */
    fun getPagerCurrentView(): View?

    /**
     * При наличии конфликта скроллящихся вью на экране, вручную поставить текущий.
     */
    fun getScrollableView(): View?
}