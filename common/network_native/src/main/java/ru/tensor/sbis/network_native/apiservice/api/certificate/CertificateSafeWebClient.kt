package ru.tensor.sbis.network_native.apiservice.api.certificate

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.CallSuper

/**
 * Расширение [WebViewClient] с дополнительной проверкой ssl сертификатов
 *
 * @author ma.kolpakov
 * Создан 10/4/2019
 */
open class CertificateSafeWebClient(
    private val certificateResolver: SSLCertificateResolver
) : WebViewClient() {

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        view.handleSslError(handler, error, certificateResolver) { v, h, e ->
            defaultSslErrorAction(v, h, e)
        }
    }

    /**
     * Дефолтное поведение при ошибке с сертификатами.
     */
    @CallSuper
    protected open fun defaultSslErrorAction(view: WebView, handler: SslErrorHandler, error: SslError) =
        super.onReceivedSslError(view, handler, error)
}