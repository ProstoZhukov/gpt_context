package ru.tensor.sbis.design.cloud_view.video.model

import android.text.Spannable
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.design.cloud_view.content.quote.Quote
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickListener
import ru.tensor.sbis.design.video_message_view.message.data.VideoMessageViewData

/**
 * Реализация по умолчанию для [VideoMessageCloudViewData]
 *
 * @author da.zhukov
 */
data class DefaultVideoMessageViewData(
    override val text: Spannable? = null,
    override val content: List<VideoMessageContent> = emptyList(),
) : VideoMessageCloudViewData

/**
 * Содержимое для отображения видеосообщения.
 */
sealed class VideoMessageContent

/**
 * Тип содержимого для отображения видеосообщения.
 *
 * @property data данные для отображения видеосообщения.
 * @property actionListener обработчик действий над видеосообщением.
 */
data class VideoMessageMediaContent(
    val data: VideoMessageViewData,
    val actionListener: MediaMessage.ActionListener?
) : VideoMessageContent()

/**
 * Тип содержимого для отображения цитаты с видеосообщением.
 *
 * @property quote данные для отображения цитаты.
 * @property actionListener обработчик действий над видеосообщением.
 */
data class VideoMessageQuoteContent(
    val quote: Quote,
    val actionListener: QuoteClickListener
) : VideoMessageContent()
