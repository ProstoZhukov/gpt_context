package ru.tensor.sbis.docwebviewer

import android.webkit.WebView
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.docwebviewer.contract.DocWebViewerDependency
import ru.tensor.sbis.docwebviewer.contract.DocWebViewerFeatureImpl
import ru.tensor.sbis.docwebviewer.di.DaggerDocWebViewerSingletonComponent
import ru.tensor.sbis.docwebviewer.di.DocWebViewerSingletonComponent
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.plugin_struct.requireIf
import ru.tensor.sbis.toolbox_decl.BuildConfig
import ru.tensor.sbis.webviewer.contract.WebViewerFeature

/**
 * Плагин для просмотрщика документов через [WebView].
 *
 * @author ma.kolpakov
 */
object DocWebViewerPlugin : BasePlugin<DocWebViewerPlugin.CustomizationOptions>() {

    internal val docWebViewerFeature by lazy {
        DocWebViewerFeatureImpl(linkOpenHandlerCreatorProvider.get(), webViewerProvider.get())
    }

    private lateinit var linkOpenHandlerCreatorProvider: FeatureProvider<LinkOpenHandlerCreator.Provider>
    private lateinit var webViewerProvider: FeatureProvider<WebViewerFeature.Provider>
    private var linkOpenerRegistrarProvider: FeatureProvider<LinkOpenerRegistrar.Provider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(DocWebViewerFeature::class.java) { docWebViewerFeature }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(LinkOpenHandlerCreator.Provider::class.java) { linkOpenHandlerCreatorProvider = it }
            .require(WebViewerFeature.Provider::class.java) { webViewerProvider = it }
            .requireIf(customizationOptions.linkOpenerHandlerEnabled, LinkOpenerRegistrar.Provider::class.java) {
                linkOpenerRegistrarProvider = it
            }
            .build()
    }

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    override fun doAfterInitialize() {
        if(customizationOptions.linkOpenerHandlerEnabled) {
            linkOpenerRegistrarProvider!!.get().linkOpenerRegistrar.registerProvider(docWebViewerFeature)
        }
    }

    internal val docWebViewerComponent: DocWebViewerSingletonComponent by lazy {
        val dependency = object : DocWebViewerDependency {
            override val linkOpenerHandlerCreator: LinkOpenHandlerCreator
                get() = linkOpenHandlerCreatorProvider.get().linkOpenerHandlerCreator
        }
        DaggerDocWebViewerSingletonComponent.factory().create(dependency)
    }

    /**
     * Конфигурация плагина.
     */
    class CustomizationOptions internal constructor() {

        /**
         * Устанавливаем обработчик для [LinkOpenerRegistrar] с базовой конфигурацией исходя из настроек [BuildConfig].
         * При необходимости изменения значения по умолчанию требуется обновить настройки через плагин в build.gradle проекта.
         * См. SabyLinkCfgPlugin
         */
        @Deprecated("Оставлен для обратной совместимости, должен быть private", ReplaceWith("SabyLinkCfgPlugin"))
        var linkOpenerHandlerEnabled: Boolean = BuildConfig.SABYLINK_DOC_WEB_VIEWER_FEATURE
    }
}