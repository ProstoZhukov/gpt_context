package ru.tensor.sbis.design_selection.contract.customization.selection

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Кастомизатор ячеек доступных для выбора в компоненте выбора.
 *
 * @author vv.chekurda
 */
interface SelectableItemsCustomization<ITEM : SelectionItem> {

    /**
     * Метод для создания набора [ViewHolderHelper], для работы с типами view от предметной области
     *
     * @see getViewHolderType
     */
    fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate,
        activityProvider: Provider<FragmentActivity>
    ): Map<Any, ViewHolderHelper<ITEM, *>>

    /**
     * Метод для определения типа [ViewHolderHelper] по модели данных [item]
     *
     * @see createViewHolderHelpers
     */
    fun getViewHolderType(item: ITEM, isMultiSelection: Boolean = true): Any
}