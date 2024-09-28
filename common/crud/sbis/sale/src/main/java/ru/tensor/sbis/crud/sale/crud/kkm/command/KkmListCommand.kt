package ru.tensor.sbis.crud.sale.crud.kkm.command

import io.reactivex.Observable
import ru.tensor.devices.generic.generated.Connection
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.crud.sale.crud.kkm.KkmRepository
import ru.tensor.sbis.crud.sale.model.CashRegister
import ru.tensor.sbis.crud.sale.model.fillConnection
import ru.tensor.sbis.crud.sale.model.isRemote
import ru.tensor.sbis.crud.sale.model.toAndroidType
import ru.tensor.sbis.sale.mobile.generated.KkmDataRefreshCallback
import ru.tensor.sbis.sale.mobile.generated.KkmFilter
import ru.tensor.sbis.sale.mobile.generated.KkmModel
import ru.tensor.sbis.sale.mobile.generated.KkmSubscription

/**@SelfDocumented */
internal class KkmListCommand(private val repository: KkmRepository) :
        BaseCommand(),
        ListObservableCommand<PagedListResult<CashRegister>, KkmFilter> {

    override fun subscribeDataRefreshedEvent(callback: DataRefreshCallback): Observable<Subscription> {
        return Observable.fromCallable { repository.setDataRefreshCallback(callback.map()) }
                .map { it.map() }
                .compose(getObservableBackgroundSchedulers())
    }

    override fun list(filter: KkmFilter): Observable<PagedListResult<CashRegister>> =
            performAction(Observable.fromCallable { repository.list(filter) }
                    .map { kkmListResult ->
                        val cashList: MutableList<CashRegister> = mutableListOf()
                        kkmListResult.result.forEach {
                            val cash = it.toAndroidType()
                            val connection = connection(cash, repository, it)
                            cash.fillConnection(connection!!)
                            cash.isConnected = repository.checkConnection(connection)
                            cashList.add(cash)
                        }
                        PagedListResult(cashList, kkmListResult.hasMore)
                    }
                    .compose(getObservableBackgroundSchedulers()))

    override fun refresh(filter: KkmFilter): Observable<PagedListResult<CashRegister>> =
            performAction(Observable.fromCallable { repository.refresh(filter) }
                    .map { kkmListResult ->
                        val cashList: MutableList<CashRegister> = mutableListOf()
                        kkmListResult.result.forEach {
                            val cash = it.toAndroidType()
                            val connection = connection(cash, repository, it)
                            cash.fillConnection(connection!!)
                            cash.isConnected = repository.checkConnection(connection)
                            cashList.add(cash)
                        }
                        PagedListResult(cashList, kkmListResult.hasMore)
                    }
                    .compose(getObservableBackgroundSchedulers()))
}

private fun DataRefreshCallback.map(): KkmDataRefreshCallback {
    return KkmAppCallback(this)
}

private fun KkmSubscription.map(): Subscription {
    return KkmControllerSubscription(this)
}

/**@SelfDocumented */
internal fun connection(
    cashRegister: CashRegister,
    repository: KkmRepository,
    kkmModel: KkmModel
): Connection? {
    return if (cashRegister.isRemote()) {
        repository.getDeviceSettings(kkmModel.remoteKkmId!!)
    } else {
        repository.getDeviceSettings(kkmModel)
    }
}

private class KkmControllerSubscription(val subscription: KkmSubscription) : Subscription() {
    override fun enable() = subscription.enable()
    override fun disable() = subscription.disable()
}

private class KkmAppCallback(val callback: DataRefreshCallback) : KkmDataRefreshCallback() {
    override fun execute() = callback.execute(hashMapOf())
}
