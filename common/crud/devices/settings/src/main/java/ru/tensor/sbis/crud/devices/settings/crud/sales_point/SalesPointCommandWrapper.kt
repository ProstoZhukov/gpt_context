package ru.tensor.sbis.crud.devices.settings.crud.sales_point

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.devices.settings.generated.AlcoMarkingSettings
import ru.tensor.devices.settings.generated.DataRefreshedSalesPointFacadeCallback
import ru.tensor.devices.settings.generated.SalesPointFilter
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.command.BreadCrumbManager
import ru.tensor.sbis.crud.devices.settings.model.AlcoholMarkingSettings
import ru.tensor.sbis.crud.devices.settings.model.SalesItem
import ru.tensor.sbis.crud.devices.settings.model.SalesPoint
import ru.tensor.sbis.crud.devices.settings.model.TaxSystem
import ru.tensor.sbis.crud.devices.settings.model.TimeZoneInside
import ru.tensor.sbis.crud.payment_settings.model.AlcoholSaleSettings
import ru.tensor.sbis.crud.payment_settings.model.CrudSubscriptionWrapper
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand

/**
 * Wrapper команд для контроллера для работы с настройками.
 */
interface SalesPointCommandWrapper {

    /** Команда для получения точек продаж */
    val listCommand: BaseListObservableCommand<PagedListResult<BaseItem<SalesPoint>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback>

    /** Команда для получения моделей точек продаж для хлебных крошек  */
    val breadCrumbsListCommand: BaseListObservableCommand<PagedListResult<BaseItem<SalesItem>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback>

    /**
     * Функция для получения HashCode фильтра, с которым была вызвана команда List
     *
     * @return HashCode фильтра
     */
    fun getFilterHashCode(): String?

    /**
     * Функция для получения айди родителя и имя родителя текущего каталога
     * при поиске с хлебными крошками
     *
     * @return пара айди родителя и имя родителя текущего каталога
     */
    fun getBreadCrumbsParentFolderItem(): Pair<String, String>?

    /**
     * Функция для получения HashCode фильтра, с которым была вызвана команда List
     * при поиске с хлебными крошками
     *
     * @return HashCode фильтра
     */
    fun getBreadCrumbsFilterHashCode(): String?

    /**
     * Функция для получения экземпляра менеджера хлебных крошек
     */
    fun getBreadCrumbsManager(): BreadCrumbManager

    /**
     * Функция для обновления точки продажи
     *
     * @param salesPoint - модель точки продажи
     *
     * @return SalesPoint
     */
    fun update(salesPoint: SalesPoint): Observable<SalesPoint>

    /**
     * Функция для получения точки продажи по идентификатору
     *
     * @param salePointId - идентификатор точки продажи String
     *
     * @return SalesPoint
     */
    fun readId(salePointId: String): Observable<SalesPoint>

    /**
     * Функция для получения точки продажи по идентификатору
     *
     * @param companyId - идентификатор компании Long
     *
     * @return SalesPoint
     */
    fun readId(companyId: Long): Observable<SalesPoint>

    /**
     * Функция для получения идентификатора склада по идентификатору компании
     *
     * @param companyId - идентификатор компании Long
     *
     * @return Int - идентификатор склада
     */
    fun getWarehouseId(companyId: Long): Observable<Long>

    /**
     * Функция для получения идентификатора склада по идентификатору компании.
     * При наличии связи запрашивает данные синхронно с облака
     *
     * @param companyId - идентификатор компании
     *
     * @return идентификатор склада
     */
    fun getWarehouseIdSynchronously(companyId: Long): Observable<Long>

    /**
     * Функция для получения идентификатора склада списания по идентификатору компании.
     * При наличии связи запрашивает данные синхронно с облака
     *
     * @param companyId - идентификатор компании
     *
     * @return идентификатор склада
     */
    fun getWriteoffWarehouseSynchronously(companyId: Long): Observable<Long>

    /**
     * Функция для получения идентификатора склада по умолчанию по идентификатору компании.
     * При наличии связи запрашивает данные синхронно с облака
     *
     * @param companyId - идентификатор компании
     *
     * @return идентификатор склада по умолчанию
     */
    fun getDefaultWarehouseSynchronously(companyId: Long): Observable<Long>

    /**
     * Функция для синхронизации данных точки продажи по идентификатору
     *
     * @param companyId - идентификатор компании Int
     *
     * @return List<SalesPoint>
     */
    fun fetch(companyId: Long): Observable<List<SalesPoint>>

    /**
     * Асинхронный метод получения доступных временных зон точки продаж
     */
    fun getAvailableTimeZones(): Single<List<TimeZoneInside>>

    /**
     * Установка callback-а на обновление точек продаж
     *
     * @param callback callback на обновление точек продаж
     * @return подписка [CrudSubscriptionWrapper]
     */
    fun setSalesPointRefreshCallback(callback: () -> Unit): Single<CrudSubscriptionWrapper>

    /**
     * Функция получения СНО точки продаж
     *
     * @param companyId - идентификатор компании Int
     *
     * @return List<TaxSystem>
     */
    fun getSalesPointTaxSystems(companyId: Long): Observable<List<TaxSystem>>

    /**
     * Функция для обновления настроек продажи алкоголя.
     *
     * @param settings - настройки продажи Алкоголя [AlcoholSaleSettings]
     * @param companyId - идентификатор компании Int
     */
    fun updateAlcoholSaleSettings(settings: AlcoholSaleSettings, companyId: Long): Completable

    /**
     * Функция для обновления настроек продажи и маркировки алкоголя.
     *
     * @param settings - настройки продажи Алкоголя [AlcoMarkingSettings]
     * @param companyId - идентификатор компании Int
     */
    fun updateAlcoMarkingSettings(settings: AlcoholMarkingSettings, companyId: Long): Completable

    /**
     * Функция для получения текущих настроек продажи алкоголя.
     *
     * @param companyId - идентификатор компании Int
     * @return [AlcoholSaleSettings]
     */
    fun getCurrentAlcoholSaleSettings(companyId: Long): Single<AlcoholSaleSettings>

    /**
     * Функция для получения текущего часового пояса.
     *
     * @param companyId - идентификатор компании Int
     * @return String - название часового пояса
     */
    fun getCurrentTimeZoneName(companyId: Long): Single<String>

    /**
     * Функция для получения настроек продажи и маркировки алкоголя.
     *
     * @param companyId - идентификатор компании Int
     */
    fun getAlcoMarkingSettings(companyId: Long): Single<AlcoholMarkingSettings>
}