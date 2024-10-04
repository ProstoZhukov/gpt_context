package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoServiceResult
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.region.DefaultRegionSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoDataMapper(private val singleSelection: Boolean) : ListMapper<DemoServiceResult, DefaultRegionSelectorItemModel> {

    override fun invoke(serviceData: DemoServiceResult) =
        mapServiceData(serviceData)

    fun mapServiceData(serviceData: DemoServiceResult): List<DefaultRegionSelectorItemModel> =
        serviceData.data
            .filterNot { singleSelection && it.id == CHOOSE_ALL_ITEM.id }
            .mapTo(ArrayList(serviceData.data.size)) {
                DefaultRegionSelectorItemModel(it.id.toString(), it.title, it.subtitle, it.counter, it.hasNested)
            }
}