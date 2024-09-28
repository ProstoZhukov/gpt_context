package ru.tensor.sbis.certificate.request.decl

import android.app.Activity
import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Реализация внешнего API для работы с заявками на продление.
 *
 * @author as.gromilov
 */
interface CertificateRequestFeature : Feature {
    /**
     * Создание интента для открытия [Activity] сценариев продления сертификата
     * @param context - ссылка на контекст
     * @param petitionUuid - идентификатор заявки
     * @param imprint - уникальный отпечаток сертификата
     *
     * @return [Intent] - интент для запуска новой активити. В случае ошибки может быть null
     */
    fun start(context: Context, petitionUuid: UUID?, imprint: String): Intent?

    /**
     * Создание интента для открытия экранов установки сертификата в контейнер
     *
     * @param requestUuid - уникальный идентификатор заявки
     */
    fun reject(requestUuid : UUID)

    /**
     * Дефолтная реализация заглушки интерфейса [CertificateRequestFeature]
     */
    object Stub : CertificateRequestFeature {
        override fun start(context: Context, petitionUuid: UUID?, imprint : String): Intent? = null
        override fun reject(requestUuid: UUID) = Unit
    }
}