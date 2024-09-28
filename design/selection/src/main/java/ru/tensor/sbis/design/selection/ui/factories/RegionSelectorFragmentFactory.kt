/**
 * Набор фабричных методов для создания компонента выбора региона
 *
 * @author ma.kolpakov
 */
@file:JvmName("SelectorFragmentFactory")
@file:JvmMultifileClass
/*
Типы стираются при сериализации, их безопасность обеспечена замкнутостью системы
 */
@file:Suppress("UNCHECKED_CAST")

package ru.tensor.sbis.design.selection.ui.factories

import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubContentProvider
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionListener
import ru.tensor.sbis.design.selection.ui.fragment.MultiSelectorFragment
import ru.tensor.sbis.design.selection.ui.fragment.single.SingleSelectorFragment
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.*
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonType
import ru.tensor.sbis.design.selection.ui.utils.stub.DefaultSelectorStubContentProvider
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase.UNKNOWN

private val DEFAULT_REGION_MULTI_SELECTOR_STRINGS = SelectorStrings(
    allSelectedTitle = R.string.selection_all_regions_selected_title
)

private const val REGION_SEARCH_QUERY_MIN_LENGTH = 2

/**
 * Создаёт фрагмент экрана одиночного выбора региона
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param itemHandleStrategy пользовательский обработчик кликов
 * @param format форматирование счётчика [RegionSelectorItemModel.counter]
 * @param needCloseButton нужно ли отображать кнопку "Закрыть" (стрелочка). Актуально для отображения выбора в диалоге
 * @param themeRes тема внешнего вида компонента
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 * @param showDividers должны ли отображаться разделители
 * @param stubContentProvider поставщик данных для заглушек. Если не указан, используется реализация по умолчанию
 * [DefaultSelectorStubContentProvider] на основе параметров из [selectorStrings].
 */
@JvmOverloads
fun <SERVICE_RESULT, DATA : RegionSelectorItemModel, FILTER, ACTIVITY : FragmentActivity, ANCHOR>
createSingleRegionSelector(
    listDependenciesFactory: SingleSelectionListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, ANCHOR>,
    completeListener: SelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    selectorStrings: SelectorStrings,
    itemHandleStrategy: SelectorItemHandleStrategy<DATA>? = null,
    format: CounterFormat = CounterFormat.DEFAULT,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme,
    isSwipeBackEnabled: Boolean = false,
    showDividers: Boolean = true,
    searchQueryMinLength: Int = REGION_SEARCH_QUERY_MIN_LENGTH,
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    case: SelectionStatisticUseCase = UNKNOWN,
): Fragment = SingleSelectorFragment().apply {
    arguments = Bundle().apply {
        isMultiSelection = false
        counterFormat = format
        this.needCloseButton = needCloseButton
        this.themeRes = themeRes
        enableHeaderShadow = true
        this.searchQueryMinLength = searchQueryMinLength

        singleDependenciesFactory =
            listDependenciesFactory as SingleSelectionListDependenciesFactory<Any, SelectorItemModel, Any, Any>

        this.selectorStrings = selectorStrings

        putSerializable(SELECTOR_COMPLETE_LISTENER, completeListener)
        putSerializable(SELECTOR_CANCEL_LISTENER, cancelListener)

        itemHandleStrategy?.let {
            this.itemHandleStrategy = it as SelectorItemHandleStrategy<SelectorItemModel>
        }

        putBoolean(SELECTOR_ENABLE_SWIPE_BACK, isSwipeBackEnabled)
        this.showDividers = showDividers
        this.isSmallSearchInputLeftSpace = isSwipeBackEnabled

        stubContentProvider?.let {
            customStubContentProvider = it as SelectorStubContentProvider<Any>
        }
        useCaseValue = case.value
    }
}

/**
 * Создаёт фрагмент экрана множественного выбора региона
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param itemHandleStrategy пользовательский обработчик кликов
 * @param format форматирование счётчика [RegionSelectorItemModel.counter]
 * @param limit максимально возможное количество элементов для выбора
 * @param needCloseButton нужно ли отображать кнопку "Закрыть" (стрелочка). Актуально для отображения выбора в диалоге
 * @param themeRes тема внешнего вида компонента
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 * @param showDividers должны ли отображаться разделители
 * @param stubContentProvider поставщик данных для заглушек. Если не указан, используется реализация по умолчанию
 * [DefaultSelectorStubContentProvider] на основе параметров из [selectorStrings].
 */
