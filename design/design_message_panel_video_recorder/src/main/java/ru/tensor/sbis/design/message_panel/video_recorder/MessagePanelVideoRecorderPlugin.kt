package ru.tensor.sbis.design.message_panel.video_recorder

import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.design.message_panel.video_recorder.integration.VideoRecorderDelegateImpl
import ru.tensor.sbis.design.message_panel.video_recorder.integration.contract.VideoRecorderDelegateFactory
import ru.tensor.sbis.design.video_message_view.player.contract.VideoPlayerViewDataFactory
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин модуля видеозаписи панели сообщений.
 *
 * @author vv.chekurda
 */
object MessagePanelVideoRecorderPlugin : BasePlugin<MessagePanelVideoRecorderPlugin.CustomisationOptions>() {

    private lateinit var mediaPlayerFeatureProvider: FeatureProvider<MediaPlayerFeature>
    private lateinit var videoPlayerViewDataFactoryProvider: FeatureProvider<VideoPlayerViewDataFactory>
    private var callStateProviderFeature: FeatureProvider<CallStateProvider>? = null

    internal val mediaPlayerFeature: MediaPlayerFeature by lazy {
        mediaPlayerFeatureProvider.get()
    }
    internal val videoPlayerViewDataFactory: VideoPlayerViewDataFactory by lazy {
        videoPlayerViewDataFactoryProvider.get()
    }
    internal val callStateProvider: CallStateProvider? by lazy {
        callStateProviderFeature?.get()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(VideoRecorderDelegateFactory::class.java) { VideoRecorderDelegateImpl.Companion },
    )
    override val customizationOptions: CustomisationOptions = CustomisationOptions()
    override val dependency: Dependency =
        Dependency.Builder()
            .require(MediaPlayerFeature::class.java, ::mediaPlayerFeatureProvider::set)
            .require(VideoPlayerViewDataFactory::class.java, ::videoPlayerViewDataFactoryProvider::set)
            .optional(CallStateProvider::class.java, ::callStateProviderFeature::set)
            .build()

    class CustomisationOptions internal constructor()
}