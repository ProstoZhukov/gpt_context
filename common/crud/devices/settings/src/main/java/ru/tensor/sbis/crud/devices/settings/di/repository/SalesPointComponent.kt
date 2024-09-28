package ru.tensor.sbis.crud.devices.settings.di.repository

import ru.tensor.devices.settings.generated.DataRefreshedSalesPointFacadeCallback
import ru.tensor.devices.settings.generated.ListResultOfSalesPointMapOfStringString
import ru.tensor.devices.settings.generated.SalesPointFacade
import ru.tensor.devices.settings.generated.SalesPointFilter
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointCommandWrapper
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointListFilter
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointRepository
import ru.tensor.sbis.crud.devices.settings.model.SalesPoint
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.plugin_struct.feature.Feature

/**@SelfDocumented*/
interface SalesPointComponent : Feature {

    /**@SelfDocumented */
    fun getSalesPointFacade(): DependencyProvider<SalesPointFacade>

    /**@SelfDocumented */
    fun getSalesPointListFilter(): SalesPointListFilter

    /**@SelfDocumented */
    fun getSalesPointRepository(): SalesPointRepository
    /**@SelfDocumented */
    fun getSalesPointCommandWrapper(): SalesPointCommandWrapper

    /**@SelfDocumented */
    fun getSalesPointListMapper(): BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesPoint>>>

    /**@SelfDocumented */
    fun getSalesPointListCommand(): BaseListObservableCommand<PagedListResult<BaseItem<SalesPoint>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback>
}
