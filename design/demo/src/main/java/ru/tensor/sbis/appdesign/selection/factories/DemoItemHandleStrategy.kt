package ru.tensor.sbis.appdesign.selection.factories

import ru.tensor.sbis.appdesign.selection.datasource.CHOOSE_ALL_ITEM
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.ui.model.region.DefaultRegionSelectorItemModel

/**
 * @author ma.kolpakov
 */
internal class DemoItemHandleStrategy : SelectorItemHandleStrategy<DefaultRegionSelectorItemModel> {

    override fun onItemClick(item: DefaultRegionSelectorItemModel): ClickHandleStrategy = when (item.id) {
        CHOOSE_ALL_ITEM.id.toString() -> ClickHandleStrategy.COMPLETE_SELECTION
        else                          -> ClickHandleStrategy.DEFAULT
    }
}