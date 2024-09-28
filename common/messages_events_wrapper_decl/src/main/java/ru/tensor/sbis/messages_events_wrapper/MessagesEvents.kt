package ru.tensor.sbis.messages_events_wrapper

/**
 * Общие ключи и значения ошибок для всех модулей.
 * Открыто для упрощения поддержки актуальности при изменениях контроллера коммуникатора,
 * поэтому стоит их использовать везде где только можно.
 */
object MessagesEvents {
    /**
     * Ключ идентификатора сообщения
     */
    val EVENT_KEY_MESSAGE_ID = "message_id"

    /**
     * Ключ статуса сообщения
     */
    val MESSAGE_STATUS_KEY = "message_status"

    /**
     * Ключ ошибки
     */
    val EVENT_KEY_ERROR = "error"

    /**
     * Ошибка "непривязанный номер телефона"
     */
    val EVENT_UNATTACHED_PHONE_NUMBER_ERROR = "unattached_phone_number"
}