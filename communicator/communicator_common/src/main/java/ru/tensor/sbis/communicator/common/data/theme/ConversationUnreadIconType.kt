package ru.tensor.sbis.communicator.common.data.theme

/**
 * Типы иконки статуса непрочитанности для ячейки диалогов/каналов
 *
 * @author vv.chekurda
 */
enum class ConversationUnreadIconType {

    /** У диалога сохранен драфт сообщения */
    DRAFT,

    /** В процессе отправки сообщения */
    SENDING,

    /** Произошла ошибка отправки сообщения */
    ERROR,

    /** Исходящее сообщение непрочитано получателем */
    UNREAD
}