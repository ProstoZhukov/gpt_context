package ru.tensor.sbis.design.audio_player_view.view.player.contact

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.AudioSource
import ru.tensor.sbis.design.audio_player_view.view.player.data.AudioPlayerViewData
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика для создания [AudioPlayerViewData].
 *
 * @author vv.chekurda
 */
interface AudioPlayerViewDataFactory : Feature {

    /**
     * Создать [AudioPlayerViewData] по [fileInfo] и [jsonObject].
     */
    fun createAudioPlayerViewData(
        uuid: UUID = UUID.randomUUID(),
        fileInfo: MediaPlayerFileInfo,
        jsonObject: JSONObject
    ): AudioPlayerViewData

    /**
     * Создать [AudioPlayerViewData] по [jsonObject].
     */
    fun createAudioPlayerViewData(
        source: AudioSource,
        jsonObject: JSONObject
    ): AudioPlayerViewData

    /**
     * Создать [AudioPlayerViewData].
     */
    fun createAudioPlayerViewData(
        source: AudioSource,
        durationSeconds: Int,
        waveform: ByteArray
    ): AudioPlayerViewData
}