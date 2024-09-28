package ru.tensor.sbis.pushnotification.repository

import ru.tensor.sbis.push.generated.PushNotification
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.repository.model.PushCloudAction
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.util.PushParserUtil

private const val CLOUD_ACTION_CANCEL_THRESHOLD = 512 // пороговое значение для подтипа удаления.

/**
 * Конвертер модели контроллера пуш-сообщения в нативную модель, которая является более лояльной к пользователю
 * и выставляется наружу в апи модуля.
 *
 * @author am.boldinov
 */
internal class PushNotificationMessageConverter {

    /**
     * Конвертирует модель контроллера в UI-модель для предоставления в апи
     */
    fun convert(source: PushNotification): PushNotificationMessage {
        val cloudAction = if (source.subType < CLOUD_ACTION_CANCEL_THRESHOLD) {
            PushCloudAction.NOTIFY
        } else {
            PushCloudAction.CANCEL
        }
        return PushNotificationMessage(
            notificationUuid = source.guid,
            type = PushType.fromValue(source.type.toString()),
            subType = source.subType,
            sendTime = source.sendTime,
            documentTime = source.documentTime,
            addresseeId = source.addresseeId,
            documentId = source.documentId,
            sendDocumentId = source.sendDocId,
            title = source.title,
            message = source.message,
            alert = source.alert,
            cloudAction = cloudAction,
            data = PushParserUtil.parseJson(source.data),
            extraData = PushParserUtil.parseJson(source.extraData),
            notifyMeta = source.pushNotifyMeta,
            rawType = source.type,
            rawData = source.data,
            rawExtraData = source.extraData
        )
    }

    /**
     * Конвертирует UI-модель пуш-сообщения, полученная ранее через [convert], обратно в модель контроллера.\
     * Используется в основном для сохранения модифицированного пуш-сообщения обратно в БД.
     */
    fun convert(source: PushNotificationMessage): PushNotification {
        return PushNotification(
            source.notificationUuid,
            source.addresseeId,
            source.rawType,
            source.subType,
            source.rawData,
            source.rawExtraData,
            source.documentTime,
            source.sendTime,
            source.documentId,
            source.sendDocumentId,
            source.title,
            source.message,
            source.alert,
            source.notifyMeta
        )
    }

}

