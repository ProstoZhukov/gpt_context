package ru.tensor.sbis.crud.sale.crud.kkm_service_mobile

import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.devices.fiscal_dr.generated.ReportOfRegistration
import ru.tensor.devices.generic.generated.Connection
import ru.tensor.devices.kkmservice.generated.DeviceInfo
import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.model.BlResult
import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.model.FiscalState
import ru.tensor.sbis.crud.sale.model.KkmInfoInside
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.sale.mobile.generated.KkmInfo
import java.util.Date

/**
 * Методы для работы с фискализацией.
 */
interface FiscalRepository : Feature {

    /** Наличие отчета о регистрации в облаке. Если отчета нет, то null. */
    suspend fun lastReportRegistrationFs(kktNumber: Long, kktRegId: String): ReportOfRegistration?

    /** Сохранить отчет о регистрации в облаке */
    suspend fun setLastReportRegistrationFs(reportOfRegistration: ReportOfRegistration)

    /**
     * Получение состояния фискального регистратора.
     * Создаяется модель состояния из информации содержащейся в DeviceInfo.
     * Легковесный метод. Можно запускать в главном потоке.
     */
    fun createFiscalState(deviceInfo: DeviceInfo): FiscalState

    /**
     * Получение состояния фискального регистратора.
     * Создаяется модель состояния из информации содержащейся в KkmInfoInside.
     * Легковесный метод. Можно запускать в главном потоке.
     */
    fun createFiscalState(kkmInfoInside: KkmInfoInside): FiscalState

    /**
     * Подписка на обновление состояния фискализации.
     */
    fun freshFiscalState(): Observable<BlResult<FiscalState>>

    /** Текущий фискальный накопитель. */
    fun currentFiscal(connection: Connection): Single<DeviceInfo>

    /**
     * Обновить состояние фискального накопителя.
     */
    suspend fun updateFiscalState(connection: Connection)

    /** Получить данные об ККМ */
    fun getKkmInfo(connection: Connection): Single<KkmInfo>

    /** Состояние фискального регистратора. */
    fun fiscalState(connection: Connection): Single<FiscalState>

    /** Закрытие фискального накопителя. */
    fun closeFiscal(
        connection: Connection,
        date: Date?
    ): Single<BlResult<Boolean>>

    /** Регистрация ККТ. */
    fun registrationFiscal(
        connection: Connection,
        reportOfRegistration: ReportOfRegistration
    ): Single<BlResult<ReportOfRegistration>>

    /** Наличие непереданных документах в ОФД. */
    fun hasOFDUntransmittedDocuments(deviceConnection: Connection): Single<BlResult<Boolean>>

    /** Есть ли открытые смены. */
    fun hasOpenedShift(deviceConnection: Connection): Single<BlResult<Boolean>>

    /** Закрыть смену. */
    fun closeShift(): Single<BlResult<Boolean>>

    /**
     * Проверка корректности ИНН.
     * Легковесный метод. Можно запускать в главном потоке.
     */
    fun checkInn(value: String): Boolean

    /**
     * Проверка корректности РНМ.
     * Легковесный метод. Можно запускать в главном потоке.
     */
    fun checkRnm(kktRegId: String, inn: String, kktNumber: String): BlResult<Boolean>

    /**
     * Полная проверка корректности РНМ.
     * РНМ просрочен, не найдено заявление на сайте ФНС, нет связи (сети интернет) с ФНС.
     */
    suspend fun checkRnmComplex(kkmSerialNumber: String, rnm: String, inn: String)

    /**
     * Ожидать пока ккт отправит документы в ОФД.
     */
    suspend fun waitOfdQueuePurge(connection: Connection)
}