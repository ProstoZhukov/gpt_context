package ru.tensor.sbis.pushnotification.controller.base.strategy

import android.os.Bundle
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.collections.Predicate
import ru.tensor.sbis.pushnotification.contract.PushCancelContract
import ru.tensor.sbis.pushnotification.model.PushData
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.util.PushParserUtil.optUUID

// подтип уведомления об удалении одиночного пуша
private const val CANCEL_SINGLE_SUBTYPE = 512

// подтип уведомления об удалении всех опубликованных пушей
private const val CANCEL_ALL_SUBTYPE = 1024

/**
 * Реализация стратегии удаления пуш-уведомлений по умолчанию.
 *
 * @author am.boldinov
 */
open class DefaultPushCancelStrategy : PushCancelStrategy<PushData> {

    /**
     * Проверяет подтип уведомления об удалении, которое инициировало облако - массовое или одиночное.
     * Проверяет дату отправки пуша об удалении, она должна быть обязательно >= чем дата отправки оригинального уведомления.
     * Проверяет идентификаторы уведомлений.
     *
     * Если все условия выполняются то считам, что пуш можно удалить.
     */
    override fun getOuterCancelMatcher(cancelMessage: PushNotificationMessage): Predicate<PushData>? {
        if (cancelMessage.subType == CANCEL_SINGLE_SUBTYPE) {
            val readNotificationUuid = cancelMessage.data.optUUID("readedNotId")
            if (readNotificationUuid != null) {
                return object : Predicate<PushData> {
                    override fun apply(t: PushData): Boolean {
                        // если ид уведомлений совпадают и оригинальное уведомление было отправлено раньше, чем уведомление об отмене
                        return readNotificationUuid == t.message.notificationUuid && t.message.sendTime <= cancelMessage.sendTime
                    }
                }
            }
            return null
        } else if (cancelMessage.subType == CANCEL_ALL_SUBTYPE) {
            return object : Predicate<PushData> {
                override fun apply(t: PushData): Boolean {
                    // если оригинальное уведомление было отправлено раньше, чем уведомление об отмене
                    return t.message.sendTime <= cancelMessage.sendTime
                }
            }
        }
        return null
    }

    /**
     * Проверяет совпадение идентификаторов уведомления и документа.
     * Если какой-то из них совпадает то считаем, что пуш можно удалить.
     */
    override fun getInnerCancelMatcher(cancelParams: Bundle): Predicate<PushData>? {
        val notificationUuid = UUIDUtils.fromString(PushCancelContract.getNotificationUuid(cancelParams))
        val documentId = PushCancelContract.getDocumentId(cancelParams)
        return object : Predicate<PushData> {
            override fun apply(t: PushData): Boolean {
                return if (notificationUuid != null) { // приоритет проверке на ид уведомлений
                    t.message.notificationUuid == notificationUuid
                } else {
                    t.message.documentId == documentId
                }
            }
        }
    }
}