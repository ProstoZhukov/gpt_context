package ru.tensor.sbis.design.message_panel.decl.message

/**
 * Вспомогательный класс для предоставления информации о моделях из сервиса [MessageService]
 *
 * @author ma.kolpakov
 */
interface MessageServiceHelper<in MESSAGE, in RESULT> {

    /**
     * Возвращает `true`, если [message] является контейнером с ошибкой
     */
    suspend fun isMessageError(message: MESSAGE): Boolean

    /**
     * Возвращает `true`, если результат отправки [message] - ошибка
     */
    suspend fun isResultError(message: RESULT): Boolean

    /**
     * Возвращает сообщение об ошибке из контейнера [message]
     */
    suspend fun getMessageError(message: MESSAGE): String

    /**
     * Возвращает сообщение об ошибке отправки из [message]
     */
    suspend fun getResultError(message: RESULT): String
}
