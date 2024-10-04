package ru.tensor.sbis.design.message_view.content.cloud_view

import android.view.View
import ru.tensor.sbis.attachments.models.AttachmentModel
import ru.tensor.sbis.attachments.ui.view.clickhandler.AttachmentUploadActionsHandler
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.content.link.LinkClickListener
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener
import ru.tensor.sbis.design.cloud_view.listener.AuthorAvatarClickListener
import ru.tensor.sbis.design.cloud_view.listener.AuthorNameClickListener
import ru.tensor.sbis.design.cloud_view.model.PersonModel
import ru.tensor.sbis.design.cloud_view.utils.swipe.CloudSwipeToQuoteListener
import ru.tensor.sbis.design.message_view.content.BaseMessageViewContentBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.AttachmentEvent
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.AuthorEvent
import ru.tensor.sbis.design.message_view.model.CloudViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewDataConverter
import java.util.UUID

/**
 * Биндер ячеек сообщений, которые включают в себя CloudView.
 *
 * @author vv.chekurda
 */
internal abstract class CloudViewContentBinder<VIEW : View, DATA : CloudViewData>(
    private val converter: MessageViewDataConverter
) : BaseMessageViewContentBinder<VIEW, DATA>() {

    /** @SelfDocumented */
    protected open fun bindCloudView(
        view: CloudView,
        data: DATA,
        listener: MessageViewListener
    ) {
        with(view) {
            val isOutgoing = data.outgoing
            val attachmentsActionsHandler = getAttachmentUploadActionsHandler(listener)
            val needShowAttachmentsUploadProgress = isOutgoing &&
                attachmentsActionsHandler.onDeleteUploadClicked != null

            setOnStatusClickListener(getStatusClickListener(listener))
            getOnLongClickListener(listener).also { listener ->
                view.contentLongClickListener = listener
                setOnLongClickListener(listener)
            }
            getOnClickListener(listener)?.also { listener ->
                view.setOnClickListener(listener)
            }
            swipeToQuoteListener = getSwipeToQuoteListener(listener)
            linkClickListener = getLinkClickListener(listener)
            phoneNumberClickListener = getPhoneNumberClickListener(listener)

            attachmentsUploadActionsHandler = attachmentsActionsHandler
            showAttachmentsUploadProgress = needShowAttachmentsUploadProgress
            maxVisibleAttachmentsCount =
                if (needShowAttachmentsUploadProgress) {
                    Int.MAX_VALUE
                } else {
                    MAX_INCOME_VISIBLE_ATTACHMENTS_COUNT
                }

            this.data = converter.toCloudComponentViewData(data = data, listener = listener)
            edited = data.edited
            sendingState = data.sendingState
            isPersonal = data.groupConversation && !isOutgoing
            canBeQuoted = data.isQuotable
            if (data.groupConversation) {
                receiverInfo = data.receiverInfo
                if (data.showAuthor) {
                    author = data.senderPersonModel
                    setOnAuthorAvatarClickListener(getAuthorAvatarClickListener(listener))
                } else {
                    author = null
                    setOnAuthorAvatarClickListener(null)
                }
                setOnAuthorNameClickListener(getAuthorNameClickListener(listener))
            } else {
                author = null
                receiverInfo = null
            }
            data.formattedDateTime?.also { dateTime = it }
        }
    }

    /** @SelfDocumented */
    protected fun getAuthorNameClickListener(listener: MessageViewListener): AuthorNameClickListener? =
        if (listener.check(AuthorEvent.OnAuthorNameClicked::class)) {
            object : AuthorNameClickListener {
                override fun onNameClicked(model: PersonModel) {
                    listener.onEvent(AuthorEvent.OnAuthorNameClicked(model))
                }
            }
        } else {
            null
        }

    /** @SelfDocumented */
    protected fun getAuthorAvatarClickListener(listener: MessageViewListener): AuthorAvatarClickListener? =
        if (listener.check(AuthorEvent.OnAuthorAvatarClicked::class)) {
            object : AuthorAvatarClickListener {
                override fun onAvatarClicked(model: PersonModel) {
                    listener.onEvent(AuthorEvent.OnAuthorAvatarClicked(model))
                }
            }
        } else {
            null
        }

    private fun getStatusClickListener(listener: MessageViewListener): (() -> Unit)? =
        if (listener.check(MessageViewEvent.BaseEvent.OnStatusClicked::class)) {
            { listener.onEvent(MessageViewEvent.BaseEvent.OnStatusClicked) }
        } else {
            null
        }

    /** @SelfDocumented */
    protected fun getSwipeToQuoteListener(listener: MessageViewListener): CloudSwipeToQuoteListener? =
        if (listener.check(MessageViewEvent.QuoteEvent.OnSwipedToQuote::class)) {
            { listener.onEvent(MessageViewEvent.QuoteEvent.OnSwipedToQuote) }
        } else {
            null
        }

    private fun getLinkClickListener(listener: MessageViewListener): LinkClickListener? =
        if (listener.check(MessageViewEvent.BaseEvent.OnLinkClicked::class)) {
            object : LinkClickListener {
                override fun onLinkClicked() {
                    listener.onEvent(MessageViewEvent.BaseEvent.OnLinkClicked)
                }
            }
        } else {
            null
        }

    private fun getPhoneNumberClickListener(listener: MessageViewListener): PhoneNumberClickListener? =
        if (listener.check(MessageViewEvent.PhoneNumberEvent.OnPhoneNumberClicked::class)) {
            object : PhoneNumberClickListener {
                override fun onPhoneNumberClicked(phoneNumber: String) {
                    listener.onEvent(MessageViewEvent.PhoneNumberEvent.OnPhoneNumberClicked(phoneNumber))
                }

                override fun onPhoneNumberLongClicked(phoneNumber: String, messageUUID: UUID?) {
                    listener.onEvent(MessageViewEvent.PhoneNumberEvent.OnPhoneNumberLongClicked(phoneNumber))
                }
            }
        } else {
            null
        }

    private fun getAttachmentUploadActionsHandler(listener: MessageViewListener): AttachmentUploadActionsHandler =
        object : AttachmentUploadActionsHandler {
            override val onDeleteUploadClicked: ((attachmentModel: AttachmentModel) -> Unit)? =
                if (listener.check(AttachmentEvent.OnAttachmentDeleteClicked::class)) {
                    { model -> listener.onEvent(AttachmentEvent.OnAttachmentDeleteClicked(model)) }
                } else {
                    null
                }

            override val onRetryUploadClicked: ((attachmentModel: AttachmentModel) -> Unit)? =
                if (listener.check(AttachmentEvent.OnAttachmentRetryUploadClicked::class)) {
                    { model -> listener.onEvent(AttachmentEvent.OnAttachmentRetryUploadClicked(model)) }
                } else {
                    null
                }

            override val onErrorUploadClicked: ((attachmentModel: AttachmentModel, errorMessage: String) -> Unit)? =
                if (listener.check(AttachmentEvent.OnAttachmentErrorUploadClicked::class)) {
                    { model, error -> listener.onEvent(AttachmentEvent.OnAttachmentErrorUploadClicked(model, error)) }
                } else {
                    null
                }
        }
}

private const val MAX_INCOME_VISIBLE_ATTACHMENTS_COUNT = 4