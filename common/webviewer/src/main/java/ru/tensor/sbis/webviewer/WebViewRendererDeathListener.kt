package ru.tensor.sbis.webviewer

import android.webkit.WebViewClient

/**
 * Слушатель события убийства процесса рендеринга WebView
 *
 * @author us.bessonov
 */
interface WebViewRendererDeathListener {

    /**
     * Вызывается при уничтожении процесса рендеринга WebView системой, что наиболее вероятно при нехватке памяти
     *
     * @see [WebViewClient.onRenderProcessGone]
     */
    fun onWebViewRendererKilled()
}