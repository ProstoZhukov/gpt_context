package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.crud.sbis.retail_settings.model.RetailSettings
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.retail_settings.generated.DataRefreshedCallback
import ru.tensor.sbis.retail_settings.generated.SettingsFilter
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.sbis.retail_settings.generated.Settings as ControllerRetailSettings

/**
 * Wrapper команд для контроллера
 */
interface RetailSettingsCommandWrapper {

    val createCommand: CreateObservableCommand<ControllerRetailSettings>
    val readCommand: ReadObservableCommand<RetailSettings>
    val updateCommand: UpdateObservableCommand<ControllerRetailSettings>
    val deleteCommand: DeleteRepositoryCommand<ControllerRetailSettings>

    val listCommand: BaseListObservableCommand<PagedListResult<RetailSettings>, SettingsFilter, DataRefreshedCallback>

    /**
     * Функция для получения настроек
     */
    fun getSettings(): Single<RetailSettings>

    /**
     * Функция для установки настроек причин возвратов/удалений
     *
     * @param allowCashierCancel - флаг, обозначающий возможность удаления причин возвратов всем пользователям, если true - можно всем, иначе - только администратору
     * @param returnRequireSale - флаг, обозначающий возможность делать возврат только по чеку, если true - только по чеку, иначе - нет
     */
    fun setSettings(allowCashierCancel: Boolean, returnRequireSale: Boolean): Completable
}
