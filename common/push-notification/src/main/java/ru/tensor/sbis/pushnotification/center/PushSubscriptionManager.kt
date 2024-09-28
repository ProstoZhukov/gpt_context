package ru.tensor.sbis.pushnotification.center

import ru.tensor.sbis.pushnotification.PushType

/**
 * Интерфейс менеджера подписки на пуш уведомления
 *
 * @author ev.grigoreva
 */
interface PushSubscriptionManager {

    /** Отключить подписку на пуши по типу */
    fun disablePushType(type: PushType)

    /** Включить подписку на пуши по типу */
    fun enablePushType(type: PushType)

    /** Отключить подписку на пуши по всем типам */
    fun disableAllPushTypes()

    /** Включить подписку на пуши по всем типам */
    fun enableAllPushTypes()

    /** Добавить подписчика на пуши по типу */
    fun addSubscriber(type: PushType, subscriber: Any)

    /** Удалить подписчика на пуши по типу */
    fun removeSubscriber(type: PushType, subscriber: Any)
}