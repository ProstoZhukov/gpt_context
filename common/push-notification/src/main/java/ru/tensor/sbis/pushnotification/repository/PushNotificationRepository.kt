package ru.tensor.sbis.pushnotification.repository

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.repository.model.SupportTypesData

/**
 * Поставщик данных для работы с кешем пуш-сообщений.
 * Каждый поступивший пуш на устройство кешируется в локальном хранилище на диске на ограниченное время,
 * но он может быть удален в случае если пользователь удаляет пуш по свайпу или программно.
 *
 * @author am.boldinov
 */
interface PushNotificationRepository {

    /**
     * Сохраняет новый токен в хранилище.
     */
    fun setToken(token: String)

    /**
     * Обрабатывает изменение списка поддерживаемых типов пушей
     *
     * @param data модель данных с типами пушей
     */
    fun setSupportTypes(data: SupportTypesData)

    /**
     * Создает пуш-сообщение на основе сырого payload, который приходит от Messaging Service.
     * Парсит основные поля, которые характерны к заполнению для всех пушей, и сохраняет сообщение в кеш.
     */
    @WorkerThread
    fun createNotification(payload: Map<String, String>): PushNotificationMessage?

    /**
     * Удаляет пуш-сообщение из кеша.
     */
    @WorkerThread
    fun removeNotification(message: PushNotificationMessage)

    /**
     * Очищает весь кеш пуш-сообщений
     */
    @WorkerThread
    fun clearAll()

    /**
     * Очищает кеш пуш-сообщений по набору типов.
     */
    @WorkerThread
    fun clearAll(types: Set<PushType>)

    /**
     * Очищает кеш пуш-сообщений по конкретному типу.
     */
    @WorkerThread
    fun clearAll(type: PushType)

    /**
     * Возвращает список пуш-сообщений из кеша по конкретному типу.
     */
    @WorkerThread
    fun getNotifications(type: PushType): List<PushNotificationMessage>

    /**
     * Возвращает список пуш-сообщений из кеша на основе списка типов пушей.
     */
    @WorkerThread
    fun getNotifications(types: Set<PushType>): List<PushNotificationMessage>

    /**
     * Возвращает список опубликованных в шторке пуш-уведомлений по заданному тегу.
     * Если пользователь смахнул уведомление или пуш был удален программно, то метод в списке может уже не вернуть этот пуш.
     *
     * @param notifyTag тег, используемый для публикации пушей через [ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface]
     */
    @WorkerThread
    fun getPublishedNotifications(notifyTag: String): List<PushNotificationMessage>

    /**
     * Перезаписывает список текущих опубликованных в шторке пуш-уведомлений по заданному тегу.
     *
     * @param notifyTag тег, используемый для публикации пушей через [ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface]
     * @param messages новый список сообщений, который был/будет опубликован и показан пользователю
     */
    @WorkerThread
    fun savePublishedNotifications(notifyTag: String, messages: List<PushNotificationMessage>)

    /**
     * Возвращает признак группировки нескольких пуш-сообщений в одно пуш-уведомление.
     */
    @WorkerThread
    fun isGrouped(): Boolean

    /**
     * Отправляет подтверждение получения пуш уведомления.
     *
     * @param confirmLink ссылка для подтверждения отправки
     */
    @AnyThread
    fun confirmPushByLink(confirmLink: String)
}