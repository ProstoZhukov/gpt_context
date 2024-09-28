package ru.tensor.sbis.communicator.crm.conversation.data.model

import android.text.Spannable
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseServiceMessage
import ru.tensor.sbis.communicator.generated.CrmConsultationIconType
import ru.tensor.sbis.communicator.generated.ServiceMessageGroup
import ru.tensor.sbis.communicator.generated.ServiceType
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import java.util.UUID

/**
 * Дата-класс модели сервисного сообщения в чатах техпоодержки.
 * @property uuid                идентификатор сообщения.
 * @property timestampSent       время отправки сообщения.
 * @property forMe               true, если входящее.
 * @property outgoing            true, если исходящее.
 * @property text                текст сообщения.
 * @property icon                иконка, которую нужно отобразить.
 * @property serviceMessageGroup группа сервисных сообщений.
 * @property serviceType         тип сервисного сообщения.
 * @property formattedDateTime   модель даты и времени для отображения.
 *
 * @author da.zhukov
 */
data class CRMServiceMessage(
    val uuid: UUID,
    val timestampSent: Long,
    val forMe: Boolean,
    val outgoing: Boolean,
    override var read: Boolean,
    val text: Spannable,
    val icon: CrmConsultationIconType?,
    override val serviceMessageGroup: ServiceMessageGroup? = null,
    val serviceType: ServiceType? = null
) : BaseServiceMessage {

    var formattedDateTime: FormattedDateTime? = null
    override val expandServiceGroupAction: () -> Unit = {}
}