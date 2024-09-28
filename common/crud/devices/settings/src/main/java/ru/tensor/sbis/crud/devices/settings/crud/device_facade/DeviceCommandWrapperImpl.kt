package ru.tensor.sbis.crud.devices.settings.crud.device_facade

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.devices.settings.generated.DataRefreshedDeviceFacadeCallback
import ru.tensor.devices.settings.generated.DeviceFilter
import ru.tensor.sbis.crud.devices.settings.model.DeviceCanUpdate
import ru.tensor.sbis.crud.devices.settings.model.DeviceInside
import ru.tensor.sbis.crud.devices.settings.model.DeviceKindInside
import ru.tensor.sbis.crud.devices.settings.model.DeviceType
import ru.tensor.sbis.crud.devices.settings.model.TaxSystem
import ru.tensor.sbis.crud.devices.settings.model.map
import ru.tensor.sbis.crud.devices.settings.model.toAndroidType
import ru.tensor.sbis.crud.devices.settings.model.toControllerType
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.devices.settings.generated.Device as ControllerDevice

/** @see DeviceCommandWrapper */
internal class DeviceCommandWrapperImpl constructor(private val repository: DeviceRepository,
                                        override val createCommand: CreateObservableCommand<ControllerDevice>,
                                        override val readCommand: ReadObservableCommand<DeviceInside>,
                                        override val deleteCommand: DeleteRepositoryCommand<ControllerDevice>,
                                        override val listCommand: BaseListObservableCommand<PagedListResult<DeviceInside>, DeviceFilter, DataRefreshedDeviceFacadeCallback>) :
        DeviceCommandWrapper,
        BaseInteractor() {

    override fun create(model: DeviceInside): Observable<DeviceInside> =
            Observable.fromCallable { repository.create(model.toControllerType()) }
                    .map { it.toAndroidType() }
                    .compose(getObservableBackgroundSchedulers())

    override fun createBaseDevice(model: DeviceType, workplace: Long): Single<DeviceInside> =
        Single.fromCallable { repository.createBaseDevice(model.toControllerType(workplace)) }
            .map { it.toAndroidType() }
            .compose(getSingleBackgroundSchedulers())

    override fun read(id: Long): Observable<DeviceInside> =
        Observable.fromCallable {
            repository.read(id)?.toAndroidType()
                ?: throw IllegalArgumentException("Device with id=$id not found")
        }
            .compose(getObservableBackgroundSchedulers())

    override fun update(deviceInside: DeviceInside, actions: List<Int>): Observable<DeviceInside> =
        Observable.fromCallable { repository.update(deviceInside.toControllerType(), ArrayList(actions)) }
                .map { it.toAndroidType() }
                .compose(getObservableBackgroundSchedulers())

    override fun delete(id: Long): Observable<Boolean> =
            Observable.fromCallable { repository.delete(id) }
                    .compose(getObservableBackgroundSchedulers())

    override fun getActiveDevice(): Observable<DeviceInside> =
            Maybe.fromCallable { repository.getActiveDevice() }.toObservable()
                    .map { it.toAndroidType() }
                    .compose(getObservableBackgroundSchedulers())

    override fun setActiveDevice(model: DeviceInside): Completable =
            Completable.fromCallable { repository.setActiveDevice(model.toControllerType()) }
                    .compose(completableBackgroundSchedulers)

    override fun canUpdate(model: DeviceInside): Observable<DeviceCanUpdate> =
            Observable.fromCallable { repository.canUpdate(model.toControllerType()) }
                    .map { DeviceCanUpdate(it.result, it.message, it.device, it.metastate, it.actions) }
                    .compose(getObservableBackgroundSchedulers())

    override fun canUpdate(model: DeviceInside, actions: List<Int>): Observable<DeviceCanUpdate> =
        Observable.fromCallable { repository.canUpdate(model.toControllerType(), ArrayList(actions)) }
            .map { DeviceCanUpdate(it.result, it.message, it.device, it.metastate, it.actions) }
            .compose(getObservableBackgroundSchedulers())

    override fun deactivate(deviceId: Long): Completable =
            Completable.fromCallable { repository.deactivate(deviceId) }
                    .compose(completableBackgroundSchedulers)

    override fun deactivate(deviceId: Long, actions: List<Int>): Completable =
        Completable.fromCallable { repository.deactivate(deviceId, ArrayList(actions)) }
            .compose(completableBackgroundSchedulers)

    override fun getActivePaymentTerminal(): Maybe<ControllerDevice?> =
        Maybe.fromCallable { repository.getActivePaymentTerminal() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getActiveScales(): Maybe<ControllerDevice?> =
        Maybe.fromCallable { repository.getActiveScales() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getActivePaymentTerminalSync() = repository.getActivePaymentTerminal()

    override fun getActiveScalesSync() = repository.getActiveScales()

    override fun getActiveQrDisplay() = repository.getActiveBuyerDisplay()

    override fun getActivePrinterSync() = repository.getActivePrinter()

    override fun getActiveScannersFromList(devices: List<DeviceInside>) =
        getActiveScannersInsideByKindFromList(devices)

    override fun getActivePaymentTerminalFromList(devices: List<DeviceInside>) =
        getActiveDeviceInsideByKindFromList(devices, DeviceKindInside.PAYMENT_TERMINAL)

    override fun getActivePrinterFromList(devices: List<DeviceInside>) =
        getActiveDeviceInsideByKindFromList(devices, DeviceKindInside.SYSTEM_PRINTER)

    override fun getActiveScalesFromList(devices: List<DeviceInside>) =
        getActiveDeviceInsideByKindFromList(devices, DeviceKindInside.POS_SCALES)

    override fun getActiveQrDisplayFromList(devices: List<DeviceInside>) =
        getActiveDeviceInsideByKindFromList(devices, DeviceKindInside.QR_CODE_DISPLAY)

    override fun getActiveVideocamsFromList(devices: List<DeviceInside>) =
        getActiveDevicesInsideByKindFromList(devices, DeviceKindInside.VIDEOCAM)

    override fun getDisabledTaxSystems(deviceId: Long): Observable<List<TaxSystem>> =
        Observable.fromCallable { repository.getDisabledTaxSystems(deviceId) }
            .map { list -> list.map { it.map() } }
            .compose(getObservableBackgroundSchedulers())

    override fun setDisabledTaxSystems(deviceId: Long, taxSystems: ArrayList<TaxSystem>): Completable =
        Completable.fromCallable { repository.setDisabledTaxSystems(deviceId, ArrayList(taxSystems.map { it.map() })) }
            .compose(completableBackgroundSchedulers)

    private fun getActiveDeviceInsideByKindFromList(devices: List<DeviceInside>, kind: DeviceKindInside) =
        devices.firstOrNull { it.kind == kind && it.isActive }

    private fun getActiveDevicesInsideByKindFromList(devices: List<DeviceInside>, kind: DeviceKindInside) =
        devices.filter { it.kind == kind  && it.isActive}

    private fun getActiveScannersInsideByKindFromList(devices: List<DeviceInside>) =
        devices.filter { it.kind == DeviceKindInside.POS_SCANNER && it.isActive }

    override fun syncPrintingSettings(deviceId: Long): Completable {
        return Completable.fromCallable { repository.syncPrintingSettings(arrayListOf(deviceId)) }
            .compose(completableBackgroundSchedulers)
    }

    override fun setSerialAndRegNumberSync(deviceID: Long, serialNumber: String, regNumber: String) {
        repository.setSerialAndRegNumber(deviceID, serialNumber, regNumber)
    }
}
