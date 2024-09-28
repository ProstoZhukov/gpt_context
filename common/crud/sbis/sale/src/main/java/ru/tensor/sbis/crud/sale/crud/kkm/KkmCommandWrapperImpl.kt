package ru.tensor.sbis.crud.sale.crud.kkm

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.devices.generic.generated.Connection
import ru.tensor.devices.generic.generated.SessionState
import ru.tensor.devices.kkmservice.generated.KkmWarning
import ru.tensor.sbis.crud.devices.settings.contract.ProductionAreaCard
import ru.tensor.sbis.crud.devices.settings.model.ConnectionTypeInside
import ru.tensor.sbis.crud.devices.settings.model.DeviceCanUpdate
import ru.tensor.sbis.crud.devices.settings.model.DeviceCardSaveArgs
import ru.tensor.sbis.crud.devices.settings.model.PrintingSettings
import ru.tensor.sbis.crud.devices.settings.model.toAndroidType
import ru.tensor.sbis.crud.sale.crud.kkm.command.connection
import ru.tensor.sbis.crud.sale.model.CashRegister
import ru.tensor.sbis.crud.sale.model.DateTimeCheckStatusInside
import ru.tensor.sbis.crud.sale.model.Driver
import ru.tensor.sbis.crud.sale.model.KkmCanUpdate
import ru.tensor.sbis.crud.sale.model.KkmInfoInside
import ru.tensor.sbis.crud.sale.model.KkmWarningInside
import ru.tensor.sbis.crud.sale.model.OFDUntransmittedDocumentsInfoInside
import ru.tensor.sbis.crud.sale.model.SupportedOperationsInside
import ru.tensor.sbis.crud.sale.model.convertKkmInfo
import ru.tensor.sbis.crud.sale.model.deviceConnection
import ru.tensor.sbis.crud.sale.model.deviceConnectionFrom
import ru.tensor.sbis.crud.sale.model.fillConnection
import ru.tensor.sbis.crud.sale.model.isRemote
import ru.tensor.sbis.crud.sale.model.kkmFrom
import ru.tensor.sbis.crud.sale.model.map
import ru.tensor.sbis.crud.sale.model.toAndroidType
import ru.tensor.sbis.crud.sale.model.toControllerType
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.sale.mobile.generated.KkmFilter
import ru.tensor.sbis.sale.mobile.generated.KkmInfo
import ru.tensor.sbis.sale.mobile.generated.KkmModel
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit

