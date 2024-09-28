package ru.tensor.sbis.communicator.base.conversation.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.ConversationLayoutManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSectionAdapter
import ru.tensor.sbis.communicator.base.conversation.R
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.BaseMessageActionsListener
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.BaseMessageViewHolder
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.design.cloud_view.utils.swipe.MessageSwipeToQuoteCallback
import ru.tensor.sbis.design.list_header.DateTimeAdapter
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.message_view.model.MessageViewData
import java.util.*

/**
 * Базовая реализация адаптера сообщений в переписке.
 *
 * @author vv.chekurda
 */
abstract class BaseConversationAdapter<MESSAGE : BaseConversationMessage>(
    private val dateUpdater: ListDateViewUpdater
) : BaseTwoWayPaginationAdapter<MESSAGE>(),
    ListSectionAdapter<MESSAGE>,
    PagingLoadingErrorActions,
    DateTimeAdapter {

    protected var highlightedMessageUuid: UUID? = null
        private set

    @JvmField
    protected var showNewerLoadingError = false

    @JvmField
    protected var showOlderLoadingError = false

    private var newerProgressOffset = 0

    private var relevantMessagePosition = NO_POSITION

    private val conversationLayoutManager: ConversationLayoutManager?
        get() = mRecyclerView?.layoutManager?.castTo<ConversationLayoutManager>()

    init {
        mWithBottomEmptyHolder = false
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        ItemTouchHelper(MessageSwipeToQuoteCallback()).attachToRecyclerView(recyclerView)
        recyclerView.recycledViewPool.setMaxRecycledViews(MESSAGE_HOLDER_TYPE, MAX_RECYCLED_VIEWS_FOR_TYPE)
    }

    override fun getSectionItem(position: Int): MESSAGE? = getItem(position)

    override fun getItemDateTime(position: Int): Date? =
        getItem(position)?.timestampSent?.let(::Date)

    fun setHighlightedMessageUuid(messageUuid: UUID?) {
        highlightedMessageUuid = messageUuid
    }

    fun setRelevantMessagePosition(position: Int) {
        relevantMessagePosition = position
    }

    fun getPositionForMessage(message: MESSAGE): Int =
        getPositionForMessageByUuid(message.uuid)

    fun getPositionForMessageByUuid(messageUuid: UUID): Int {
        val position = content.indexOfFirst { messageUuid == it.uuid }
        return if (position != NO_POSITION) position + mOffset else NO_POSITION
    }

    abstract fun getMessagesDiffCallback(
        last: List<MessageViewData>,
        current: List<MessageViewData>
    ): DiffUtil.Callback

    override fun setData(dataList: List<MESSAGE>?, offset: Int) {
        val notifyDataSetChanged: Boolean
        val result: DiffUtil.DiffResult?
        if (!dataList.isNullOrEmpty() && content.isNotEmpty()) {
            result = DiffUtil.calculateDiff(
                getMessagesDiffCallback(
                    content.toViewDataList(),
                    dataList.toViewDataList()
                )
            )
            notifyDataSetChanged = false
        } else {
            result = null
            notifyDataSetChanged = true
        }

        mOffset = offset + newerProgressOffset
        setContent(dataList, notifyDataSetChanged)
        result?.dispatchUpdatesTo(BaseConversationUpdateCallback(this, mOffset, mRecyclerView))
    }

    private fun List<MESSAGE>.toViewDataList() = this.map { it.viewData }

    override fun setDataWithoutNotify(dataList: List<MESSAGE>?, offset: Int) {
        mOffset = offset + newerProgressOffset
        if (dataList != null) {
            setContent(dataList, false)
        } else {
            setContent(null, true)
        }
    }

    override fun setContent(newContent: List<MESSAGE>?, notifyDataSetChanged: Boolean) {
        setRelevantMessagePositionOnInit(newContent)
        super.setContent(newContent, notifyDataSetChanged)
    }

    /**
     * Установка позиции релевантного сообщения при инициализации списка
     * @param newContent новый список сообщений
     */
    private fun setRelevantMessagePositionOnInit(newContent: List<MESSAGE>?) {
        if (needSetupRelevantMessagePosition(newContent)) {
            setupRelevantMessagePosition()
        }
    }

    /** Проверка необходимости установки позиции релевантного сообщения */
    private fun needSetupRelevantMessagePosition(newContent: List<MESSAGE>?) =
        content.isEmpty() && !newContent.isNullOrEmpty() && relevantMessagePosition != NO_POSITION

    /** Установка позиции релевантного сообщения */
    private fun setupRelevantMessagePosition() {
        conversationLayoutManager?.setInitialAnchorPosition(relevantMessagePosition + mOffset)
        relevantMessagePosition = NO_POSITION
    }

    private fun refreshInitialRelevantMessagePosition() {
        val initialPendingPosition = conversationLayoutManager?.initialPendingPosition
        if (initialPendingPosition != null) {
            relevantMessagePosition = initialPendingPosition
            setupRelevantMessagePosition()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<MESSAGE> =
        when (viewType) {
            PAGING_LOADING_ERROR_ITEM -> {
                AbstractViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.communicator_base_conersation_item_list_paging_error,
                        parent,
                        false
                    )
                )
            }
            HOLDER_PROGRESS -> createProgressViewHolder(parent)
            else -> createEmptyViewHolder(parent)
        }

    private fun createEmptyViewHolder(parent: ViewGroup): AbstractViewHolder<MESSAGE> {
        val item = View(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(0, 0)
        }
        return AbstractViewHolder(item)
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<MESSAGE>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        item?.also { holder.checkHighlight(it) }
        holder.castTo<BaseMessageViewHolder<MESSAGE, *>>()?.bind(item!!)
    }

    protected fun AbstractViewHolder<MESSAGE>.checkHighlight(item: MESSAGE) {
        highlightedMessageUuid?.let {
            if (item.message?.uuid == it) {
                castTo<BaseMessageViewHolder<*, *>>()?.highlight()
                setHighlightedMessageUuid(null)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val itemPosition = position - mOffset
        val itemType = when {
            itemPosition == -1 -> {
                if (mShowNewerLoadingProgress) {
                    if (showNewerLoadingError) PAGING_LOADING_ERROR_ITEM else HOLDER_PROGRESS
                } else {
                    HOLDER_EMPTY
                }
            }
            itemPosition == mContent.size -> {
                if (mShowOlderLoadingProgress) {
                    if (showOlderLoadingError) PAGING_LOADING_ERROR_ITEM else HOLDER_PROGRESS
                } else {
                    HOLDER_EMPTY
                }
            }
            getItem(position) != null -> MESSAGE_HOLDER_TYPE
            else -> HOLDER_EMPTY
        }
        return itemType
    }

    override fun getItemType(message: MESSAGE?): Int =
        if (message != null) MESSAGE_HOLDER_TYPE else HOLDER_EMPTY

    override fun showNewerLoadingProgress(showNewerLoadingProgress: Boolean) {
        val isChanged = mShowNewerLoadingProgress != showNewerLoadingProgress
        if (!isChanged) return

        mShowNewerLoadingProgress = showNewerLoadingProgress
        showNewerLoadingError = false

        if (mShowNewerLoadingProgress) {
            newerProgressOffset = 1
            mOffset += newerProgressOffset
            refreshInitialRelevantMessagePosition()
            notifyItemInserted(mOffset - newerProgressOffset)
        } else {
            mOffset -= newerProgressOffset
            newerProgressOffset = 0
            refreshInitialRelevantMessagePosition()
            notifyItemRemoved(mOffset)
        }
    }

    override fun showOlderLoadingProgress(showOlderLoadingProgress: Boolean) {
        if (showOlderLoadingProgress && content.size < UNI_DIRECTION_ERROR_LIST_SIZE) return
        val isChanged = mShowOlderLoadingProgress != showOlderLoadingProgress
        if (!isChanged) return

        mShowOlderLoadingProgress = showOlderLoadingProgress
        showOlderLoadingError = false

        val progressItemPosition = content.size + mOffset
        if (!mWithBottomEmptyHolder) {
            if (showOlderLoadingProgress) {
                notifyItemInserted(progressItemPosition)
            } else {
                notifyItemRemoved(progressItemPosition)
            }
        } else {
            notifyItemChanged(progressItemPosition)
        }
    }

    override fun showNewerLoadingError() {
        if (mShowNewerLoadingProgress && !showNewerLoadingError) {
            showNewerLoadingError = true
            notifyItemChanged(mOffset - 1)
        }
    }

    override fun showOlderLoadingError() {
        if (mShowOlderLoadingProgress && !showOlderLoadingError) {
            showOlderLoadingError = true
            notifyItemChanged(content.size + mOffset)
        }
    }

    override fun resetPagingLoadingError() {
        showNewerLoadingError = false
        if (mShowNewerLoadingProgress) {
            notifyItemChanged(mOffset - newerProgressOffset)
        }
        showOlderLoadingError = false
        if (mShowOlderLoadingProgress) {
            notifyItemChanged(content.size + mOffset)
        }
    }

    fun updateSendingState(adapterPosition: Int) {
        getItem(adapterPosition)?.castTo<BaseConversationMessage>()?.message?.sendingState?.let { sendingState ->
            mRecyclerView?.findViewHolderForAdapterPosition(adapterPosition)
                ?.castTo<BaseMessageViewHolder<MESSAGE, BaseMessageActionsListener<MESSAGE>>>()
                ?.updateSendingState(sendingState)
        }
    }

    open fun clear() {
        mOffset = 0
        newerProgressOffset = 0
        mShowNewerLoadingProgress = false
        mShowOlderLoadingProgress = false
        showNewerLoadingError = false
        showOlderLoadingError = false
        relevantMessagePosition = NO_POSITION
        highlightedMessageUuid = null
        mContent = listOf()
    }
}

const val MESSAGE_HOLDER_TYPE = 2
const val MAX_RECYCLED_VIEWS_FOR_TYPE = 50
private const val PAGING_LOADING_ERROR_ITEM = -5
private const val UNI_DIRECTION_ERROR_LIST_SIZE = 5