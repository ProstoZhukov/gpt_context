package ru.tensor.sbis.design.selection.ui.contract

import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectorItemListeners
import ru.tensor.sbis.design.selection.ui.factories.DEFAULT_SELECTION_LIMIT
import ru.tensor.sbis.design.selection.ui.factories.MINIMAL_COUNT_FOR_MULTI_SELECTION
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.stubview.StubViewCase

/**
 * Настройки компонента множественного выбора
 *
 * @author ma.kolpakov
 */
data class MultiSelectorOptions<DATA : SelectorItemModel, ACTIVITY : FragmentActivity> @JvmOverloads constructor(
    /**
     * Подписка на получение результата выбора
     */
    val successListener: MultiSelectionListener<DATA, ACTIVITY>,
    /**
     * Подписка на отмену выбора
     */
    val cancelListener: SelectionCancelListener<ACTIVITY>,
    /**
     * Сообщение, если данных больше нет
     */
    @StringRes val noResultsTitle: Int,
    /**
     * Дополнительное сообщение, если данных больше нет
     */
    @StringRes val noResultsDescription: Int,
    /**
     * Сообщение, в случае неудачного поиска
     */
    @StringRes val notFoundTitle: Int = StubViewCase.NO_FILTER_RESULTS.messageRes,
    /**
     * Дополнительное сообщение, в случае неудачного поиска
     */
    @StringRes val notFoundDescription: Int = StubViewCase.NO_FILTER_RESULTS.detailsRes,
    /**
     * Объект, который позволяет установить реагирование на различные нажатия по разным элементам
     */
    val itemListeners: SelectorItemListeners<DATA, ACTIVITY> = SelectorItemListeners(),
    /**
     * Максимально допустимое количество элементов, которые можно выбрать
     */
    @IntRange(from = MINIMAL_COUNT_FOR_MULTI_SELECTION) val selectionLimit: Int = DEFAULT_SELECTION_LIMIT,
    /**
     * Сообщение, которое показывается в случае если количество выбранных элементов превысило максимально допустимое
     */
    @StringRes val selectionLimitText: Int = R.string.selection_limit_exceeded,
    /**
     * Способ начального выбора данных
     */
    val selectionMode: SelectorSelectionMode = SelectorSelectionMode.REPLACE_ALL_IF_FIRST,
    /**
     * Способ показа кнопки завершения выбора
     */
    val doneButtonVisibilityMode: SelectorDoneButtonVisibilityMode = SelectorDoneButtonVisibilityMode.AUTO_HIDDEN
)