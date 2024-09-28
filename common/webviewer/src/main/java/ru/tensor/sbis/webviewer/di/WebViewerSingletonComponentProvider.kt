package ru.tensor.sbis.webviewer.di

import android.app.Application
import android.content.Context
import ru.tensor.sbis.webviewer.WebViewerPlugin
import ru.tensor.sbis.webviewer.contract.WebViewerFeature

/**
 * Предоставляет DI компонент [WebViewerSingletonComponent], публичное АПИ [WebViewerFeature] для
 * открытия документа в WebView
 *
 * @author ma.kolpakov
 */
object WebViewerSingletonComponentProvider {

    @JvmStatic
    @JvmName("getComponent")
    internal fun getComponent(context: Context): WebViewerSingletonComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return WebViewerPlugin.webViewerComponent
    }

    @Suppress("unused")
    @JvmStatic
    fun getWebViewerFeature(context: Context): WebViewerFeature {
        return getComponent(context).getWebViewerFeature()
    }
}