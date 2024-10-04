package ru.tensor.sbis.design.message_view.content.video_message

import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.content.cloud_view.CloudViewContentBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.VideoCloudViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewDataConverter
import ru.tensor.sbis.design.message_view.utils.MessageViewPool
import ru.tensor.sbis.design.cloud_view.video.VideoMessageCloudView

/**
 * Биндер видеосообщений.
 *
 * @author vv.chekurda
 */
internal class VideoMessageContentBinder(
    private val converter: MessageViewDataConverter
) : CloudViewContentBinder<VideoMessageCloudView, VideoCloudViewData>(converter) {

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is VideoCloudViewData

    override fun getContent(messageViewPool: MessageViewPool, data: VideoCloudViewData): VideoMessageCloudView =
        if (data.outgoing) {
            messageViewPool.outcomeVideoCloudView
        } else {
            messageViewPool.incomeVideoCloudView
        }

    override fun bindData(
        view: VideoMessageCloudView,
        data: VideoCloudViewData,
        listener: MessageViewListener
    ) {
        val cloudVideoData = converter.toVideoCloudComponentViewData(
            data = data,
            listener = listener
        )
        view.apply {
            setMediaActionListener(converter.getMediaActionListener(listener))
            getOnLongClickListener(listener).also { listener ->
                setOnLongClickListener(listener)
                view.contentLongClickListener = listener
            }
            swipeToQuoteListener = getSwipeToQuoteListener(listener)

            this.data = cloudVideoData
            edited = data.edited
            sendingState = data.sendingState
            isPersonal = data.groupConversation && !data.outgoing
            canBeQuoted = data.isQuotable

            if (data.groupConversation) {
                receiverInfo = data.receiverInfo
                if (data.showAuthor) {
                    author = data.senderPersonModel
                    setOnAuthorAvatarClickListener(getAuthorAvatarClickListener(listener))
                }
                setOnAuthorNameClickListener(getAuthorNameClickListener(listener))
            } else {
                author = null
                receiverInfo = null
            }
            data.formattedDateTime?.also {
                setFormattedDateTime(view, it)
            }
        }
    }

    override fun updateSendingState(view: VideoMessageCloudView, sendingState: SendingState) {
        view.sendingState = sendingState
    }

    override fun setFormattedDateTime(view: VideoMessageCloudView, formattedDateTime: FormattedDateTime) {
        view.dateTime = formattedDateTime
    }
}