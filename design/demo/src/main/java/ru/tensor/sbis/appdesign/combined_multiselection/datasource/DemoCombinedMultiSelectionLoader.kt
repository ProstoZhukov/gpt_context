package ru.tensor.sbis.appdesign.combined_multiselection.datasource

import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoCombinedMultiSelectionLoader : MultiSelectionLoader<SelectorItemModel> {

    override fun loadSelectedItems(): List<SelectorItemModel> = emptyList()
}
