/**
 * Набор расширений для работы с аргументами фрагментов без использования ключей. Аргументы в этом файле определены на
 * уровне компонента
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.ui.utils

import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.bl.utils.DefaultSelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubContentProvider
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.listeners.NewGroupClickListener
import ru.tensor.sbis.design.selection.ui.factories.ListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.factories.SingleSelectionListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.list.items.MultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.items.multi.DefaultMultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.items.single.DefaultSingleSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonType
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase

private const val THEME_RES = "THEME_RES"

private const val IS_MULTI_SELECTION = "IS_MULTI_SELECTION"
private const val IS_HIERARCHICAL_DATA = "IS_HIERARCHICAL_DATA"
private const val SELECTION_MODE = "SELECTION_MODE"
private const val DONE_BUTTON_MODE = "DONE_BUTTON_MODE"
private const val NEED_CLOSE_BUTTON = "NEED_CLOSE_BUTTON"
private const val NEW_GROUP_CLICK_LISTENER = "NEW_GROUP_CLICK_LISTENER"
private const val FIXED_BUTTON_TYPE = "FIXED_BUTTON_TYPE"
private const val SELECTION_LIMIT = "SELECTION_LIMIT"
private const val COUNTER_FORMAT = "COUNTER_FORMAT"
private const val ENABLE_HEADER_SHADOW = "ENABLE_HEADER_SHADOW"
private const val SEARCH_QUERY_MIN_LENGTH = "SEARCH_QUERY_MIN_LENGTH"
private const val SHOW_DIVIDERS = "SHOW_DIVIDERS"
private const val PREFETCH_CHECK_FUNCTION = "PREFETCH_CHECK_FUNCTION"
private const val ENABLE_RECENT_SELECTION_CACHE = "ENABLE_RECENT_SELECTION_CACHE"
private const val IS_SMALL_SEARCH_INPUT_LEFT_SPACE = "IS_SMALL_SEARCH_INPUT_LEFT_SPACE"
private const val SELECTION_STATISTIC_USE_CASE = "SELECTION_STATISTIC_USE_CASE"

private const val SELECTOR_DEPENDENCIES_FACTORY = "SELECTOR_DEPENDENCIES_FACTORY"
private const val SELECTOR_ITEM_HANDLE_STRATEGY = "SELECTOR_ITEM_HANDLE_STRATEGY"
private const val MULTI_CUSTOMISATION = "MULTI_CUSTOMISATION"
private const val SINGLE_CUSTOMISATION = "SINGLE_CUSTOMISATION"
private const val CUSTOM_STUB_CONTENT_PROVIDER = "CUSTOM_STUB_CONTENT_PROVIDER"
private const val SELECTOR_STRINGS = "SELECTOR_STRINGS"

private const val DEFAULT_SEARCH_QUERY_MIN_LENGTH = 1

/**
 * Информация о режиме работы. Используется в общих реализациях, где по окружению невозможно определить, в каком режиме
 * должен работать компонент. Речь идёт преимущественно о компонентах работы со списком, используется один в обоих
 * режимах работы.
 *
 * Параметр должен быть явно определён в фабричном методе, на этапе создания компонента. Область использования не
 * должна распространяться дальше DI
 */
internal var Bundle.isMultiSelection: Boolean
    get() {
        check(containsKey(IS_MULTI_SELECTION)) { "Selection type is not defined" }
        return getBoolean(IS_MULTI_SELECTION)
    }
    set(value) = putBoolean(IS_MULTI_SELECTION, value)

/**
 * Метка того, работает ли фрагмент с иерархичными данными или нет. Ориентироваться на тип нельзя т.к. должна быть
 * возможность использовать реализации [HierarchySelectorItemModel] как с иерархией так и без
 */
internal var Bundle.isHierarchicalData: Boolean
    get() = getBoolean(IS_HIERARCHICAL_DATA)
    set(value) = putBoolean(IS_HIERARCHICAL_DATA, value)

