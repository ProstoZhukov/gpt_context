package ru.tensor.sbis.crud.devices.settings.di.repository

import ru.tensor.devices.settings.generated.DataRefreshedWorkplaceFacadeCallback
import ru.tensor.devices.settings.generated.ListResultOfWorkplaceMapOfStringString
import ru.tensor.devices.settings.generated.WorkplaceFacade
import ru.tensor.devices.settings.generated.WorkplaceFilter
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.workplace.DeviceIdRepository
import ru.tensor.sbis.crud.devices.settings.crud.workplace.WorkplaceCommandWrapper
import ru.tensor.sbis.crud.devices.settings.crud.workplace.WorkplaceListFilter
import ru.tensor.sbis.crud.devices.settings.crud.workplace.WorkplaceRepository
import ru.tensor.sbis.crud.devices.settings.model.Workplace
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.devices.settings.generated.Workplace as ControllerWorkplace

/**@SelfDocumented*/
interface WorkplaceComponent : Feature {

    /**@SelfDocumented */
    fun getWorkplaceManager(): DependencyProvider<WorkplaceFacade>
    /**@SelfDocumented */
    fun getWorkplaceRepository(): WorkplaceRepository
    /**@SelfDocumented */
    fun getDeviceIdRepository(): DeviceIdRepository
    /**@SelfDocumented */
    fun getWorkplaceCommandWrapper(): WorkplaceCommandWrapper
    /**@SelfDocumented */
    fun getWorkplaceListCommand(): BaseListObservableCommand<PagedListResult<BaseItem<Workplace>>, WorkplaceFilter, DataRefreshedWorkplaceFacadeCallback>

    /**@SelfDocumented */
    fun getWorkplaceMapper(): BaseModelMapper<ControllerWorkplace, Workplace>
    /**@SelfDocumented */
    fun getWorkplaceListMapper(): BaseModelMapper<ListResultOfWorkplaceMapOfStringString, PagedListResult<BaseItem<Workplace>>>

    /**@SelfDocumented */
    fun getWorkplaceListFilter(): WorkplaceListFilter

    /**@SelfDocumented */
    fun getWorkplaceCreateCommand(): CreateObservableCommand<ControllerWorkplace>
    /**@SelfDocumented */
    fun getWorkplaceReadCommand(): ReadObservableCommand<Workplace>
    /**@SelfDocumented */
    fun getWorkplaceUpdateCommand(): UpdateObservableCommand<ControllerWorkplace>
    /**@SelfDocumented */
    fun getWorkplaceDeleteCommand(): DeleteRepositoryCommand<ControllerWorkplace>
}
