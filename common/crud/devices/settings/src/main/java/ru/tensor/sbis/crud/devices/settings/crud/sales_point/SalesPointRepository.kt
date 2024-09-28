package ru.tensor.sbis.crud.devices.settings.crud.sales_point

import ru.tensor.devices.settings.generated.ListResultOfSalesPointMapOfStringString
import ru.tensor.devices.settings.generated.SalesPoint
import ru.tensor.devices.settings.generated.SalesPointFilter
import ru.tensor.devices.settings.generated.DataRefreshedSalesPointFacadeCallback
import ru.tensor.sbis.crud.devices.settings.model.AlcoholMarkingSettings
import ru.tensor.sbis.crud.devices.settings.model.TaxSystem
import ru.tensor.sbis.crud.devices.settings.model.TimeZoneInside
import ru.tensor.sbis.crud.payment_settings.model.AlcoholSaleSettings
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository

/**
 * Интерфейс для связи с контроллером.
 */
interface SalesPointRepository :
        BaseListRepository<ListResultOfSalesPointMapOfStringString, SalesPointFilter, DataRefreshedSalesPointFacadeCallback> {

    /**
     * Функция для обновления точки продажи
     *
     * @param entity - модель точки продажи
     *
     * @return SalesPoint
     */
    fun update(entity: SalesPoint): SalesPoint

    /**
     * Функция для получения точки продажи по идентификатору
     *
     * @param salePointId - идентификатор точки продажи String
     *
     * @return SalesPoint
     */
    fun readId(salePointId: String): SalesPoint

    /**
     * Функция для получения точки продажи по идентификатору
     *
     * @param companyId - идентификатор компании Int
     *
     * @return SalesPoint
     */
    fun readId(companyId: Long): SalesPoint

    /**
     * Функция для получения идентификатора склада по идентификатору компании
     *
     * @param companyId - идентификатор компании Int
     *
     * @return Int - идентификатор склада
     */
    fun getWarehouseId(companyId: Long): Long?

    /**
     * Функция для получения идентификатора склада по идентификатору компании.
     * При наличии связи запрашивает данные синхронно с облака
     *
     * @param companyId - идентификатор компании
     *
     * @return идентификатор склада
     */
    fun getWarehouseIdSynchronously(companyId: Long): Long?

    /**
     * Функция для получения идентификатора склада списания по идентификатору компании.
     * При наличии связи запрашивает данные синхронно с облака
     *
     * @param companyId - идентификатор компании
     *
     * @return идентификатор склада
     */
    fun getWriteoffWarehouseSynchronously(companyId: Long): Long?

    /**
     * Функция для получения идентификатора склада по умолчанию по идентификатору компании.
     * При наличии связи запрашивает данные синхронно с облака
     *
     * @param companyId - идентификатор компании
     *
     * @return идентификатор склада по умолчанию
     */
    fun getDefaultWarehouseSynchronously(companyId: Long): Long?

    /**
     * Метод получения доступных временных зон точки продаж
     */
    fun getAvailableTimeZones(): List<TimeZoneInside>

    /**
     * Метод получения СНО
     */
    fun getTaxSystems(companyId: Long): List<TaxSystem>

    /**
     * Метод получения настроек алкоголя.
     */
    fun getCurrentAlcoholSaleSettings() : AlcoholSaleSettings?

    /**
     * Метод обновления настроек продажи алкоголя.
     */
    fun updateAlcoholSaleSettings(settings: AlcoholSaleSettings, companyId: Long)

    /**
     * Метод обновления настроек продажи и маркировки алкоголя.
     */
    fun updateAlcoMarkingSettings(settings: AlcoholMarkingSettings, companyId: Long)

    /**
     * Метод получения настроек продажи и маркировки алкоголя.
     */
    fun getAlcoMarkingSettings(companyId: Long) : AlcoholMarkingSettings

    /** Получить регион текущей ТП. */
    fun getCurrentSalePointRegion(): String?
}
