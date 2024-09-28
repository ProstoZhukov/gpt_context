package ru.tensor.sbis.design.message_view.content.service_views

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import org.json.JSONObject
import ru.tensor.sbis.common.util.getCompatFont
import ru.tensor.sbis.communicator.generated.CrmConsultationIconType
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageContentItem
import ru.tensor.sbis.communicator.generated.MessageContentItemType
import ru.tensor.sbis.communicator.generated.ServiceMessage
import ru.tensor.sbis.communicator.generated.ServiceType
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.message_view.R
import ru.tensor.sbis.design.message_view.model.ServiceViewData
import ru.tensor.sbis.design.message_view.utils.rich_text_converter.MessageRichTextConverter
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan
import ru.tensor.sbis.design.text_span.span.SbisSpannableString
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import java.util.Objects
import ru.tensor.sbis.design.R as RDesign

/**
 * Вспомогательные методы для получения данных о содержимом сервисных сообщений.
 *
 * @author dv.baranov
 */

/** Извлечь [MessageContentItem] из модели [Message]. */
fun findServiceMessageContentItem(message: Message, converter: MessageRichTextConverter): MessageContentItem? {
    message.rootElements.forEach { index ->
        val item = message.content[index]
        // Скорее всего временный костыль для отображения приветственного сообщения.
        // выпилить после TODO:https://online.sbis.ru/opendoc.html?guid=6285b67e-efef-47f4-8953-e2f2428762d3&client=3
        when {
            item.itemType == MessageContentItemType.SERVICE &&
                item.serviceMessage?.text.isNullOrEmpty() &&
                item.serviceType == ServiceType.CONSULTATION_STARTED &&
                message.textModel.isNotEmpty() -> {
                val serviceText: String = converter.convert(message.textModel).toString()
                return item.apply {
                    serviceMessage?.text = serviceText
                }
            }
            item.serviceType == ServiceType.CRM_CONSULTATION_START && !item.serviceMessage?.text.isNullOrEmpty() -> {
                return item.apply { text = item.serviceMessage?.text ?: "" }
            }
            item.itemType == MessageContentItemType.SERVICE ||
                item.itemType == MessageContentItemType.SERVICE_MESSAGE_GROUP -> {
                return item.apply { serviceMessageGroup?.folded = message.serviceMessageGroup?.folded == true }
            }
            item.serviceType == ServiceType.CONSULTATION_RATE_REQUEST -> return item
        }
    }
    return null
}

/** Проверить является ли сообщение сервисным. */
fun Message.isService(): Boolean {
    val serviceItem = content.find {
        it.itemType == MessageContentItemType.SERVICE ||
            it.itemType == MessageContentItemType.SERVICE_MESSAGE_GROUP ||
            it.serviceType == ServiceType.CONSULTATION_RATE_REQUEST
    } ?: return false
    return !showAsMessageServiceTypes.contains(serviceItem.serviceType)
}

private val showAsMessageServiceTypes = setOf(
    ServiceType.NOT_SERVICE,
    ServiceType.DIALOG_INVITE,
    ServiceType.NOT_SIGNED,
    ServiceType.SIGNING_REQUEST,
    ServiceType.SIGNED,
    ServiceType.DOCUMENT_ACCESS,
    ServiceType.MY_CIRCLES_INVITE,
    ServiceType.OTHER_SERVICE_MESSAGE,
    ServiceType.FILE_ACCESS_REQUEST,
    ServiceType.FILE_ACCESS_REQUEST_REJECTED,
    ServiceType.FILE_CHANGE_ACCESS_GRANTED,
    ServiceType.FILE_VIEW_ACCESS_GRANTED,
    ServiceType.FILE_CHANGE_PLUS_ACCESS_GRANTED,
    ServiceType.TASK_LINKED,
    ServiceType.TASK_APPENDED,
    ServiceType.AUDIO_MESSAGE,
    ServiceType.VIDEO_MESSAGE,
    ServiceType.THREAD_STARTED,
    ServiceType.CONSULATION_RATE,
    ServiceType.CHATBOT_MESSAGE
)

