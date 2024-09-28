package ru.tensor.sbis.crud.sale.crud.refusal_reason.command

import io.reactivex.Observable
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonRepository
import ru.tensor.sbis.crud.sale.model.RefusalReason
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonDataRefreshCallback
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonFilter
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonListResult
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonSubscription

/**@SelfDocumented */
internal class RefusalReasonListCommand(private val repository: RefusalReasonRepository,
                                        private val listMapper: BaseModelMapper<RefusalReasonListResult, PagedListResult<RefusalReason>>) :
        BaseCommand(),
        ListObservableCommand<PagedListResult<RefusalReason>, RefusalReasonFilter> {

    override fun subscribeDataRefreshedEvent(callback: DataRefreshCallback): Observable<Subscription> {
        return Observable.fromCallable { repository.setDataRefreshCallback(callback.map()) }
                .map { it.map() }
                .compose(getObservableBackgroundSchedulers())
    }

    override fun list(filter: RefusalReasonFilter): Observable<PagedListResult<RefusalReason>> =
            performAction(Observable.fromCallable { repository.list(filter) }
                    .map(listMapper)
                    .compose(getObservableBackgroundSchedulers()))

    override fun refresh(filter: RefusalReasonFilter): Observable<PagedListResult<RefusalReason>> =
            performAction(Observable.fromCallable { repository.refresh(filter) }
                    .map(listMapper)
                    .compose(getObservableBackgroundSchedulers()))
}

private fun DataRefreshCallback.map(): RefusalReasonDataRefreshCallback {
    return RefusalReasonAppCallback(this)
}

private fun RefusalReasonSubscription.map(): Subscription {
    return RefusalReasonControllerSubscription(this)
}

private class RefusalReasonControllerSubscription(val subscription: RefusalReasonSubscription) : Subscription() {
    override fun enable() = subscription.enable()
    override fun disable() = subscription.disable()
}

private class RefusalReasonAppCallback(val callback: DataRefreshCallback) : RefusalReasonDataRefreshCallback() {
    override fun execute() = callback.execute(hashMapOf())
}
