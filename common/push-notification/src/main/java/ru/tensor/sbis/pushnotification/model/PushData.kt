package ru.tensor.sbis.pushnotification.model

import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Модель данных по пуш уведомлению.
 * Может расширяться прикладными разработчиками для добавления собственных полей.
 *
 * @property message поступившее на устройство пуш-сообщение
 * @property notificationType тип уведомления, к которому относится пуш
 *
 * @author am.boldinov
 */
open class PushData(val message: PushNotificationMessage) {

    val notificationType = message.type.toNotificationType()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PushData

        if (message != other.message) return false
        if (notificationType != other.notificationType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + notificationType.hashCode()
        return result
    }


}