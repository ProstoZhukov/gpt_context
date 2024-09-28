package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.crud.sbis.retail_settings.model.*
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateObservableCommand
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.retail_settings.generated.DataRefreshedCallback
import ru.tensor.sbis.retail_settings.generated.SettingsFilter
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.sbis.retail_settings.generated.Settings as ControllerRetailSettings

internal class RetailSettingsCommandWrapperImpl(
    private val repository: RetailSettingsRepository,
    override val createCommand: CreateObservableCommand<ControllerRetailSettings>,
    override val readCommand: ReadObservableCommand<RetailSettings>,
    override val updateCommand: UpdateObservableCommand<ControllerRetailSettings>,
    override val deleteCommand: DeleteRepositoryCommand<ControllerRetailSettings>,
    override val listCommand: BaseListObservableCommand<PagedListResult<RetailSettings>, SettingsFilter, DataRefreshedCallback>
) : RetailSettingsCommandWrapper, BaseInteractor() {

    override fun getSettings(): Single<RetailSettings> =
        Single.create<RetailSettings> { emitter ->
            val filter = SettingsFilter()
            val refreshOnDataRefreshed = object : DataRefreshedCallback() {
                override fun onEvent() {
                    if (emitter.isDisposed) return

                    val refreshedSettings = repository.refresh(filter).result.firstOrNull()?.map()
                    if (refreshedSettings != null) {
                        emitter.onSuccess(refreshedSettings)
                    }
                }
            }
            val subscription = repository.subscribeDataRefreshedEvent(refreshOnDataRefreshed)
            emitter.setCancellable { subscription.disable() }

            val settings = repository.list(filter).result.firstOrNull()?.map()
            if (settings != null) {
                emitter.onSuccess(settings)
            }
        }.compose(getSingleBackgroundSchedulers())

    override fun setSettings(allowCashierCancel: Boolean, returnRequireSale: Boolean): Completable =
        Completable.fromCallable { repository.setSettings(allowCashierCancel, returnRequireSale) }
            .compose(completableBackgroundSchedulers)
}