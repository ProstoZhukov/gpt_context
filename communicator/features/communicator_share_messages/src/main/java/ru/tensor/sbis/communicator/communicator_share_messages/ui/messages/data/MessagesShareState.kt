package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.data

/**
 * Состояния шаринга в сообщения.
 *
 * @author dv.baranov
 */
enum class MessagesShareState {

    /**
     * Начальное состояние, пользователь выбирает в какую переписку пошарить.
     */
    CONVERSATION_SELECTION,

    /**
     * Переписка выбрана, теперь пользователь может ввести текст сообщения.
     */
    ENTERING_COMMENT,

    /**
     * Пользователь нажал кнопку отправки, сообщение доставляется.
     */
    SENDING
}