package ru.tensor.sbis.network_native.apiservice.api.certificate

import androidx.annotation.CheckResult
import okhttp3.OkHttpClient
import java.security.cert.Certificate
import javax.net.ssl.TrustManager

/**
 * Интерфейс инструмента для разрешения SSL сертификата, который отсутстует на устройстве
 *
 * @author ma.kolpakov
 * Создан 10/4/2019
 */
interface SSLCertificateResolver {

    /**
     * Установка [TrustManager] в [builder]
     */
    fun inject(builder: OkHttpClient.Builder)

    /**
     * Проверка подозрительных сертификатов. Предполагается, что этот метод вызывается после системных проверок, когда
     * они уже отказали сертификату. Если непонятно, как дополнительно проверять, нужно вернуть `false`
     */
    @CheckResult
    fun check(certificate: Certificate): Boolean
}