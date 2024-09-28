package ru.tensor.sbis.link_opener

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.link_opener.contract.LinkOpenerDependency
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureConfiguration
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureFacade
import ru.tensor.sbis.link_opener.domain.SupportedLinksProviderImpl
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerPendingLinkFeature
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.toolbox_decl.linkopener.SupportedLinksProvider
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.toolbox_decl.linkopener.service.DecoratedLinkFeature

/**
 * Плагин компонента открытия ссылок.
 *
 * @author as.chadov
 */
object LinkOpenerPlugin : BasePlugin<LinkOpenerPlugin.CustomizationOptions>() {

    private lateinit var mainActivityProvider: FeatureProvider<MainActivityProvider>
    private lateinit var docWebViewerFeatureProvider: FeatureProvider<DocWebViewerFeature>
    private lateinit var decoratedLinkFeature: FeatureProvider<DecoratedLinkFeature>
    private lateinit var networkUtilsProvider: FeatureProvider<NetworkUtils>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(OpenLinkController.Provider::class.java) { LinkOpenerFeatureFacade },
        FeatureWrapper(LinkOpenerRegistrar.Provider::class.java) { LinkOpenerFeatureFacade },
        FeatureWrapper(LinkOpenHandlerCreator.Provider::class.java) { LinkOpenerFeatureFacade },
        FeatureWrapper(LinkOpenerPendingLinkFeature.Provider::class.java) { LinkOpenerFeatureFacade },
        FeatureWrapper(SupportedLinksProvider::class.java) { SupportedLinksProviderImpl(application) }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(MainActivityProvider::class.java) { mainActivityProvider = it }
        .require(DocWebViewerFeature::class.java) { docWebViewerFeatureProvider = it }
        .require(DecoratedLinkFeature::class.java) { decoratedLinkFeature = it }
        .require(NetworkUtils::class.java) { networkUtilsProvider = it }
        .build()

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    override fun initialize() {
        val dependency = object : LinkOpenerDependency, DecoratedLinkFeature by decoratedLinkFeature.get() {
            override val networkUtils get() = networkUtilsProvider.get()
            override fun getMainActivityIntent() = mainActivityProvider.get().getMainActivityIntent()
            override fun showDocumentLink(context: Context, title: String?, url: String) =
                docWebViewerFeatureProvider.get().showDocumentLink(context, title, url)

            override fun showDocumentLink(context: Context, title: String?, url: String, uuid: String?) =
                docWebViewerFeatureProvider.get().showDocumentLink(context, title, url, uuid)

            override fun createDocumentActivityIntent(
                context: Context,
                title: String?,
                url: String,
                uuid: String?
            ): Intent = docWebViewerFeatureProvider.get().createDocumentActivityIntent(context, title, url, uuid)

        }
        LinkOpenerFeatureFacade.configure(
            context = application,
            dependency = dependency,
            configuration = customizationOptions.configuration
        )
    }

    /**
     * Предназначен для настройки модуля.
     */
    class CustomizationOptions internal constructor() {

        /**
         * Опциональная конфигурация использования компонента открытия ссылок.
         */
        var configuration: LinkOpenerFeatureConfiguration = LinkOpenerFeatureConfiguration.DEFAULT
    }
}