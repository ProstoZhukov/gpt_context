package ru.tensor.sbis.communicator.send_message.model

import ru.tensor.sbis.communicator.send_message.worker.SendMessageWorker

/**
 * Статус выполнения работы [SendMessageWorker] по отправке сообщения.
 *
 * @author dv.baranov
 */
internal enum class SendMessageWorkStatus {

    /**
     * Сообщение еще не отправляли.
     */
    NOT_SENT,

    /**
     * Нужно дождаться доставки сообщения.
     */
    NEED_WAIT_FOR_RESULT,

    /**
     * SyncStatus сообщения Success - сообщение доставлено.
     */
    RESULT_SUCCESS,

    /**
     * SyncStatus сообщения ERROR - доставка сообщения с ошибкой.
     */
    RESULT_FAILURE,

    /**
     * Работу отменил пользователь.
     */
    CANCELED_BY_USER
}
