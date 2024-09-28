package ru.tensor.sbis.communicator.common.data.theme

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.UUIDUtils.NIL_UUID
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communicator.common.CommunicatorCommonPlugin.communicatorCommonComponent
import ru.tensor.sbis.communicator.common.util.mapPersonDecorationToInitialsStubData
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.generated.ConversationViewData
import ru.tensor.sbis.communicator.generated.DocumentType
import ru.tensor.sbis.communicator.generated.RelevantMessageType
import ru.tensor.sbis.communicator.generated.SearchResultSpan
import ru.tensor.sbis.communicator.generated.ServiceType
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.persons.ConversationRegistryItem
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.profiles.generated.Gender
import ru.tensor.sbis.profiles.generated.PersonName
import ru.tensor.sbis.richtext.converter.RichTextConverter
import ru.tensor.sbis.richtext.converter.json.JsonRichTextConverter
import java.util.UUID
import ru.tensor.sbis.communication_decl.R as RCommunication
import ru.tensor.sbis.communicator.design.R as RCommunicator
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.profiles.generated.Person as ControllerPersonViewData

/**
 * Метод возвращает дефолтную UI модель диалога [ConversationModel]
 *
 * @return дефолтную модель диалога [ConversationModel]
 */
fun getNewStubItem() = ConversationModel(
    NIL_UUID,
    StringUtils.EMPTY,
    null,
    0,
    null,
    SyncStatus.OUT_OF_SYNC,
    ArrayList(0),
    0,
    ArrayList(0),
    NIL_UUID,
    RelevantMessageType.MESSAGE,
    null,
    "Stub",
    null,
    0,
    null,
    isOutgoing = false,
    isRead = false,
    isReadByMe = false,
    isForMe = false,
    serviceText = null,
    unreadCount = 0,
    formattedUnreadCount = "",
    documentUuid = null,
    documentType = null,
    documentUrl = null,
    documentIconText = null,
    externalEntityTitle = null,
    attachments = null,
    attachmentsRegistryModels = null,
    attachmentCount = 0,
    isChatForView = false,
    isChatForOperations = false,
    isPrivateChat = false,
    isUnreadCountImportant = false,
    isConversationHiddenOrArchived = false,
    canBeMarkedUnread = false,
    canBeMarkedRead = false,
    isSocnetEvent = false,
    socnetServiceObject = null,
    canBeDeleted = false,
    canBeUndeleted = false,
    searchHighlights = null,
    nameHighlights = null,
    docsHighlights = null,
    dialogNameHighlights = null,
    isPinned = false,
    meIsOwner = false,
    isInMyCompany = false,
    chatType = ChatType.UNKNOWN,
    canBeUnhide = false,
    isViewed = true,
    canSendMessage = true,
    isGroupConversation = true
)

/**
 * Маппер, преобразующий модель контроллера [ConversationViewData] в UI модель [ConversationModel]
 */