/**
 * Режим отметки элементов выбранными в компоненте множественного выбора
 */
internal var Bundle.selectionMode: SelectorSelectionMode
    get() = getString(SELECTION_MODE)?.run(SelectorSelectionMode::valueOf)
        ?: SelectorSelectionMode.REPLACE_ALL_IF_FIRST
    set(value) = putString(SELECTION_MODE, value.name)

/**
 * Режим работы кнопки "Применить" в компоненте множественного выбора
 */
internal var Bundle.doneButtonMode: SelectorDoneButtonVisibilityMode
    get() = getString(DONE_BUTTON_MODE)?.run(SelectorDoneButtonVisibilityMode::valueOf)
        ?: SelectorDoneButtonVisibilityMode.AUTO_HIDDEN
    set(value) = putString(DONE_BUTTON_MODE, value.name)

/**
 * Настройка отображения кнопки "Закрыть" (стрелочка "Назад")
 */
internal var Bundle.needCloseButton: Boolean
    get() = getBoolean(NEED_CLOSE_BUTTON, true)
    set(value) = putBoolean(NEED_CLOSE_BUTTON, value)

/**
 * Обработчик нажатий на кнопку "Новая группа"
 *
 * @see FixedButtonType.CREATE_GROUP
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal var Bundle.newGroupListener: NewGroupClickListener<FragmentActivity>
    get() = getSerializable(NEW_GROUP_CLICK_LISTENER) as NewGroupClickListener<FragmentActivity>
    set(value) = putSerializable(NEW_GROUP_CLICK_LISTENER, value)

/**
 * Тип "фиксированной кнопки", механику которой нужно активировать
 */
internal var Bundle.fixedButtonType: FixedButtonType?
    get() = getString(FIXED_BUTTON_TYPE)?.run(FixedButtonType::valueOf)
    set(value) = putString(FIXED_BUTTON_TYPE, value?.name)

/**
 * Ограничение количества выбираемых элементов
 */
internal var Bundle.selectionLimit: Int
    get() = getInt(SELECTION_LIMIT, Int.MAX_VALUE)
    set(value) = putInt(SELECTION_LIMIT, value)

/**
 * Форматирование счётчиков
 */
internal var Bundle.counterFormat: CounterFormat
    get() = getString(COUNTER_FORMAT)?.run(CounterFormat::valueOf) ?: CounterFormat.DEFAULT
    set(value) = putString(COUNTER_FORMAT, value.name)

/**
 * Настройка отображения тени под шапкой
 */
internal var Bundle.enableHeaderShadow: Boolean
    get() = getBoolean(ENABLE_HEADER_SHADOW, false)
    set(value) = putBoolean(ENABLE_HEADER_SHADOW, value)

/**
 * Минимальное число введённых символов для выполнения поиска
 */
internal var Bundle.searchQueryMinLength: Int
    get() = getInt(SEARCH_QUERY_MIN_LENGTH, DEFAULT_SEARCH_QUERY_MIN_LENGTH)
    set(value) = putInt(SEARCH_QUERY_MIN_LENGTH, value)

internal var Bundle.showDividers: Boolean
    get() = getBoolean(SHOW_DIVIDERS, true)
    set(value) = putBoolean(SHOW_DIVIDERS, value)

/**
 * Функция для проверки необходимости догрузить данные при выборе
 */
internal var Bundle.prefetchCheckFunction: PrefetchCheckFunction<*>
    get() = getSerializable(PREFETCH_CHECK_FUNCTION) as PrefetchCheckFunction<*>?
        ?: DefaultPrefetchCheckFunction()
    set(value) = putSerializable(PREFETCH_CHECK_FUNCTION, value)

