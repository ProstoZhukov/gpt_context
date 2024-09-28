package ru.tensor.sbis.network_native.apiservice.api.certificate

import android.net.http.X509TrustManagerExtensions
import ru.tensor.sbis.network_native.httpclient.Server
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * Реализация [X509TrustManager] для последовательной проверки
 *
 * @author ma.kolpakov
 * Создан 10/4/2019
 */
internal class CompositeTrustManager(
    vararg trustManagers: X509TrustManager
) : X509TrustManager {

    private val managers = trustManagers

    private val accepted: Array<X509Certificate> = managers
        .map(X509TrustManager::getAcceptedIssuers)
        .flatMap(Array<X509Certificate>::asIterable)
        .toTypedArray()

    private val extensions by lazy {
        /*
        Из-за наличия <domain-config> в конфигурации, при проверке необходимо использовать X509TrustManagerExtensions,
        чтобы учитывался хост.
        */
        managers.map { X509TrustManagerExtensions(it) }
    }

    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        var error: Exception? = null
        managers.firstOrNull {
            try {
                it.checkClientTrusted(chain, authType)
                // первый, кто подтвердил проверку, разрешает использование сертификата
                true
            } catch (e: CertificateException) {
                error = e
                false
            }
        } ?: onTrustCheckError(error)
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        var error: Exception? = null
        extensions.firstOrNull {
            try {
                it.checkServerTrusted(chain, authType, Server.getInstance().host.fullHostUrl)
                // первый, кто подтвердил проверку, разрешает использование сертификата
                true
            } catch (e: CertificateException) {
                error = e
                false
            }
        } ?: onTrustCheckError(error)
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> = accepted

    private fun onTrustCheckError(source: Exception?) {
        throw CertificateException("None of trust managers accept the chain", source)
    }
}