@JvmOverloads
fun <SERVICE_RESULT, DATA : RegionSelectorItemModel, FILTER, ACTIVITY : FragmentActivity, ANCHOR>
createMultiRegionSelector(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, ANCHOR>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    selectorStrings: SelectorStrings = DEFAULT_REGION_MULTI_SELECTOR_STRINGS,
    itemHandleStrategy: SelectorItemHandleStrategy<DATA>? = null,
    format: CounterFormat = CounterFormat.DEFAULT,
    limit: Int = DEFAULT_SELECTION_LIMIT,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme,
    isSwipeBackEnabled: Boolean = false,
    showDividers: Boolean = true,
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    case: SelectionStatisticUseCase = UNKNOWN,
): Fragment = MultiSelectorFragment().apply {
    arguments = Bundle().apply {
        isMultiSelection = true
        isHierarchicalData = false
        counterFormat = format
        selectionLimit = limit
        this.needCloseButton = needCloseButton
        this.themeRes = themeRes
        fixedButtonType = if (itemHandleStrategy == null) null else FixedButtonType.CHOOSE_ALL
        enableHeaderShadow = true
        searchQueryMinLength = REGION_SEARCH_QUERY_MIN_LENGTH

        multiDependenciesFactory = listDependenciesFactory as ListDependenciesFactory<Any, SelectorItemModel, Any, Any>

        this.selectorStrings = selectorStrings

        putSerializable(SELECTOR_COMPLETE_LISTENER, completeListener)
        putSerializable(SELECTOR_CANCEL_LISTENER, cancelListener)

        itemHandleStrategy?.let {
            this.itemHandleStrategy = it as SelectorItemHandleStrategy<SelectorItemModel>
        }

        putBoolean(SELECTOR_ENABLE_SWIPE_BACK, isSwipeBackEnabled)
        this.showDividers = showDividers

        stubContentProvider?.let {
            customStubContentProvider = it as SelectorStubContentProvider<Any>
        }
        useCaseValue = case.value
    }
}

/**
 * Создаёт фрагмент экрана множественного выбора регионов с иерархическими переходами
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param itemHandleStrategy пользовательский обработчик кликов
 * @param format форматирование счётчика [RegionSelectorItemModel.counter]
 * @param limit максимально возможное количество элементов для выбора
 * @param needCloseButton нужно ли отображать кнопку "Закрыть" (стрелочка). Актуально для отображения выбора в диалоге
 * @param themeRes тема внешнего вида компонента
 * @param showDividers должны ли отображаться разделители
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 * @param stubContentProvider поставщик данных для заглушек. Если не указан, используется реализация по умолчанию
 * [DefaultSelectorStubContentProvider] на основе параметров из [selectorStrings].
 */
@JvmOverloads
fun <SERVICE_RESULT, DATA, FILTER, ACTIVITY : FragmentActivity, ANCHOR> createMultiHierarchicalRegionSelector(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, ANCHOR>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    selectorStrings: SelectorStrings = DEFAULT_REGION_MULTI_SELECTOR_STRINGS,
    itemHandleStrategy: SelectorItemHandleStrategy<DATA>? = null,
    format: CounterFormat = CounterFormat.DEFAULT,
    limit: Int = DEFAULT_SELECTION_LIMIT,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme,
    showDividers: Boolean = true,
    isSwipeBackEnabled: Boolean = false,
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    case: SelectionStatisticUseCase = UNKNOWN,
): Fragment where DATA : RegionSelectorItemModel,
                  DATA : HierarchySelectorItemModel = MultiSelectorFragment().apply {
    arguments = Bundle().apply {
        isMultiSelection = true
        isHierarchicalData = true
        counterFormat = format
        selectionLimit = limit
        this.needCloseButton = needCloseButton
        this.themeRes = themeRes
        fixedButtonType = if (itemHandleStrategy == null) null else FixedButtonType.CHOOSE_ALL
        enableHeaderShadow = true
        searchQueryMinLength = REGION_SEARCH_QUERY_MIN_LENGTH

        multiDependenciesFactory = listDependenciesFactory as ListDependenciesFactory<Any, SelectorItemModel, Any, Any>

        this.selectorStrings = selectorStrings

        putSerializable(SELECTOR_COMPLETE_LISTENER, completeListener)
        putSerializable(SELECTOR_CANCEL_LISTENER, cancelListener)

        itemHandleStrategy?.let {
            this.itemHandleStrategy = it as SelectorItemHandleStrategy<SelectorItemModel>
        }

        putBoolean(SELECTOR_ENABLE_SWIPE_BACK, isSwipeBackEnabled)
        this.showDividers = showDividers

        stubContentProvider?.let {
            customStubContentProvider = it as SelectorStubContentProvider<Any>
        }
        useCaseValue = case.value
    }
}
