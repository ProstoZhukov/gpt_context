/**
 * Набор фабричных методов для создания компонента выбора получателей
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
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.*
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.listeners.*
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientMultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientSelectorDataProvider
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientsResultHelper
import ru.tensor.sbis.design.selection.ui.fragment.MultiSelectorFragment
import ru.tensor.sbis.design.selection.ui.fragment.single.SingleSelectorFragment
import ru.tensor.sbis.design.selection.ui.list.items.multi.recipient.RecipientMultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.items.single.recipient.RecipientSingleSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.*
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonType
import ru.tensor.sbis.design.selection.ui.utils.stub.DefaultSelectorStubContentProvider
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase

/**
 * Псевдоним компонента для множественного выбора получателей
 */
private typealias PersonSelectorFragment = MultiSelectorFragment

/**
 * Создаёт фрагмент экрана одиночного выбора получателей
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param newGroupClickListener подписка на нажатие кнопки "Новая группа"
 * @param stubContentProvider поставщик данных для заглушек. Если не указан, используется реализация по умолчанию
 * [DefaultSelectorStubContentProvider] на основе параметров из [selectorStrings]
 * @param needCloseButton нужно ли отображать кнопку "Закрыть" (стрелочка). Актуально для отображения выбора в диалоге
 * @param themeRes тема внешнего вида компонента
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 */
fun <SERVICE_RESULT, DATA : RecipientSelectorItemModel, FILTER, ACTIVITY : FragmentActivity, ANCHOR>
createSingleRecipientSelector(
    listDependenciesFactory: SingleSelectionListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, ANCHOR>,
    completeListener: SelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    selectorStrings: SelectorStrings = SelectorStrings(),
    newGroupClickListener: NewGroupClickListener<ACTIVITY>? = null,
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme_Recipient,
    isSwipeBackEnabled: Boolean = false,
    case: SelectionStatisticUseCase = SelectionStatisticUseCase.UNKNOWN
): Fragment = SingleSelectorFragment().apply {
    arguments = Bundle().apply {
        isMultiSelection = false
        isHierarchicalData = false
        singleCustomisation = RecipientSingleSelectorCustomisation()
        stubContentProvider?.let {
            customStubContentProvider = it as SelectorStubContentProvider<Any>
        }

        newGroupClickListener?.let {
            fixedButtonType = FixedButtonType.CREATE_GROUP
            newGroupListener = it as NewGroupClickListener<FragmentActivity>
        }
        this.needCloseButton = needCloseButton
        this.themeRes = themeRes

        singleDependenciesFactory =
            listDependenciesFactory as SingleSelectionListDependenciesFactory<Any, SelectorItemModel, Any, Any>

        this.selectorStrings = selectorStrings

        putSerializable(SELECTOR_COMPLETE_LISTENER, completeListener)
        putSerializable(SELECTOR_CANCEL_LISTENER, cancelListener)

        putBoolean(SELECTOR_ENABLE_SWIPE_BACK, isSwipeBackEnabled)
        useCaseValue = case.value
    }
}

/**
 * Создаёт фрагмент экрана множественного выбора получателей
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param prefetchCheckFunction функция проверки необходимости загрузить дополнительные данные
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param stubContentProvider поставщик данных для заглушек. Если не указан, используется реализация по умолчанию
 * [DefaultSelectorStubContentProvider] на основе параметров из [selectorStrings]
 * @param limit максимально возможное количество элементов для выбора
 * @param selectionMode режим обработки пользовательского выбора
 * @param doneButtonVisibilityMode режим работы кнопки "Применить"
 * @param needCloseButton нужно ли отображать кнопку "Закрыть" (стрелочка). Актуально для отображения выбора в диалоге
 * @param themeRes тема внешнего вида компонента
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 */
@JvmOverloads
fun <SERVICE_RESULT, DATA : RecipientSelectorItemModel, FILTER, ACTIVITY : FragmentActivity, ANCHOR>
createMultiRecipientSelector(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, ANCHOR>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    prefetchCheckFunction: PrefetchCheckFunction<in DATA>? = null,
    selectorStrings: SelectorStrings = SelectorStrings(
        allSelectedTitle = R.string.selection_all_recipients_selected_title
    ),
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    @IntRange(from = MINIMAL_COUNT_FOR_MULTI_SELECTION) limit: Int = DEFAULT_SELECTION_LIMIT,
    selectionMode: SelectorSelectionMode = SelectorSelectionMode.REPLACE_ALL_IF_FIRST,
    doneButtonVisibilityMode: SelectorDoneButtonVisibilityMode = SelectorDoneButtonVisibilityMode.AUTO_HIDDEN,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme_Recipient,
    isSwipeBackEnabled: Boolean = false,
    case: SelectionStatisticUseCase = SelectionStatisticUseCase.UNKNOWN,
): Fragment = createMultiSelectionFragment(
    listDependenciesFactory,
    completeListener,
    cancelListener,
    prefetchCheckFunction,
    selectorStrings,
    stubContentProvider,
    limit,
    selectionMode,
    doneButtonVisibilityMode,
    needCloseButton,
    themeRes,
    isSwipeBackEnabled,
    case
)

