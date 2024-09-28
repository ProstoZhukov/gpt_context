/**
 * Инструмент для создания WebChromeClient, используемого в DocumentWebView
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.webviewer.utils

import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebView

/**
 * Создаёт [WebChromeClient], с указанием обработчиков [WebChromeClient.onReceivedTitle] и
 * [WebChromeClient.onCreateWindow]
 */
@JvmOverloads
internal fun createCustomWebChromeClient(onReceivedTitle: () -> Unit, onCreateWindow: ((Message) -> Boolean)? = null) =
    object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            onReceivedTitle.invoke()
        }

        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message
        ): Boolean {
            return onCreateWindow?.invoke(resultMsg)
                ?: super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        }
    }