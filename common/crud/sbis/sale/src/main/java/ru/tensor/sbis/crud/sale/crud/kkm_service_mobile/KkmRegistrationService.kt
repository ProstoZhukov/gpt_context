package ru.tensor.sbis.crud.sale.crud.kkm_service_mobile

import ru.tensor.devices.fiscal_dr.generated.ReportOfRegistration
import ru.tensor.devices.generic.generated.Connection
import ru.tensor.devices.kkmregistrationservice.generated.KkmRegistrationServiceMobile
import ru.tensor.devices.kkmregistrationservice.generated.KktRegistrationInfo
import javax.inject.Inject

/**
 * Сервис регистрации ккм.
 */
internal class KkmRegistrationService @Inject constructor() {

    private val controller by lazy { KkmRegistrationServiceMobile.instance() }

    /**
     * Ожидать пока ккт отправит документы в ОФД.
     */
    fun waitOfdQueuePurge(connection: Connection) {
        val result = controller.waitOfdQueuePurge(connection, 180_000)
        result.checkHasError()
    }

    /**
     * Проверить ккт по регистрационному номеру.
     */
    fun checkKkmByRegNumber(kkmSerialNumber: String, rnm: String, inn: String) {
        val result = controller.checkKkmByRegNumber(kkmSerialNumber, rnm, inn)
        result.checkHasError()
    }

    /** Сохранить отчет о регистрации в облаке */
    fun setLastReportRegistrationFs(reportOfRegistration: ReportOfRegistration) {
        val kktRegId = reportOfRegistration.registration.kktRegId
        val kktNumber = reportOfRegistration.kktNumber?.toLongOrNull()
        kktNumber?.let {
           val result = controller.setLastReportRegistrationFs(kktNumber, kktRegId, reportOfRegistration)
            result.checkHasError()
        }
    }

    /** Наличие отчета о регистрации в облаке. Если отчета нет, то null. */
    fun getLastReportRegistrationFs(kktNumber: Long, kktRegId: String): ReportOfRegistration? {
        val result = controller.getLastReportRegistrationFs(kktNumber, kktRegId)
        result.checkHasError()
        return result.reportOfRegistration
    }

    private fun KktRegistrationInfo.checkHasError() {
        if (registrationStatus.status != 0L) throw RuntimeException(registrationStatus.description)
    }
}