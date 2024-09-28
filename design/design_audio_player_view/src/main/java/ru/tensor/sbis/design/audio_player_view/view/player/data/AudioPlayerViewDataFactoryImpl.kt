package ru.tensor.sbis.design.audio_player_view.view.player.data

import androidx.core.net.toUri
import org.json.JSONObject
import ru.tensor.sbis.attachments.decl.canonicalUri
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.AudioSource
import ru.tensor.sbis.communication_decl.communicator.media.data.SourceData
import ru.tensor.sbis.communication_decl.communicator.media.getDuration
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WaveformUtils
import ru.tensor.sbis.design.audio_player_view.view.player.contact.AudioPlayerViewDataFactory
import java.util.UUID

/**
 * Реализация фабрики [AudioPlayerViewDataFactory].
 *
 * @author vv.chekurda
 */
internal object AudioPlayerViewDataFactoryImpl : AudioPlayerViewDataFactory {

    override fun createAudioPlayerViewData(
        uuid: UUID,
        fileInfo: MediaPlayerFileInfo,
        jsonObject: JSONObject
    ): AudioPlayerViewData =
        createAudioPlayerViewData(
            source = AudioSource(
                uuid = uuid,
                data = fileInfo.canonicalLocalUri()?.let { uri ->
                    SourceData.UriData(uri.toUri())
                } ?: SourceData.DiskData(fileInfo.attachId)
            ),
            jsonObject = jsonObject
        )

    override fun createAudioPlayerViewData(
        source: AudioSource,
        jsonObject: JSONObject
    ): AudioPlayerViewData =
        createAudioPlayerViewData(
            source = source,
            durationSeconds = jsonObject.getDuration(),
            waveform = WaveformUtils.getWaveform(jsonObject)
        )

    override fun createAudioPlayerViewData(
        source: AudioSource,
        durationSeconds: Int,
        waveform: ByteArray
    ): AudioPlayerViewData =
        AudioPlayerViewData(
            audioSource = source,
            durationSeconds = durationSeconds,
            waveform = waveform
        )

    private fun MediaPlayerFileInfo.canonicalLocalUri(): String? =
        canonicalUri(localPath)
}