package ru.tensor.sbis.webviewer.utils

import android.os.Build
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebViewClient
import ru.tensor.sbis.webviewer.WebViewRendererDeathListener

/**
 * Обработчик [WebViewClient.onRenderProcessGone], вызывающий [WebViewRendererDeathListener] при убийстве процесса
 * рендеринга WebView системой
 *
 * @author us.bessonov
 */
class WebViewRenderProcessGoneHandler {

    private var listener: WebViewRendererDeathListener? = null

    /** @SelfDocumented */
    fun setRendererDeathListener(listener: WebViewRendererDeathListener?) {
        this.listener = listener
    }

    /**
     * @see [WebViewClient.onRenderProcessGone]
     */
    fun onWebViewRenderProcessGone(detail: RenderProcessGoneDetail) : Boolean {
        listener?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !detail.didCrash()) {
                it.onWebViewRendererKilled()
                listener = null
                return true
            }
        }
        return false
    }

}


