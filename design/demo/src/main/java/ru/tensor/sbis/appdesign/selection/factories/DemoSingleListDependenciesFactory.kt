package ru.tensor.sbis.appdesign.selection.factories

import android.content.Context
import ru.tensor.sbis.appdesign.selection.data.DemoFilter
import ru.tensor.sbis.appdesign.selection.data.DemoServiceResult
import ru.tensor.sbis.appdesign.selection.datasource.*
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.SingleSelectionListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.model.region.DefaultRegionSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author us.bessonov
 */
class DemoSingleListDependenciesFactory : SingleSelectionListDependenciesFactory<DemoServiceResult, DefaultRegionSelectorItemModel, DemoFilter, Int> {

    override fun getServiceWrapper(appContext: Context): ServiceWrapper<DemoServiceResult, DemoFilter> =
        DemoServiceWrapper(appContext.run { DemoRegionController /* Получение контроллера из DI */ })

    override fun getSelectionLoader(appContext: Context): SingleSelectionLoader<DefaultRegionSelectorItemModel> =
        DemoSingleSelectionLoader(DemoRegionController, getDataMapper())

    override fun getFilterFactory(appContext: Context): FilterFactory<DefaultRegionSelectorItemModel, DemoFilter, Int> =
        DemoFilterFactory()

    override fun getMapperFunction(appContext: Context): ListMapper<DemoServiceResult, DefaultRegionSelectorItemModel> =
        getDataMapper()

    override fun getResultHelper(appContext: Context): ResultHelper<Int, DemoServiceResult> =
        DemoResultHelper()

    private fun getDataMapper() = DemoDataMapper(singleSelection = true)
}