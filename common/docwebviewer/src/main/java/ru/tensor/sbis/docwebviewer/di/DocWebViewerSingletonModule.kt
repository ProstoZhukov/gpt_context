package ru.tensor.sbis.docwebviewer.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.docwebviewer.DocWebViewerPlugin
import javax.inject.Singleton

/**
 *  DI модуль, предоставляющий зависимости компоненту открытия документа через WebView
 *
 * @author ma.kolpakov
 */
@Module
internal class DocWebViewerSingletonModule {

    @Singleton
    @Provides
    internal fun provideDocumentWebViewerFeature(): DocWebViewerFeature =
        DocWebViewerPlugin.docWebViewerFeature
}