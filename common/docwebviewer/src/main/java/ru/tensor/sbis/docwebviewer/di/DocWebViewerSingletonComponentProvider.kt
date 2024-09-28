package ru.tensor.sbis.docwebviewer.di

import android.content.Context
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.docwebviewer.DocWebViewerPlugin

/**
 * Предоставляет DI компонент [DocWebViewerSingletonComponent],
 * публичное АПИ [DocWebViewerFeature] для открытия документа в WebView
 *
 * @author ma.kolpakov
 */
object DocWebViewerSingletonComponentProvider {

    @JvmStatic
    @JvmName("getComponent")
    internal fun getComponent(context: Context): DocWebViewerSingletonComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return DocWebViewerPlugin.docWebViewerComponent
    }

    @Suppress("unused")
    @JvmStatic
    fun getDocWebViewerFeature(context: Context): DocWebViewerFeature {
        return getComponent(
            context
        ).getDocWebViewerFeature()
    }
}