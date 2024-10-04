package ru.tensor.sbis.design.message_panel.audio_recorder

import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.design.audio_player_view.view.player.contact.AudioPlayerViewDataFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.AudioRecorderDelegateImpl
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.RecordCancelConfirmationDialogProviderImpl
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.AudioRecorderDelegateFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.RecordCancelConfirmationDialogProvider
import ru.tensor.sbis.design.message_panel.decl.record.AudioWaveformHelper
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин модуля аудиозаписи панели сообщений.
 *
 * @author vv.chekurda
 */
object MessagePanelAudioRecorderPlugin : BasePlugin<Unit>() {

    private lateinit var mediaPlayerFeatureProvider: FeatureProvider<MediaPlayerFeature>
    private lateinit var waveformHelperProviderFeature: FeatureProvider<AudioWaveformHelper.Provider>
    private lateinit var audioPlayerViewFactoryProvider: FeatureProvider<AudioPlayerViewDataFactory>
    private var callStateProviderFeature: FeatureProvider<CallStateProvider>? = null

    internal val mediaPlayerFeature: MediaPlayerFeature by lazy {
        mediaPlayerFeatureProvider.get()
    }
    internal val waveformHelperProvider: AudioWaveformHelper.Provider by lazy {
        waveformHelperProviderFeature.get()
    }
    internal val audioPlayerViewDataFactory: AudioPlayerViewDataFactory by lazy {
        audioPlayerViewFactoryProvider.get()
    }
    internal val callStateProvider: CallStateProvider? by lazy {
        callStateProviderFeature?.get()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(AudioRecorderDelegateFactory::class.java) { AudioRecorderDelegateImpl.Companion },
        FeatureWrapper(RecordCancelConfirmationDialogProvider::class.java) { RecordCancelConfirmationDialogProviderImpl },
    )

    override val dependency: Dependency =
        Dependency.Builder()
            .require(MediaPlayerFeature::class.java, ::mediaPlayerFeatureProvider::set)
            .require(AudioWaveformHelper.Provider::class.java, ::waveformHelperProviderFeature::set)
            .require(AudioPlayerViewDataFactory::class.java, ::audioPlayerViewFactoryProvider::set)
            .optional(CallStateProvider::class.java, ::callStateProviderFeature::set)
            .build()

    override val customizationOptions = Unit
}