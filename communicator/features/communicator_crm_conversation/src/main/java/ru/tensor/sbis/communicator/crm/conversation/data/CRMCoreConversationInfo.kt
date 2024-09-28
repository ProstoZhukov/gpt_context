package ru.tensor.sbis.communicator.crm.conversation.data

import ru.tensor.sbis.communicator.base.conversation.data.BaseCoreConversationInfo
import ru.tensor.sbis.consultations.generated.ConsultationActions
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.consultations.generated.ConsultationChannel
import java.util.UUID

/**
 * Модель основной информации для инициализации чата CRM.
 * @see [BaseCoreConversationInfo]
 *
 * @property conversationUuid      идентификатор переписки.
 * @property messageUuid           идентификатор релевантного сообщения.
 * @property sourceId              идентификатор связанного источника консультаций, для возможности создавать консультации.
 * @property chatName              название чата.
 * @property photoUrl              URL фото чата.
 * @property isCompleted           true, если чат завершенный.
 * @property allowedMenuOptions    доступные действия с чатом через меню в тулбаре.
 * @property crmConsultationCase   тип чата-консультации.
 * @property operatorId            идентификатор оператора.
 * @property authorId              идентификатор консультируемого(автора консультации или адресата для консультаций созданных оператором).
 * @property consultationChannel   связанный канал.
 * @property isNewConsultationMode признак того что консультация должна открываться как новая(может быть новой или сразу выбранной в канале).
 *
 * @author da.zhukov
 */
internal data class CRMCoreConversationInfo(
    override var conversationUuid: UUID? = null,
    override val messageUuid: UUID? = null,
    override val isFullViewMode: Boolean = true,
    var sourceId: UUID? = null,
    val chatName: String? = null,
    val photoUrl: String? = null,
    var isCompleted: Boolean = false,
    var isMessagePanelVisible: Boolean = true,
    var allowedMenuOptions: ConsultationActions? = null,
    val crmConsultationCase: CRMConsultationCase = CRMConsultationCase.Unknown(),
    var operatorId: UUID? = null,
    var authorId: UUID? = null,
    var consultationChannel: ConsultationChannel? = null,
    val isNewConsultationMode: Boolean = false
) : BaseCoreConversationInfo {

    @Suppress("UNUSED_PARAMETER")
    override var isChat: Boolean
        get() = true
        set(value) {
            throw UnsupportedOperationException("CRM conversation is always chat")
        }
}
