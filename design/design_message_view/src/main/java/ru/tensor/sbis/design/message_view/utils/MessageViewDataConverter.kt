package ru.tensor.sbis.design.message_view.utils

import android.content.Context
import android.text.Spannable
import android.view.View
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communicator.generated.MessageContentItem
import ru.tensor.sbis.communicator.generated.MessageContentItemType
import ru.tensor.sbis.communicator.generated.ServiceType
import ru.tensor.sbis.design.cloud_view.content.attachments.AttachmentClickListener
import ru.tensor.sbis.design.cloud_view.content.attachments.model.MessageAttachment
import ru.tensor.sbis.design.cloud_view.content.certificate.DefaultSignature
import ru.tensor.sbis.design.cloud_view.content.grant_access.GrantAccessActionListener
import ru.tensor.sbis.design.cloud_view.content.quote.DefaultQuote
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickListener
import ru.tensor.sbis.design.cloud_view.content.signing.SigningActionListener
import ru.tensor.sbis.design.cloud_view.content.utils.BaseMessageResourceHolder
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool
import ru.tensor.sbis.design.cloud_view.model.AttachmentCloudContent
import ru.tensor.sbis.design.cloud_view.model.AudioMessageCloudContent
import ru.tensor.sbis.design.cloud_view.model.ContainerCloudContent
import ru.tensor.sbis.design.cloud_view.model.DefaultCloudViewData
import ru.tensor.sbis.design.cloud_view.model.EmptyCloudContent
import ru.tensor.sbis.design.cloud_view.model.GrantAccessButtonsCloudContent
import ru.tensor.sbis.design.cloud_view.model.LinkCloudContent
import ru.tensor.sbis.design.cloud_view.model.QuoteCloudContent
import ru.tensor.sbis.design.cloud_view.model.ServiceCloudContent
import ru.tensor.sbis.design.cloud_view.model.SignatureCloudContent
import ru.tensor.sbis.design.cloud_view.model.SigningButtonsCloudContent
import ru.tensor.sbis.design.cloud_view.model.TaskLinkedServiceCloudContent
import ru.tensor.sbis.design.cloud_view.video.model.DefaultVideoMessageViewData
import ru.tensor.sbis.design.cloud_view.video.model.VideoMessageCloudViewData
import ru.tensor.sbis.design.cloud_view.video.model.VideoMessageMediaContent
import ru.tensor.sbis.design.cloud_view.video.model.VideoMessageQuoteContent
import ru.tensor.sbis.design.message_view.MessageViewPlugin.complainServiceFeatureProvider
import ru.tensor.sbis.design.message_view.MessageViewPlugin.richTextConverter
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.AttachmentEvent
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.ButtonsEvent
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.MediaEvent
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.QuoteEvent
import ru.tensor.sbis.design.message_view.model.CloudViewData
import ru.tensor.sbis.design.message_view.model.MessageCloudViewData
import ru.tensor.sbis.design.message_view.model.MessageType
import ru.tensor.sbis.design.message_view.model.VideoCloudViewData
import java.util.UUID
import ru.tensor.sbis.design.cloud_view.model.CloudViewData as CloudComponentViewData

/**
 * Конвертер модели данных MessageView в модели данных для отображения ячеек сообщений, видеосообщений.
 *
 * @author dv.baranov
 */
