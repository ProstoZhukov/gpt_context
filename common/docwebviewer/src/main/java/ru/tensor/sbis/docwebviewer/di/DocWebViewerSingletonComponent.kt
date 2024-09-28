package ru.tensor.sbis.docwebviewer.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.docwebviewer.contract.DocWebViewerDependency
import javax.inject.Singleton

/**
 * DI компонент модуля открытия документа в WebView
 *
 * @author ma.kolpakov
 */
@Singleton
@Component(modules = [DocWebViewerSingletonModule::class])
interface DocWebViewerSingletonComponent {

    fun getDocWebViewerFeature(): DocWebViewerFeature

    fun getDependency(): DocWebViewerDependency

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance dependency: DocWebViewerDependency
        ): DocWebViewerSingletonComponent
    }
}
