package ru.tensor.sbis.crud.sale.crud.kkm

import ru.tensor.devices.generic.generated.Connection
import ru.tensor.devices.generic.generated.SessionInfo
import ru.tensor.devices.kkmservice.generated.KkmWarning
import ru.tensor.devices.kkmservice.generated.SupportedOperations
import ru.tensor.devices.settings.generated.Device
import ru.tensor.devices.settings.generated.DeviceFacade
import ru.tensor.devices.srf.generated.PrintProperties
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.devices.settings.model.PrintingSettings
import ru.tensor.sbis.crud.devices.settings.model.toController
import ru.tensor.sbis.sale.mobile.generated.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**@SelfDocumented */
class KkmRepositoryImpl(
    private val controller: DependencyProvider<KkmFacade>,
    private val kkmController: DependencyProvider<DeviceFacade>
) : KkmRepository {

    override fun create(): KkmModel =
            controller.get().create()

    override fun read(id: Long): KkmModel? =
            controller.get().read(id)

    override fun update(model: KkmModel): KkmModel =
            controller.get().update(model)

    override fun update(model: KkmModel, deviceConnection: Connection, actions: ArrayList<Int>): KkmModel =
            controller.get().update(model, deviceConnection, actions)

    override fun delete(id: Long): Boolean =
            controller.get().delete(id)

    override fun list(filter: KkmFilter): KkmListResult =
            controller.get().list(filter)

    override fun refresh(filter: KkmFilter): KkmListResult =
            controller.get().refresh(filter)

    override fun setDataRefreshCallback(callback: KkmDataRefreshCallback): KkmSubscription =
            controller.get().setDataRefreshCallback(callback)

    override fun saveKkm(model: KkmModel, deviceConnection: Connection) =
            controller.get().saveKkm(model, deviceConnection)

    override fun checkConnection(deviceConnection: Connection): Boolean =
            controller.get().checkConnection(deviceConnection)

    override fun getDeviceInfo(deviceConnection: Connection) =
        controller.get().getDeviceInfo(deviceConnection)

    override fun checkValidity(rnm: String): Boolean =
            controller.get().checkValidity(rnm)

    override fun getOFDUntransmittedDocumentsInfo(): OFDUntransmittedDocumentsInfo? {
        val result = controller.get().getOFDUntransmittedDocumentsInfo()
        Timber.i("KkmFacade.getOFDUntransmittedDocumentsInfo: $result")
        return result
    }

    override fun getOFDUntransmittedDocumentsInfo(deviceConnection: Connection): OFDUntransmittedDocumentsInfo? =
            controller.get().getOFDUntransmittedDocumentsInfo(deviceConnection)

    override fun getActiveKkm(): KkmModel? =
            controller.get().getActiveKkm()

    override fun getActiveKkmInfo(): KkmInfo? =
         controller.get().getActiveKkmInfo()

    override fun getDeviceSettings(id: Long): Connection? =
            controller.get().getDeviceSettings(id)

    override fun getDeviceSettings(kkm: KkmModel): Connection? =
            controller.get().getDeviceSettings(kkm)

    override fun getKkmInfo(model: KkmModel, deviceConnection: Connection): KkmInfo {
        return if(model.remoteKkmId != null) {
            controller.get().getKkmInfoRemote(model)
        } else {
            controller.get().getKkmInfoStrong(deviceConnection)
        }
    }

    override fun checkSettingDateTimeForKkm(): DateTimeCheckState {
        val result = controller.get().checkSettingDateTimeForKkm()
        Timber.i("KkmFacade.checkSettingDateTimeForKkm: $result")
        return result
    }

    override fun setDateTimeToKkm() =
            controller.get().setDateTimeToKkm()

    override fun canUpdate(model: KkmModel, deviceConnection: Connection, actions: List<Int>): KkmCanUpdate =
        controller.get().canUpdate(model, deviceConnection, ArrayList(actions))

    override fun deactivate(kkmId: Long, actions: List<Int>) =
            controller.get().deactivate(kkmId, ArrayList(actions))

    override fun openCashbox() =
            controller.get().openCashbox()

    override fun getActiveKkmSessionInfo(): SessionInfo? =
            controller.get().getActiveKkmSessionInfo()

    override fun isKKMConnected(model: KkmModel, deviceConnection: Connection): Boolean {
        return if(model.remoteKkmId != null) {
            controller.get().getKkmInfoRemote(model) != null
        } else {
            controller.get().checkConnection(deviceConnection)
        }
    }

    override fun printDocumentCopy(fiscalDocumentNumber: Long, fiscalKkmNumber: String?) {
        controller.get().printDocumentCopy(fiscalDocumentNumber,fiscalKkmNumber)
    }

    override fun printFreeContentPage(args: String) {
        controller.get().printFreeContentDocument(args)
    }

    override fun printTestTicket(connection: Connection, printingSettings: PrintingSettings) {
        controller.get().printTestTicket(connection, printingSettings.toController(), PrintProperties())
    }

    override fun getSupportedOperations(deviceConnection: Connection): SupportedOperations =
        controller.get().getSupportedOperations(deviceConnection)

    override fun getKkmWarnings(deviceConnection: Connection): List<KkmWarning> =
        controller.get().getKkmWarnings(deviceConnection)

    override fun printTicketForSale(saleUuid: UUID) {
        controller.get().printTicketForSale(saleUuid)
    }

    override fun initOfdExchangeAndRequestPermissionsIfRequired() {
        controller.get().initOfdExchangeAndRequestPermissionsIfRequired()
    }

    override fun initOfdExchangeAndRequestPermissionsIfRequired(connection: Connection) {
        controller.get().initOfdExchangeAndRequestPermissionsIfRequired(connection)
    }

    override fun kkmToDevice(model: KkmModel, deviceConnection: Connection): Device {
        return controller.get().kkmToDevice(model, deviceConnection)
    }

    override fun fetchKKM(kkmID: Long): Device {
        return kkmController.get().fetch(kkmID)
    }
}
