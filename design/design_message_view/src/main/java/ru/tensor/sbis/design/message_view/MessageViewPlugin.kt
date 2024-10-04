package ru.tensor.sbis.design.message_view

import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.message_view.contact.MessageViewComponentsFactory
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.MessageViewComponentsFactoryImpl
import ru.tensor.sbis.design.message_view.utils.rich_text_converter.MessageRichTextConverterImpl
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController

/**
 * Плагин модуля [MessageView].
 *
 * @author dv.baranov
 */
object MessageViewPlugin : BasePlugin<MessageViewPlugin.CustomizationOptions>() {

    internal lateinit var openLinkControllerProvider: FeatureProvider<OpenLinkController.Provider>
    internal lateinit var docWebViewerFeatureProvider: FeatureProvider<DocWebViewerFeature>
    internal var audioMessageViewDataFactoryProvider: FeatureProvider<AudioMessageViewDataFactory>? = null
    internal var videoMessageViewDataFactoryProvider: FeatureProvider<VideoMessageViewDataFactory>? = null
    internal var complainServiceFeatureProvider: FeatureProvider<ComplainService.Provider>? = null
    internal var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null

    internal val richTextConverter: MessageRichTextConverterImpl by lazy {
        MessageRichTextConverterImpl(application)
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(MessageViewComponentsFactory::class.java) { MessageViewComponentsFactoryImpl }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(OpenLinkController.Provider::class.java) { openLinkControllerProvider = it }
            .require(DocWebViewerFeature::class.java) { docWebViewerFeatureProvider = it }
            .optional(AudioMessageViewDataFactory::class.java) { audioMessageViewDataFactoryProvider = it }
            .optional(VideoMessageViewDataFactory::class.java) { videoMessageViewDataFactoryProvider = it }
            .optional(ComplainService.Provider::class.java) { complainServiceFeatureProvider = it }
            .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
            .build()
    }

    override val customizationOptions = CustomizationOptions()

    class CustomizationOptions internal constructor() {

        var needUseDefaultTheme: Boolean = false
    }
}