/**
 * Создаёт фрагмент экрана множественного выбора получателей
 *
 * @param dataProvider поставщик данных для списка получателей
 * @param selectionLoader загрузчик информации о выбранных элементах при инициализации
 * @param resultHelper объект для получения информации о загруженных данных
 * @param options объект настроек поведения и внешнего вида
 * @param themeRes тема внешнего вида компонента
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 */
@JvmOverloads
fun <DATA : RecipientSelectorItemModel, ACTIVITY : FragmentActivity, ANCHOR> createMultiRecipientSelector(
    dataProvider: RecipientSelectorDataProvider<DATA>,
    selectionLoader: RecipientMultiSelectionLoader,
    resultHelper: RecipientsResultHelper<ANCHOR, DATA>,
    options: MultiSelectorOptions<DATA, ACTIVITY>,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme_Recipient,
    isSwipeBackEnabled: Boolean = false,
    case: SelectionStatisticUseCase = SelectionStatisticUseCase.UNKNOWN
): Fragment = PersonSelectorFragment().apply {
    arguments = Bundle().apply {
        isMultiSelection = true
        isHierarchicalData = false
        selectionLimit = options.selectionLimit
        multiCustomisation = RecipientMultiSelectorCustomisation()
        doneButtonMode = options.doneButtonVisibilityMode
        selectionMode = options.selectionMode
        this.needCloseButton = needCloseButton
        this.themeRes = themeRes

        recipientDataProvider = dataProvider as RecipientSelectorDataProvider<RecipientSelectorItemModel>
        recipientsMultiSelectionLoader = selectionLoader
        recipientsResultHelper = resultHelper as RecipientsResultHelper<Any, RecipientSelectorItemModel>
        selectorItemListeners = options.itemListeners as SelectorItemListeners<SelectorItemModel, FragmentActivity>

        putSerializable(SELECTOR_COMPLETE_LISTENER, options.successListener)
        putSerializable(SELECTOR_CANCEL_LISTENER, options.cancelListener)

        selectorStrings = SelectorStrings(
            limitExceeded = options.selectionLimitText,
            notFoundTitle = options.notFoundTitle,
            notFoundDescription = options.notFoundDescription,
            allSelectedTitle = options.noResultsTitle,
            allSelectedDescription = options.noResultsDescription
        )

        putBoolean(SELECTOR_ENABLE_SWIPE_BACK, isSwipeBackEnabled)
        enableRecentSelectionCaching = true
        useCaseValue = case.value
    }
}

internal fun <SERVICE_RESULT, DATA : SelectorItemModel, FILTER, ACTIVITY : FragmentActivity, ANCHOR>
createMultiSelectionFragment(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, ANCHOR>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    prefetchCheckFunction: PrefetchCheckFunction<in DATA>? = null,
    selectorStrings: SelectorStrings = SelectorStrings(
        allSelectedTitle = R.string.selection_all_recipients_selected_title
    ),
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    @IntRange(from = MINIMAL_COUNT_FOR_MULTI_SELECTION) limit: Int = DEFAULT_SELECTION_LIMIT,
    selectionMode: SelectorSelectionMode = SelectorSelectionMode.REPLACE_ALL_IF_FIRST,
    doneButtonVisibilityMode: SelectorDoneButtonVisibilityMode = SelectorDoneButtonVisibilityMode.AUTO_HIDDEN,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme_Recipient,
    isSwipeBackEnabled: Boolean = false,
    case: SelectionStatisticUseCase = SelectionStatisticUseCase.UNKNOWN,
): Fragment = MultiSelectorFragment().withArgs {
    isMultiSelection = true
    isHierarchicalData = false
    selectionLimit = limit
    multiCustomisation = RecipientMultiSelectorCustomisation()
    stubContentProvider?.let {
        customStubContentProvider = it as SelectorStubContentProvider<Any>
    }
    this.selectionMode = selectionMode
    doneButtonMode = doneButtonVisibilityMode
    this.needCloseButton = needCloseButton
    this.themeRes = themeRes

    multiDependenciesFactory = listDependenciesFactory as ListDependenciesFactory<Any, SelectorItemModel, Any, Any>

    putSerializable(SELECTOR_COMPLETE_LISTENER, completeListener)
    putSerializable(SELECTOR_CANCEL_LISTENER, cancelListener)
    prefetchCheckFunction?.let {
        this.prefetchCheckFunction = it
    }
    this.selectorStrings = selectorStrings

    putBoolean(SELECTOR_ENABLE_SWIPE_BACK, isSwipeBackEnabled)
    enableRecentSelectionCaching = true
    useCaseValue = case.value
}
