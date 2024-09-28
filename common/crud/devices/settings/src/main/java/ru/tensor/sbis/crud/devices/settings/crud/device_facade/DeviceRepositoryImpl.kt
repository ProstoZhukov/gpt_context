package ru.tensor.sbis.crud.devices.settings.crud.device_facade

import ru.tensor.devices.settings.generated.*
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/** @see DeviceRepository */
internal class DeviceRepositoryImpl(private val controller: DependencyProvider<DeviceFacade>) :
        DeviceRepository {

    override fun create(): Device =
            controller.get().create()

    override fun createBaseDevice(model: Device): Device =
        controller.get().createBase(model)

    override fun read(uuid: UUID): Device =
            controller.get().read(uuid) ?: Device()

    override fun readFromCache(uuid: UUID): Device =
            read(uuid)

    override fun update(model: Device): Device =
            controller.get().update(model)

    override fun delete(uuid: UUID): Boolean =
            controller.get().delete(uuid)

    override fun list(filter: DeviceFilter): ListResultOfDeviceMapOfStringString {
        return controller.get().list(filter).also { result ->
            logDeviceFacade(::list.name, result, filter)
        }
    }

    override fun refresh(filter: DeviceFilter): ListResultOfDeviceMapOfStringString {
        return controller.get().refresh(filter).also { result ->
            logDeviceFacade(::refresh.name, result, filter)
        }
    }

    private fun logDeviceFacade(
        methodName: String,
        result: ListResultOfDeviceMapOfStringString,
        filter: DeviceFilter,
    ) = with(filter) {
        Timber.i(
            """
            DeviceFacade.$methodName limit=$limit offset=$offset byMyWorkplace=$byMyWorkplace byRemotePlace=$byRemotePlace byWorkplace=$byWorkplace
            result size=${result.result.size} haveMore=${result.haveMore}
            """.trimIndent()
        )
    }

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedDeviceFacadeCallback): Subscription =
            controller.get().dataRefreshed().subscribe(callback)

    override fun create(model: Device): Device =
            controller.get().create(model)

    override fun read(id: Long): Device? =
            controller.get().read(id)

    override fun update(device: Device, actions: ArrayList<Int>): Device =
            controller.get().update(device, actions)

    override fun delete(id: Long): Boolean =
            controller.get().delete(id)

    override fun getActiveDevice(): Device? =
            controller.get().getActiveDevice()

    override fun setActiveDevice(model: Device) {
        controller.get().setActiveDevice(model)
    }

    override fun setDisabledTaxSystems(deviceId: Long, taxSystems: ArrayList<TaxSystem>) {
        controller.get().setDisabledTaxSystems(deviceId, taxSystems)
    }

    override fun canUpdate(model: Device): DeviceCanUpdate =
            controller.get().canUpdate(model)

    override fun canUpdate(model: Device, actions: ArrayList<Int>): DeviceCanUpdate =
        controller.get().canUpdate(model, actions)

    override fun deactivate(deviceId: Long) =
            controller.get().deactivate(deviceId)

    override fun deactivate(deviceId: Long, actions: ArrayList<Int>) =
        controller.get().deactivate(deviceId, actions)

    override fun getActivePaymentTerminal(): Device? = controller.get().getActivePaymentTerminal()

    override fun getActiveScales(): Device? = controller.get().getActiveScales()

    override fun getActivePrinter(): Device? = controller.get().getActivePrinter()

    override fun getActiveBuyerDisplay(): Device? = controller.get().getActiveBuyerDisplay()

    override fun getDisabledTaxSystems(deviceId: Long): List<TaxSystem> = controller.get().getDisabledTaxSystems(deviceId)

    override fun syncPrintingSettings(deviceIds: ArrayList<Long>) = controller.get().syncPrintingSettings(deviceIds)

    override fun setSerialAndRegNumber(deviceID: Long, serialNumber: String, regNumber: String) {
        controller.get().setSerialAndRegNumber(deviceID, serialNumber, regNumber)
    }
    
    override suspend fun isOnlinePaymentEnabled(deviceID: Long): Boolean {
        return controller.get().isOnlinePaymentEnabled(deviceID)
    }

    override suspend fun isQrDestinationEnabled(deviceID: Long): Boolean {
        return controller.get().isQrDestinationEnabled(deviceID)
    }
}
