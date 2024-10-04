/**
 * Набор фабричных методов для создания фрагмента выбора получателей из сотрудкиков
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
import androidx.annotation.IntRange
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubContentProvider
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener
import ru.tensor.sbis.design.selection.ui.fragment.MultiSelectorFragment
import ru.tensor.sbis.design.selection.ui.list.items.multi.recipient.RecipientMultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.items.single.recipient.RecipientSingleSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.*
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase.UNKNOWN

/**
 * Создаёт фрагмент экрана множественного выбора сотрудника
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param prefetchCheckFunction функция проверки необходимости загрузить дополнительные данные
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param selectionMode режим обработки пользовательского выбора
 * @param needCloseButton нужно ли отображать кнопку "Закрыть" (стрелочка). Актуально для отображения выбора в диалоге
 * @param themeRes тема внешнего вида компонента
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 */
@JvmOverloads
fun <SERVICE_RESULT, DATA, FILTER, ACTIVITY : FragmentActivity> createMultiEmployeeSelector(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, Int>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    prefetchCheckFunction: PrefetchCheckFunction<in DATA>? = null,
    selectorStrings: SelectorStrings = SelectorStrings(
        allSelectedTitle = R.string.selection_all_employees_selected_title,
        allSelectedDescription = R.string.selection_all_employees_selected_description
    ),
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    @IntRange(from = MINIMAL_COUNT_FOR_MULTI_SELECTION) limit: Int = DEFAULT_SELECTION_LIMIT,
    doneButtonVisibilityMode: SelectorDoneButtonVisibilityMode = SelectorDoneButtonVisibilityMode.AUTO_HIDDEN,
    selectionMode: SelectorSelectionMode = SelectorSelectionMode.REPLACE_ALL_IF_FIRST,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme_Recipient,
    isSwipeBackEnabled: Boolean = false,
    case: SelectionStatisticUseCase = UNKNOWN
): Fragment where DATA : HierarchySelectorItemModel,
                  DATA : RecipientSelectorItemModel = MultiSelectorFragment().apply {
    arguments = Bundle().apply {
        isMultiSelection = true
        isHierarchicalData = true
        selectionLimit = limit
        multiCustomisation = RecipientMultiSelectorCustomisation()
        stubContentProvider?.let {
            customStubContentProvider = it as SelectorStubContentProvider<Any>
        }
        doneButtonMode = doneButtonVisibilityMode
        this.selectionMode = selectionMode
        this.needCloseButton = needCloseButton
        this.themeRes = themeRes

        multiDependenciesFactory = listDependenciesFactory as ListDependenciesFactory<Any, SelectorItemModel, Any, Any>

        this.selectorStrings = selectorStrings

        putSerializable(SELECTOR_COMPLETE_LISTENER, completeListener)
        putSerializable(SELECTOR_CANCEL_LISTENER, cancelListener)
        prefetchCheckFunction?.let {
            this.prefetchCheckFunction = it
        }

        putBoolean(SELECTOR_ENABLE_SWIPE_BACK, isSwipeBackEnabled)
        useCaseValue = case.value
    }
}

/**
 * Создаёт фрагмент экрана одиночного выбора сотрудника
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param prefetchCheckFunction функция проверки необходимости загрузить дополнительные данные
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param needCloseButton нужно ли отображать кнопку "Закрыть" (стрелочка). Актуально для отображения выбора в диалоге
 * @param themeRes тема внешнего вида компонента
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 */
@JvmOverloads
fun <SERVICE_RESULT, DATA, FILTER, ACTIVITY : FragmentActivity> createSingleEmployeeSelector(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, Int>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    prefetchCheckFunction: PrefetchCheckFunction<in DATA>? = null,
    selectorStrings: SelectorStrings = SelectorStrings(),
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    doneButtonVisibilityMode: SelectorDoneButtonVisibilityMode = SelectorDoneButtonVisibilityMode.AUTO_HIDDEN,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme_Recipient,
    isSwipeBackEnabled: Boolean = false,
    case: SelectionStatisticUseCase = UNKNOWN
): Fragment where DATA : HierarchySelectorItemModel,
                  DATA : RecipientSelectorItemModel = MultiSelectorFragment().apply {
    arguments = Bundle().apply {
        isMultiSelection = false
        isHierarchicalData = true
        singleCustomisation = RecipientSingleSelectorCustomisation()
        stubContentProvider?.let {
            customStubContentProvider = it as SelectorStubContentProvider<Any>
        }
        doneButtonMode = doneButtonVisibilityMode
        this.needCloseButton = needCloseButton
        this.themeRes = themeRes

        singleDependenciesFactory =
            listDependenciesFactory as SingleSelectionListDependenciesFactory<Any, SelectorItemModel, Any, Any>

        this.selectorStrings = selectorStrings

        putSerializable(SELECTOR_COMPLETE_LISTENER, completeListener)
        putSerializable(SELECTOR_CANCEL_LISTENER, cancelListener)
        prefetchCheckFunction?.let {
            this.prefetchCheckFunction = it
        }

        putBoolean(SELECTOR_ENABLE_SWIPE_BACK, isSwipeBackEnabled)
        useCaseValue = case.value
    }
}