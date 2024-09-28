package ru.tensor.sbis.crud.sale.crud.kkm_service_mobile

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.operators.single.SingleFromCallable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.devices.fiscal_dr.generated.FdLifeState
import ru.tensor.devices.fiscal_dr.generated.FdLifeState.Companion.FISCAL_MODE
import ru.tensor.devices.fiscal_dr.generated.ReportOfRegistration
import ru.tensor.devices.generic.generated.Connection
import ru.tensor.devices.generic.generated.DocumentType.Companion.REPORT_OF_REGISTRATION
import ru.tensor.devices.generic.generated.SessionState
import ru.tensor.devices.kkmservice.generated.DeviceInfo
import ru.tensor.sbis.CXX.RpcException
import ru.tensor.sbis.CXX.SbisException
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.model.BlResult
import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.model.FiscalState
import ru.tensor.sbis.crud.sale.model.KkmInfoInside
import ru.tensor.sbis.sale.mobile.generated.KkmFacade
import ru.tensor.sbis.sale.mobile.generated.KkmInfo
import ru.tensor.sbis.sale.mobile.generated.SaleMobileException
import ru.tensor.sbis.sale.mobile.generated.ShiftKkmFacade
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import ru.tensor.devices.fiscal_dr.generated.FdDocumentType.Companion.REPORT_OF_REGISTRATION as FISCAL_REPORT_OF_REGISTRATION

/**
 * Методы для работы с фискализацией.
 */
