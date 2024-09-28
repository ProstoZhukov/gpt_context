package ru.tensor.sbis.design.cloud_view.model

/**
 * Перечисление состояний отправки
 *
 * @author ma.kolpakov
 */
enum class SendingState {
    /* Отправлено */
    SENT,

    /* В процессе отправки */
    SENDING,

    /* Во время отправки произошла ошибка. Необходима повторная отправка вручную */
    NEEDS_MANUAL_SEND,

    /* Прочитано */
    IS_READ
}