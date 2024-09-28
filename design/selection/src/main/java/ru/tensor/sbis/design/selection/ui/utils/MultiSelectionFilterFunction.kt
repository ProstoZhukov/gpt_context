package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.ui.di.common.FilterFunction
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Фильтрация списка выбранных элементов по вхождению поисковой строки
 *
 * @author ma.kolpakov
 */
internal class MultiSelectionFilterFunction : FilterFunction {

    override fun apply(selection: List<SelectorItemModel>, searchQuery: String): List<SelectorItemModel> {
        return if (searchQuery.isEmpty())
            /*
            Подпишемся на обнуление поисковой строки. В этот момент нужно очищать выделение у выбранных элементов
                TODO: 4/24/2020 https://online.sbis.ru/opendoc.html?guid=760800e7-561e-429d-b44b-db18132098e7
             */
            selection.onEach { it.meta.queryRanges = emptyList() }
        else
            selection
                // для каждого элемента найдём вхождение поисковой строки
                .onEach { it.meta.queryRanges = it.getQueryRangeList(searchQuery) }
                // наличие диапазона вхождения будем использовать как признак для фильтрации
                .filter { it.meta.queryRanges.isNotEmpty() }
    }
}