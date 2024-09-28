package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui

import android.view.inputmethod.EditorInfo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import kotlinx.coroutines.rx2.asFlow
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.communicator_crm_chat_list.databinding.CommunicatorCrmConnectionFragmentBinding
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.mapper.CRMConnectionItemClickHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListView.Event
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListView.Model
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.launchAndCollect

/**
 * @author da.zhukov
 */
internal class CRMConnectionListViewImpl(
    private val binding: CommunicatorCrmConnectionFragmentBinding,
    listComponentFactory: CRMConnectionListComponentFactory,
    crmConnectionItemClickHelper: CRMConnectionItemClickHelper
) : BaseMviView<Model, Event>(), CRMConnectionListView {

    override val renderer: ViewRenderer<Model> = diff {
        diff(
            get = Model::query,
            set = {
                binding.crmConnectionSearchInput.apply {
                    setSearchText(it ?: StringUtils.EMPTY)
                }
            }
        )
    }

    private val queryChangeFlow get() = binding.crmConnectionSearchInput
        .searchQueryChangedObservable().asFlow()

    private val cancelSearchFlow get() = binding.crmConnectionSearchInput
        .cancelSearchObservable().asFlow()

    private val searchActionsFlow get() = binding.crmConnectionSearchInput
        .searchFieldEditorActionsObservable().asFlow()

    init {
        val lifecycleOwner: LifecycleOwner? = binding.root.findViewTreeLifecycleOwner()
        val scope = lifecycleOwner?.lifecycleScope

        scope?.launchWhenStarted {
            launchAndCollect(queryChangeFlow) {
                dispatch(
                    Event.EnterSearchQuery(query = it)
                )
            }
            launchAndCollect(cancelSearchFlow) {
                dispatch(
                    Event.EnterSearchQuery(query = null)
                )
            }
            launchAndCollect(searchActionsFlow) {
                if (it == EditorInfo.IME_ACTION_NEXT || it == EditorInfo.IME_ACTION_SEARCH) {
                    binding.crmConnectionSearchInput.hideKeyboard()
                }
            }
            launchAndCollect(crmConnectionItemClickHelper.onItemCheckboxFlow) {
                dispatch(
                    Event.ItemSelected(
                        Pair(it.first, it.second,)
                    )
                )
            }
            listComponentFactory.create(binding.crmConnectionList).apply {
                launchAndCollect(onItemClick.asFlow()) { item ->
                    dispatch(
                        Event.ItemSelected(
                            Pair(item.id, item.label)
                        )
                    )
                }
            }
        }
    }
}