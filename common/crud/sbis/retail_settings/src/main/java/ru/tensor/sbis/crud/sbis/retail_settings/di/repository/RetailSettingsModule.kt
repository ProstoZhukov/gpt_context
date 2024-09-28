package ru.tensor.sbis.crud.sbis.retail_settings.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.payment_settings.crud.PaymentSettingsCommandWrapper
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSaleSettingsCommandWrapper
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSaleSettingsCommandWrapperImpl
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSettingsCommandWrapper
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSettingsCommandWrapperImpl
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSettingsListFilter
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSettingsRepository
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSettingsRepositoryImpl
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.mapper.RetailSettingsListMapper
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.mapper.RetailSettingsMapper
import ru.tensor.sbis.crud.sbis.retail_settings.model.RetailSettings
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateObservableCommand
import ru.tensor.sbis.retail_settings.generated.DataRefreshedCallback
import ru.tensor.sbis.retail_settings.generated.ListResultOfSettingsMapOfStringString
import ru.tensor.sbis.retail_settings.generated.RetailSettingsService
import ru.tensor.sbis.retail_settings.generated.SettingsFacade
import ru.tensor.sbis.retail_settings.generated.SettingsFilter
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommandImpl
import ru.tensor.sbis.retail_settings.generated.Settings as ControllerRetailSettings

@Module
class RetailSettingsModule {

    @Provides
    internal fun provideManager(service: DependencyProvider<RetailSettingsService>):
            DependencyProvider<SettingsFacade> =
            DependencyProvider.create { service.get().settingsFacade() }

    @Provides
    internal fun provideFilter(): RetailSettingsListFilter = RetailSettingsListFilter()

    @Provides
    internal fun provideRepository(controller: DependencyProvider<SettingsFacade>):
            RetailSettingsRepository = RetailSettingsRepositoryImpl(controller)

    @Provides
    internal fun provideCommandWrapper(repository: RetailSettingsRepository,
                                       createCommand: CreateObservableCommand<ControllerRetailSettings>,
                                       readCommand: ReadObservableCommand<RetailSettings>,
                                       updateCommand: UpdateObservableCommand<ControllerRetailSettings>,
                                       deleteCommand: DeleteRepositoryCommand<ControllerRetailSettings>,
                                       listCommand: BaseListObservableCommand<PagedListResult<RetailSettings>, SettingsFilter, DataRefreshedCallback>):
            RetailSettingsCommandWrapper =
            RetailSettingsCommandWrapperImpl(
                repository,
                createCommand,
                readCommand,
                updateCommand,
                deleteCommand,
                listCommand
            )

    @Provides
    internal fun provideSaleSettingsCommandWrapper(
        retailSettingsCommandWrapper: RetailSettingsCommandWrapper,
        paymentSettingsCommandWrapper: PaymentSettingsCommandWrapper
    ): RetailSaleSettingsCommandWrapper {
        return RetailSaleSettingsCommandWrapperImpl(retailSettingsCommandWrapper, paymentSettingsCommandWrapper)
    }

    @Provides
    internal fun provideMapper(context: Context):
            BaseModelMapper<ControllerRetailSettings, RetailSettings> =
            RetailSettingsMapper(context)

    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<ListResultOfSettingsMapOfStringString, PagedListResult<RetailSettings>> =
            RetailSettingsListMapper(context)

    @Provides
    internal fun provideCreateCommand(repository: RetailSettingsRepository):
            CreateObservableCommand<ControllerRetailSettings> =
            CreateCommand(repository)

    @Provides
    internal fun provideReadCommand(repository: RetailSettingsRepository,
                                    mapper: BaseModelMapper<ControllerRetailSettings, RetailSettings>):
            ReadObservableCommand<RetailSettings> =
            ReadCommand<RetailSettings, ControllerRetailSettings>(repository, mapper)

    @Provides
    internal fun provideUpdateCommand(repository: RetailSettingsRepository):
            UpdateObservableCommand<ControllerRetailSettings> =
            UpdateCommand(repository)

    @Provides
    internal fun provideDeleteCommand(repository: RetailSettingsRepository):
            DeleteRepositoryCommand<ControllerRetailSettings> =
            DeleteRepositoryCommandImpl(repository)

    @Provides
    internal fun provideListCommand(repository: RetailSettingsRepository,
                                    mapper: BaseModelMapper<ListResultOfSettingsMapOfStringString, PagedListResult<RetailSettings>>):
            BaseListObservableCommand<PagedListResult<RetailSettings>, SettingsFilter, DataRefreshedCallback> =
            BaseListCommand(repository, mapper)
}
