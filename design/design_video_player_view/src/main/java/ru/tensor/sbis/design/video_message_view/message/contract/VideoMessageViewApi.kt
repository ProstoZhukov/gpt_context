package ru.tensor.sbis.design.video_message_view.message.contract

import android.view.View
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.design.video_message_view.message.VideoMessageView
import ru.tensor.sbis.design.video_message_view.message.data.VideoMessageViewData

/**
 * API компонента видео сообщения с расшифровкой [VideoMessageView].
 *
 * @author vv.chekurda
 */
interface VideoMessageViewApi : MediaMessage {

    /**
     * Признак исходящего сообщения.
     */
    var outcome: Boolean

    /**
     * Данными для отображения видеосообщения.
     */
    var data: VideoMessageViewData?

    /**
     * Обработчик событий связанных с видеосообщениями.
     */
    var actionListener: MediaMessage.ActionListener?

    /**
     * Установить слушатель клика по кнопке удалить.
     * Крестик для удаления отображается при наличии слушателя.
     */
    var onDeleteClickListener: View.OnClickListener?
}