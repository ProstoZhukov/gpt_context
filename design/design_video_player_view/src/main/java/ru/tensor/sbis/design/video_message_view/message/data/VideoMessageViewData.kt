package ru.tensor.sbis.design.video_message_view.message.data

import ru.tensor.sbis.communication_decl.communicator.media.data.MediaMessageData
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaType
import ru.tensor.sbis.design.video_message_view.player.data.VideoPlayerViewData

/**
 * Модель с данными для отображения видеосообщения.
 *
 * @property playerData данные для плеера.
 * @property recognizedText распознанное сообщение.
 * @property recognized статус распознания:
 * - null - идет распознание;
 * - true - распознано;
 * - false - не удалась распознать.
 * @property isEdited признак редакции расшифровки.
 *
 * @author da.zhukov
 */
data class VideoMessageViewData internal constructor(
    val playerData: VideoPlayerViewData,
    override val recognizedText: CharSequence?,
    override val recognized: Boolean?,
    val isEdited: Boolean = false
) : MediaMessageData {

    override val duration: Int
        get() = playerData.durationSeconds

    override val type: MediaType = MediaType.VIDEO

    /**
     * Состояние развернутости сообщения. false, если сообщение должно быть свернуто
     */
    var isExpanded: Boolean = false
}
