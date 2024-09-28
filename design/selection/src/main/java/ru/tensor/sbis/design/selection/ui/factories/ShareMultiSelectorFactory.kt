package ru.tensor.sbis.design.selection.ui.factories

import androidx.annotation.IntRange
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.common.util.addArgs
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.contract.listener.CombinedMultiSelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubContentProvider
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.enableRecentSelectionCaching
import ru.tensor.sbis.design.selection.ui.utils.itemHandleStrategy
import ru.tensor.sbis.design.selection.ui.utils.stub.DefaultSelectorStubContentProvider

/**
 * Создаёт фрагмент множественного выбора (получателей и диалогов) для функционала "Поделиться"
 *
 * @param listDependenciesFactory фабрика зависимостей
 * @param completeListener обработчик успешного результата выбора
 * @param cancelListener обработчик события отмены выбора
 * @param prefetchCheckFunction функция проверки необходимости загрузить дополнительные данные
 * @param selectorStrings содержит специфичные сообщения об ошибках
 * @param stubContentProvider поставщик данных для заглушек. Если не указан, используется реализация по умолчанию
 * [DefaultSelectorStubContentProvider] на основе параметров из [selectorStrings]
 * @param limit максимально возможное количество элементов для выбора
 * @param needCloseButton нужно ли отображать кнопку "Закрыть" (стрелочка). Актуально для отображения выбора в диалоге
 * @param themeRes тема внешнего вида компонента
 * @param isSwipeBackEnabled должен ли поддерживаться свайп для возврата назад
 */
@JvmOverloads
@Suppress("UNCHECKED_CAST")
fun <SERVICE_RESULT, DATA : SelectorItemModel, FILTER, ACTIVITY : FragmentActivity, ANCHOR> createShareMultiSelector(
    listDependenciesFactory: ListDependenciesFactory<SERVICE_RESULT, DATA, FILTER, ANCHOR>,
    completeListener: MultiSelectionListener<DATA, ACTIVITY>,
    cancelListener: SelectionCancelListener<ACTIVITY>,
    prefetchCheckFunction: PrefetchCheckFunction<in DATA>? = null,
    selectorStrings: SelectorStrings = SelectorStrings(
        allSelectedTitle = R.string.selection_all_recipients_selected_title
    ),
    stubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>? = null,
    @IntRange(from = MINIMAL_COUNT_FOR_MULTI_SELECTION) limit: Int = DEFAULT_SELECTION_LIMIT,
    needCloseButton: Boolean = true,
    @StyleRes themeRes: Int = R.style.SelectionDefaultTheme_Recipient,
    isSwipeBackEnabled: Boolean = false
): Fragment = createMultiSelectionFragment(
    listDependenciesFactory,
    completeListener,
    cancelListener,
    prefetchCheckFunction,
    selectorStrings,
    stubContentProvider,
    limit,
    SelectorSelectionMode.REPLACE_ALL_IF_FIRST,
    SelectorDoneButtonVisibilityMode.AUTO_HIDDEN,
    needCloseButton,
    themeRes,
    isSwipeBackEnabled
).addArgs {
    itemHandleStrategy = CombinedMultiSelectorItemHandleStrategy()
    enableRecentSelectionCaching = false
}
