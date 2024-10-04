/**
 * Набор фабричных методов для создания компонента выбора
 *
 * @author ma.kolpakov
 */
@file:JvmName("SelectorFragmentFactory")
@file:JvmMultifileClass

package ru.tensor.sbis.design.selection.ui.factories

import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionListener
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.CounterFormat

internal const val SELECTOR_COMPLETE_LISTENER = "SELECTOR_COMPLETE_LISTENER"
internal const val SELECTOR_CANCEL_LISTENER = "SELECTOR_CANCEL_LISTENER"
internal const val SELECTOR_ENABLE_SWIPE_BACK = "SELECTOR_ENABLE_SWIPE_BACK"

internal const val MINIMAL_COUNT_FOR_MULTI_SELECTION = 2L

/**
 * Ограничение по умолчанию на выбор элементов
 */
const val DEFAULT_SELECTION_LIMIT = 50

/**
 * Создаёт фрагмент экрана одиночного выбора
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param itemHandleStrategy пользовательский обработчик кликов
 */
@JvmOverloads
@Deprecated(
    "TODO: 6/18/2020 https://online.sbis.ru/opendoc.html?guid=2176cfee-a003-48e0-a646-9a5f5d2a1be5",
    replaceWith = ReplaceWith("createSingleRegionSelector")
)
fun <SERVICE_RESULT, DATA : SelectorItemModel, FILTER, ACTIVITY : FragmentActivity> createSingleSelectorFragment(
    listDependenciesFactory: SingleSelectionListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, Int>,
    completeListener: SelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    selectorStrings: SelectorStrings,
    itemHandleStrategy: SelectorItemHandleStrategy<DATA>? = null
): Fragment = TODO("https://online.sbis.ru/opendoc.html?guid=365bb302-62b6-4f70-9571-6fbaaec529ed")

/**
 * Создаёт фрагмент экрана множественного выбора
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param itemHandleStrategy пользовательский обработчик кликов
 * @param limit максимально возможное количество элементов для выбора
 */
@Deprecated(
    "TODO: 6/18/2020 https://online.sbis.ru/opendoc.html?guid=2176cfee-a003-48e0-a646-9a5f5d2a1be5",
    replaceWith = ReplaceWith("createMultiRegionSelector")
)
@JvmOverloads
fun <SERVICE_RESULT, DATA : SelectorItemModel, FILTER, ACTIVITY : FragmentActivity> createMultiSelectorFragment(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, Int>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    selectorStrings: SelectorStrings,
    itemHandleStrategy: SelectorItemHandleStrategy<DATA>? = null,
    format: CounterFormat = CounterFormat.DEFAULT,
    @IntRange(from = MINIMAL_COUNT_FOR_MULTI_SELECTION)
    limit: Int = DEFAULT_SELECTION_LIMIT
): Fragment = TODO("https://online.sbis.ru/opendoc.html?guid=365bb302-62b6-4f70-9571-6fbaaec529ed")

/**
 * Создаёт фрагмент экрана множественного выбора иерархических данных
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param itemHandleStrategy пользовательский обработчик кликов
 * @param limit максимально возможное количество элементов для выбора
 */
@JvmOverloads
@JvmName("createHierarchyMultiSelectorFragment")
@Deprecated(
    "TODO: 6/18/2020 https://online.sbis.ru/opendoc.html?guid=2176cfee-a003-48e0-a646-9a5f5d2a1be5",
    replaceWith = ReplaceWith("createMultiHierarchicalRegionSelector")
)
fun <SERVICE_RESULT, DATA : HierarchySelectorItemModel, FILTER, ACTIVITY : FragmentActivity>
createMultiSelectorFragment(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, Int>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    selectorStrings: SelectorStrings,
    itemHandleStrategy: SelectorItemHandleStrategy<DATA>? = null,
    @IntRange(from = MINIMAL_COUNT_FOR_MULTI_SELECTION)
    limit: Int = DEFAULT_SELECTION_LIMIT
): Fragment = TODO("https://online.sbis.ru/opendoc.html?guid=365bb302-62b6-4f70-9571-6fbaaec529ed")