package ru.tensor.sbis.communicator.core.utils

import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.common.analytics.CommunicatorAnalyticsUtil
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.viewer_factory.data.DialogAttachmentViewerArgsFactory.createArgs
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import ru.tensor.sbis.viewer.decl.slider.source.ViewerArgsSource
import ru.tensor.sbis.communicator.common.viewer_factory.MessagesViewerSliderCollectionFactory
import ru.tensor.sbis.communicator.design.R
import java.util.*

/**
 * Created by gs.raudiyaynen on 10.07.2017.
 */
object MessageUtils {

    /** @SelfDocumented */
    @Suppress("MemberVisibilityCanBePrivate")
    fun findFirstTextInMessage(message: Message): String? {
        val contentItemList = message.content
        return findFirstTextInChilds(contentItemList, message.rootElements)
    }

    /** @SelfDocumented */
    fun getMessageTextForQuote(message: Message, resourceProvider: ResourceProvider? = null): String =
        when {
            resourceProvider != null && message.isAudioMessage -> {
                resourceProvider.getString(R.string.communicator_audio_message_text_in_dialog_registry)
            }
            resourceProvider != null && message.isVideoMessage -> {
                resourceProvider.getString(R.string.communicator_video_message_text_in_dialog_registry)
            }
            else -> {
                findFirstTextInMessage(message)
                    ?: findFirstQuoteItem(message)?.let { quote ->
                        findFirstTextInChilds(message.content, quote.children)
                    }
                    ?: StringUtils.EMPTY
            }
        }

    /**
     * Получить подзаголовок для редакции сообщения.
     *
     * @return для аудио и видео сообщений возвращает соответствующий заголовок, для остальных null для дефолт реализации.
     */
    fun getEditMessageSubtitle(message: Message, resourceProvider: ResourceProvider? = null): String? =
        when {
            resourceProvider == null -> null
            message.isAudioMessage -> {
                resourceProvider.getString(R.string.communicator_audio_message_text_in_dialog_registry)
            }
            message.isVideoMessage -> {
                resourceProvider.getString(R.string.communicator_video_message_text_in_dialog_registry)
            }
            message.messageText.isNullOrEmpty() && message.attachmentCount > 0 -> {
                resourceProvider.getString(R.string.communicator_edit_message_with_attachments_subtitle)
            }
            else -> null
        }

    /**
     * Получить текст для редактируемого сообщения.
     *
     * @return для аудио и видео сообщений без расшифровки возвращает пустую строку,
     * чтобы не подставлять дефолтный текст отсутствующей расшифровки,
     * для всех остальных null для дефолт реализации.
     */
    fun getEditMessageText(message: Message): String? =
        if (message.mediaMessageData?.recognized == false && !message.edited) {
            StringUtils.EMPTY
        } else {
            null
        }

    private fun findFirstQuoteItem(message: Message): MessageContentItem? {
        repeat(message.rootElements.size) { i ->
            message.content[message.rootElements[i]]
                .takeIf { it.itemType == MessageContentItemType.QUOTE }
                ?.let { return it }
        }
        return null
    }

    private fun findFirstTextInChilds(itemList: List<MessageContentItem>, children: List<Int>): String? {
        val childrenCount = children.size
        var item: MessageContentItem
        val result = StringBuilder()
        for (i in 0 until childrenCount) {
            item = itemList[children[i]]
            val text = item.text
            val link = item.linkUrl
            if (item.itemType == MessageContentItemType.TEXT && text.isNotEmpty()) {
                result.append(text.trim { it <= ' ' })
                result.append(" ")
            }
            if (item.itemType == MessageContentItemType.LINK && link != null && link.isNotEmpty()) {
                result.append(item.linkUrl)
                result.append(" ")
            }
        }
        return result.toString().trim { it <= ' ' }.ifEmpty { null }
    }

    /** @SelfDocumented */
    fun getSenderNameForQuote(message: Message): String {
        if (findFirstTextInMessage(message) == null) {
            findFirstQuoteItem(message)?.quote?.let { quote ->
                return quote.senderNameLast + quote.senderNameFirst.let { if (it.isNotEmpty()) " ${it[0]}." else "" }
            }
        }
        return message.senderName.let { personName ->
            personName.last + personName.first.let { if (it.isNotEmpty())  " %s.".format(it[0]) else StringUtils.EMPTY }
        }
    }

    /** @SelfDocumented */
    fun createViewerSliderArgs(dialogUuid: UUID, message: Message, attachment: AttachmentViewModel, analyticsUtil: CommunicatorAnalyticsUtil? = null): ViewerSliderArgs =
        createViewerSliderArgs(
            dialogUuid,
            attachment,
            message.content.mapNotNull { item ->
                item.attachment?.takeIf { item.itemType == MessageContentItemType.ATTACHMENT }
            },
            analyticsUtil = analyticsUtil,
        )

    /**
     * Создать аргументы для сквозного просмотрщика вложений диалога
     *
     * @param dialogUuid  идентификатор диалога
     * @param attachment  кликнутое пользователем вложение из этого списка
     */
    fun createViewerSliderArgs(
        dialogUuid: UUID,
        attachment: AttachmentViewModel,
        messageAttachments: List<AttachmentViewModel> = listOf(attachment),
        analyticsUtil: CommunicatorAnalyticsUtil?,
    ): ViewerSliderArgs {
        return ViewerSliderArgs(
            ViewerArgsSource.Collection(
                messageAttachments.map { it.fileInfoViewModel.createArgs(it.uuid) },
                messageAttachments.indexOfFirst { it.uuid == attachment.uuid }.coerceAtLeast(0),
                MessagesViewerSliderCollectionFactory(dialogUuid, attachment.uuid, analyticsUtil),
            ),
        )
    }
}