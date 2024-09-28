package ru.tensor.sbis.communicator.crm.conversation.presentation.ui

import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.generated.CrmConsultationIconType
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.consultations.generated.ChannelIconType
import ru.tensor.sbis.consultations.generated.SourceViewModel
import java.util.UUID

internal class CRMMessageCollectionFilter : MessageCollectionFilter() {
    private var isOperatorCase: Boolean = false
    private var source: SourceViewModel? = null

    override fun getMessageFilter(fromUuid: UUID?, pageSize: Int, requestId: String?): MessageFilter {
        return super.getMessageFilter(fromUuid, pageSize, requestId).apply {
            startCrmConsultationText = source?.name ?: if (isOperatorCase) "" else null
            crmConsultationIconType = source?.icon?.toCrmConsultationIconType() ?: CrmConsultationIconType.UNKNOWN
        }
    }

    fun setIsOperatorCase(isOperatorCase: Boolean) {
        this.isOperatorCase = isOperatorCase
    }

    fun setSource(source: SourceViewModel?) {
        this.source = source
    }

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