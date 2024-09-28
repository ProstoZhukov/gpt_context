package ru.tensor.sbis.appdesign.selection.factories

import android.content.Context
import ru.tensor.sbis.appdesign.selection.data.DemoFilter
import ru.tensor.sbis.appdesign.selection.data.DemoServiceResult
import ru.tensor.sbis.appdesign.selection.datasource.*
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.ListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.model.region.DefaultRegionSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author ma.kolpakov
 */
class DemoListDependenciesFactory : ListDependenciesFactory<DemoServiceResult, DefaultRegionSelectorItemModel, DemoFilter, Int> {

    override fun getServiceWrapper(appContext: Context): ServiceWrapper<DemoServiceResult, DemoFilter> =
        DemoServiceWrapper(appContext.run { DemoRegionController /* Получение контроллера из DI */ })

    override fun getSelectionLoader(appContext: Context): MultiSelectionLoader<DefaultRegionSelectorItemModel> =
        DemoSelectionLoader(DemoRegionController, getDataMapper())

    override fun getFilterFactory(appContext: Context): FilterFactory<DefaultRegionSelectorItemModel, DemoFilter, Int> =
        DemoFilterFactory()

    override fun getMapperFunction(appContext: Context): ListMapper<DemoServiceResult, DefaultRegionSelectorItemModel> =
        getDataMapper()

    override fun getResultHelper(appContext: Context): ResultHelper<Int, DemoServiceResult> =
        DemoResultHelper()

    private fun getDataMapper() = DemoDataMapper(singleSelection = false)
}