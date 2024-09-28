package ru.tensor.sbis.widget_player

import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.richtext.RichTextPlugin
import ru.tensor.sbis.widget_player.contract.WidgetPlayerDependency
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.toolbox_decl.linkopener.service.DecoratedLinkFeature
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory
import ru.tensor.sbis.widget_player.contract.WidgetPlayerStoreInitializer

/**
 * @author am.boldinov
 */
object WidgetPlayerPlugin : BasePlugin<Unit>() {

    private val fallbackThemedContext by lazy(LazyThreadSafetyMode.NONE) {
        RichTextPlugin.themedContext(application)
    }

    @JvmField
    internal val component = object : WidgetPlayerDependency {
        override val openLinkController: OpenLinkController?
            get() = openLinkControllerFeatureProvider?.get()?.openLinkController
        override val webViewerFeature: DocWebViewerFeature?
            get() = webViewerFeatureProvider?.get()
        override val decoratedLinkServiceRepository: LinkDecoratorServiceRepository?
            get() = decoratedLinkFeatureProvider?.get()?.linkDecoratorServiceRepository
        override val themedContext: SbisThemedContext
            get() = themedAppContext ?: fallbackThemedContext
        override val initializers: Set<WidgetPlayerStoreInitializer>
            get() = widgetInitializers
        override val audioMessageViewDataFactory: AudioMessageViewDataFactory?
            get() = audioMessageViewDataFactoryProvider?.get()
        override val videoMessageViewDataFactory: VideoMessageViewDataFactory?
            get() = videoMessageViewDataFactoryProvider?.get()
        override val viewerSliderIntentFactory: ViewerSliderIntentFactory?
            get() = viewerSliderIntentFactoryProvider?.get()
    }

    private var decoratedLinkFeatureProvider: FeatureProvider<DecoratedLinkFeature>? = null
    private var webViewerFeatureProvider: FeatureProvider<DocWebViewerFeature>? = null
    private var openLinkControllerFeatureProvider: FeatureProvider<OpenLinkController.Provider>? = null
    private var widgetInitializers = mutableSetOf<WidgetPlayerStoreInitializer>()
    private var audioMessageViewDataFactoryProvider: FeatureProvider<AudioMessageViewDataFactory>? = null
    private var videoMessageViewDataFactoryProvider: FeatureProvider<VideoMessageViewDataFactory>? = null
    private var viewerSliderIntentFactoryProvider: FeatureProvider<ViewerSliderIntentFactory>? = null

    override val api = emptySet<FeatureWrapper<out Feature>>()

    override val dependency = Dependency.Builder()
        .optional(DecoratedLinkFeature::class.java) { decoratedLinkFeatureProvider = it }
        .optional(DocWebViewerFeature::class.java) { webViewerFeatureProvider = it }
        .optional(OpenLinkController.Provider::class.java) { openLinkControllerFeatureProvider = it }
        .optionalSet(WidgetPlayerStoreInitializer::class.java) {
            it?.forEach { provider ->
                widgetInitializers.add(provider.get())
            }
        }
        .optional(AudioMessageViewDataFactory::class.java) { audioMessageViewDataFactoryProvider = it }
        .optional(VideoMessageViewDataFactory::class.java) { videoMessageViewDataFactoryProvider = it }
        .optional(ViewerSliderIntentFactory::class.java) { viewerSliderIntentFactoryProvider = it }
        .build()

    override val customizationOptions = Unit

    override fun doAfterInitialize() {
        super.doAfterInitialize()
        widgetInitializers.add(WidgetPlayerDefaultInitializer(component))
    }
}