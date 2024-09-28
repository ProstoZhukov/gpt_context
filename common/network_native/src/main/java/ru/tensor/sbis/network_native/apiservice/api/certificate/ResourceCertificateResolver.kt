package ru.tensor.sbis.network_native.apiservice.api.certificate

import android.content.Context
import okhttp3.OkHttpClient
import ru.tensor.sbis.network_native.R
import timber.log.Timber
import java.security.cert.Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author ma.kolpakov
 * Создан 10/4/2019
 */
internal class ResourceCertificateResolver(
    context: Context
) : SSLCertificateResolver {

    private val certificates: List<Certificate> by lazy {
        listOf(
            loadCertificate(context, R.raw.sectigo_rsa_organization_validation_secure_server_ca),
            loadCertificate(context, R.raw.russian_trusted_root_ca),
            loadCertificate(context, R.raw.russian_trusted_sub_ca),
            loadCertificate(context, R.raw.cert_ca),
            loadCertificate(context, R.raw.alphasslcasha256g4)
        )
    }

    override fun inject(builder: OkHttpClient.Builder) {
        try {
            val trustManager: X509TrustManager = CompositeTrustManager(
                getDefaultTrustManager(), getCustomTrustManager(certificates)
            )
            val trustManagers = arrayOf<TrustManager>(trustManager)
            val context = SSLContext.getInstance("TLS")
            context.init(null, trustManagers, null)

            builder.sslSocketFactory(context.socketFactory, trustManager)

        } catch (e: Exception) {
            Timber.w(e, "Unable to inject custom trust manager")
        }
    }

    override fun check(certificate: Certificate): Boolean {
        certificates.forEach {
            try {
                certificate.verify(it.publicKey)
                return true
            } catch (e: Exception) {
                Timber.w(e, "Certificate verification failed")
            }
        }
        Timber.w("Certificate verification failed")
        return false
    }
}