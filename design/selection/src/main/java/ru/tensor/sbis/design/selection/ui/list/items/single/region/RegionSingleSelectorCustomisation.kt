package ru.tensor.sbis.design.selection.ui.list.items.single.region

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Переопределение внешнего вида по стандарту одиночного выбора регионов
 *
 * @author ma.kolpakov
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент создания фрагмента */)
internal class RegionSingleSelectorCustomisation : SelectorCustomisation {

    override fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate<SelectorItemModel>,
        iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
        activityProvider: Provider<FragmentActivity>?,
    ): Map<Any, ViewHolderHelper<SelectorItemModel, *>> {
        return mapOf(
            RegionSingleSelectorViewHolderHelper::class.java to
                RegionSingleSelectorViewHolderHelper() as ViewHolderHelper<SelectorItemModel, *>
        )
    }

    override fun getViewHolderType(model: SelectorItemModel): Any = RegionSingleSelectorViewHolderHelper::class.java
}