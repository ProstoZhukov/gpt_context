package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.region.DefaultRegionSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoSelectionLoader(
    private val controller: DemoRegionController,
    private val mapper: DemoDataMapper
) : MultiSelectionLoader<DefaultRegionSelectorItemModel> {

    override fun loadSelectedItems(): List<DefaultRegionSelectorItemModel> =
        controller.loadSelectedItems().run(mapper::mapServiceData)
}