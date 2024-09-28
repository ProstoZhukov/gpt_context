package ru.tensor.sbis.design.message_view.model

import android.text.Spannable
import android.text.SpannableString
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.design.cloud_view.model.PersonModel
import ru.tensor.sbis.design.cloud_view.model.ReceiverInfo
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.ui.MessageView
import java.util.UUID

/**
 * Базовая модель данных компонента [MessageView], необходимая при построении всех видов ячеек.
 *
 * @author vv.chekurda
 */
interface CoreMessageData {

    /** UUID - сообщения, которому соответствует эта модель. */
    val uuid: UUID

    /** Дата и время ячейки. */
    val formattedDateTime: FormattedDateTime?

    /** Является ли ячейка частью групповой переписки. */
    var groupConversation: Boolean

    /**
     * Должна ли ячейка отображаться как исходящее сообщение, но с автором
     * (используется в задачах для широковещательных сообщений).
     */
    val isOutcomeMessageWithAuthor: Boolean

    /** Стиль ячейки, если ячейка содержит облачко. true - стиль исходящего сообщения, false - входящего. */
    val outgoing: Boolean

    /** Редактировалось ли сообщение, когда ячейка - облачко. */
    val edited: Boolean

    /** Состояние отправки. */
    var sendingState: SendingState

    /** Состояние прочитанности получателем. */
    val readByReceiver: Boolean

    /** Можно ли процитировать. */
    val isQuotable: Boolean

    /** Модель персоны отправителя. */
    val senderPersonModel: PersonModel?

    /** Модель получателя. */
    val receiverInfo: ReceiverInfo?

    /** Модель текста. */
    val textModel: String

    /** Полученный текст из модели сообщения. */
    var messageText: Spannable

    /** Сервисный объект. */
    val serviceObject: String

    /** Контент сообщения. Содержит в себе content и rootElements. */
    val messageContent: MessageContent

    /** Нужно ли показывать автора. */
    val showAuthor: Boolean

    val isRichTextConverted: Boolean
}

/**
 * Реализация [CoreMessageData].
 *
 * @author vv.chekurda
 */
internal data class CoreMessageDataImpl(
    override val uuid: UUID = UUIDUtils.NIL_UUID,
    override var groupConversation: Boolean = false,
    override val isOutcomeMessageWithAuthor: Boolean = false,
    override val outgoing: Boolean = false,
    override val edited: Boolean = false,
    override var sendingState: SendingState = SendingState.IS_READ,
    override val readByReceiver: Boolean = false,
    override val isQuotable: Boolean = false,
    override val senderPersonModel: PersonModel? = null,
    override val receiverInfo: ReceiverInfo? = null,
    override val textModel: String = StringUtils.EMPTY,
    override var messageText: Spannable = SpannableString(StringUtils.EMPTY),
    override val serviceObject: String = StringUtils.EMPTY,
    override val messageContent: MessageContent = MessageContent(ArrayList(), ArrayList()),
    override val formattedDateTime: FormattedDateTime? = null
) : CoreMessageData {

    override val showAuthor: Boolean
        get() = !outgoing || isOutcomeMessageWithAuthor

    override val isRichTextConverted: Boolean
        get() = messageText.isNotEmpty()
}