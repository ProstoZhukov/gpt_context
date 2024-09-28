package ru.tensor.sbis.design.message_panel.decl.record

import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView.RecipientsViewData

/**
 * Данные для декорирования процесса записи аудио и видео сообщений.
 *
 * @property recipientsData текущие данные по выбранным получателям сообщения
 * @property quoteData текущие данные по цитируемому сообщению.
 *
 * @author vv.chekurda
 */
data class RecorderDecorData(
    val recipientsData: RecipientsViewData? = null,
    val quoteData: MessagePanelQuote? = null
)