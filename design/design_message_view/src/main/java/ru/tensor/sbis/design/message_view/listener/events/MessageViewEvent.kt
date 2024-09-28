package ru.tensor.sbis.design.message_view.listener.events

import android.view.View
import ru.tensor.sbis.attachments.models.AttachmentModel
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.design.cloud_view.model.PersonModel
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ConsultationRateType
import ru.tensor.sbis.design.message_view.ui.MessageView
import java.util.UUID

/**
 * Интерфейс слушателя кликов [MessageView].
 *
 * @author vv.chekurda
 */
fun interface EventListener<in E : MessageViewEvent> {

    /** @SelfDocumented */
    fun onEvent(event: E)
}

/**
 * Интерфейс событий MessageView.
 *
 * @author vv.chekurda
 */
sealed interface MessageViewEvent {

    /** События, которые могут произойти с большей частью ячеек. */
    sealed interface BaseEvent : MessageViewEvent {

        /** @SelfDocumented */
        class OnLongClicked(val view: View) : BaseEvent

        /** @SelfDocumented */
        object OnClicked : BaseEvent

        /** @SelfDocumented */
        object OnLinkClicked : BaseEvent

        /** Клик на статус сообщения. */
        object OnStatusClicked : BaseEvent
    }

    /** События цитирования сообщения. */
    sealed interface QuoteEvent : MessageViewEvent {

        /** Свайп, вызывающий цитирование. */
        object OnSwipedToQuote : QuoteEvent

        /** Клик по цитате. */
        class OnQuoteClicked(val quotedMessageUuid: UUID) : QuoteEvent

        /** Лонг-клик по цитате. */
        class OnQuoteLongClicked(val quotedMessageUuid: UUID) : QuoteEvent
    }

    /** События, связанные с автором сообщения. */
    sealed interface AuthorEvent : MessageViewEvent {

        /** @SelfDocumented */
        class OnAuthorAvatarClicked(val model: PersonModel) : AuthorEvent

        /** @SelfDocumented */
        class OnAuthorNameClicked(val model: PersonModel) : AuthorEvent
    }

    /** События вложений сообщения. */
    sealed interface AttachmentEvent : MessageViewEvent {

        /** @SelfDocumented */
        class OnAttachmentClicked(val attachment: AttachmentViewModel) : AttachmentEvent

        /** @SelfDocumented */
        class OnAttachmentDeleteClicked(val attachmentModel: AttachmentModel) : AttachmentEvent

        /** Принудительная перезагрузка вложения. */
        class OnAttachmentRetryUploadClicked(val attachmentModel: AttachmentModel) : AttachmentEvent

        /** Клик на иконку ошибки загрузки вложения. */
        class OnAttachmentErrorUploadClicked(
            val attachmentModel: AttachmentModel,
            val errorMessage: String
        ) : AttachmentEvent
    }

    /** События с телефонным номером. */
    sealed interface PhoneNumberEvent : MessageViewEvent {

        /** @SelfDocumented */
        class OnPhoneNumberClicked(val phoneNumber: String) : PhoneNumberEvent

        /** @SelfDocumented */
        class OnPhoneNumberLongClicked(val phoneNumber: String) : PhoneNumberEvent
    }

    /** События медиа контента. */
    sealed interface MediaEvent : MessageViewEvent {

        /** Клик по иконке раскрытия распознанного текста. */
        class OnMediaRecognizedTextClicked(val isExpanded: Boolean) : MediaEvent

        /** Клик по иконке ошибки воспроизведения. */
        class OnMediaPlaybackError(val error: Throwable) : MediaEvent
    }

    /** События клика кнопок внутри облачков. */
    sealed interface ButtonsEvent : MessageViewEvent {

        /** Клик на кнопку подписания файла. */
        class OnSigningButtonClicked(val isAccepted: Boolean) : ButtonsEvent

        /** Клик на кнопку разрешения доступа к файлу. */
        class OnGrantAccessButtonClicked(
            val isAccepted: Boolean,
            val button: View? = null
        ) : ButtonsEvent
    }

    /** События ячеек из чатов тех.поддержки. */
    sealed interface CRMEvent : MessageViewEvent {

        /** Клик кнопки от чат-бота. */
        class OnChatBotButtonClicked(val title: String) : CRMEvent

        /** Клик по кнопке приветствия. */
        class OnGreetingClicked(val title: String) : CRMEvent

        /** Клик по кнопке выставления рейтинга работы оператора. */
        class OnRateRequestButtonClicked(val rateType: ConsultationRateType, val disableComment: Boolean) : CRMEvent

        /** Событие скролла к концу переписки. */
        object ScrollToBottom : CRMEvent
    }

    /** События сервисных сообщений. */
    sealed interface ServiceEvent : MessageViewEvent {

        /** @SelfDocumented */
        object OnServiceMessageClicked : ServiceEvent
    }

    /** События тредов. */
    sealed interface ThreadEvent : MessageViewEvent {

        /** @SelfDocumented */
        object OnThreadMessageClicked : ThreadEvent

        /** @SelfDocumented */
        object OnThreadCreationServiceClicked : ThreadEvent
    }
}