package ru.tensor.sbis.pushnotification.model.factory

import ru.tensor.sbis.pushnotification.model.PushData
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Реализация фабрики по умолчанию, создает модель [PushData].
 * Может подойти для случаев простейших пуш-уведомлений, где отображается заголовок и текст, которые уже
 * лежат в готовом виде в сообщении [PushNotificationMessage].
 *
 * @author am.boldinov
 */
class SimplePushDataFactory : PushDataFactory<PushData> {

    override fun create(message: PushNotificationMessage): PushData {
        return PushData(message)
    }
}