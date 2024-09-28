package ru.tensor.sbis.design.audio_player_view

import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WaveformDownscaleUtil
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageViewDataFactoryImpl
import ru.tensor.sbis.design.audio_player_view.view.player.contact.AudioPlayerViewDataFactory
import ru.tensor.sbis.design.audio_player_view.view.player.data.AudioPlayerViewDataFactoryImpl
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин компонента аудиопроигрывания.
 *
 * @author vv.chekurda
 */
object AudioPlayerViewPlugin : BasePlugin<Unit>() {

    private lateinit var mediaPlayerFeatureProvider: FeatureProvider<MediaPlayerFeature>
    private var waveformHelperProviderFeature: FeatureProvider<WaveformDownscaleUtil.Provider>? = null

    internal val defaultMediaPlayer: MediaPlayer by lazy {
        mediaPlayerFeatureProvider.get().getMediaPlayer()
    }

    internal val waveformHelperProvider: WaveformDownscaleUtil.Provider? by lazy {
        waveformHelperProviderFeature?.get()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(AudioMessageViewDataFactory::class.java) { AudioMessageViewDataFactoryImpl },
        FeatureWrapper(AudioPlayerViewDataFactory::class.java) { AudioPlayerViewDataFactoryImpl }
    )

    override val dependency: Dependency =
        Dependency.Builder()
            .require(MediaPlayerFeature::class.java, ::mediaPlayerFeatureProvider::set)
            .optionalSet(WaveformDownscaleUtil.Provider::class.java) { waveformHelperProviderFeature = it?.first() }
            .build()

    override val customizationOptions = Unit
}