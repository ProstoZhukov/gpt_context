package ru.tensor.sbis.design.selection.ui.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.selection.ui.di.common.FilterFunction
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Выполняет фильтрацию выбранных элементов для заданного поискового запроса
 *
 * @author us.bessonov
 */
internal class FilteredSelection(source: Observable<List<SelectorItemModel>>, private val filter: FilterFunction) {

    private var lastSelection = emptyList<SelectorItemModel>()
    private var lastSelectionPublisher = PublishSubject.create<List<SelectorItemModel>>()
    private var query = ""

    /**
     * [Observable] со списком выбранных элементов, учитывающий текущий поисковый запрос.
     * Список обновляется при изменении состава элементов, либо при изменении поискового запроса
     */
    val selection: Observable<List<SelectorItemModel>> = source
        .doOnNext { lastSelection = it }
        .mergeWith(lastSelectionPublisher)
        .map { filter.apply(it, query) }

    /**
     * Задаёт текст поискового запроса
     */
    fun setQuery(query: String) {
        /*
        Не обновляем список выбранных, если ожидается изменение состояния панели с видимого на скрытое.
        Фактически список выбранных обновится после обновления списка или видимости заглушки.
        Обновление выбранных до обновления списка позволяет избежать рассинхрона изменения видимости
         */
        val shouldUpdate = filter.apply(lastSelection, this.query).isEmpty() ||
            filter.apply(lastSelection, query).isNotEmpty()
        this.query = query
        if (shouldUpdate) publishLastSelection()
    }

    /** @SelfDocumented */
    fun onListUpdated() {
        publishLastSelection()
    }

    /** @SelfDocumented */
    fun onStubVisibilityUpdated() {
        publishLastSelection()
    }

    private fun publishLastSelection() = lastSelectionPublisher.onNext(lastSelection)
}