class ConversationMapper(
    context: SbisThemedContext,
    private val attachmentRegisterModelMapper: AttachmentRegisterModelMapper
) : BaseModelMapper<ConversationViewData, ConversationModel>(context) {

    @ColorInt
    private val relevantMessageColorUnimportant: Int = ContextCompat.getColor(context, RDesign.color.text_color_black_2)

    private val attachmentsPlaceholder = context.getString(RCommunicator.string.communicator_conversation_attachments_placeholder)

    private val richTextConverter: RichTextConverter = JsonRichTextConverter(context)

    private val complainService = communicatorCommonComponent.dependency.complainServiceProvider?.getComplainService()

    init {
        richTextConverter.setCustomTagHandler(SmileTagHandler.TAG, SmileTagHandler(context))
    }

    /** @SelfDocumented **/
    override fun apply(conversationFromController: ConversationViewData): ConversationModel =
        toModel(conversationFromController, forChats = false, isConversationHiddenOrArchived = false)

    /** @SelfDocumented **/
    fun applyToChat(conversationFromController: ConversationViewData): ConversationModel =
        toModel(conversationFromController, forChats = true, isConversationHiddenOrArchived = false)

    /** @SelfDocumented **/
    fun applyToList(
        conversationsFromController: List<ConversationViewData>,
        forChats: Boolean,
        isConversationHiddenOrArchived: Boolean
    ): List<ConversationRegistryItem> =
        conversationsFromController.map {
            toModel(it, forChats, isConversationHiddenOrArchived)
        }

    private fun toModel(conversationFromController: ConversationViewData,
                        forChats: Boolean,
                        isConversationHiddenOrArchived: Boolean): ConversationModel {
        val collagePersonsViewData = when {
            forChats -> {
                if (needCreatePersonsViewDataForChat(conversationFromController)) {
                    createPersonsViewDataForChat(conversationFromController, isPrivateChat(conversationFromController))
                } else {
                    conversationFromController.participantsCollage
                }
            }
            else     -> conversationFromController.participantsCollage
        }
        val participantsCollage = toPersonData(collagePersonsViewData)
        val dateForSort = conversationFromController.dateForSort
        val isUnreadColorImportant = calculateUnreadCountImportance(conversationFromController)

        val isAuthorBlocked = complainService?.isPersonBlocked(conversationFromController.messagePersonId) ?: false
        val isBlockedContent = isAuthorBlocked && !conversationFromController.isOutgoing

        val messageText: Spannable? = when {
            isBlockedContent ->
                SpannableString(mContext.getString(RCommunication.string.communication_decl_blocked_user_content_text)).also {
                it.setSpan(
                    ForegroundColorSpan(mContext.getThemeColorInt(RDesign.attr.readonlyTextColor)),
                    0,
                    it.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            conversationFromController.serviceType == ServiceType.AUDIO_MESSAGE ->
                SpannableString(mContext.getString(RCommunicator.string.communicator_audio_message_text_in_dialog_registry))
            conversationFromController.serviceType == ServiceType.VIDEO_MESSAGE ->
                SpannableString(mContext.getString(RCommunicator.string.communicator_video_message_text_in_dialog_registry))
            forChats -> calculateRelevantMessageWithSenderText(conversationFromController)
            conversationFromController.isSocnetEvent || conversationFromController.isNotice ->
                convertToRichText(conversationFromController.messageTextModel)
            else -> SpannableString(conversationFromController.messageText)
        }

        val titleText = if (forChats) conversationFromController.title else conversationFromController.messagePersonName
        val titlePostfix = if (conversationFromController.participantsCount > 1) "(+${conversationFromController.participantsCount - 1})" else null

        val documentIconText: String?
        val documentNameText: CharSequence?
        val documentHighlights: List<SearchSpan>?
        val hasDocumentTitle = !conversationFromController.documentTitle.isNullOrBlank()
        val showDocumentName = (conversationFromController.title.isEmpty() && hasDocumentTitle)
                || (!forChats && conversationFromController.isChat)
        val showDialogTitle = conversationFromController.title.isNotEmpty()
        when {
            showDocumentName -> {
                documentIconText = getDocumentIconText(conversationFromController)
                getDocumentNameAndHighlights(conversationFromController, forChats).let {
                    documentNameText = it.first
                    documentHighlights = it.second
                }
            }
            showDialogTitle  -> {
                documentIconText = if (hasDocumentTitle) getDocumentIconText(conversationFromController) else null
                documentNameText = null
                documentHighlights = conversationFromController.docsHighlights.toModel()
            }
            else             -> {
                documentIconText = null
                documentNameText = null
                documentHighlights = null
            }
        }

        val unreadIconType = with(conversationFromController) {
            when {
                isRead && isReadByMe -> null
                messageType == RelevantMessageType.SENDING -> ConversationUnreadIconType.SENDING
                messageType == RelevantMessageType.MESSAGE
                    && syncStatus == SyncStatus.ERROR -> ConversationUnreadIconType.ERROR
                messageType == RelevantMessageType.DRAFT -> ConversationUnreadIconType.DRAFT
                !isRead && isOutgoing -> ConversationUnreadIconType.UNREAD
                else -> null
            }
        }

        val isInMyCompany = (conversationFromController.isOutgoing || conversationFromController.isSocnetEvent)
                && !conversationFromController.messagePersonCompany.isNullOrBlank()

        val files: List<AttachmentViewModel> = when {
            isBlockedContent -> emptyList()
            // Для аудио и видео сообщений вложения не отображаются
            conversationFromController.serviceType == ServiceType.AUDIO_MESSAGE
                    || conversationFromController.serviceType == ServiceType.VIDEO_MESSAGE -> emptyList()
            else -> conversationFromController.files
        }

        val noticeData = if (conversationFromController.isNotice) {
            ThemeNoticeData(
                uuid = conversationFromController.uuid,
                toolbarTitle = titleText,
                photoUrl = participantsCollage.firstOrNull()?.photoUrl ?: StringUtils.EMPTY,
                noticeType = conversationFromController.noticeType ?: -1
            )
        } else null

        return with(conversationFromController) {
            ConversationModel(
                uuid,
                titleText,
                titlePostfix,
                dateForSort,
                dateForSort2,
                syncStatus,
                participantsCollage,
                participantsCount,
                generateParticipantsUuids(participantsCollage),
                messageUuid,
                messageType,
                unreadIconType,
                messagePersonName,
                messagePersonCompany,
                messageDateSent,
                messageText,
                isOutgoing,
                isRead,
                isReadByMe,
                isForMe,
                if (!isBlockedContent) configureServiceMessage(serviceType, serviceText) else null,
                unreadCount,
                formatUnreadCount(unreadCount),
                documentUuid,
                documentType,
                documentUrl?.let(UrlUtils::formatUrl),
                documentIconText,
                documentNameText,
                files,
                files.toViewModelList(),
                files.size,
                forChats,
                isChat,
                isPrivateChat(conversationFromController),
                isUnreadColorImportant,
                isConversationHiddenOrArchived,
                canBeMarkedUnread && !isNotice,
                canBeMarkedRead,
                isSocnetEvent,
                if (isSocnetEvent && !isBlockedContent) deserializeSocnetServiceObject(serviceObject) else null,
                canBeDeleted,
                canBeUndeleted,
                if (!isBlockedContent) textHighlights.toModel() else null,
                titleHighlights.toModel(),
                documentHighlights,
                conversationFromController.dialogNameHighlights.toModel(),
                isPinned,
                canBeUnhide,
                isViewed,
                meIsOwner,
                isGroupConversation = isGroupConversation,
                isInMyCompany = isInMyCompany,
                chatType = chatType,
                isChatUnreadCounterGray = isChat && notificationOptions.notificationsPrivateEvents && unreadForMeCount == 0,
                dialogTitle = if (!isChat && documentNameText.isNullOrEmpty()) title else null,
                folderUuid = folder,
                isAuthorBlocked = isAuthorBlocked,
                photoUrl = photoUrl.ifEmpty { null },
                conversationButtons = messageUuid?.let {
                    if (serviceObject.isNotEmpty()) createButtons(serviceObject, it) else null
                },
                canSendMessage = canSendMessageTo,
                noticeData = noticeData
        )}
    }

    private fun createButtons(serviceObject: String, messageUUID: UUID): List<ConversationButton>? {
        val buttonsIsEmpty = JSONObject(serviceObject).optString("buttons").isEmpty()
        if (buttonsIsEmpty) return null

        val jsonButtons = JSONObject(serviceObject).getJSONArray("buttons")
        val buttons = mutableListOf<ConversationButton>()

        (0 until jsonButtons.length()).forEach {
            val buttonServiceObject = jsonButtons.getJSONObject(it)
            val isActive = buttonServiceObject.optBoolean("active")
            val title = buttonServiceObject.optString("caption")
            val link = buttonServiceObject.optString("openLink")
            val correctLink = if (link == "null") null else link
            val isOutlinedMode = buttonServiceObject.optString("viewMode") == "outlined"
            val buttonId = buttonServiceObject.optString("name")
            buttons.add(
                ConversationButton(
                    messageUUID = messageUUID,
                    buttonId = buttonId,
                    title = title,
                    link = correctLink,
                    isActive = isActive,
                    isOutlineMode = isOutlinedMode
                )
            )
        }
        return buttons
    }

    private fun getDocumentNameAndHighlights(
        conversationFromController: ConversationViewData,
        forChats: Boolean
    ): Pair<String?, ArrayList<SearchSpan>?> =
        when {
            !forChats && conversationFromController.isChat ->
                conversationFromController.title to conversationFromController.docsHighlights.toModel()

            !forChats && !conversationFromController.isSocnetEvent -> {
                val docHighlights = conversationFromController.docsHighlights.toModel()
                if (DocumentType.SOCNET_GROUP == conversationFromController.documentType) {
                    val additionalText: String= mContext.resources.getString(RCommunicator.string.communicator_dialog_group) + ": "
                    val documentNameText = additionalText + conversationFromController.documentTitle
                    val highlightsWithOffset = getHighlightsWithOffset(docHighlights, additionalText.length)
                    documentNameText to highlightsWithOffset
                } else {
                    conversationFromController.documentTitle to docHighlights
                }
            }

            else -> null to null
        }

    private fun getDocumentIconText(conversationFromController: ConversationViewData): String? =
        when {
            conversationFromController.isSocnetEvent -> null
            isPrivateChat(conversationFromController) -> getString(RDesign.string.design_mobile_icon_menu_messages)
            conversationFromController.isChat -> getString(RDesign.string.design_mobile_icon_message_contour)
            DocumentType.DISK_FOLDER == conversationFromController.documentType -> getString(RDesign.string.design_mobile_icon_folder_solid)
            DocumentType.SOCNET_GROUP == conversationFromController.documentType -> getString(RDesign.string.design_mobile_icon_person_with_ties)
            else -> getString(RDesign.string.design_mobile_icon_document)
        }

    private fun isPrivateChat(conversationFromController: ConversationViewData) =
        conversationFromController.chatType == ChatType.PRIVATE

    private fun needCreatePersonsViewDataForChat(conversationFromController: ConversationViewData) =
        isPrivateChat(conversationFromController) || conversationFromController.photoUrl.isNotEmpty()

    private fun toPersonData(personViewDataList: List<ControllerPersonViewData>): List<PersonData> =
        personViewDataList.map { personViewData ->
            PersonData(
                personViewData.uuid.takeIf { it != NIL_UUID },
                personViewData.photoUrl,
                personViewData.photoDecoration.mapPersonDecorationToInitialsStubData()
            )
        }

    private fun createPersonsViewDataForChat(conversationData: ConversationViewData, isPrivate: Boolean): List<ControllerPersonViewData> {
        val participantsList = conversationData.participantsCollage
        val chatPhotoUrl = conversationData.photoUrl

        return if (isPrivate && participantsList.isNotEmpty()) {
            listOf(
                participantsList[0].run {
                    ControllerPersonViewData(
                        uuid,
                        localFace,
                        name,
                        photoUrl,
                        photoDecoration,
                        Gender.UNKNOWN,
                        false
                    )
                }
            )
        } else {
            listOf(
                ControllerPersonViewData(
                    NIL_UUID,
                    null,
                    PersonName(),
                    chatPhotoUrl,
                    null,
                    Gender.UNKNOWN,
                    false
                )
            )
        }
    }

    private fun calculateUnreadCountImportance(conversationFromController: ConversationViewData): Boolean {
        return when {
            conversationFromController.notificationOptions == ChatNotificationOptions(
                notificationsTurnedOff = false,
                notificationsPrivateEvents = true,
                notificationsAdminEvents = true
            )
                    || conversationFromController.unreadForMeCount > 0 -> true
            else -> false
        }
    }

    private fun generateParticipantsUuids(participantsCollage: List<PersonData>): List<UUID> {
        return participantsCollage.asSequence().map(PersonData::uuid).filterNotNull().toList()
    }

    private fun calculateRelevantMessageWithSenderText(conversation: ConversationViewData): Spannable? {
        val messageText = when {
            conversation.messageText.isNotEmpty() -> conversation.messageText
            else -> null
        }
        val hasText = !TextUtils.isEmpty(messageText)
        val hasServiceText = !TextUtils.isEmpty(conversation.serviceText)
        val hasAttachments = conversation.attachmentCount > 0

        val spannableString: Spannable? = when {
            hasText || hasAttachments -> {
                SpannableStringBuilder().also {
                    if (hasText) it.append(messageText)
                    if (hasAttachments) {
                        if (hasText) it.appendLine()
                        it.append(attachmentsPlaceholder)
                    }
                }
            }

            hasServiceText -> SpannableString(conversation.serviceText)

            else -> null
        }
        spannableString?.setSpan(
            ForegroundColorSpan(relevantMessageColorUnimportant),
            0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    private fun convertToRichText(text: String?): Spannable? = text?.let {
        richTextConverter.convert(text)
    }

    /** @SelfDocumented **/
    fun formatUnreadCount(unreadCount: Int): String =
        when {
            unreadCount <= 0 -> ""
            unreadCount <= 99 -> unreadCount.toString()
            else -> "99+"
        }

    private fun configureServiceMessage(
        serviceType: ServiceType,
        serviceText: String?
    ): Spannable? {
        val labelContrastTextColor = TextColor.LABEL_CONTRAST.getValue(mContext)
        return serviceText?.let {
            val primaryTextColor = StyleColor.PRIMARY.getTextColor(mContext)
            when (serviceType) {
                ServiceType.SIGNING_REQUEST -> configureServiceTypeText(primaryTextColor, it)
                ServiceType.SIGNED -> configureServiceTypeText(primaryTextColor, it)
                ServiceType.NOT_SIGNED -> configureServiceTypeText(primaryTextColor, it)
                ServiceType.DIALOG_INVITE -> configureServiceTypeText(labelContrastTextColor, it)
                ServiceType.MY_CIRCLES_INVITE,
                ServiceType.DOCUMENT_ACCESS -> configureServiceTypeText(
                    StyleColor.UNACCENTED.getTextColor(mContext),
                    it
                )
                ServiceType.FILE_ACCESS_REQUEST -> configureServiceTypeText(primaryTextColor, it)
                ServiceType.FILE_VIEW_ACCESS_GRANTED,
                ServiceType.FILE_CHANGE_ACCESS_GRANTED,
                ServiceType.FILE_CHANGE_PLUS_ACCESS_GRANTED -> configureServiceTypeText(
                    StyleColor.SUCCESS.getTextColor(mContext),
                    it
                )

                ServiceType.FILE_ACCESS_REQUEST_REJECTED -> configureServiceTypeText(
                    StyleColor.DANGER.getTextColor(mContext),
                    it
                )
                else -> configureServiceTypeText(labelContrastTextColor, it)
            }
        } ?: when (serviceType) {
            ServiceType.DIALOG_INVITE -> configureServiceTypeText(
                labelContrastTextColor,
                RCommunicator.string.communicator_dialog_invite
            )

            ServiceType.MY_CIRCLES_INVITE -> configureServiceTypeText(
                labelContrastTextColor,
                RCommunicator.string.communicator_circle_invite
            )
            else -> null
        }
    }

    private fun configureServiceTypeText(@ColorInt color: Int,
                                         serviceText: String): Spannable {
        return SpannableString(serviceText).also {
            it.setSpan(
                    ForegroundColorSpan(color),
                    0,
                    it.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun configureServiceTypeText(@ColorInt color: Int,
                                         @StringRes serviceText: Int): Spannable {
        return SpannableString(mContext.getString(serviceText)).also {
            it.setSpan(
                    ForegroundColorSpan(color),
                    0,
                    it.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun ArrayList<SearchResultSpan>.toModel() = mapTo(ArrayList()) { SearchSpan(it.start, it.end) }

    private fun deserializeSocnetServiceObject(serialized: String?): ConversationModel.SocnetServiceObject? {
        return serialized?.let {
            val jsonObject = JSONObject(it)
            val isInviteInGroup = isInviteInGroupEventType(jsonObject)
            if (jsonObject.has("comment_id")) {
                ConversationModel.SocnetServiceObject(
                    UUIDUtils.fromString(jsonObject.getString("comment_id")),
                    it,
                    isInviteInGroup
                )
            } else {
                ConversationModel.SocnetServiceObject(null, it, isInviteInGroup)
            }
        }
    }

    private fun getHighlightsWithOffset(highlights: List<SearchSpan>, offset: Int): ArrayList<SearchSpan> {
        val highlightsWithAdditionalText: ArrayList<SearchSpan> = arrayListOf()
        for (highlight: SearchSpan in highlights) {
            val start = highlight.start + offset
            val end = highlight.end + offset
            highlightsWithAdditionalText.add(SearchSpan(start, end))
        }
        return highlightsWithAdditionalText
    }

    private fun getString(@StringRes stringRes: Int): String =
        mContext.resources.getString(stringRes)

    private fun List<AttachmentViewModel>.toViewModelList(): List<AttachmentRegisterModel> =
        this.map { attachmentRegisterModelMapper.map(it.fileInfoViewModel) }

    private fun isInviteInGroupEventType(jsonObject: JSONObject): Boolean {
        val eventType = if (jsonObject.has("event_type")) {
            jsonObject.getString("event_type")
        } else null
        return eventType?.equals(SOCNET_INVITE_IN_GROUP_EVENT) == true || eventType?.equals(SOCNET_GROUP_INVITE_IN_GROUP_EVENT) == true
    }
}

/** Код события приглашения в группу. */
private const val SOCNET_INVITE_IN_GROUP_EVENT = "20"
/** Код события приглашения компании. */
private const val SOCNET_GROUP_INVITE_IN_GROUP_EVENT = "21"