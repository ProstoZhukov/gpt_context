package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.utils

/**
 * Получить текст сообщения из текста шаринга + комментария в панели ввода.
 *
 * @author vv.chekurda
 */
internal fun getMessageText(shareText: String, comment: String): String =
    when {
        shareText.isNotEmpty() && comment.isNotEmpty() -> {
            StringBuilder()
                .appendLine(comment)
                .append(shareText)
                .toString()
        }
        comment.isNotEmpty() -> comment
        shareText.isNotEmpty() -> shareText
        else -> comment
    }