/** Получить текст сервисного сообщения. */
fun getServiceMessageText(
    item: MessageContentItem,
    context: Context
): Pair<SpannableString, ChatServiceMessageClickableSpan?> {
    val stringBuilder = SpannableStringBuilder()
    val mChatServiceMorePeopleText = context.getString(R.string.design_message_view_chat_members_more)
    val mChatRestoreText = context.getString(R.string.design_message_view_chat_restore)
    val mChatLockIcon = context.getString(RDesign.string.design_mobile_icon_lock)
    var span: ChatServiceMessageClickableSpan? = null

    if (MessageContentItemType.SERVICE_MESSAGE_GROUP == item.itemType) {
        val serviceMessageGroup = item.serviceMessageGroup!!
        stringBuilder.append(serviceMessageGroup.text)
        span = ChatServiceMessageClickableSpan(false)
        stringBuilder.setSpan(span, 0, stringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    } else {
        val serviceMessage = item.serviceMessage
        when (Objects.requireNonNull<ServiceMessage?>(serviceMessage).type) {
            ServiceType.CHAT_CLOSED -> {
                val active = serviceMessage!!.activeChatClosedMessage
                if (active) {
                    stringBuilder.append(mChatLockIcon)
                    stringBuilder.setSpan(
                        CustomTypefaceSpan(TypefaceManager.getSbisMobileIconTypeface(context)),
                        0,
                        1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    stringBuilder.append(" ")
                }
                stringBuilder.append(serviceMessage.text)
                if (active) {
                    stringBuilder.append(" ")
                    val start = stringBuilder.length
                    stringBuilder.append(mChatRestoreText)
                    val end = stringBuilder.length
                    span = ChatServiceMessageClickableSpan(true)
                    stringBuilder.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            ServiceType.ADDED_CHAT_ADMINS,
            ServiceType.REMOVED_CHAT_ADMINS,
            ServiceType.ADDED_CHAT_PARTICIPANTS,
            ServiceType.REMOVED_CHAT_PARTICIPANTS -> {
                val personList = serviceMessage!!.personList
                if (personList != null) {
                    stringBuilder.append(personList.brief)
                    if (personList.foldedCount > 0) {
                        stringBuilder.append(mChatServiceMorePeopleText).append(personList.foldedCount.toString())
                    }
                } else {
                    stringBuilder.append(serviceMessage.text)
                }
                span = ChatServiceMessageClickableSpan(false)
                stringBuilder.setSpan(span, 0, stringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            else -> {
                stringBuilder.append(serviceMessage!!.text)
                span = ChatServiceMessageClickableSpan(false)
                stringBuilder.setSpan(span, 0, stringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
    return Pair<SpannableString, ChatServiceMessageClickableSpan?>(SbisSpannableString(stringBuilder), span)
}

/**
 * Определить в модели сообщения наличие сервисного сообщения, которое так же содержит дополнительный
 * контент для отображения.
 *
 * @param message - модель сообщения
 */
internal fun isMessageContainsServiceMessageWithContent(message: Message?, serviceObject: JSONObject?): Boolean {
    if (message == null) return false
    if (serviceObject != null) {
        // Комментарий по совещанию (смогу, не смогу и т.д.) показываем как обычное сообщение
        val isAnswer = serviceObject.optString("serviceType") == "meeting-invite-answer"
        if (isAnswer) return false
    }
    for (item in message.content) {
        if (item.itemType == MessageContentItemType.SERVICE &&
            (
                item.serviceType == ServiceType.OTHER_SERVICE_MESSAGE ||
                    item.serviceType == ServiceType.TASK_LINKED ||
                    item.serviceType == ServiceType.TASK_APPENDED
                )
        ) { return true }
    }
    return false
}

/** Установить внутренние отступы для сервисного сообщения. */
internal fun setServiceContainerPadding(view: View, isGroupConversation: Boolean) {
    val minServiceContainerPaddingStart: Int = view.context.resources.getDimensionPixelOffset(
        R.dimen.design_message_view_dual_message_item_incoming_body_margin_left_min
    )
    val serviceContainerPaddingStart: Int = view.context.resources.getDimensionPixelOffset(
        R.dimen.design_message_view_dual_message_item_incoming_body_margin_left
    )
    val serviceContainerPaddingEnd: Int = view.context.resources.getDimensionPixelOffset(
        R.dimen.design_message_view_message_item_group_padding_right
    )
    if (isGroupConversation) {
        view.setPadding(serviceContainerPaddingStart, 0, serviceContainerPaddingEnd, 0)
    } else {
        view.setPadding(minServiceContainerPaddingStart, 0, serviceContainerPaddingEnd, 0)
    }
}

/** Получить текст сервисного сообщения для чата-консультации. */
internal fun getCrmServiceMessageText(
    context: Context,
    serviceData: ServiceViewData
): CharSequence {
    val text = serviceData.text
    val icon = serviceData.icon?.getIcon(text, serviceData.serviceMessage?.type)
    return if (icon != null && serviceData.isOperator && text.isNotEmpty()) {
        getServiceTextWithIcon(text, icon, context)
    } else {
        text
    }
}

private fun CrmConsultationIconType.getIcon(text: Spannable, serviceType: ServiceType?): String? = when (this) {
    CrmConsultationIconType.UNKNOWN -> serviceType?.tryGetIconForMessage(text.contains(REASSIGN_REGEX))
    CrmConsultationIconType.MOBILE_APP -> SbisMobileIcon.Icon.smi_PhoneCell1.character.toString()
    CrmConsultationIconType.SITE -> SbisMobileIcon.Icon.smi_WWW.character.toString()
    CrmConsultationIconType.SABY -> SbisMobileIcon.Icon.smi_sbisbird.character.toString()
    CrmConsultationIconType.SABY_PINK -> PINK_COLOR_PREFIX + SbisMobileIcon.Icon.smi_sbisbird.character.toString()
    CrmConsultationIconType.VK -> SbisMobileIcon.Icon.smi_vkontakte.character.toString()
    CrmConsultationIconType.TELEGRAM -> SbisMobileIcon.Icon.smi_Telegram.character.toString()
    CrmConsultationIconType.EMAIL -> SbisMobileIcon.Icon.smi_Email.character.toString()
    CrmConsultationIconType.VIBER -> SbisMobileIcon.Icon.smi_Viber.character.toString()
    CrmConsultationIconType.OK -> SbisMobileIcon.Icon.smi_Yes.character.toString()
    CrmConsultationIconType.WHATSAPP -> SbisMobileIcon.Icon.smi_Whatsapp.character.toString()
    CrmConsultationIconType.FACEBOOK -> SbisMobileIcon.Icon.smi_facebook.character.toString()
    CrmConsultationIconType.YANDEX -> SbisMobileIcon.Icon.smi_Yandex.character.toString()
    CrmConsultationIconType.INSTAGRAM -> SbisMobileIcon.Icon.smi_Instagram.character.toString()
    CrmConsultationIconType.AVITO -> SbisMobileIcon.Icon.smi_Avito.character.toString()
    else -> null
}

private fun ServiceType.tryGetIconForMessage(isContainReassignText: Boolean): String? = when {
    this == ServiceType.CRM_CONSULTATION_START || this == ServiceType.OTHER_SERVICE_MESSAGE ||
        this == ServiceType.CONSULTATION_ADD_OPERATOR || isContainReassignText ->
        SbisMobileIcon.Icon.smi_ArrowReturn.character.toString()
    this == ServiceType.CONSULTATION_CLOSED ->
        SbisMobileIcon.Icon.smi_messageContour.character.toString()
    else -> null
}

private fun getServiceTextWithIcon(text: Spannable, icon: String, context: Context): CharSequence {
    val isPinkSaby = icon.startsWith(PINK_COLOR_PREFIX)
    val spannableIcon = SpannableString(icon.removePrefix(PINK_COLOR_PREFIX))
    spannableIcon.setIconSpan(
        CustomTypefaceSpan(
            context.getCompatFont(RDesign.font.sbis_mobile_icons)
        )
    )
    if (isPinkSaby) {
        spannableIcon.setIconSpan(
            ForegroundColorSpan(
                context.getColorFrom(RDesign.color.palette_color_red2)
            )
        )
    }
    return TextUtils.concat(text, " ", spannableIcon)
}

private fun Spannable.setIconSpan(span: Any) {
    this.setSpan(
        span,
        0,
        this.length,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
}

private const val PINK_COLOR_PREFIX = "pink_"
private const val REASSIGN_REGEX = "переназнач"
