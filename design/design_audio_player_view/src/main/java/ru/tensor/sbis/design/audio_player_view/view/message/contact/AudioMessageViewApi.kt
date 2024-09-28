package ru.tensor.sbis.design.audio_player_view.view.message.contact

import android.view.View
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.design.audio_player_view.view.message.AudioMessageView
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageViewData

/**
 * API компонента аудио сообщения с расшифровкой [AudioMessageView].
 *
 * @author vv.chekurda
 */
interface AudioMessageViewApi : MediaMessage {

    /**
     * Установить данные для отображения аудиосообщения.
     */
    var data: AudioMessageViewData?

    /**
     * Установить слушатель действий над аудиосообщением.
     */
    var actionListener: MediaMessage.ActionListener?

    /**
     * Установить слушатель клика по кнопке удалить.
     * Крестик для удаления отображается при наличии слушателя.
     */
    var onDeleteClickListener: View.OnClickListener?

    /**
     * Признак отображения скругленной карточкой.
     * По умолчанию true.
     */
    var isCard: Boolean

    /**
     * Сбросить состояние перед добавлением в пул для переиспользывания.
     */
    fun recycle()
}