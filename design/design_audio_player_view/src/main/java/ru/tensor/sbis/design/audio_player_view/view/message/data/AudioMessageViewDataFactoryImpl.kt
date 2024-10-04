package ru.tensor.sbis.design.audio_player_view.view.message.data

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.AudioSource
import ru.tensor.sbis.communication_decl.communicator.media.getRecognized
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.audio_player_view.view.player.data.AudioPlayerViewDataFactoryImpl.createAudioPlayerViewData
import java.util.UUID

/**
 * Реализация фабрики [AudioMessageViewDataFactory].
 *
 * @author vv.chekurda
 */
internal object AudioMessageViewDataFactoryImpl : AudioMessageViewDataFactory {

    override fun createAudioMessageViewData(
        uuid: UUID,
        fileInfo: MediaPlayerFileInfo,
        jsonObject: JSONObject,
        recognizedText: CharSequence?
    ): AudioMessageViewData =
        AudioMessageViewData(
            playerData = createAudioPlayerViewData(
                uuid = uuid,
                fileInfo = fileInfo,
                jsonObject = jsonObject
            ),
            emotion = AudioMessageEmotion.getAudioMessageEmotion(jsonObject),
            recognizedText = recognizedText,
            recognized = jsonObject.getRecognized()
        )

    override fun createAudioMessageViewData(
        source: AudioSource,
        jsonObject: JSONObject,
        recognizedText: CharSequence?
    ): AudioMessageViewData =
        AudioMessageViewData(
            playerData = createAudioPlayerViewData(
                source = source,
                jsonObject = jsonObject
            ),
            emotion = AudioMessageEmotion.getAudioMessageEmotion(jsonObject),
            recognizedText = recognizedText,
            recognized = jsonObject.getRecognized()
        )

    override fun createAudioMessageViewData(
        source: AudioSource,
        durationSeconds: Int,
        waveform: ByteArray,
        emotion: AudioMessageEmotion,
        recognizedText: CharSequence?,
        recognized: Boolean?
    ): AudioMessageViewData =
        AudioMessageViewData(
            playerData = createAudioPlayerViewData(
                source = source,
                durationSeconds = durationSeconds,
                waveform = waveform
            ),
            emotion = emotion,
            recognizedText = recognizedText,
            recognized = recognized
        )
}