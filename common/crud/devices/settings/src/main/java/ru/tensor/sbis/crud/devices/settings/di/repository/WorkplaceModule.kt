package ru.tensor.sbis.crud.devices.settings.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.devices.settings.generated.*
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.workplace.*
import ru.tensor.sbis.crud.devices.settings.crud.workplace.mapper.WorkplaceListMapper
import ru.tensor.sbis.crud.devices.settings.crud.workplace.mapper.WorkplaceMapper
import ru.tensor.sbis.crud.devices.settings.model.Workplace
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommandImpl
import ru.tensor.devices.settings.generated.Workplace as ControllerWorkplace

/**@SelfDocumented*/
@Module
internal class WorkplaceModule {

    /**@SelfDocumented*/
    @Provides
    internal fun provideFilter(): WorkplaceListFilter = WorkplaceListFilter()

    /**@SelfDocumented*/
    @Provides
    internal fun provideManager(devicesSettings: DependencyProvider<DevicesSettings>):
            DependencyProvider<WorkplaceFacade> =
            DependencyProvider.create { devicesSettings.get().workplace() }

    /**@SelfDocumented*/
    @Provides
    internal fun provideDeviceIdRepository(workplaceFacade: DependencyProvider<WorkplaceFacade>):
            DeviceIdRepository = DeviceIdRepository(workplaceFacade)

    /**@SelfDocumented*/
    @Provides
    @Suppress("ConstantConditionIf")
    internal fun provideRepository(manager: DependencyProvider<WorkplaceFacade>):
            WorkplaceRepository = WorkplaceRepositoryImpl(manager)

    /**@SelfDocumented*/
    @Provides
    internal fun provideCommandWrapper(workplaceRepository: WorkplaceRepository,
                                       deviceIdRepository: DeviceIdRepository,
                                       createCommand: CreateObservableCommand<ControllerWorkplace>,
                                       readCommand: ReadObservableCommand<Workplace>,
                                       updateCommand: UpdateObservableCommand<ControllerWorkplace>,
                                       deleteCommand: DeleteRepositoryCommand<ControllerWorkplace>,
                                       listCommand: BaseListObservableCommand<PagedListResult<BaseItem<Workplace>>, WorkplaceFilter, DataRefreshedWorkplaceFacadeCallback>):
            WorkplaceCommandWrapper =
            WorkplaceCommandWrapperImpl(workplaceRepository, deviceIdRepository, createCommand, readCommand, updateCommand, deleteCommand, listCommand)

    /**@SelfDocumented*/
    @Provides
    internal fun provideMapper(context: Context):
            BaseModelMapper<ControllerWorkplace, Workplace> =
            WorkplaceMapper(context)

    /**@SelfDocumented*/
    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<ListResultOfWorkplaceMapOfStringString, PagedListResult<BaseItem<Workplace>>> =
            WorkplaceListMapper(context)

    /**@SelfDocumented*/
    @Provides
    internal fun provideCreateCommand(repository: WorkplaceRepository):
            CreateObservableCommand<ControllerWorkplace> =
            CreateCommand(repository)

    /**@SelfDocumented*/
    @Provides
    internal fun provideReadCommand(repository: WorkplaceRepository,
                                    mapper: BaseModelMapper<ControllerWorkplace, Workplace>):
            ReadObservableCommand<Workplace> =
            ReadCommand(repository, mapper)

    /**@SelfDocumented*/
    @Provides
    internal fun provideUpdateCommand(repository: WorkplaceRepository):
            UpdateObservableCommand<ControllerWorkplace> =
            UpdateCommand(repository)

    /**@SelfDocumented*/
    @Provides
    internal fun provideDeleteCommand(repository: WorkplaceRepository):
            DeleteRepositoryCommand<ControllerWorkplace> =
            DeleteRepositoryCommandImpl(repository)

    /**@SelfDocumented*/
    @Provides
    internal fun provideListCommand(repository: WorkplaceRepository,
                                    mapper: BaseModelMapper<ListResultOfWorkplaceMapOfStringString, PagedListResult<BaseItem<Workplace>>>):
            BaseListObservableCommand<PagedListResult<BaseItem<Workplace>>, WorkplaceFilter, DataRefreshedWorkplaceFacadeCallback> =
            BaseListCommand(repository, mapper)
}
