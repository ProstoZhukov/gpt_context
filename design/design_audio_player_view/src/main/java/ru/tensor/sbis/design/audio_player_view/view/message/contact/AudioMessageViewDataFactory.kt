package ru.tensor.sbis.design.audio_player_view.view.message.contact

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageViewData
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика для создания [AudioMessageViewData].
 *
 * @author vv.chekurda
 */
interface AudioMessageViewDataFactory : Feature {

    /**
     * Создать [AudioMessageViewData] по [fileInfo] и [jsonObject].
     */
    fun createAudioMessageViewData(
        uuid: UUID = UUID.randomUUID(),
        fileInfo: MediaPlayerFileInfo,
        jsonObject: JSONObject,
        recognizedText: CharSequence? = null
    ): AudioMessageViewData

    /**
     * Создать [AudioMessageViewData] по [jsonObject].
     */
    fun createAudioMessageViewData(
        source: MediaSource.AudioSource,
        jsonObject: JSONObject,
        recognizedText: CharSequence? = null
    ): AudioMessageViewData

    /**
     * Создать [AudioMessageViewData].
     */
    fun createAudioMessageViewData(
        source: MediaSource.AudioSource,
        durationSeconds: Int,
        waveform: ByteArray,
        emotion: AudioMessageEmotion,
        recognizedText: CharSequence? = null,
        recognized: Boolean? = false
    ): AudioMessageViewData
}