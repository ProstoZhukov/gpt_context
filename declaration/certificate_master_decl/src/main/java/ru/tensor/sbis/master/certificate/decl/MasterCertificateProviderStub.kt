package ru.tensor.sbis.master.certificate.decl

import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.util.UUID

/**
 * @author as.gromilov
 *
 * Реализация заглушки для всех приложений. Если необходимо использовать полноценную реализацию,
 * необходимо подключать модуль [:certificate_master].
 */
@Suppress("unused")
class MasterCertificateProviderStub : MasterCertificateProvider {

    override fun getGenerateIntent(context: Context, requestUuid: UUID): Intent? {
        Timber.d(IllegalStateException("Заглушка для интерфейса MasterCertificateProvider"))
        return null
    }

    override fun getInstallIntent(context: Context, requestUuid: UUID): Intent? {
        Timber.d(IllegalStateException("Заглушка для интерфейса MasterCertificateProvider"))
        return null
    }
}