package ru.tensor.sbis.communicator.crm.conversation.data.model.crud

import ru.tensor.sbis.communicator.base.conversation.data.model.crud.BaseConversationFilter
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communicator.generated.CrmConsultationIconType
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.consultations.generated.ChannelIconType
import ru.tensor.sbis.consultations.generated.SourceViewModel
import java.util.ArrayList
import java.util.UUID
import javax.inject.Inject

/**
 * Фильтр для запросов списка на экране чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationFilter @Inject constructor() : BaseConversationFilter() {

    var source: SourceViewModel? = null

    @Inject
    internal lateinit var coreConversationInfo: CRMCoreConversationInfo

    override fun queryBuilder(): Builder<CRMConversationMessage, MessageFilter> =
        CRMConversationFilterBuilder(
            themeUuid,
            relevantMessageUuid,
            source,
            coreConversationInfo.crmConsultationCase is CRMConsultationCase.Operator
        )

    /**
     * Билдер фильтра
     * @param themeUuid           идентификатор чата
     * @param relevantMessageUuid идентификатор релевантного сообщения
     */
    class CRMConversationFilterBuilder internal constructor(
        themeUuid: UUID,
        relevantMessageUuid: UUID?,
        private val source: SourceViewModel?,
        private val isOperatorChatType: Boolean
    ) : BaseConversationFilterBuilder<CRMConversationMessage>(themeUuid, relevantMessageUuid) {

        override fun build(): MessageFilter =
            MessageFilter(
                themeId = themeUuid,
                direction = queryDirection!!,
                fromUuid = fromUUID,
                count = mItemsCount,
                includeAnchor = inclusive,
                requestId = requestId ?: "",
                unfoldedGroups = ArrayList(),
                groupServiceMessages = true,
                parentId = null,
                startCrmConsultationText = source?.name ?: if (isOperatorChatType) "" else null,
                crmConsultationIconType = source?.icon?.toCrmConsultationIconType() ?: CrmConsultationIconType.UNKNOWN,
                reversed = true,
                isGroupConversation = false,
                isBothway = false
            )

        private fun ChannelIconType.toCrmConsultationIconType(): CrmConsultationIconType {
            return when (this) {
                ChannelIconType.SITE -> CrmConsultationIconType.SITE
                ChannelIconType.SABY -> CrmConsultationIconType.SABY
                ChannelIconType.VK -> CrmConsultationIconType.VK
                ChannelIconType.TELEGRAM -> CrmConsultationIconType.TELEGRAM
                ChannelIconType.EMAIL -> CrmConsultationIconType.EMAIL
                ChannelIconType.VIBER -> CrmConsultationIconType.VIBER
                ChannelIconType.OK -> CrmConsultationIconType.OK
                ChannelIconType.WHATSAPP -> CrmConsultationIconType.WHATSAPP
                ChannelIconType.FACEBOOK -> CrmConsultationIconType.FACEBOOK
                ChannelIconType.YANDEX -> CrmConsultationIconType.YANDEX
                ChannelIconType.INSTAGRAM -> CrmConsultationIconType.INSTAGRAM
                ChannelIconType.AVITO -> CrmConsultationIconType.AVITO
                ChannelIconType.MOBILE_APP -> CrmConsultationIconType.MOBILE_APP
                ChannelIconType.SABYGET -> CrmConsultationIconType.SABY_PINK
                ChannelIconType.CHAT_WIDGET -> CrmConsultationIconType.UNKNOWN
                ChannelIconType.UNKNOWN -> CrmConsultationIconType.UNKNOWN
            }
        }
    }
}