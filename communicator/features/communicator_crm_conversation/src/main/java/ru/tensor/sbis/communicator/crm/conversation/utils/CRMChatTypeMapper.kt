package ru.tensor.sbis.communicator.crm.conversation.utils

import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CrmChannelType
import ru.tensor.sbis.communicator.generated.ConsultationChatType
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyItemType

/**
 * Маппер из модели [CRMConsultationCase] в [ConsultationChatType].
 *
 * @author dv.baranov
 */
internal fun CRMConsultationCase.toConsultationChatType() = when (this) {
    is CRMConsultationCase.Client -> ConsultationChatType.CLIENT
    is CRMConsultationCase.Operator -> ConsultationChatType.OPERATOR
    is CRMConsultationCase.SalePoint -> ConsultationChatType.SALES_POINT
    is CRMConsultationCase.Unknown -> ConsultationChatType.UNKNOWN
}

/**
 * Маппер из модели [CrmChannelType] в [ChannelHeirarchyItemType].
 *
 * @author da.zhukov
 */
internal fun CrmChannelType.toChannelHeirarchyItemType() = when (this) {
    CrmChannelType.CHANNEL_FOLDER -> ChannelHeirarchyItemType.CHANNEL_FOLDER
    CrmChannelType.CHANNEL -> ChannelHeirarchyItemType.CHANNEL
    CrmChannelType.OPEN_LINE -> ChannelHeirarchyItemType.OPEN_LINE
    CrmChannelType.CONTACT -> ChannelHeirarchyItemType.CONTACT
    CrmChannelType.CHANNEL_FOLDER_GROUP -> ChannelHeirarchyItemType.CHANNEL_FOLDER_GROUP
    CrmChannelType.CHANNEL_GROUP_TYPE -> ChannelHeirarchyItemType.CHANNEL_GROUP_TYPE
}