/**@SelfDocumented */
@Suppress("DEPRECATION")
internal class KkmCommandWrapperImpl(
    private val repository: KkmRepository,
    override val listCommand: ListObservableCommand<PagedListResult<CashRegister>, KkmFilter>
) : KkmCommandWrapper, BaseInteractor() {

    override fun create(): Observable<CashRegister> =
        Observable.fromCallable { repository.create() }
            .map { it.toAndroidType() }
            .compose(getObservableBackgroundSchedulers())

    override fun read(id: Long): Observable<CashRegister> =
        Observable.fromCallable {
            repository.read(id)?.toAndroidType()
                ?: throw IllegalArgumentException("Kkm with id=$id not found")
        }
            .compose(getObservableBackgroundSchedulers())

    override fun update(cashRegister: CashRegister,
                        typeUuid: UUID,
                        actions: List<Int>): Completable =
            Completable.fromCallable {
                repository.update(
                        createKkm(cashRegister, typeUuid),
                        createDeviceConnection(cashRegister),
                        ArrayList(actions)
                )
            }
                    .compose(completableBackgroundSchedulers)

    override fun delete(id: Long): Observable<Boolean> =
            Observable.fromCallable { repository.delete(id) }
                    .compose(getObservableBackgroundSchedulers())

    override fun saveKkm(cashRegister: CashRegister,
                         typeUuid: UUID): Completable =
            Completable.fromCallable {
                repository.saveKkm(
                        createKkm(cashRegister, typeUuid),
                        createDeviceConnection(cashRegister))
            }
                    .compose(completableBackgroundSchedulers)

    override fun saveOrUpdateKkmPresto(
        prestoKkmCard: ProductionAreaCard,
        args: DeviceCardSaveArgs,
        cashRegister: CashRegister,
        typeUuid: UUID,
        isProductionEnabled: Boolean
    ): Single<DeviceCanUpdate> {
        return Single.fromCallable {
            val kkmModel = createKkm(cashRegister, typeUuid)
            val connection = createDeviceConnection(cashRegister)
            val mappedDevice = repository.kkmToDevice(kkmModel, connection).toAndroidType()
            val patchedSettings = mappedDevice.settings?.copy(printKitchenOrders = isProductionEnabled)
            mappedDevice.copy(settings = patchedSettings)
        }
            .flatMap { device -> prestoKkmCard.saveAsync(device, args) }
            .compose(getSingleBackgroundSchedulers())
    }

    override fun checkConnection(deviceConnection: Connection): Observable<Boolean> =
            Observable.fromCallable { repository.checkConnection(deviceConnection) }
                    .compose(getObservableBackgroundSchedulers())

    override suspend fun checkConnectionCoroutines(deviceConnection: Connection): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(repository.checkConnection(deviceConnection))
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }

    override fun checkValidity(rnm: String): Observable<Boolean> =
            Observable.fromCallable { repository.checkValidity(rnm) }
                    .compose(getObservableBackgroundSchedulers())

    override fun printTestTicket(connection: Connection, printingSettings: PrintingSettings): Completable =
        Completable.fromCallable { repository.printTestTicket(connection, printingSettings) }

    override fun getOFDUntransmittedDocumentsInfo(): Observable<OFDUntransmittedDocumentsInfoInside> =
        Observable.fromCallable {
            repository.getOFDUntransmittedDocumentsInfo()?.toAndroidType()
                ?: throw IllegalArgumentException("OFDUntransmittedDocumentsInfo not found")
        }
            .compose(getObservableBackgroundSchedulers())

    override fun getOFDUntransmittedDocumentsInfo(
        macAddress: String,
        name: String?,
        driver: Driver,
        connectionType: Int
    ): Observable<OFDUntransmittedDocumentsInfoInside> =
        Observable.fromCallable {
            val deviceConnection = deviceConnectionFrom(
                deviceName = name ?: "",
                macAddress = macAddress,
                driver = driver.toControllerType(),
                connectionType = connectionType,
                countryCode = -1
            )
            repository.getOFDUntransmittedDocumentsInfo(deviceConnection)?.toAndroidType()
                ?: throw IllegalArgumentException("OFDUntransmittedDocumentsInfo not found, $deviceConnection")
        }
            .compose(getObservableBackgroundSchedulers())

    override fun getActiveKkm(fetchConnectionSettings: Boolean): CashRegister? =
            repository.getActiveKkm()?.let { kkmModel ->
                val cashRegister = kkmModel.toAndroidType()
                cashRegister.apply {
                    if (fetchConnectionSettings)
                        fillConnection(cashRegister, kkmModel)
                    if (cashRegister.connectionType == ConnectionTypeInside.WEBKASSA.toInt()) {
                        val connection = connection(cashRegister, repository, kkmModel)
                        connection?.let {
                            cashRegister.login = it.login
                            cashRegister.password = it.password.orEmpty()
                            cashRegister.host = it.host
                            cashRegister.passwordReadId = it.passwordReadId
                        }
                    }
                }
            }

    override fun getActiveKkmInfo(): KkmInfo? {
        return repository.getActiveKkmInfo()
    }

    override fun getActiveKkmAsync(): Observable<CashRegister> =
            Maybe.fromCallable { getActiveKkm() }
                    .flatMapObservable { device ->
                        Observable
                            .fromCallable {
                                val isConnected = try {
                                    repository.checkConnection(device.deviceConnection())
                                } catch (e: Exception) {
                                    Timber.e(e)
                                    false
                                }
                                device.copy(
                                    isConnected = isConnected
                                )
                            }
                            .timeout(15, TimeUnit.SECONDS)
                            .onErrorResumeNext(Function {
                                Observable.just(device)
                            })
                            .startWith(device)
                    }

    override fun getKkmInfoAsync(cashRegister: CashRegister): Single<KkmInfoInside> =
        Single.fromCallable { repository.getKkmInfo(createKkm(cashRegister), createDeviceConnection(cashRegister)) }
            .map { kkmInfo ->
                when {
                    kkmInfo.serialNumber.isEmpty() -> KkmInfoInside()
                    else -> convertKkmInfo(kkmInfo)
                }
            }
            .compose(getSingleBackgroundSchedulers())

    override fun checkSettingDateTimeForKkm(): Observable<DateTimeCheckStatusInside> =
        Observable.fromCallable { repository.checkSettingDateTimeForKkm() }
            .map { it.toAndroidType() }
            .compose(getObservableBackgroundSchedulers())

    override fun setDateTimeToKkm(): Observable<Unit> =
        Observable.fromCallable { repository.setDateTimeToKkm() }
            .compose(getObservableBackgroundSchedulers())

    override fun canUpdate(cashRegister: CashRegister,
                           missCheckRegnum: Boolean,
                           typeUuid: UUID,
                           actions: List<Int>): Observable<KkmCanUpdate> =
            Observable.fromCallable {
                repository.canUpdate(
                        createKkm(cashRegister, typeUuid, missCheckRegnum),
                        createDeviceConnection(cashRegister),
                        actions
                )
            }
                    .map { KkmCanUpdate(it.result, it.message, it.device, it.metastate, it.actions) }
                    .compose(getObservableBackgroundSchedulers())

    override fun deactivate(kkmId: Long, actions: List<Int>): Completable =
            Completable.fromCallable { repository.deactivate(kkmId, actions) }
                    .compose(completableBackgroundSchedulers)

    override fun openCashbox(): Completable =
            Completable.fromCallable { repository.openCashbox() }
                    .compose(completableBackgroundSchedulers)

    override fun isActiveKkmShiftExpired(): Single<Boolean> =
            Single.fromCallable {
                val sessionInfo = repository.getActiveKkmSessionInfo()
                sessionInfo?.sessionState == SessionState.SHIFT_OPEN_24HOURS_EXCEEDED
            }
                    .compose(getSingleBackgroundSchedulers())

    override fun isKKMConnected(cashRegister: CashRegister): Single<Boolean> {
        return Single.fromCallable {
            repository.isKKMConnected(createKkm(cashRegister), createDeviceConnection(cashRegister))
        }
    }

    override fun getSupportedOperations(deviceConnection: Connection): SupportedOperationsInside =
        repository.getSupportedOperations(deviceConnection).map()

    override fun getKkmWarnings(deviceConnection: Connection): Observable<List<KkmWarningInside>> =
        Observable.fromCallable {
            return@fromCallable if (deviceConnection.connectionType == ConnectionTypeInside.WEBKASSA.toInt()) {
                emptyList<KkmWarning>()
            } else {
                repository.getKkmWarnings(deviceConnection)
            }
        }
            .map {
                it.map(KkmWarning::map)
            }
            .compose(getObservableBackgroundSchedulers())


    private fun createKkm(cashRegister: CashRegister,
                          typeUuid: UUID = UUID.randomUUID(),
                          missCheckRegnum: Boolean = false): KkmModel =
            kkmFrom(id = cashRegister.id,
                    deviceName = cashRegister.deviceName.orEmpty(),
                    kkmName = cashRegister.deviceName.orEmpty(),
                    serialNumber = cashRegister.serialNumber.orEmpty(),
                    registrationNumber = cashRegister.registerNumber.orEmpty(),
                    isActive = cashRegister.cloudActive,
                    companyId = cashRegister.companyId!!.toLong(),
                    workplaceId = cashRegister.workplaceId!!.toLong(),
                    typeUUID = typeUuid,
                    missCheckRegnum = missCheckRegnum,
                    kkmId = cashRegister.kkmId,
                    remoteKkmId = cashRegister.remoteKkmId,
                    printReceiptEnabled = cashRegister.printReceiptEnabled,
                    printTicket = cashRegister.printTicket,
                    remoteWorkplaces = cashRegister.remoteWorkplaces
            )

    private fun createDeviceConnection(cashRegister: CashRegister): Connection =
            deviceConnectionFrom(
                    deviceName = cashRegister.deviceName.orEmpty(),
                    macAddress = cashRegister.macAddress,
                    driver = cashRegister.driver.orEmpty(),
                    connectionType = cashRegister.connectionType ?: 0,
                    vid = cashRegister.vid,
                    pid = cashRegister.pid,
                    serialPort = cashRegister.port.orEmpty(),
                    serialNumber = cashRegister.serialNumber,
                    tcpIpAddress = cashRegister.ipAddress ?: "0.0.0.0",
                    tcpIpPort = cashRegister.tcpPort ?: 0,
                    printerName = cashRegister.deviceName.orEmpty(),
                    login = cashRegister.login,
                    password = cashRegister.password,
                    host = cashRegister.host,
                    countryCode = cashRegister.countryCode
            )

    private fun fillConnection(cashRegister: CashRegister, kkmModel: KkmModel) {
        if (!cashRegister.isRemote()) {
            cashRegister.fillConnection(repository.getDeviceSettings(kkmModel)!!)
        }
    }

    override fun printTicketForSale(saleUuid: UUID): Completable {
        return Completable.fromCallable {
            repository.printTicketForSale(saleUuid)
        }
            .compose(completableBackgroundSchedulers)
    }

    override fun getActiveKkmSupportedOperations(): Maybe<SupportedOperationsInside> =
        Maybe.fromCallable {
            getActiveKkm(true)?.let { cashRegister ->
                getSupportedOperations(cashRegister.deviceConnection())
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override suspend fun isNonFiscal(cashRegister: CashRegister): Boolean =
        withContext(Dispatchers.IO) {
            repository.getDeviceInfo(cashRegister.deviceConnection()).registrationParameters.fdLifeState != 3
        }
}