package ru.tensor.sbis.network_native.apiservice.api.certificate

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.OkHttpClient
import java.security.cert.Certificate

/**
 * Реализация [SSLCertificateResolver], которая не вносит модификаций в процедуру обработки сетрификатов
 *
 * @author ma.kolpakov
 * Создан 10/4/2019
 */
@Suppress("unused")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
internal object DefaultCertificateResolver : SSLCertificateResolver {

    override fun inject(builder: OkHttpClient.Builder) = Unit

    override fun check(certificate: Certificate) = false
}