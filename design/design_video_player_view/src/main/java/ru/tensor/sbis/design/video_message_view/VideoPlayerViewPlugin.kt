package ru.tensor.sbis.design.video_message_view

import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.design.video_message_view.message.data.VideoMessageViewDataFactoryImpl
import ru.tensor.sbis.design.video_message_view.player.contract.VideoPlayerViewDataFactory
import ru.tensor.sbis.design.video_message_view.player.data.VideoPlayerViewDataFactoryImpl
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин компонента видеопроигрывателя.
 *
 * @author vv.chekurda
 */
object VideoPlayerViewPlugin : BasePlugin<Unit>() {

    private lateinit var mediaPlayerFeatureProvider: FeatureProvider<MediaPlayerFeature>

    internal val defaultMediaPlayer: MediaPlayer by lazy {
        mediaPlayerFeatureProvider.get().getMediaPlayer()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(VideoPlayerViewDataFactory::class.java) { VideoPlayerViewDataFactoryImpl },
        FeatureWrapper(VideoMessageViewDataFactory::class.java) { VideoMessageViewDataFactoryImpl }
    )

    override val dependency: Dependency =
        Dependency.Builder()
            .require(MediaPlayerFeature::class.java, ::mediaPlayerFeatureProvider::set)
            .build()

    override val customizationOptions: Unit = Unit
}