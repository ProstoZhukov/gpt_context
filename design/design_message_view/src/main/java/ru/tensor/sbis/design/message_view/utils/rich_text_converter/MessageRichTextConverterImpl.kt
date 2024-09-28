package ru.tensor.sbis.design.message_view.utils.rich_text_converter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import org.json.JSONObject
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communication_decl.communicator.media.isAudioMessage
import ru.tensor.sbis.communication_decl.communicator.media.isVideoMessage
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageContentItem
import ru.tensor.sbis.communicator.generated.MessageContentItemType
import ru.tensor.sbis.communicator.generated.Quote
import ru.tensor.sbis.design.message_view.utils.MessageDecoratedLinkOpener
import ru.tensor.sbis.richtext.converter.RichTextConverter
import ru.tensor.sbis.richtext.converter.cfg.Configuration
import ru.tensor.sbis.richtext.converter.cfg.DefaultDecoratedLinkConfiguration
import ru.tensor.sbis.richtext.converter.cfg.RenderOptions
import ru.tensor.sbis.richtext.converter.json.JsonRichTextConverter
import timber.log.Timber
import java.util.UUID

/**
 * Реализация конвертера обычной строки в декорированную.
 *
 * @author da.zhukov
 */
internal class MessageRichTextConverterImpl(
    private val context: Context,
    private val optimizeConvert: Boolean = false
) : MessageRichTextConverter {

    private val richTextConverter: RichTextConverter = createConverter()

    private fun createConverter(): RichTextConverter {
        val decoratedLinkConfiguration = DefaultDecoratedLinkConfiguration(MessageDecoratedLinkOpener())
        val renderOptions = RenderOptions()
            .drawLinkAsDecorated(true)
            .drawWrappedImages(true)
        val configuration = Configuration.Builder(renderOptions)
            .decoratedLinkConfiguration(decoratedLinkConfiguration)
            .build()
        return JsonRichTextConverter(context, configuration)
    }

    override fun convert(source: String): Spannable =
        richTextConverter.convert(source)

    /**
     * Добавлено пробрасывание ошибки, в случае, если RichTextConverter вернет пустые данные.
     * Попытка отловить ошибку
     * <a href="https://online.sbis.ru/opendoc.html?guid=2d9ed4a4-9416-4617-b18c-f7dc8bb7b391">...</a>
     */
    override fun getRichTextMessage(
        message: Message,
        serviceObject: JSONObject?
    ): Spannable {
        val messageString: String
        val isMediaMessage: Boolean
        if (serviceObject.isAudioMessage() || serviceObject.isVideoMessage()) {
            // Для медиа сообщений распознанный текст будет передан через медиа данные
            isMediaMessage = true
            messageString = serviceObject!!.optString("quotedText")
            addQuoteContentForMediaMessages(message, messageString)
        } else {
            isMediaMessage = false
            messageString = message.textModel
        }

        return getOptimizedMessageText(messageString, message.content, isMediaMessage)
    }

    override fun clearReferences() = Unit

    fun getOptimizedMessageText(
        textModel: String,
        content: List<MessageContentItem>,
        isMediaMessage: Boolean = false,
        isChatBotMessage: Boolean = false
    ): Spannable {
        var hasQuote = false
        var hasLink = false
        var hasText = false
        content.forEach {
            if (!hasQuote && it.itemType == MessageContentItemType.QUOTE) hasQuote = true
            if (!hasLink && it.itemType == MessageContentItemType.LINK) hasLink = true
            if (!hasText && it.itemType == MessageContentItemType.TEXT) hasText = true
        }
        val isRichText = isMediaMessage || hasQuote || hasLink || isChatBotMessage ||
            textModel.contains(Regex(RICH_REGEX)) ||
            (!hasText && textModel.isNotEmpty())

        return when {
            !isRichText -> {
                val builder = SpannableStringBuilder()
                val textContents = content.filter { it.itemType == MessageContentItemType.TEXT }
                textContents.forEachIndexed { index, contentItem ->
                    if (contentItem.itemType == MessageContentItemType.TEXT) {
                        if (index < textContents.lastIndex) {
                            builder.appendLine(contentItem.text)
                        } else {
                            builder.append(contentItem.text)
                        }
                    }
                }
                builder
            }
            !optimizeConvert || hasLink -> {
                richTextConverter.convert(textModel)
            }
            else -> {
                SpannableString("")
            }
        }
    }

    /**
     * Временное решение для фикса проблемы с подскроллом к цитируемому сообщению.
     * На IOS парсинг происходит на уровне RichTextConverter, а наш не умеет доставать uuid
     * цитируемого сообщения, поэтому пока что делаем это руками на старых рельсах.
     * [...](https://online.sbis.ru/opendoc.html?guid=a3763de1-1434-47e7-b46d-35f846a0d433&client=3)
     *
     * @param inputMessage модель сообщения.
     * @param mediaMessageQuoteBlock текст блока цитаты.
     */
    private fun addQuoteContentForMediaMessages(
        inputMessage: Message,
        mediaMessageQuoteBlock: String
    ) {
        if (mediaMessageQuoteBlock.isEmpty()) return
        try {
            val dataMsgIds = mediaMessageQuoteBlock.split("data-msg-id\": \"".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (dataMsgIds.isNotEmpty()) {
                var i = 1
                while (i < dataMsgIds.size) {
                    val uuidPart = dataMsgIds[i]
                    val uuidEndIndex = uuidPart.indexOf('"')
                    val stringUuid = dataMsgIds[i].substring(0, uuidEndIndex)
                    val msgUuid: UUID = UUIDUtils.fromString(stringUuid)
                    val quoteContent = MessageContentItem()
                    quoteContent.itemType = MessageContentItemType.QUOTE
                    quoteContent.quote = Quote(msgUuid, UUID.randomUUID())
                    inputMessage.content.add(quoteContent)
                    i += 2
                }
            }
        } catch (parseQuoteUuidException: Exception) {
            Timber.e(parseQuoteUuidException)
        }
    }
}

private const val A_REGEX = "\\[\"a\","
private const val S_REGEX = "\\[\"s\","
private const val EM_REGEX = "\\[\"em\","
private const val U_REGEX = "\\[\"u\","
private const val STRONG_REGEX = "\\[\"strong\","
private const val SPAN_REGEX = "\\[\"span\","
private const val RICH_REGEX = "$S_REGEX | $EM_REGEX | $U_REGEX | $STRONG_REGEX | $SPAN_REGEX | $A_REGEX"