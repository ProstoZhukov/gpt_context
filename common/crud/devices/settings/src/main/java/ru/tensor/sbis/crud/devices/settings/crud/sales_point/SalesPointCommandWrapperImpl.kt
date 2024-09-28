package ru.tensor.sbis.crud.devices.settings.crud.sales_point

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.devices.settings.generated.DataRefreshedSalesPointFacadeCallback
import ru.tensor.devices.settings.generated.SalesPointFilter
import ru.tensor.devices.settings.generated.VisibilityType
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.command.BreadCrumbManager
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.command.BreadCrumbsSalesPointListCommand
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.command.SalesPointListCommand
import ru.tensor.sbis.crud.devices.settings.model.AlcoholMarkingSettings
import ru.tensor.sbis.crud.devices.settings.model.SalesItem
import ru.tensor.sbis.crud.devices.settings.model.SalesPoint
import ru.tensor.sbis.crud.devices.settings.model.TaxSystem
import ru.tensor.sbis.crud.devices.settings.model.TimeZoneInside
import ru.tensor.sbis.crud.devices.settings.model.map
import ru.tensor.sbis.crud.payment_settings.model.AlcoholSaleSettings
import ru.tensor.sbis.crud.payment_settings.model.CrudSubscriptionWrapper
import ru.tensor.sbis.crud.payment_settings.model.map
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import java.lang.ref.WeakReference

/** @see SalesPointCommandWrapper */
internal class SalesPointCommandWrapperImpl(
        private val repository: SalesPointRepository,
        override val listCommand: BaseListObservableCommand<PagedListResult<BaseItem<SalesPoint>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback>,
        override val breadCrumbsListCommand: BaseListObservableCommand<PagedListResult<BaseItem<SalesItem>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback>
) :
    SalesPointCommandWrapper,
    BaseInteractor() {

    override fun readId(companyId: Long): Observable<SalesPoint> =
        Observable.fromCallable { repository.readId(companyId) }
            .map { it.map() }
            .compose(getObservableBackgroundSchedulers())

    override fun readId(salePointId: String): Observable<SalesPoint> =
        Observable.fromCallable { repository.readId(salePointId) }
            .map { it.map() }
            .compose(getObservableBackgroundSchedulers())

    override fun update(salesPoint: SalesPoint): Observable<SalesPoint> =
        Observable.fromCallable { repository.update(salesPoint.map()) }
            .map { it.map() }
            .compose(getObservableBackgroundSchedulers())

    override fun getWarehouseId(companyId: Long): Observable<Long> =
        Observable.fromCallable {
            repository.getWarehouseId(companyId)
                ?: throw IllegalArgumentException("WarehouseId with CompanyId=$companyId not found")
        }
            .compose(getObservableBackgroundSchedulers())

    override fun getWarehouseIdSynchronously(companyId: Long): Observable<Long> =
        Observable.fromCallable {
            repository.getWarehouseIdSynchronously(companyId)
                ?: throw IllegalArgumentException("WarehouseId with CompanyId=$companyId not found")
        }
            .compose(getObservableBackgroundSchedulers())

    override fun getWriteoffWarehouseSynchronously(companyId: Long): Observable<Long> =
        Observable.fromCallable {
            repository.getWriteoffWarehouseSynchronously(companyId)
                ?: throw IllegalArgumentException("WriteoffWarehouse with CompanyId=$companyId not found")
        }
            .compose(getObservableBackgroundSchedulers())

    override fun getDefaultWarehouseSynchronously(companyId: Long): Observable<Long> =
        Observable.fromCallable {
            repository.getDefaultWarehouseSynchronously(companyId)
                ?: throw IllegalArgumentException("DefaultWarehouse with CompanyId=$companyId not found")
        }
            .compose(getObservableBackgroundSchedulers())

    override fun getFilterHashCode(): String? = (listCommand as SalesPointListCommand).hashCodeFilter

    override fun getBreadCrumbsParentFolderItem(): Pair<String, String>? =
        (breadCrumbsListCommand as BreadCrumbsSalesPointListCommand).parentFolderItem

    override fun getBreadCrumbsFilterHashCode(): String? =
        (breadCrumbsListCommand as BreadCrumbsSalesPointListCommand).hashCodeFilter

    override fun getBreadCrumbsManager(): BreadCrumbManager {
        return (listCommand as SalesPointListCommand).breadCrumbManager
    }

    override fun fetch(companyId: Long): Observable<List<SalesPoint>> =
        listCommand.list(
            SalesPointFilter().apply {
                company = companyId
                fetchWarehouse = true
                fetchMainTaxSystem = true
                visible = VisibilityType.SHOW_ALL
            }
        )
            .map { it.dataList.map { salesPoint -> salesPoint.data } }
            .compose(getObservableBackgroundSchedulers())

    override fun getAvailableTimeZones(): Single<List<TimeZoneInside>> =
        Single.fromCallable { repository.getAvailableTimeZones() }
            .compose(getSingleBackgroundSchedulers())

    override fun setSalesPointRefreshCallback(callback: () -> Unit): Single<CrudSubscriptionWrapper> {
        val weakCallback = WeakReference(callback)
        return listCommand
            .subscribeDataRefreshedEvent(object : DataRefreshedSalesPointFacadeCallback() {
                override fun onEvent(param: HashMap<String, String>) {
                    weakCallback.get()?.invoke()
                }
            })
            .map { CrudSubscriptionWrapper(it) }
            .firstOrError()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getSalesPointTaxSystems(companyId: Long): Observable<List<TaxSystem>> =
        Observable.fromCallable { repository.getTaxSystems(companyId) }
            .compose(getObservableBackgroundSchedulers())

    override fun updateAlcoholSaleSettings(settings: AlcoholSaleSettings, companyId: Long): Completable =
        Completable.fromCallable { repository.updateAlcoholSaleSettings(settings, companyId) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun updateAlcoMarkingSettings(settings: AlcoholMarkingSettings, companyId: Long): Completable =
        Completable.fromCallable { repository.updateAlcoMarkingSettings(settings, companyId) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getCurrentAlcoholSaleSettings(companyId: Long): Single<AlcoholSaleSettings> =
        loadSalesPointSettings(companyId).map {
            it.alcoholSaleSettings.map()
        }
            .compose(getSingleBackgroundSchedulers())

    override fun getCurrentTimeZoneName(companyId: Long): Single<String> =
        loadSalesPointSettings(companyId).map {
            it.timeZoneName
        }
            .compose(getSingleBackgroundSchedulers())

    private fun loadSalesPointSettings(companyId: Long) =
        Single.fromCallable {
            val salesPointFilter = SalesPointFilter().apply {
                company = companyId
                isFolder = false
            }
            repository.refresh(salesPointFilter)
        }.map {
            it.result.first().settings
        }

    override fun getAlcoMarkingSettings(companyId: Long): Single<AlcoholMarkingSettings> =
        Single.fromCallable { repository.getAlcoMarkingSettings(companyId) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}
