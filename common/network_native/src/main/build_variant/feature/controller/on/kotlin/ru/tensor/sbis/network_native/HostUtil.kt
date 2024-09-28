package ru.tensor.sbis.network_native

import android.net.Uri
import ru.tensor.sbis.desktop.working_domain.generated.DomainDescription
import ru.tensor.sbis.desktop.working_domain.generated.MainDomainController
import timber.log.Timber


internal object ServerUtil {

    @JvmStatic
    fun getSbisHostUrl(url: String): String? {
        val domain = DomainDescription().apply { this.url = url }

        try {
            if (MainDomainController.isOnlineDomain(domain)) {
                val uri = Uri.parse(domain.url)
                return uri.host
            } else {
                return null
            }
        } catch (exception: Exception) {
            Timber.e(exception)
            return null
        }
    }
}