/**
 * Объект для создания зависимостей для компонента списка
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal var Bundle.multiDependenciesFactory: ListDependenciesFactory<Any, SelectorItemModel, Any, Any>
    get() = getSerializable(SELECTOR_DEPENDENCIES_FACTORY) as ListDependenciesFactory<Any, SelectorItemModel, Any, Any>
    set(value) = putSerializable(SELECTOR_DEPENDENCIES_FACTORY, value)

/**
 * Объект для создания зависимостей для компонента списка
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal var Bundle.singleDependenciesFactory: SingleSelectionListDependenciesFactory<Any, SelectorItemModel, Any, Any>
    get() = getSerializable(SELECTOR_DEPENDENCIES_FACTORY) as
        SingleSelectionListDependenciesFactory<Any, SelectorItemModel, Any, Any>
    set(value) = putSerializable(SELECTOR_DEPENDENCIES_FACTORY, value)

/**
 * Объект для определения правил обработки нажатий по элементам
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal var Bundle.itemHandleStrategy: SelectorItemHandleStrategy<SelectorItemModel>
    get() = getSerializable(SELECTOR_ITEM_HANDLE_STRATEGY) as SelectorItemHandleStrategy<SelectorItemModel>?
        ?: DefaultSelectorItemHandleStrategy()
    set(value) = putSerializable(SELECTOR_ITEM_HANDLE_STRATEGY, value)

/**
 * Объект для переопределения внешнего вида множественного выбора
 */
internal var Bundle.multiCustomisation: MultiSelectorCustomisation
    get() = getSerializable(MULTI_CUSTOMISATION) as MultiSelectorCustomisation? ?: DefaultMultiSelectorCustomisation()
    set(value) = putSerializable(MULTI_CUSTOMISATION, value)

/**
 * Объект для переопределения внешнего вида одиночного выбора
 */
internal var Bundle.singleCustomisation: SelectorCustomisation
    get() = getSerializable(SINGLE_CUSTOMISATION) as SelectorCustomisation? ?: DefaultSingleSelectorCustomisation()
    set(value) = putSerializable(SINGLE_CUSTOMISATION, value)

/**
 * Пользовательская реализация [SelectorStubContentProvider]
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal var Bundle.customStubContentProvider: SelectorStubContentProvider<Any>?
    get() = getSerializable(CUSTOM_STUB_CONTENT_PROVIDER) as SelectorStubContentProvider<Any>?
    set(value) = putSerializable(CUSTOM_STUB_CONTENT_PROVIDER, value)

/**
 * Объект для переопределения строк, специфичных для предметной области
 */
internal var Bundle.selectorStrings: SelectorStrings
    get() = getParcelable(SELECTOR_STRINGS) ?: SelectorStrings()
    set(value) = putParcelable(SELECTOR_STRINGS, value)

/**
 * Настройка кэширования недавно выбранных элементов
 */
internal var Bundle.enableRecentSelectionCaching: Boolean
    get() = getBoolean(ENABLE_RECENT_SELECTION_CACHE, false)
    set(value) = putBoolean(ENABLE_RECENT_SELECTION_CACHE, value)

/**
 * Ресурс темы, которая будет применяться к компоненту выбора
 */
@get:StyleRes
internal var Bundle.themeRes: Int
    get() = getInt(THEME_RES).also {
        check(it != 0) { "Unable to show selector without theme" }
    }
    set(@StyleRes value) = putInt(THEME_RES, value)

/**
 * Использование маленького левого отступа от строки поиска для выравнивания с ячейками,
 * используется в регионах.
 */
internal var Bundle.isSmallSearchInputLeftSpace: Boolean
    get() = getBoolean(IS_SMALL_SEARCH_INPUT_LEFT_SPACE, false)
    set(value) = putBoolean(IS_SMALL_SEARCH_INPUT_LEFT_SPACE, value)

internal var Bundle.useCaseValue: String
    get() = getString(SELECTION_STATISTIC_USE_CASE, SelectionStatisticUseCase.UNKNOWN.value)
    set(value) = putString(SELECTION_STATISTIC_USE_CASE, value)
