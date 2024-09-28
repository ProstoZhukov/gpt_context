package ru.tensor.sbis.link_opener.di

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.link_opener.contract.LinkOpenerDependency
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureConfiguration
import ru.tensor.sbis.link_opener.di.LinkOpenerSingletonModule.BindsDIModule
import ru.tensor.sbis.link_opener.domain.LinkOpenHandlerCreatorImpl
import ru.tensor.sbis.link_opener.domain.LinkOpenerPendingLinkFeatureImpl
import ru.tensor.sbis.link_opener.domain.LinkOpenerRegistrarImpl
import ru.tensor.sbis.link_opener.domain.OpenLinkControllerImpl
import ru.tensor.sbis.link_opener.domain.router.LinkOpenHandlerFactory.LinkOpenHandlerProducer
import ru.tensor.sbis.link_opener.domain.router.producer.ForeignLinkOpenHandlerProducer
import ru.tensor.sbis.link_opener.domain.router.producer.InAppLinkOpenHandlerProducer
import ru.tensor.sbis.link_opener.domain.router.producer.OtherInAppLinkOpenHandlerProducer
import ru.tensor.sbis.link_opener.domain.router.producer.OurLinkOpenHandlerProducer
import ru.tensor.sbis.link_opener.domain.router.producer.TabsLinkOpenHandlerProducer
import ru.tensor.sbis.link_opener.ui.LinkOpenerProgressDispatcher
import ru.tensor.sbis.link_opener.ui.LinkOpenerProgressDispatcherImpl
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerPendingLinkFeature
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository
import javax.inject.Named
import javax.inject.Singleton

/**
 *  DI-модуль, предоставляющий синглтон зависимости компоненту открытия ссылок [LinkOpenerSingletonComponent].
 *
 *  @author as.chadov
 */
@Suppress("unused")
@Module(includes = [BindsDIModule::class])
internal class LinkOpenerSingletonModule {

    @Singleton
    @Provides
    @WorkerThread
    fun provideServiceRepository(dependency: LinkOpenerDependency): LinkDecoratorServiceRepository =
        dependency.linkDecoratorServiceRepository

    @Provides
    fun provideNetworkUtils(dependency: LinkOpenerDependency): NetworkUtils =
        dependency.networkUtils

    @Provides
    @Named(DOMAIN_KEYWORDS)
    fun provideHost(
        context: Context,
        configuration: LinkOpenerFeatureConfiguration
    ): List<String> = context.resources.run {
        getStringArray(configuration.domainKeywords).toList() +
            getStringArray(configuration.customDomainKeywords).toList()
    }

    @Provides
    @Named(CUSTOM_TABS_COLOR)
    fun provideIsCustomTabsColor(context: Context, configuration: LinkOpenerFeatureConfiguration) =
        ContextCompat.getColor(context, configuration.customToolbarColor)

    @Module
    interface BindsDIModule {

        @Binds
        fun provideOpenLinkController(impl: OpenLinkControllerImpl): OpenLinkController

        @Binds
        fun provideLinkOpenerRegistrar(impl: LinkOpenerRegistrarImpl): LinkOpenerRegistrar

        @Binds
        fun provideLinkOpenHandlerCreator(impl: LinkOpenHandlerCreatorImpl): LinkOpenHandlerCreator

        @Singleton
        @Binds
        fun provideLinkOpenerProgressDispatcher(impl: LinkOpenerProgressDispatcherImpl): LinkOpenerProgressDispatcher

        @Binds
        fun provideLinkOpenerPendingLinkFeature(impl: LinkOpenerPendingLinkFeatureImpl): LinkOpenerPendingLinkFeature

        @[Binds IntoMap ClassKey(InAppLinkOpenHandlerProducer::class)]
        fun asProducerOne(impl: InAppLinkOpenHandlerProducer): LinkOpenHandlerProducer

        @[Binds IntoMap ClassKey(OtherInAppLinkOpenHandlerProducer::class)]
        fun asProducerTwo(impl: OtherInAppLinkOpenHandlerProducer): LinkOpenHandlerProducer

        @[Binds IntoMap ClassKey(OurLinkOpenHandlerProducer::class)]
        fun asProducerThree(impl: OurLinkOpenHandlerProducer): LinkOpenHandlerProducer

        @[Binds IntoMap ClassKey(TabsLinkOpenHandlerProducer::class)]
        fun asProducerFour(impl: TabsLinkOpenHandlerProducer): LinkOpenHandlerProducer

        @[Binds IntoMap ClassKey(ForeignLinkOpenHandlerProducer::class)]
        fun asProducerFive(impl: ForeignLinkOpenHandlerProducer): LinkOpenHandlerProducer
    }
}

/** Ключевые слова для проверки, что это ссылка на домен СБИС. */
internal const val DOMAIN_KEYWORDS = "domainKeywords"

/** Цвет заливки тулбара CustomTabs. */
internal const val CUSTOM_TABS_COLOR = "customTabsColor"