internal class FiscalRepositoryImpl(
    private val kkmFacade: DependencyProvider<KkmFacade>,
    private val shiftKkmFacade: DependencyProvider<ShiftKkmFacade>,
    private val kkmRegistrationService: KkmRegistrationService
) : FiscalRepository {
    private val timberTag = "FiscalUI"

    private val publishSubject = PublishSubject.create<BlResult<FiscalState>>()

    override fun freshFiscalState(): Observable<BlResult<FiscalState>> = publishSubject

    override fun currentFiscal(connection: Connection): Single<DeviceInfo> =
        SingleFromCallable {
            val deviceInfo = kkmFacade.get().getDeviceInfo(connection)
            Timber.d("$timberTag: currentFiscal: $deviceInfo")
            deviceInfo
        }

    override suspend fun updateFiscalState(connection: Connection) = withContext(Dispatchers.IO) {
        updateFiscalStateInternal(connection)
    }

    override fun getKkmInfo(connection: Connection): Single<KkmInfo> =
        SingleFromCallable {
            val kkmInfo = kkmFacade.get().getKkmInfoStrong(connection)
            Timber.d("$timberTag: getKkmInfo: $kkmInfo")
            kkmInfo
        }

    override fun fiscalState(connection: Connection): Single<FiscalState> {
        return currentFiscal(connection)
            .map(::createFiscalState)
    }

    /** Закрытие фискального накопителя */
    override fun closeFiscal(
        connection: Connection,
        date: Date?
    ): Single<BlResult<Boolean>> = Single.fromCallable<BlResult<Boolean>> {
        kkmFacade.get().closeFiscalDrive(connection, date)
        kkmFacade.get().getDeviceInfo(connection)
            .registrationParameters.reportOfRegistration?.let { reportRegistration ->
                try {
                    kkmRegistrationService.setLastReportRegistrationFs(reportRegistration)
                } catch (ex: Exception) {
                    BlResult.Failure(-1, ex.message)
                }
            }

        BlResult.Success(true)
    }
        .doOnSuccess { updateFiscalStateInternal(connection) }
        .onErrorReturn(::handleError)

    /** Регистрация ККТ */
    override fun registrationFiscal(
        connection: Connection,
        reportOfRegistration: ReportOfRegistration
    ): Single<BlResult<ReportOfRegistration>> = Single.fromCallable<BlResult<ReportOfRegistration>> {
        reportOfRegistration.apply {
            registration.attributes.descriptor.apply {
                fdDocumentType = FISCAL_REPORT_OF_REGISTRATION
                descriptor =
                    ru.tensor.devices.generic.generated.DocumentDescriptor(REPORT_OF_REGISTRATION)

            }
        }
        Timber.d("$timberTag: registrationFiscalIncome: $reportOfRegistration")
        kkmFacade.get().registerFiscalDrive(reportOfRegistration, connection)
        val result = kkmFacade.get().getDeviceInfo(connection).registrationParameters.reportOfRegistration
            ?: kotlin.run {
                Timber.d("$timberTag: registrationFiscalResult: result getDeviceInfo is null")
                reportOfRegistration
            }
        BlResult.Success(result)
    }
        .onErrorReturn(::handleError)

    override fun hasOFDUntransmittedDocuments(deviceConnection: Connection) = Single.fromCallable<BlResult<Boolean>> {
        val result = kkmFacade.get().getOFDUntransmittedDocumentsInfo(deviceConnection)
            ?.notTransmittedDocuments
            ?.notTransmittedDocumentsQuantity
            ?.run { this > 0 }
            ?: false
        BlResult.Success(result)
    }
        .onErrorReturn(::handleError)

    override fun hasOpenedShift(deviceConnection: Connection) = Single.fromCallable<BlResult<Boolean>> {
        val sessionInfo = kkmFacade.get().getActiveKkmSessionInfo(deviceConnection)
        val isOpenedShift = when (sessionInfo?.sessionState) {
            SessionState.SHIFT_OPEN_24HOURS_NOT_EXCEEDED,
            SessionState.SHIFT_OPEN_24HOURS_EXCEEDED,
            SessionState.SHIFT_OPEN_FD_SHIFT_CLOSED -> true
            else -> false
        }
        BlResult.Success(isOpenedShift)
    }
        .onErrorReturn(::handleError)

    override fun closeShift(): Single<BlResult<Boolean>> = Single.fromCallable<BlResult<Boolean>> {
        shiftKkmFacade.get().closeShift()
        BlResult.Success(true)
    }
        .onErrorReturn(::handleError)

    override fun checkInn(value: String): Boolean {
        return kkmFacade.get().checkInn(value)
    }

    override fun checkRnm(kktRegId: String, inn: String, kktNumber: String): BlResult<Boolean> {
        return try {
            when (kkmFacade.get().checkKktRegId(kktRegId, inn, kktNumber)) {
                true -> BlResult.Success(true)
                false -> BlResult.Failure(-1, "Unknown error")
            }
        } catch (ex: Exception) {
            BlResult.Failure(-1, ex.message)
        }
    }

    override suspend fun checkRnmComplex(kkmSerialNumber: String, rnm: String, inn: String) =
        withContext(Dispatchers.IO) {
            kkmRegistrationService.checkKkmByRegNumber(kkmSerialNumber, rnm, inn)
        }

    override suspend fun waitOfdQueuePurge(connection: Connection) = withContext(Dispatchers.IO) {
        kkmRegistrationService.waitOfdQueuePurge(connection)
    }

    private fun updateFiscalStateInternal(connection: Connection) {
        val result = try {
            val deviceInfo = kkmFacade.get().getDeviceInfo(connection)
            val fiscalState = createFiscalState(deviceInfo)
            BlResult.Success(fiscalState)
        } catch (ex: Exception) {
            handleError(ex)
        }

        publishSubject.onNext(result)
    }

    override suspend fun lastReportRegistrationFs(kktNumber: Long, kktRegId: String): ReportOfRegistration? =
        withContext(Dispatchers.IO) {
            kkmRegistrationService.getLastReportRegistrationFs(kktNumber, kktRegId)
        }

    override suspend fun setLastReportRegistrationFs(reportOfRegistration: ReportOfRegistration) {
        kkmRegistrationService.setLastReportRegistrationFs(reportOfRegistration)
    }

    override fun createFiscalState(deviceInfo: DeviceInfo): FiscalState {
        return createFiscalState(
            dateTime = deviceInfo.deviceInfoBase.dateTime,
            fdLifeState = deviceInfo.registrationParameters.fdLifeState
        )
    }

    override fun createFiscalState(kkmInfoInside: KkmInfoInside): FiscalState {
        return createFiscalState(
            dateTime = kkmInfoInside.mDateTime,
            fdLifeState = kkmInfoInside.fdLifeState
        )
    }

    private fun createFiscalState(
        dateTime: Date?,
        fdLifeState: Int
    ): FiscalState {
        val fiscalIsClose = fdLifeState == FdLifeState.POST_FISCAL_MODE
            || fdLifeState == FdLifeState.GET_DATA_FROM_FD_ARCHIVE
        val hasFiscal = fdLifeState == FISCAL_MODE

        val currentDate = Calendar.getInstance().time
        val dateDevice = dateTime ?: currentDate
        val dateMatch =
            if (fdLifeState == FdLifeState.FISCALIZATION_READY) getDateDiff(
                dateDevice,
                currentDate,
                TimeUnit.DAYS
            ) == 0L
            else true

        val fiscalIsSetup = fdLifeState != FdLifeState.INSTALLATION

        val fiscalState = FiscalState(
            isSetup = fiscalIsSetup,
            isClosed = fiscalIsClose,
            hasFiscal = hasFiscal,
            dateMatch = dateMatch
        )
        Timber.i("$timberTag: fiscalState: $fiscalState")
        return fiscalState
    }

    @Suppress("KotlinConstantConditions")
    private fun <T> handleError(error: Throwable): BlResult<T> {
        Timber.e("$timberTag: $error")
        val description = when (error) {
            is RpcException -> error.errorUserMessage.ifEmpty { error.errorMessage }
            is SbisException -> error.errorUserMessage.ifEmpty { error.errorMessage }
            is SaleMobileException -> error.errorUserMessage.ifEmpty { error.errorMessage }
            else -> error.message
        }
        return BlResult.Failure(
            errorCode = -1,
            description = description,
            details = null
        )
    }

    @Suppress("SameParameterValue")
    private fun getDateDiff(firstDate: Date, secondDate: Date, timeUnit: TimeUnit): Long {
        val diffInMillies = secondDate.time - firstDate.time
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }
}