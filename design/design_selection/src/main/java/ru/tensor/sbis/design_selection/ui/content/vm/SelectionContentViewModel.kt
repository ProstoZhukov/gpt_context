package ru.tensor.sbis.design_selection.ui.content.vm

import io.reactivex.Observable
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter

/**
 * Вью-модель области контента компонента выбора.
 *
 * @author vv.chekurda
 */
internal interface SelectionContentViewModel<ITEM : SelectionItem> {

    /**
     * Подписка на клик по отмене выбранного элемента.
     */
    val onUnselectClicked: Observable<Pair<ITEM, Boolean>>

    /**
     * Выбрать элемент.
     *
     * @param item элемент для выбора.
     * @param append true, если необходимо явно добавить элемент к списку выбранных.
     */
    fun select(item: ITEM, append: Boolean)

    /**
     * Отменить выбранный элемент [item].
     */
    fun unselect(item: ITEM, animate: Boolean = true)

    /**
     * Обработать клик по заголовку папки текущего списка.
     */
    fun onFolderTitleClicked()

    /**
     * Установить роутера для навигации [router].
     */
    fun setRouter(router: SelectionRouter?)

    /**
     * Изменилась видимость текущего контента в иерархии.
     */
    fun onContentVisibilityChanged(isVisible: Boolean)

    /**
     * Обновить список невыбранных элементов.
     */
    fun reset()
}