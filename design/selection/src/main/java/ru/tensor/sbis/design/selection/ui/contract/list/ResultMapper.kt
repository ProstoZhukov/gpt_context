package ru.tensor.sbis.design.selection.ui.contract.list

import io.reactivex.functions.BiFunction
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.ui.di.common.FilterFunction
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.list.ListItemMapper
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.getQueryRangeList
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.ChooseAllFixedButtonViewModel
import ru.tensor.sbis.list.base.domain.entity.Mapper
import ru.tensor.sbis.list.view.section.Options
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Plain
import java.util.*

/**
 * Псевдоним для функции слияния общего списка с выбранными элементами
 */
internal typealias SelectorMergeFunction =
    BiFunction<List<SelectorItemModel>, List<SelectorItemModel>, List<SelectorItemModel>>

/**
 * Предназначен для преобразования списка элементов типа [SERVICE_RESULT] в [ListData].
 * При преобразовании значение [searchQuery] должно быть актуальным, для выделения совпадений с поисковой строкой
 *
 * @author us.bessonov
 */
internal class ResultMapper<SERVICE_RESULT>(
    private val listMapper: ListMapper<SERVICE_RESULT, SelectorItemModel>,
    private val listItemMapper: ListItemMapper,
    private val metaFactory: ItemMetaFactory,
    private val chooseAllButtonVm: ChooseAllFixedButtonViewModel?,
    /**
     * Функция, которая фильтрует элементы из списка выбранных по поисковой строке и применяет выделение к оставшимся
     */
    private val filterFunction: FilterFunction,
    /**
     * Функция, которая сливает список выбранных элементов с общим списком
     */
    private val mergeFunction: SelectorMergeFunction,
    private val recentSelectionCachingFunction: RecentSelectionCachingFunction,
    private val showDividers: Boolean = true
) : Mapper<SERVICE_RESULT> {

    /**
     * Поисковая строка. При установке применяется фильтрация списка [selection]. Результат сохраняется в
     * [filteredSelection]
     */
    var searchQuery: String = ""
        set(value) {
            field = value
            filteredSelection = filterFunction.apply(selection, value)
        }

    /**
     * Список выбранных элементов. При установке применяется фильтрация по поисковой строке [searchQuery] и сохранение
     * результата в [filteredSelection]
     */
    var selection = emptyList<SelectorItemModel>()
        set(value) {
            field = value
            filteredSelection = filterFunction.apply(value, searchQuery)
        }

    var filteredSelection = selection
        private set

    private val modelMappingCache = WeakHashMap<SERVICE_RESULT, List<SelectorItemModel>>()

    /**
     * Закэшированы ли ранее выбранные элементы, которые могут быть присутствовать в конечном списке
     */
    fun hasRecentlySelectedItems() = recentSelectionCachingFunction.hasRecentlySelectedItems()

    override fun map(from: List<SERVICE_RESULT>): ListData {
        val cachedItems = from.flatMap { result ->
            modelMappingCache.getOrPut(result) {
                listMapper(result).map(::prepareItem)
            }
        }
        val itemsIncludingRecentlySelected = recentSelectionCachingFunction.apply(
            filteredSelection,
            cachedItems,
            searchQuery.isNotEmpty()
        )
        val filteredItems = mergeFunction.apply(filteredSelection, itemsIncludingRecentlySelected)
        val listItems = filteredItems.map(listItemMapper::toItem)
        return Plain(listItems, options = Options(hasDividers = showDividers, needDrawDividerUnderFirst = true))
    }

    /**
     * Подготовка элемента для отображения
     *
     * @return элемент с заполненными атрибутами
     */
    private fun prepareItem(model: SelectorItemModel): SelectorItemModel = model.apply {
        metaFactory.attachItemMeta(model)
        // среди загруженных элементов отбирается "Выбрать все" и передаётся для хранения
        if (model.meta.handleStrategy == ClickHandleStrategy.COMPLETE_SELECTION) {
            chooseAllButtonVm?.setData(model)
        }
        model.meta.queryRanges = model.getQueryRangeList(searchQuery)
    }
}
