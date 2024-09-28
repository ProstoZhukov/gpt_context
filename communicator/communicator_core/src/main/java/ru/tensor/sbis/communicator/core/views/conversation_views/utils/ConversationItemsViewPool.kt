package ru.tensor.sbis.communicator.core.views.conversation_views.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsView
import ru.tensor.sbis.communicator.core.views.conversation_views.ConversationItemView
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.folders.FoldersView
import ru.tensor.sbis.design.message_view.utils.SimpleViewPool
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.richtext.view.RichTextView
import ru.tensor.sbis.swipeablelayout.SwipeableLayout

/**
 * Пул для view ячеек списка реестра диалогов и их контента.
 *
 * @author vv.chekurda
 */
class ConversationItemsViewPool(
    private val context: Context,
    prefetch: Boolean = true
) {

    val conversationItemView: ConversationItemView
        get() = conversationItemViewPool.getView()

    val swipeableLayout: SwipeableLayout
        get() = swipeableLayoutViewPool.getView()

    val attachmentsView: AttachmentsView
        get() = attachmentsViewPool.getView()

    val richText: RichTextView
        get() = richTextPool.getView()

    val foldersView: FoldersView
        get() = foldersViewPool.getView()

    private val attachmentItemViewPool = RecyclerView.RecycledViewPool()

    private val attachmentsViewPool = SimpleViewPool {
        AttachmentsView(context).apply {
            viewPool = attachmentItemViewPool
        }
    }

    private val foldersViewPool = SimpleViewPool {
        FoldersView(context)
    }

    private val richTextPool = SimpleViewPool {
        RichTextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getDimen(R.attr.fontSize_m_scaleOn))
            setTextColor(context.getThemeColorInt(R.attr.textColor))
            isVisible = text.isNotEmpty()
        }
    }

    private val conversationItemViewPool = SimpleViewPool {
        ConversationItemView(context = context, viewPool = this)
    }

    private val swipeableLayoutViewPool = SimpleViewPool {
        SwipeableLayout(context)
    }

    private var prefetchJob: Job? = null

    init {
        if (prefetch) {
            prefetchJob = CoroutineScope(Dispatchers.Default).launch {
                prefetchFolders()
                prefetchConversationItems(this)
                prefetchAttachments(this)
                prefetchRichTexts()
                prefetchJob = null
            }
        }
    }

    private fun prefetchFolders() {
        foldersViewPool.prefetch(count = PREFETCH_FOLDERS_COUNT)
    }

    private fun prefetchConversationItems(scope: CoroutineScope) {
        repeat(PREFETCH_CONVERSATION_ITEM_COUNT) {
            if (!scope.isActive) return@repeat
            conversationItemViewPool.prefetch()
        }
        repeat(PREFETCH_CONVERSATION_ITEM_COUNT) {
            if (!scope.isActive) return@repeat
            swipeableLayoutViewPool.prefetch()
        }
    }

    private fun prefetchAttachments(scope: CoroutineScope) {
        prefetchAttachmentsViews(scope)
        prefetchAttachmentsItemViews(scope)
    }

    private fun prefetchRichTexts() {
        richTextPool.prefetch(PREFETCH_RICH_TEXT_COUNT)
    }

    private fun prefetchAttachmentsViews(scope: CoroutineScope) {
        repeat(ATTACHMENTS_VIEW_POOL_SIZE) {
            if (!scope.isActive) return@repeat
            attachmentsViewPool.prefetch()
        }
    }

    private fun prefetchAttachmentsItemViews(scope: CoroutineScope) {
        val view = attachmentsViewPool.getView()
        repeat(PREFETCH_ATTACHMENT_ITEM_PREVIEW_COUNT) {
            if (!scope.isActive) return@repeat
            view.prefetchViewHolders(previewCount = 1, videoCount = 0, iconCount = 0)
        }
        repeat(PREFETCH_ATTACHMENT_ITEM_VIDEO_COUNT) {
            if (!scope.isActive) return@repeat
            view.prefetchViewHolders(previewCount = 0, videoCount = 1, iconCount = 1)
        }
        attachmentsViewPool.addView(view)
    }

    fun recycle(view: View) {
        when (view) {
            is AttachmentsView -> attachmentsViewPool.addView(view)
            is RichTextView -> richTextPool.addView(view)
            // Для остальных типов.
            else -> Unit
        }
    }

    fun flush() {
        prefetchJob?.cancel()
        attachmentsViewPool.clear()
        attachmentItemViewPool.clear()
        richTextPool.clear()
        conversationItemViewPool.clear()
        swipeableLayoutViewPool.clear()
    }
}

private const val PREFETCH_ATTACHMENT_ITEM_PREVIEW_COUNT = 7
private const val PREFETCH_ATTACHMENT_ITEM_VIDEO_COUNT = 3
private const val ATTACHMENTS_VIEW_POOL_SIZE = 3
private const val PREFETCH_CONVERSATION_ITEM_COUNT = 7
private const val PREFETCH_FOLDERS_COUNT = 1
private const val PREFETCH_RICH_TEXT_COUNT = 2