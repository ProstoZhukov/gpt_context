package ru.tensor.sbis.webviewer.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.webviewer.contract.WebViewerDependency
import ru.tensor.sbis.webviewer.contract.WebViewerFeature
import javax.inject.Singleton

/**
 * DI компонент модуля открытия документа в WebView
 *
 * @author ma.kolpakov
 */
@Singleton
@Component(modules = [WebViewerSingletonModule::class])
interface WebViewerSingletonComponent {

    fun getWebViewerFeature(): WebViewerFeature

    fun getDependency(): WebViewerDependency

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance dependency: WebViewerDependency
        ): WebViewerSingletonComponent
    }
}
