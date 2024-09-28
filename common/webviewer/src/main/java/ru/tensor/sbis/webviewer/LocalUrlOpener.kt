package ru.tensor.sbis.webviewer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.design_notification.SbisPopupNotification
import timber.log.Timber

private const val INTENT_SCHEME = "intent://"

/**
 * Обрабатывает специфичные ссылки, для которых непригодно открытие непосредственно в WebView.
 *
 * @author us.bessonov
 */
internal class LocalUrlOpener(private val context: Context) {

    fun open(url: String): Boolean = when {
        UrlUtils.isMailUrl(url) -> {
            CommonUtils.sendEmail(context, Uri.parse(url))
            true
        }
        UrlUtils.isTelUrl(url) -> {
            CommonUtils.callPhone(context, Uri.parse(url))
            true
        }
        URLUtil.isHttpUrl(url) -> {
            CommonUtils.openLinkInExternalApp(context, url)
            true
        }
        url.startsWith(INTENT_SCHEME) -> {
            val intent = Intent.parseUri(url, 0)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Timber.e("Failed to open intent with '${intent.data}': ${e.message}")
                SbisPopupNotification.pushToast(context, ru.tensor.sbis.common.R.string.common_open_link_browser_error)
            }
            true
        }
        else -> false
    }
}