package ru.tensor.sbis.communicator.crm.conversation.data.mapper

import android.content.Context
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase.SalePoint
import ru.tensor.sbis.communicator.base.conversation.data.model.ConversationAccess
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationFeatureFacade.crmMessageMapperHelper
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.data.CRMConversationData
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communicator.generated.Conversation
import ru.tensor.sbis.consultations.generated.ConsultationActionsFlags
import ru.tensor.sbis.consultations.generated.ConsultationConversationData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import java.util.*
import javax.inject.Inject

/**
 * Маппер модели данных о чате из моделей контроллера [Conversation] и [ConsultationConversationData] в UI [CRMConversationData].
 *
 * @author da.zhukov
 */
internal class CRMConversationDataMapper @Inject constructor(
    context: Context,
    val coreConversationInfo: CRMCoreConversationInfo
) {

    private val isBrand: Boolean = coreConversationInfo.crmConsultationCase.castTo<SalePoint>()?.isBrand == true
    private val brandTitle = PlatformSbisString.Res(R.string.communicator_crm_brand_title).getCharSequence(context)

    private val ConsultationConversationData.toolbarData: ToolbarData
        get() {
            val toolbarTitle = if (isBrand && coreConversationInfo.isNewConsultationMode) brandTitle else title
            val showOnlyTitle = if (isBrand) true else subTitle.isNullOrBlank()
            val consultationPhoto: String?
            val personData: PersonData?

            when {
                icon.isPersonPhotoData() -> {
                    consultationPhoto = StringUtils.EMPTY

                    val photoUrl = icon.fieldPersonPhotoData?.photoUrl
                    val initials = icon.fieldPersonPhotoData?.initials
                    val backgroundColorHex = icon.fieldPersonPhotoData?.backgroundColorHex
                    personData = PersonData(
                        photoUrl = photoUrl,
                        initialsStubData = initials?.let {
                            backgroundColorHex?.let { colorHex ->
                                InitialsStubData(
                                    initials = it,
                                    colorHex = colorHex
                                )
                            }
                        }
                    )
                }
                icon.isChannelIconData() -> {
                    val needShowChannelPhoto = !icon.fieldChannelIconData?.url.isNullOrBlank()
                            && icon.fieldChannelIconData?.url?.contains("null") != true

                    if (needShowChannelPhoto) {
                        personData = PersonData(
                            photoUrl = icon.fieldChannelIconData?.url,
                            initialsStubData = null
                        )
                        consultationPhoto = null
                    } else {
                        personData = PersonData()
                        consultationPhoto = icon.fieldChannelIconData?.iconName
                    }
                }
                else -> {
                    personData = PersonData()
                    consultationPhoto = null
                }
            }

            return ToolbarData(
                photoDataList = listOf(personData),
                title = toolbarTitle,
                subtitle = subTitle ?: "",
                showOnlyTitle = showOnlyTitle,
                consultationPhoto = consultationPhoto,
                isChat = true
            )
        }

    fun map(
        consultation: ConsultationConversationData?,
        conversation: Conversation?,
        nextConsultationUUID: UUID? = null
    ): CRMConversationData {
        val currentUserId = UUIDUtils.validateUuid(crmConversationDependency?.getCurrentAccount()?.personId)
        crmMessageMapperHelper.isGroupConsultation = (conversation?.participantCount ?: 0) > 2
                || conversation?.participants?.find { it.uuid == currentUserId } == null

        val isCompletedConsultation = consultation?.allowedActions?.contains(ConsultationActionsFlags.CAN_SEND_MESSAGE) == false
                && consultation.allowedActions?.contains(ConsultationActionsFlags.CAN_TAKE) == false

        return CRMConversationData(
            conversationAccess = ConversationAccess(conversation?.chatPermissions),
            unreadCount = conversation?.unreadMessagesCount ?: 0,
            toolbarData = consultation?.toolbarData,
            unreadChatsMessagesCounter = consultation?.unreadMsgCounter ?: 0,
            isCompletedChat = isCompletedConsultation,
            createConsultationButton = consultation?.createConsultationButton,
            sourceId = consultation?.sourceId ?: consultation?.salesPointId,
            conversationUUID = consultation?.id,
            allowedActions = consultation?.allowedActions,
            nextConsultationUUID = nextConsultationUUID,
            isHistory = consultation?.isHistory ?: false,
            isNew = consultation?.isNew ?: false,
            isHideMenu = consultation?.isHideMenu ?: false,
            authorId = consultation?.authorId,
            source = consultation?.source,
            isDraft = consultation?.isDraft ?: false,
            operatorId = consultation?.operatorId,
            channel = consultation?.channel,
            isGroupConversation = crmMessageMapperHelper.isGroupConsultation
        )
    }
}