package ru.tensor.sbis.link_opener.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.link_opener.contract.LinkOpenerDependency
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureConfiguration
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerPendingLinkFeature
import javax.inject.Singleton

/**
 * Синглтон DI компонент модуля открытия ссылок.
 *
 * @author as.chadov
 */
@Suppress("unused")
@Singleton
@Component(
    modules = [LinkOpenerSingletonModule::class]
)
interface LinkOpenerSingletonComponent {

    fun getContext(): Context

    fun getOpenLinkController(): OpenLinkController

    fun getLinkOpenerRegistrar(): LinkOpenerRegistrar

    fun getLinkOpenHandlerCreator(): LinkOpenHandlerCreator

    fun getLinkOpenerPendingLinkFeature(): LinkOpenerPendingLinkFeature

    val dependency: LinkOpenerDependency

    val configuration: LinkOpenerFeatureConfiguration

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance dependency: LinkOpenerDependency,
            @BindsInstance configuration: LinkOpenerFeatureConfiguration
        ): LinkOpenerSingletonComponent
    }
}
