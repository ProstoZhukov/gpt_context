package ru.tensor.sbis.design.message_view.controller

import android.content.Context
import android.graphics.Canvas
import android.view.View
import androidx.core.view.children
import ru.tensor.sbis.common.util.safeThrow
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.cloud_view.utils.swipe.CloudSwipeToQuoteListener
import ru.tensor.sbis.design.cloud_view.utils.swipe.MessageSwipeToQuoteBehavior
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.MessageViewPool
import ru.tensor.sbis.design.message_view.content.MessageViewContentBinder
import ru.tensor.sbis.design.message_view.content.message.MessageCloudViewContentBinder
import ru.tensor.sbis.design.message_view.content.crm_views.chat_bot_buttons.ChatBotButtonsBinder
import ru.tensor.sbis.design.message_view.content.crm_views.greetings_view.GreetingsMessageBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.MessageViewListenerChanges
import ru.tensor.sbis.design.message_view.utils.MessageViewDataConverter
import ru.tensor.sbis.design.message_view.utils.castTo
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.RateContentBinder
import ru.tensor.sbis.design.message_view.content.service_views.ServiceContentBinder
import ru.tensor.sbis.design.message_view.content.service_views.ServiceMaterialsContentBinder
import ru.tensor.sbis.design.message_view.content.threads.ThreadContentBinder
import ru.tensor.sbis.design.message_view.content.threads.ThreadCreationServiceContentBinder
import ru.tensor.sbis.design.message_view.content.video_message.VideoMessageContentBinder
import ru.tensor.sbis.design.message_view.model.ServiceMaterialsViewData
import ru.tensor.sbis.design.message_view.model.ServiceViewData
import ru.tensor.sbis.design.message_view.utils.HighlightDrawable
import ru.tensor.sbis.design.util.dpToPx
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Реализует логику компонента [MessageView].
 * @see MessageViewAPI
 *
 * @author dv.baranov
 */
internal class MessageViewController : MessageViewAPI {

    private lateinit var messageView: MessageView
    private lateinit var converter: MessageViewDataConverter
    private lateinit var viewPool: MessageViewPool

    private var contentView: View? = null
    private var binder: Binder? = null
    private var onClickListener: View.OnClickListener? = null

    private val context: Context
        get() = messageView.context

    private val swipeToQuoteHelper = SwipeToQuoteHelper()
    private val listener = MessageViewListener()

    private val binders: List<Binder> by lazy(NONE) {
        listOf(
            MessageCloudViewContentBinder(converter),
            VideoMessageContentBinder(converter),
            ServiceContentBinder,
            ServiceMaterialsContentBinder(converter),
            ThreadContentBinder,
            ThreadCreationServiceContentBinder,
            RateContentBinder(converter),
            GreetingsMessageBinder,
            ChatBotButtonsBinder(converter)
        ).castTo()!!
    }

    /** Инициализировать контроллер. */
    internal fun initController(messageView: MessageView) {
        this.messageView = messageView
    }

    override val swipeToQuoteBehavior: MessageSwipeToQuoteBehavior = swipeToQuoteHelper

    override var viewData: MessageViewData? = null
        set(value) {
            val lastData = field
            field = value
            if (value == null) return

            updateContent(
                viewData = value,
                isContentChanged = contentView == null || isContentTypeChanged(lastData, value)
            )
        }

    /** @SelfDocumented */
    fun setOnClickListener(listener: View.OnClickListener?) {
        onClickListener = listener
        contentView?.setOnClickListener(listener)
    }

    private fun updateContent(viewData: MessageViewData, isContentChanged: Boolean) {
        val binder = binders.find { it.isDataSupported(viewData) }
        this.binder = binder
        if (binder == null) {
            safeThrow(
                IllegalArgumentException("Неподдерживаемый тип MessageViewData ${viewData::class.java.simpleName}")
            )
            return
        }

        if (isContentChanged) updateContentView(binder, viewData)
        binder.bindData(requireNotNull(contentView), viewData, listener)
    }

    private fun updateContentView(binder: Binder, viewData: MessageViewData) {
        recycleViews()
        contentView = binder.getContent(viewPool, viewData).also {
            swipeToQuoteHelper.onContentChanged(it)
            it.setOnClickListener(onClickListener)
        }
        updateBackgroundTranslation(viewData)
        messageView.layoutParams = binder.contentLayoutParams
        messageView.addView(contentView, binder.contentLayoutParams)
    }

    private fun isContentTypeChanged(
        old: MessageViewData?,
        new: MessageViewData
    ) =
        old == null ||
            old::class != new::class ||
            old.type != new.type

    override fun setMessageViewPool(viewPool: MessageViewPool) {
        this.viewPool = viewPool
        converter = MessageViewDataConverter(
            context,
            viewPool.messageBlockViewPool
        )
    }

    override fun changeEventListeners(changes: MessageViewListenerChanges) {
        listener.changeEventListeners(changes)
    }

    override fun updateSendingState(sendingState: SendingState) {
        binder?.updateSendingState(requireNotNull(contentView), sendingState)
    }

    override fun setFormattedDateTime(formattedDateTime: FormattedDateTime) {
        binder?.setFormattedDateTime(requireNotNull(contentView), formattedDateTime)
    }

    override fun changeRejectProgress(show: Boolean) {
        contentView?.castTo<CloudView>()?.showRejectProgress = show
    }

    override fun changeAcceptProgress(show: Boolean) {
        contentView?.castTo<CloudView>()?.showAcceptProgress = show
    }

    private fun updateBackgroundTranslation(data: MessageViewData) {
        messageView.background.castTo<HighlightDrawable>()?.translationY =
            if (data is ServiceMaterialsViewData) {
                context.dpToPx(MATERIALS_HIGHLIGHT_TRANSLATION_DP).toFloat()
            } else {
                0f
            }
    }

    override fun recycleViews() {
        messageView.children.forEach {
            it.setOnClickListener(null)
            it.setOnLongClickListener(null)
            viewData?.castTo<ServiceViewData>()?.clickableSpan?.let { span ->
                span.callback?.let { callback -> span.removeCallback(callback) }
            }
            viewPool.addView(it)
        }
        messageView.removeAllViews()
        contentView = null
    }
}

private class SwipeToQuoteHelper : MessageSwipeToQuoteBehavior {

    private var contentBehavior: MessageSwipeToQuoteBehavior? = null

    /**
     * Обработать событие изменения содержимого [MessageView].
     */
    fun onContentChanged(view: View) {
        contentBehavior = view as? MessageSwipeToQuoteBehavior
    }
    override var canBeQuoted: Boolean
        get() = contentBehavior?.canBeQuoted ?: false
        set(value) {
            if (contentBehavior?.canBeQuoted != value) {
                contentBehavior?.canBeQuoted = value
            }
        }

    override var swipeToQuoteListener: CloudSwipeToQuoteListener?
        get() = contentBehavior?.swipeToQuoteListener
        set(value) {
            if (contentBehavior?.swipeToQuoteListener != value) {
                contentBehavior?.swipeToQuoteListener = value
            }
        }

    override val movementFlags: Int
        get() = contentBehavior?.movementFlags ?: 0

    override fun draw(canvas: Canvas, dx: Float, isSwiping: Boolean): Boolean =
        contentBehavior?.draw(canvas, dx, isSwiping) ?: false
}

private typealias Binder = MessageViewContentBinder<View, MessageViewData>
private const val MATERIALS_HIGHLIGHT_TRANSLATION_DP = 4