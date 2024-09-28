package ru.tensor.sbis.pushnotification.controller

import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Обработчик входящих пуш уведомлений, который их перехватывает
 * в обход буферизации, парсинга и кеширования.
 * Сообщения от FCM напрямую делегируются этому обработчику.
 *
 * @author am.boldinov
 */
fun interface PushHandler {
    /**
     * Обрабатывает пуш уведомления в таком виде, в котором они поступили на устройство
     *
     * @param rawMessage  сырое fcm сообщение
     * @param userMessage подготовленное пользовательское сообщение.
     * Может быть null если пуш предназначен не для текущего пользователя или
     * параметры пуша являются не валидными.
     */
    fun handle(rawMessage: Map<String, String>, userMessage: PushNotificationMessage?)
}