internal class MessageViewDataConverter(
    private val context: Context,
    private val viewPool: MessagesViewPool,
    private val complainService: ComplainService? = complainServiceFeatureProvider?.get()?.getComplainService()
) {

    /**
     * Преобразовать модель данных для отображения MessageView в модель для отображения CloudView.
     */
    fun toCloudComponentViewData(
        data: CloudViewData,
        listener: MessageViewListener,
        isDownscaledImages: Boolean = false
    ): CloudComponentViewData {
        return DefaultCloudViewData(
            getMessageText(data),
            data.messageContent.content.mapIndexedNotNull { _, item ->
                when (item.itemType) {
                    MessageContentItemType.QUOTE ->
                        QuoteCloudContent(
                            quote = DefaultQuote(
                                enclosingMessageUuid = data.uuid,
                                messageUuid = item.quote?.messageUuid ?: UUID.randomUUID()
                            ),
                            listener = getQuoteClickListener(listener)
                        )

                    MessageContentItemType.ATTACHMENT -> {
                        val audioData = if (data is MessageCloudViewData) data.audioViewData else null
                        if (audioData != null) {
                            AudioMessageCloudContent(
                                data = audioData,
                                actionListener = getMediaActionListener(listener)
                            )
                        } else {
                            val attachmentClickListener =
                                if (listener.check(AttachmentEvent.OnAttachmentClicked::class)) {
                                    object : AttachmentClickListener {
                                        override fun onAttachmentClicked(
                                            context: Context,
                                            attachment: MessageAttachment,
                                            attachments: List<MessageAttachment>
                                        ) {
                                            listener.onEvent(AttachmentEvent.OnAttachmentClicked(item.attachment!!))
                                        }
                                    }
                                } else {
                                    null
                                }

                            AttachmentCloudContent(
                                attachment = item.attachment!!.fileInfoViewModel.asMessageAttachment(context),
                                isDownscaledImages = isDownscaledImages,
                                listener = attachmentClickListener
                            )
                        }
                    }

                    MessageContentItemType.SERVICE ->
                        when {
                            item.serviceType == ServiceType.TASK_LINKED ||
                                item.serviceType == ServiceType.TASK_APPENDED -> {
                                TaskLinkedServiceCloudContent(
                                    text = item.serviceMessage!!.text,
                                    textColor = viewPool.getTextColor(getServiceMessageColor(item))
                                )
                            }

                            !item.serviceMessage?.text.isNullOrBlank() || item.serviceMessage?.personList != null -> {
                                ServiceCloudContent(
                                    text = item.serviceMessage!!.text,
                                    textColor = viewPool.getTextColor(getServiceMessageColor(item))
                                )
                            }

                            else -> EmptyCloudContent
                        }

                    MessageContentItemType.SIGNATURE -> {
                        SignatureCloudContent(
                            DefaultSignature(item.signature!!.title, item.signature!!.isMine)
                        )
                    }

                    MessageContentItemType.CONTAINER -> {
                        ContainerCloudContent(item.children)
                    }

                    MessageContentItemType.SIGNING_BUTTONS -> {
                        val signingListener = if (listener.check(ButtonsEvent.OnSigningButtonClicked::class)) {
                            object : SigningActionListener {
                                override fun onAcceptClicked() {
                                    listener.onEvent(ButtonsEvent.OnSigningButtonClicked(true))
                                }

                                override fun onDeclineClicked() {
                                    listener.onEvent(ButtonsEvent.OnSigningButtonClicked(false))
                                }
                            }
                        } else {
                            null
                        }
                        SigningButtonsCloudContent(signingListener)
                    }

                    MessageContentItemType.GRANT_ACCESS_BUTTONS -> {
                        val grandListener = if (listener.check(ButtonsEvent.OnGrantAccessButtonClicked::class)) {
                            object : GrantAccessActionListener {
                                override fun onGrantAccessClicked(sender: View) {
                                    listener.onEvent(ButtonsEvent.OnGrantAccessButtonClicked(true, sender))
                                }

                                override fun onDenyAccessClicked() {
                                    listener.onEvent(ButtonsEvent.OnGrantAccessButtonClicked(false))
                                }
                            }

                        } else {
                            null
                        }
                        GrantAccessButtonsCloudContent(grandListener)
                    }

                    MessageContentItemType.LINK -> LinkCloudContent(data.groupConversation)
                    MessageContentItemType.TEXT -> EmptyCloudContent

                    else -> null
                }
            },
            data.messageContent.rootElements,
            isAuthorBlocked = isAuthorBlocked(data.senderPersonModel?.personData?.uuid),
            messageUuid = data.uuid
        )
    }

    /**
     * Конвертация модели сообщений в контент компонента видеоособщения
     *
     * @author da.zhukov
     */
    internal fun toVideoCloudComponentViewData(
        data: VideoCloudViewData,
        listener: MessageViewListener
    ): VideoMessageCloudViewData {
        return DefaultVideoMessageViewData(
            data.messageText,
            data.messageContent.content.mapIndexedNotNull { _, item ->
                when (item.itemType) {
                    MessageContentItemType.QUOTE ->
                        VideoMessageQuoteContent(
                            quote = DefaultQuote(
                                enclosingMessageUuid = data.uuid,
                                messageUuid = item.quote?.messageUuid ?: UUID.randomUUID()
                            ),
                            actionListener = getQuoteClickListener(listener)
                        )

                    MessageContentItemType.ATTACHMENT ->
                        VideoMessageMediaContent(
                            data = data.videoViewData,
                            actionListener = getMediaActionListener(listener)
                        )

                    else -> null
                }
            }
        )
    }

    /**
     * Вернуть слушатели кликов медиа-сообщений, если они заданы для MessageView.
     */
    fun getMediaActionListener(listener: MessageViewListener): MediaMessage.ActionListener =
        object : MediaMessage.ActionListener {
            override fun onExpandClicked(expanded: Boolean): Boolean {
                listener.onEvent(MediaEvent.OnMediaRecognizedTextClicked(expanded))
                return expanded
            }

            override fun onMediaPlaybackError(error: Throwable) {
                listener.onEvent(MediaEvent.OnMediaPlaybackError(error))
            }
        }

    private fun getQuoteClickListener(listener: MessageViewListener): QuoteClickListener =
        object : QuoteClickListener {
            override fun onQuoteClicked(quotedMessageUuid: UUID) {
                listener.onEvent(QuoteEvent.OnQuoteClicked(quotedMessageUuid))
            }

            override fun onQuoteLongClicked(quotedMessageUuid: UUID, enclosingMessageUuid: UUID) {
                listener.onEvent(QuoteEvent.OnQuoteLongClicked(quotedMessageUuid, enclosingMessageUuid))
            }
        }

    private fun isAuthorBlocked(senderUuid: UUID?): Boolean =
        if (senderUuid != null && complainService != null) {
            complainService.isPersonBlocked(senderUuid)
        } else {
            false
        }

    private fun getServiceMessageColor(it: MessageContentItem) =
        when (it.serviceType) {
            ServiceType.SIGNING_REQUEST -> BaseMessageResourceHolder.SIGN_REQUEST
            ServiceType.SIGNED -> BaseMessageResourceHolder.SIGNED
            ServiceType.NOT_SIGNED -> BaseMessageResourceHolder.NOT_SIGNED
            ServiceType.DIALOG_INVITE,
            ServiceType.MY_CIRCLES_INVITE -> BaseMessageResourceHolder.DIALOG_INVITE
            ServiceType.DOCUMENT_ACCESS -> BaseMessageResourceHolder.DOCUMENT_ACCESS
            ServiceType.FILE_ACCESS_REQUEST -> BaseMessageResourceHolder.FILE_ACCESS_REQUEST
            ServiceType.FILE_VIEW_ACCESS_GRANTED,
            ServiceType.FILE_CHANGE_ACCESS_GRANTED,
            ServiceType.FILE_CHANGE_PLUS_ACCESS_GRANTED -> BaseMessageResourceHolder.FILE_ACCESS_REQUEST_GRANTED
            ServiceType.FILE_ACCESS_REQUEST_REJECTED -> BaseMessageResourceHolder.FILE_ACCESS_REQUEST_REJECTED
            else -> BaseMessageResourceHolder.DEFAULT_SERVICE_TYPE
        }

    private fun getMessageText(data: CloudViewData): Spannable {
        if (!data.isRichTextConverted &&
            data.textModel.isNotEmpty() &&
            (data as? MessageCloudViewData)?.audioViewData == null
        ) {
            data.messageText = richTextConverter.getOptimizedMessageText(
                data.textModel,
                data.messageContent.content,
                isChatBotMessage = data.type == MessageType.CHAT_BOT_BUTTONS
            )
        }
        return data.messageText
    }
}
