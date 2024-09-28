package ru.tensor.sbis.design_selection.contract.customization.selected.panel.listener

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Слушатель кликов по невыбранным элементам списка компонента выбора.
 *
 * @author vv.chekurda
 */
internal class SelectedItemClickListener<ITEM : SelectionItem> {

    private val _onUnselectClicked = PublishSubject.create<Pair<ITEM, Boolean>>()

    /**
     * Для подписки на события клика для отмены выбранного элемента.
     */
    val onUnselectClicked: Observable<Pair<ITEM, Boolean>> = _onUnselectClicked

    /**
     * Обработать клик для отмены выбранного элемента [item].
     */
    fun onUnselectClicked(item: SelectionItem, animate: Boolean = true) {
        @Suppress("UNCHECKED_CAST")
        _onUnselectClicked.onNext((item as ITEM) to animate)
    }
}