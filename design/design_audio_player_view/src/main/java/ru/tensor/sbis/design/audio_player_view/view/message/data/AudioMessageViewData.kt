package ru.tensor.sbis.design.audio_player_view.view.message.data

import ru.tensor.sbis.communication_decl.communicator.media.data.MediaMessageData
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaType
import ru.tensor.sbis.design.audio_player_view.view.player.data.AudioPlayerViewData

/**
 * Модель с данными для отображения аудиосообщения.
 *
 * @property playerData данные для плеера.
 * @property emotion эмоция сообщения.
 * @property recognizedText распознанное сообщение.
 * @property recognized статус распознания:
 * - null - идет распознание;
 * - true - распознано;
 * - false - не удалась распознать.
 *
 * @author vv.chekurda
 */
data class AudioMessageViewData internal constructor(
    val playerData: AudioPlayerViewData,
    val emotion: AudioMessageEmotion,
    override val recognizedText: CharSequence?,
    override val recognized: Boolean?
) : MediaMessageData {

    override val duration: Int
        get() = playerData.durationSeconds

    override val type: MediaType = MediaType.AUDIO

    /**
     * Состояние развернутости сообщения. false, если сообщение должно быть свернуто
     */
    var isExpanded: Boolean = false
}