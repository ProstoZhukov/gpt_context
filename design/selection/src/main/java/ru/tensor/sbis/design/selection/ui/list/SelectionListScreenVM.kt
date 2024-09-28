package ru.tensor.sbis.design.selection.ui.list

import io.reactivex.Observable
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.FixedButtonViewModel
import ru.tensor.sbis.list.base.presentation.ListScreenVM

/**
 * @author ma.kolpakov
 */
internal interface SelectionListScreenVM : ListScreenVM, FixedButtonViewModel<Any> {

    val itemClicked: Observable<SelectorItemModel>

    fun onItemClicked(item: SelectorItemModel)

    /**
     * Обновление списка в соответствии с поисковой строкой
     */
    fun refresh(searchQuery: String)

    /**
     * Перезагрузка данных с существующим фильтром
     */
    fun reload()

    /**
     * Обновление списка в соответствии с пользовательским выбором
     */
    fun onSelectionChanged(selection: List<SelectorItemModel>)

    /**
     * Оповещает [SelectionListScreenVM] о том, что с компонентом выбора закончена работа и можно игнорировать запросы
     * на обновление. Важно доставить событие завершения так скоро, как это возможно
     */
    fun onSelectionCompleted()
}