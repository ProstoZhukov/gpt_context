package ru.tensor.sbis.webviewer.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.webviewer.WebViewerPlugin
import ru.tensor.sbis.webviewer.contract.WebViewerFeature
import javax.inject.Singleton

/**
 *  DI модуль, предоставляющий зависимости компоненту WebViewer
 *
 *  @author ma.kolpakov
 */
@Module
internal class WebViewerSingletonModule {

    @Singleton
    @Provides
    internal fun provideDocumentWebViewerFeature(): WebViewerFeature =
        WebViewerPlugin.webViewerFeature
}