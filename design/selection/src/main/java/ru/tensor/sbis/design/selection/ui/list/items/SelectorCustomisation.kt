package ru.tensor.sbis.design.selection.ui.list.items

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.items.single.region.RegionSingleSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import java.io.Serializable
import javax.inject.Provider

/**
 * Интерфейс для переопределения внешнего вида. Доступны стандартные реализации для регионов и адресатов
 *
 * @see RegionSingleSelectorCustomisation
 * @author ma.kolpakov
 */
internal interface SelectorCustomisation : Serializable {

    /**
     * Метод для создания набора [ViewHolderHelper], для работы с типами view от предметной области
     *
     * @see getViewHolderType
     */
    fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate<SelectorItemModel>,
        iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>? = null,
        activityProvider: Provider<FragmentActivity>? = null,
    ): Map<Any, ViewHolderHelper<SelectorItemModel, *>>

    /**
     * Метод для определения типа [ViewHolderHelper] по модели данных [model]
     *
     * @see createViewHolderHelpers
     */
    fun getViewHolderType(model: SelectorItemModel): Any
}