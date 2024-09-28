/**
 * Инструменты для работы с SSL сетрификатами
 *
 * @author ma.kolpakov
 * Создан 10/4/2019
 */
@file:JvmName("SSLCertificateUtil")

package ru.tensor.sbis.network_native.apiservice.api.certificate

import android.content.Context
import android.content.res.Resources
import android.net.http.SslCertificate
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import androidx.annotation.RawRes
import ru.tensor.sbis.network_native.NetworkPlugin
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Обработка ошибок сертификата с учётом [ApiService.getCertificateResolver]
 */
@JvmOverloads
inline fun WebView.handleSslError(
    handler: SslErrorHandler,
    error: SslError,
    resolver: SSLCertificateResolver = NetworkPlugin.apiServiceProvider.apiService().certificateResolver,
    defaultBehaviour: (WebView, SslErrorHandler, SslError) -> Unit
) {
    val certificate = error.certificate.X509Certificate()
    when {
        certificate == null -> {
            Timber.w("X509Certificate is null. Unable to check it")
            defaultBehaviour(this, handler, error)
        }
        // если проверки пройдены, разрешаем
        resolver.check(certificate) -> handler.proceed()
        else -> defaultBehaviour(this, handler, error)
    }
}

/**
 * Получение реализации [SSLCertificateResolver]
 */
internal fun getCertificateResolver(context: Context): SSLCertificateResolver =
    /*
    Используется ResourceCertificateResolver всегда, т.к. он не отменяет стандартную проверку сертификата,
    а лишь дополняет её. Т.е. если стандартная не одобрила сертификат, то следующей пойдёт кастомная проверка
    См. метод getCustomTrustManager, поле certificate в ResourceCertificateResolver и их использование
    */
    ResourceCertificateResolver(context)

/**
 * Загрузка сертификата из ресурса [certificateRes]
 */
@Throws(Resources.NotFoundException::class, CertificateException::class)
internal fun loadCertificate(context: Context, @RawRes certificateRes: Int): Certificate {
    return context.resources.openRawResource(certificateRes).use { caInput ->
        CertificateFactory.getInstance("X.509").generateCertificate(caInput)
    }
}

/**
 * Получение актуального [X509TrustManager] с учётом сертификата [Certificate]
 */
@Throws(IOException::class, CertificateException::class, KeyStoreException::class, NoSuchAlgorithmException::class)
internal fun getCustomTrustManager(certificates: List<Certificate>): X509TrustManager {
    val keyStoreType = KeyStore.getDefaultType()
    val keyStore = KeyStore.getInstance(keyStoreType)
    keyStore.load(null, null)
    certificates.forEachIndexed { index, certificate ->
        keyStore.setCertificateEntry("ca$index", certificate)
    }

    return getDefaultTrustManager(keyStore)
}

/**
 * Получение актуального [X509TrustManager]. Нужно запрашивать перед использованием, может меняться во время исполнения
 *
 * @see TrustManagerFactory.getDefaultAlgorithm
 */
@Throws(KeyStoreException::class, NoSuchAlgorithmException::class)
internal fun getDefaultTrustManager(keyStore: KeyStore? = null): X509TrustManager {
    val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
    val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
    tmf.init(keyStore)

    return tmf.trustManagers.first() as X509TrustManager
}

/**
 * Получение вложенного сертификата. Метод можно удалить при установке минимальной версии API 29
 *
 * @see SslCertificate.getX509Certificate
 */
fun SslCertificate.X509Certificate(): Certificate? {
    // решение предложено в https://stackoverflow.com/a/37851959/3926506
    val bundle = SslCertificate.saveState(this)
    val bytes = bundle.getByteArray("x509-certificate")
    return if (bytes == null) {
        Timber.w("Unable to get ssl certificate. Data is empty")
        null
    } else {
        try {
            val certFactory = CertificateFactory.getInstance("X.509")
            certFactory.generateCertificate(ByteArrayInputStream(bytes))
        } catch (e: CertificateException) {
            Timber.w(e)
            null
        }
    }
}
