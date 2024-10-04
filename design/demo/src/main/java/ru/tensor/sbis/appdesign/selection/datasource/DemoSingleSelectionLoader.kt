package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.region.DefaultRegionSelectorItemModel

/**
 * @author us.bessonov
 */
class DemoSingleSelectionLoader(
    private val controller: DemoRegionController,
    private val mapper: DemoDataMapper
) : SingleSelectionLoader<DefaultRegionSelectorItemModel> {

    override fun loadSelectedItem(): DefaultRegionSelectorItemModel? =
        controller.loadSelectedItems().run(mapper::mapServiceData).firstOrNull()
}