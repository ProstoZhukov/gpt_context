package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui

import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.crm.conversation.databinding.CommunicatorCrmQuickReplyBinding
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyView.Event
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.helpers.QuickReplyClickActionHandler
import ru.tensor.sbis.communicator.declaration.crm.model.QuickReplyParams
import ru.tensor.sbis.design.utils.extentions.doOnDetachedFromWindow

/**
 * Реализация View содержимого списка быстрых ответов.
 *
 * @author dv.baranov
 */
internal class QuickReplyViewImpl(
    private val binding: CommunicatorCrmQuickReplyBinding,
    listComponentFactory: QuickReplyListComponentFactory,
    private val params: QuickReplyParams,
    private val quickReplyClickActionHandler: QuickReplyClickActionHandler,
) : BaseMviView<QuickReplyView.Model, Event>(), QuickReplyView {

    override val renderer: ViewRenderer<QuickReplyView.Model> =
        diff {
            diff(
                get = QuickReplyView.Model::searchText,
                set = { handleNewSearchText(it) },
            )
            diff(
                get = QuickReplyView.Model::folderTitle,
                set = { handleFolderTitleChanged(it) },
            )
        }

    private val queryChangeFlow get() = binding.communicatorCrmQuickReplySearchInput
        .searchQueryChangedObservable().asFlow()

    private val cancelSearchFlow get() = binding.communicatorCrmQuickReplySearchInput
        .cancelSearchObservable().asFlow()

    init {
        listComponentFactory.create(binding.communicatorCrmQuickReplyList)
        val lifecycleOwner: LifecycleOwner? = binding.root.findViewTreeLifecycleOwner()
        val scope = lifecycleOwner?.lifecycleScope
        scope?.launchWhenStarted {
            launchAndCollect(queryChangeFlow) {
                dispatch(Event.EnterSearchQuery(it))
            }
            launchAndCollect(cancelSearchFlow) {
                dispatch(Event.EnterSearchQuery(StringUtils.EMPTY))
            }
            launchAndCollect(quickReplyClickActionHandler.onItemClick) { item ->
                if (item.isGroup && params.needFolderView) {
                    dispatch(Event.FolderChanged(item.text, item.id))
                } else if (!item.isTitle) {
                    binding.communicatorCrmQuickReplySearchInput.clearSearch()
                    dispatch(Event.OnQuickReplyItemClicked(item.id, item.text, item.isPinned))
                }
            }
            launchAndCollect(quickReplyClickActionHandler.onSwipeMenuItemClick) {
                dispatch(Event.OnPinClick(it.first, it.second))
            }
        }
        binding.communicatorCrmQuickReplyFolderTitleLayout.run {
            setOnClickListener {
                if (isVisible) {
                    dispatch(Event.FolderChanged(StringUtils.EMPTY, null))
                }
            }
        }
        binding.communicatorCrmQuickReplySearchInput.isVisible = params.needSearchInput
        binding.communicatorCrmQuickReplyFolderTitleLayout.isVisible = params.needFolderView
        doIf(params.isEditSearch) { registerQuickReplyDataObserver(binding.communicatorCrmQuickReplyList.list) }
    }

    private fun handleNewSearchText(newText: String) {
        binding.communicatorCrmQuickReplySearchInput.run {
            setSearchText(newText)
        }
    }

    private fun handleFolderTitleChanged(title: String) {
        binding.communicatorCrmQuickReplyFolderTitleLayout.run {
            isVisible = title.isNotEmpty() && params.needFolderView
            setTitle(title)
        }
        binding.communicatorCrmQuickReplySearchInput.run {
            isVisible = title.isEmpty() && params.needSearchInput
        }
    }

    private fun registerQuickReplyDataObserver(recyclerView: RecyclerView) {
        recyclerView.adapter?.let { adapter ->
            val observer = object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    dispatch(Event.OnListElementsCountChanged(adapter.itemCount))
                }
            }
            adapter.registerAdapterDataObserver(observer)
            recyclerView.doOnDetachedFromWindow {
                adapter.unregisterAdapterDataObserver(observer)
            }
        }
    }

    override fun setSearchQuery(query: String) {
        binding.communicatorCrmQuickReplySearchInput.setSearchText(query)
    }

    override fun handleHeightChanges(heightEqualZero: Boolean) {
        binding.communicatorCrmQuickReplyList.list.apply {
            if (heightEqualZero) {
                scrollToPosition(0)
            }
        }
    }

    override fun setScrollListener(listener: RecyclerView.OnScrollListener?) {
        listener?.let { binding.communicatorCrmQuickReplyList.list.addOnScrollListener(it) }
            ?: binding.communicatorCrmQuickReplyList.list.clearOnScrollListeners()
    }

    private fun <T> CoroutineScope.launchAndCollect(
        flow: Flow<T>,
        collector: (T) -> Unit,
    ) = launch { flow.collect(collector) }
}
