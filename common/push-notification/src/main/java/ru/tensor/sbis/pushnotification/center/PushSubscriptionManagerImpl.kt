package ru.tensor.sbis.pushnotification.center

import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.repository.PushNotificationRepository
import ru.tensor.sbis.pushnotification.repository.model.SupportTypesData

/**
 * Класс, отвечающий за подписку на пуш уведомления в контроллере
 *
 * @author ev.grigoreva
 */
internal class PushSubscriptionManagerImpl(
    private val repository: PushNotificationRepository
): PushSubscriptionManager {

    private val subscribers = HashMap<PushType, HashSet<Any>>()
    private val disabledTypes = HashSet<PushType>()

    override fun enablePushType(type: PushType) {
        disabledTypes.remove(type)
        onSupportTypesChanged()
    }

    override fun disablePushType(type: PushType) {
        disabledTypes.add(type)
        onSupportTypesChanged()
    }

    override fun disableAllPushTypes() {
        disabledTypes.addAll(subscribers.keys.toSet())
        onSupportTypesChanged()
    }

    override fun enableAllPushTypes() {
        disabledTypes.clear()
        onSupportTypesChanged()
    }

    override fun addSubscriber(type: PushType, subscriber: Any) {
        subscribers[type] = (subscribers[type] ?: HashSet()).apply { add(subscriber) }
        onSupportTypesChanged()
    }

    override fun removeSubscriber(type: PushType, subscriber: Any) {
        subscribers[type]?.apply {
            remove(subscriber)
            if (isEmpty()) {
                subscribers.remove(type)
            }
        }
        onSupportTypesChanged()
    }

    private fun onSupportTypesChanged() {
        repository.setSupportTypes(SupportTypesData(subscribers.keys.toSet().minus(disabledTypes)))
    }
}