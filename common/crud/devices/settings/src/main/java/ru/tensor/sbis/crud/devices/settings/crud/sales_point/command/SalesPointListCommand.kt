package ru.tensor.sbis.crud.devices.settings.crud.sales_point.command

import io.reactivex.Observable
import ru.tensor.devices.settings.generated.DataRefreshedSalesPointFacadeCallback
import ru.tensor.devices.settings.generated.ListResultOfSalesPointMapOfStringString
import ru.tensor.devices.settings.generated.Metadata
import ru.tensor.devices.settings.generated.SalesPointFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseCommand
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointRepository
import ru.tensor.sbis.crud.devices.settings.model.SalesPoint
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.platform.generated.Subscription
import java.util.HashMap

/** @SelfDocumented */
typealias SalesPointListObservableCommand = BaseListObservableCommand<PagedListResult<BaseItem<SalesPoint>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback>

/** Класс для получения списочных данных о точках продаж */
internal class SalesPointListCommand(private val repository: SalesPointRepository,
                                     private val listMapper: BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesPoint>>>) :
        BaseCommand(),
        SalesPointListObservableCommand {

    val breadCrumbManager = BreadCrumbManagerImpl()

    /** HashCode фильтра, с которым была вызвана команда List */
    var hashCodeFilter: String? = null

    /**@SelfDocumented */
    override fun list(filter: SalesPointFilter):
            Observable<PagedListResult<BaseItem<SalesPoint>>> =
            performAction(Observable.fromCallable { repository.list(filter) }).compose(filter)

    /**@SelfDocumented */
    override fun refresh(filter: SalesPointFilter):
            Observable<PagedListResult<BaseItem<SalesPoint>>> =
            performAction(Observable.fromCallable { repository.refresh(filter) }).compose(filter)

    /**@SelfDocumented */
    override fun subscribeDataRefreshedEvent(callback: DataRefreshedSalesPointFacadeCallback): Observable<Subscription> =
        Observable.fromCallable { repository.subscribeDataRefreshedEvent(callback) }
            .compose(getObservableBackgroundSchedulers())

    private fun Observable<ListResultOfSalesPointMapOfStringString>.compose(filter: SalesPointFilter):
            Observable<PagedListResult<BaseItem<SalesPoint>>> =
            doOnNext {
                val folderId = it?.metadata?.get(Metadata.PARENT_FOLDER_ID)
                val folderTitle = it?.metadata?.get(Metadata.PARENT_FOLDER_NAME)
                if (it.metadata != null) {
                    breadCrumbManager.proceed(folderId, folderTitle)
                }
                hashCodeFilter = if (it.metadata != null) it.metadata?.get(Metadata.FILTER_HASH) ?: "" else null
            }
                    .map(listMapper)
                    .compose(getObservableBackgroundSchedulers())
}

/**
 * Загрузить данные точек продаж методом list и обновлять через refresh при их изменении из-за синхронизации.
 */
fun SalesPointListObservableCommand.listAndRefreshOnDataRefreshed(filter: SalesPointFilter) =
    Observable.create { emitter ->
        val refreshOnDataRefreshed = object : DataRefreshedSalesPointFacadeCallback() {
            override fun onEvent(params: HashMap<String, String>) {
                val refreshResult = refresh(filter).blockingFirst()
                emitter.onNext(refreshResult)
            }
        }
        val subscription = subscribeDataRefreshedEvent(refreshOnDataRefreshed).blockingFirst()

        emitter.setCancellable { subscription.disable() }
        if (emitter.isDisposed) {
            subscription.disable()
            return@create
        }

        val listResult = list(filter).blockingFirst()
        emitter.onNext(listResult)
    }