package ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates

import androidx.media3.exoplayer.ExoPlaybackException
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MediaMessageActionListener
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage

/**
 * Делегат реестра сообщений для работы с медиасообщениями.
 *
 * @author da.zhukov
 */
internal class ConversationMediaActionDelegate :
    ConversationMessagesBaseDelegate(),
    MediaMessageActionListener {

    override fun onMediaMessageExpandClicked(data: ConversationMessage, expanded: Boolean): Boolean =
        expanded

    override fun onMediaPlaybackError(error: Throwable) {
        when (error) {
            is ExoPlaybackException -> {
                view?.showErrorPopup(R.string.communicator_conversation_audio_message_playback_error)
            }
            else -> {
                view?.showErrorPopup(error.message)
            }
        }
    }
}