package ru.tensor.sbis.design_selection.ui.content.listener

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design_selection.ui.content.listener.SelectionItemClickListener.ClickType.*
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Слушатель кликов по элементам списка невыбранных.
 *
 * @author vv.chekurda
 */
internal class SelectionItemClickListener<ITEM : SelectionItem> {

    /**
     * Тип клика по элементу.
     */
    enum class ClickType {
        /**
         * Добавление к списку выбранных.
         */
        ADD,

        /**
         * Клик по самой ячейкке.
         */
        CLICK,

        /**
         * Долгий клик по самой ячейке.
         */
        LONG_CLICK,

        /**
         * Клик для навигации по ячейке.
         */
        NAVIGATE
    }

    private val clickSubject = PublishSubject.create<Pair<ITEM, ClickType>>()

    /**
     * События кликов по элементам в формате пары: элемент + тип клика.
     */
    val clickEvent: Observable<Pair<ITEM, ClickType>> = clickSubject

    /**
     * Обработать нажатие на кнопку добавления элемента к списку выбранных.
     */
    fun onAddButtonClicked(item: ITEM) {
        clickSubject.onNext(item to ADD)
    }

    /**
     * Обработать клик на саму ячейку.
     */
    fun onItemClicked(item: ITEM) {
        clickSubject.onNext(item to CLICK)
    }

    /**
     * Обработать долгий клик на саму ячейку.
     */
    fun onItemLongClicked(item: ITEM) {
        clickSubject.onNext(item to LONG_CLICK)
    }

    /**
     * Обработать клик, по которому начинается навигация.
     */
    fun onNavigateClicked(item: ITEM) {
        clickSubject.onNext(item to NAVIGATE)
    }
}