package ru.tensor.sbis.crud.sbis.retail_settings.di.repository

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateObservableCommand
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSettingsCommandWrapper
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSettingsListFilter
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSettingsRepository
import ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.RetailSaleSettingsCommandWrapper
import ru.tensor.sbis.crud.sbis.retail_settings.model.RetailSettings
import ru.tensor.sbis.retail_settings.generated.DataRefreshedCallback
import ru.tensor.sbis.retail_settings.generated.ListResultOfSettingsMapOfStringString
import ru.tensor.sbis.retail_settings.generated.SettingsFacade
import ru.tensor.sbis.retail_settings.generated.SettingsFilter
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.sbis.retail_settings.generated.Settings as ControllerRetailSettings

interface RetailSettingsComponent {

    fun getRetailSettingsFacade(): DependencyProvider<SettingsFacade>

    fun getRetailSettingsListFilter(): RetailSettingsListFilter

    fun getRetailSettingsRepository(): RetailSettingsRepository
    fun getRetailSettingsCommandWrapper(): RetailSettingsCommandWrapper
    fun getRetailSaleSettingsCommandWrapper(): RetailSaleSettingsCommandWrapper

    fun getRetailSettingsMapper(): BaseModelMapper<ControllerRetailSettings, RetailSettings>
    fun getRetailSettingsListMapper(): BaseModelMapper<ListResultOfSettingsMapOfStringString, PagedListResult<RetailSettings>>

    fun getRetailSettingsCreateCommand(): CreateObservableCommand<ControllerRetailSettings>
    fun getRetailSettingsReadCommand(): ReadObservableCommand<RetailSettings>
    fun getRetailSettingsUpdateCommand(): UpdateObservableCommand<ControllerRetailSettings>
    fun getRetailSettingsDeleteCommand(): DeleteRepositoryCommand<ControllerRetailSettings>
    fun getRetailSettingsListCommand(): BaseListObservableCommand<PagedListResult<RetailSettings>, SettingsFilter, DataRefreshedCallback>
}
