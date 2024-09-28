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
import ru.tensor.sbis.crud.devices.settings.model.SalesItem
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Класс для получения списочных данных о точках продаж в режиме хлебных крошек.
 * Используется при поиске по точкам продаж, например/
 */
internal class BreadCrumbsSalesPointListCommand(private val repository: SalesPointRepository,
                                                private val listMapper: BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesItem>>>,
                                                private val findMapper: BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesItem>>>) :
        BaseCommand(),
        BaseListObservableCommand<PagedListResult<BaseItem<SalesItem>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback> {

    /**
     * Пара: айди и имя родителя
     * Под родителем подразумевается папка, внутри которой могут быть точки продаж или другие папки
     */
    var parentFolderItem: Pair<String, String>? = null

    /** HashCode фильтра, с которым была вызвана команда List */
    var hashCodeFilter: String? = null

    /**@SelfDocumented */
    override fun list(filter: SalesPointFilter):
            Observable<PagedListResult<BaseItem<SalesItem>>> =
            performAction(Observable.fromCallable { repository.list(filter) }).compose(filter)

    /**@SelfDocumented */
    override fun refresh(filter: SalesPointFilter):
            Observable<PagedListResult<BaseItem<SalesItem>>> =
            performAction(Observable.fromCallable { repository.refresh(filter) }).compose(filter)

    /**@SelfDocumented */
    override fun subscribeDataRefreshedEvent(callback: DataRefreshedSalesPointFacadeCallback): Observable<Subscription> =
        Observable.fromCallable { repository.subscribeDataRefreshedEvent(callback) }
            .compose(getObservableBackgroundSchedulers())

    private fun Observable<ListResultOfSalesPointMapOfStringString>.compose(filter: SalesPointFilter):
            Observable<PagedListResult<BaseItem<SalesItem>>> =
            doOnNext {
                parentFolderItem =
                        if (it.metadata != null && filter.folder != null && filter.folder!!.isNotBlank()) Pair(
                                it.metadata?.get(Metadata.PARENT_FOLDER_ID) ?: "",
                                it.metadata?.get(Metadata.PARENT_FOLDER_NAME) ?: "")
                        else null

                hashCodeFilter = if (it.metadata != null) it.metadata?.get(Metadata.FILTER_HASH)
                        ?: "" else null
            }
                    .map(if (filter.searchString.isNullOrBlank()) listMapper else findMapper)
                    .compose(getObservableBackgroundSchedulers())
}
