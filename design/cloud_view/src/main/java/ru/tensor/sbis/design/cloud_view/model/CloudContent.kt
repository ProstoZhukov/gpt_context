package ru.tensor.sbis.design.cloud_view.model

import androidx.annotation.ColorInt
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageViewData
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.content.attachments.AttachmentClickListener
import ru.tensor.sbis.design.cloud_view.content.attachments.model.MessageAttachment
import ru.tensor.sbis.design.cloud_view.content.certificate.Signature
import ru.tensor.sbis.design.cloud_view.content.grant_access.GrantAccessActionListener
import ru.tensor.sbis.design.cloud_view.content.quote.Quote
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickListener
import ru.tensor.sbis.design.cloud_view.content.signing.SigningActionListener

/**
 * Содержимое для ячейки-облачка. Типы [CloudContent] определяют поддерживаемые компоненты внутри облачка.
 * На основе типа определяется, какой именно компонент нужно создать для облачка и какие данные и подписки ему передать.
 *
 * @see CloudView
 *
 * @author ma.kolpakov
 */
sealed class CloudContent

/**
 * Тип содержимого для отображения цитаты
 */
data class QuoteCloudContent(
    val quote: Quote,
    val listener: QuoteClickListener
) : CloudContent()

/**
 * Тип содержимого для отображения сервисного сообщения
 */
data class ServiceCloudContent(
    val text: String,
    @ColorInt val textColor: Int
) : CloudContent()

/**
 * Тип содержимого для отображения сервисного сообщения о прикреплении задачи
 */
data class TaskLinkedServiceCloudContent(
    val text: String,
    @ColorInt val textColor: Int
) : CloudContent()

/**
 * Тип содержимого для отображения подписей
 */
data class SignatureCloudContent(
    val signature: Signature
) : CloudContent()

/**
 * Тип содержимого для отображения кнопок подписания
 */
data class SigningButtonsCloudContent(
    val actionListener: SigningActionListener?
) : CloudContent()

/**
 * Тип содержимого для отображения кнопок предоставления доступа к файлу
 */
data class GrantAccessButtonsCloudContent(
    val actionListener: GrantAccessActionListener?
) : CloudContent()

/**
 * Тип содержимого для отображения аудиосообщения.
 *
 * @property data данные для отображения аудиосообщения.
 * @property actionListener обработчик действий над аудиосообщением.
 */
data class AudioMessageCloudContent(
    val data: AudioMessageViewData,
    val actionListener: MediaMessage.ActionListener?
) : CloudContent()

/**
 * Тип содержимого для отображения вложенной структуры
 */
data class ContainerCloudContent(
    val children: List<Int>
) : CloudContent()

/**
 * Тип контента без содержимого. Исполльзуется в случаях, если нужно отобразить только структуру
 * ячейки-облака
 */
object EmptyCloudContent : CloudContent()

/**
 * Тип контента без содержимого. Исполльзуется в случаях, если нужно отобразить только ссылку в структуре
 * ячейки-облака.
 */
data class LinkCloudContent(val isGroupConversation: Boolean) : CloudContent()

/**
 * Тип содержимого для отображения вложений
 */
data class AttachmentCloudContent(
    val attachment: MessageAttachment,
    val isDownscaledImages: Boolean,
    var listener: AttachmentClickListener? = null
) : CloudContent()