package ru.tensor.sbis.pushnotification.controller

import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Результат обработки списка пуш уведомлений контроллером
 *
 * @property notifyDisplayed список новых уведомлений, которые будут показаны пользователю
 * @property notifyHidden список новых уведомлений, которые пользователь не увидит
 * @property updateDisplayed список обновлений уведомлений, которые будут показаны пользователю
 * @property updateHidden список обновлений уведомлений, которые пользователь не увидит
 * @property cancelled список отмененных к показу уведомлений
 *
 * @author ev.grigoreva
 */
data class HandlingResult(
    val notifyDisplayed: List<PushNotificationMessage> = emptyList(),
    val notifyHidden: List<PushNotificationMessage> = emptyList(),
    val updateDisplayed: List<PushNotificationMessage> = emptyList(),
    val updateHidden: List<PushNotificationMessage> = emptyList(),
    val cancelled: List<PushNotificationMessage> = emptyList()
)
