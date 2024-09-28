package ru.tensor.sbis.pushnotification.repository.model

import org.json.JSONObject
import ru.tensor.sbis.push.generated.PushNotifyMeta
import ru.tensor.sbis.pushnotification.PushType
import java.util.*

/**
 * Модель пуш-сообщения, поступившего на устройство для обработки.
 * Модель кешируется в локальном хранилище, может быть получена посредством
 * [ru.tensor.sbis.pushnotification.repository.PushNotificationRepository]
 *
 * @property notificationUuid уникальный идентификатор уведомления
 * @property type тип пуш-уведомления
 * @property subType подтип пуш-уведомления, может быть заполнен прикладными разработчиками облака
 * @property sendTime дата отправки уведомления
 * @property documentTime дата создания документа, по которому инициировано уведомление
 * @property addresseeId идентификатор пользователя, которому адресуется данный пуш.
 * Должен быть текущий авторизованный пользователь, в противном случае модель пуш-сообщения создана не будет
 * @property documentId идентификатор документа, по которому инициировано уведомление
 * @property sendDocumentId идентификатор отправки документа
 * @property title заголовок пуш-уведомления по умолчанию, можно переопределить при публикации
 * @property message текст пуш-уведомления по умолчанию, можно переопределить при публикации
 * @property alert короткое сообщение, которое может быть отражено во всплывающем уведомлении поверх других приложений
 * @property cloudAction предполагаемое действие с пуш-уведомлением
 * @property data json с прикладными данными по пуш-уведомлению
 * @property extraData json с дополнительными прикладными данными по пуш-уведомлению
 *
 * @author am.boldinov
 */
class PushNotificationMessage(
    val notificationUuid: UUID,
    val type: PushType,
    val subType: Int,
    val sendTime: Long,
    val documentTime: Long,
    val addresseeId: Long,
    val documentId: String?,
    val sendDocumentId: Int?,
    val title: String,
    val message: String,
    val alert: String,
    val cloudAction: PushCloudAction,
    val data: JSONObject,
    val extraData: JSONObject,
    internal var notifyMeta: PushNotifyMeta?,
    internal val rawType: Int,
    internal val rawData: String,
    internal val rawExtraData: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PushNotificationMessage

        if (notificationUuid != other.notificationUuid) return false
        if (type != other.type) return false
        if (subType != other.subType) return false
        if (sendTime != other.sendTime) return false
        if (documentTime != other.documentTime) return false
        if (addresseeId != other.addresseeId) return false
        if (documentId != other.documentId) return false
        if (sendDocumentId != other.sendDocumentId) return false
        if (title != other.title) return false
        if (message != other.message) return false
        if (alert != other.alert) return false
        if (cloudAction != other.cloudAction) return false
        if (notifyMeta != other.notifyMeta) return false
        if (rawType != other.rawType) return false
        if (rawData != other.rawData) return false
        if (rawExtraData != other.rawExtraData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = notificationUuid.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + subType
        result = 31 * result + sendTime.hashCode()
        result = 31 * result + documentTime.hashCode()
        result = 31 * result + addresseeId.hashCode()
        result = 31 * result + (documentId?.hashCode() ?: 0)
        result = 31 * result + (sendDocumentId ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + alert.hashCode()
        result = 31 * result + cloudAction.hashCode()
        result = 31 * result + (notifyMeta?.hashCode() ?: 0)
        result = 31 * result + rawType
        result = 31 * result + rawData.hashCode()
        result = 31 * result + rawExtraData.hashCode()
        return result
    }
}