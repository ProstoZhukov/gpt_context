package ru.tensor.sbis.crud.devices.settings.crud.sales_point

import ru.tensor.devices.settings.generated.*
import ru.tensor.devices.settings.generated.SalesPoint
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.devices.settings.model.*
import ru.tensor.sbis.crud.devices.settings.model.TaxSystem
import ru.tensor.sbis.crud.payment_settings.model.AlcoholSaleSettings
import ru.tensor.sbis.crud.payment_settings.model.map
import ru.tensor.sbis.platform.generated.Subscription

/** @see SalesPointRepository */
internal class SalesPointRepositoryImpl(private val controller: DependencyProvider<SalesPointFacade>) :
    SalesPointRepository {

    override fun readId(salePointId: String): SalesPoint =
        controller.get().readId(salePointId) ?: SalesPoint()

    override fun readId(companyId: Long): SalesPoint =
        controller.get().readId(companyId) ?: SalesPoint()

    override fun update(entity: SalesPoint): SalesPoint =
        controller.get().update(entity)

    override fun getWarehouseId(companyId: Long): Long? =
        controller.get().getWarehouse(companyId)

    override fun getWarehouseIdSynchronously(companyId: Long): Long? =
        controller.get().getWarehouseSynchronously(companyId)

    override fun getWriteoffWarehouseSynchronously(companyId: Long): Long? =
        controller.get().getWriteoffWarehouseSynchronously(companyId)

    override fun getDefaultWarehouseSynchronously(companyId: Long): Long? =
        controller.get().getDefaultWarehouseSynchronously(companyId)

    override fun list(filter: SalesPointFilter): ListResultOfSalesPointMapOfStringString =
        controller.get().list(filter)

    override fun refresh(filter: SalesPointFilter): ListResultOfSalesPointMapOfStringString =
        controller.get().refresh(filter)

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedSalesPointFacadeCallback): Subscription =
        controller.get().dataRefreshed().subscribe(callback)

    override fun getAvailableTimeZones(): List<TimeZoneInside> =
        controller.get().getAvailableTimeZones().map { it.map() }

    override fun getTaxSystems(companyId: Long): List<TaxSystem> =
        controller.get().getTaxSystems(companyId).map { it.map() }

    override fun getCurrentAlcoholSaleSettings(): AlcoholSaleSettings? =
        controller.get().getCurrentAlcoholSaleSettings()?.map()

    override fun updateAlcoholSaleSettings(settings: AlcoholSaleSettings, companyId: Long) {
        controller.get().updateAlcoholSaleSettings(settings.map(), companyId)
    }

    override fun updateAlcoMarkingSettings(settings: AlcoholMarkingSettings, companyId: Long) {
        controller.get().updateAlcoMarkingSettings(settings.toControllerType(), companyId)
    }

    override fun getAlcoMarkingSettings(companyId: Long) : AlcoholMarkingSettings {
        return controller.get().getAlcoMarkingSettings(companyId).toAndroidType()
    }

    override fun getCurrentSalePointRegion(): String? {
        return controller.get().getCurrentRegionCode()
    }
}
