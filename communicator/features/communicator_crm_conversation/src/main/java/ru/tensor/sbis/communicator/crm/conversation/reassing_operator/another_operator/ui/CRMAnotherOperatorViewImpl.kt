package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui

import android.view.inputmethod.EditorInfo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.crm.conversation.databinding.CommunicatorCrmAnotherOperatorFragmentBinding
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.view.input.searchinput.SearchColorType

/**
 * @author da.zhukov
 */
internal class CRMAnotherOperatorViewImpl(
    private val binding: CommunicatorCrmAnotherOperatorFragmentBinding,
    listComponentFactory: CRMAnotherOperatorListComponentFactory,
) : BaseMviView<CRMAnotherOperatorView.Model, CRMAnotherOperatorView.Event>(),
    CRMAnotherOperatorView
{
    override val renderer: ViewRenderer<CRMAnotherOperatorView.Model> = diff {
        diff(
            get = CRMAnotherOperatorView.Model::query,
            set = { binding.crmAnotherOperatorToolbar.searchInput?.setSearchText(it ?: StringUtils.EMPTY) }
        )
        diff(
            get = CRMAnotherOperatorView.Model::filter,
            set = { binding.crmAnotherOperatorToolbar.searchInput?.setSelectedFilters(listOf(it ?: StringUtils.EMPTY)) }
        )
    }

    private val queryChangeFlow
        get() = binding.crmAnotherOperatorToolbar.searchInput?.searchQueryChangedObservable()?.asFlow()

    private val cancelSearchFlow
        get() = binding.crmAnotherOperatorToolbar.searchInput?.cancelSearchObservable()?.asFlow()

    private val searchActionsFlow
        get() = binding.crmAnotherOperatorToolbar.searchInput?.searchFieldEditorActionsObservable()?.asFlow()

    private val searchFilterClickFlow
        get() = binding.crmAnotherOperatorToolbar.searchInput?.filterClickObservable()?.asFlow()

    init {
        val lifecycleOwner: LifecycleOwner? = binding.root.findViewTreeLifecycleOwner()
        val scope = lifecycleOwner?.lifecycleScope

        with(binding) {
            crmAnotherOperatorToolbar.apply {
                content = SbisTopNavigationContent.SearchInput
                showBackButton = true
                searchInput?.apply {
                    setHasFilter(true)
                    setSearchColor(SearchColorType.ADDITIONAL)
                }
                backBtn?.setOnClickListener {
                    scope?.launch {
                        dispatch(CRMAnotherOperatorView.Event.BackButtonClick)
                    }
                }
            }
        }

        scope?.launchWhenStarted {
            queryChangeFlow?.let {
                launchAndCollect(it) { query ->
                    dispatch(CRMAnotherOperatorView.Event.EnterSearchQuery(query))
                }
            }
            cancelSearchFlow?.let {
                launchAndCollect(it) {
                    dispatch(CRMAnotherOperatorView.Event.EnterSearchQuery(null))
                }
            }
            searchActionsFlow?.let { flow ->
                launchAndCollect(flow) {
                    if (it == EditorInfo.IME_ACTION_NEXT || it == EditorInfo.IME_ACTION_SEARCH) {
                        binding.crmAnotherOperatorToolbar.searchInput?.hideKeyboard()
                    }
                }
            }
            searchFilterClickFlow?.let {
                launchAndCollect(it) {
                    dispatch(CRMAnotherOperatorView.Event.FilterClick)
                }
            }

            listComponentFactory.create(binding.crmAnotherOperatorList).apply {
                launchAndCollect(onItemClick.asFlow()) {
                    dispatch(CRMAnotherOperatorView.Event.OnItemClick(it.id))
                }
            }
        }
    }

    private fun <T> CoroutineScope.launchAndCollect(
        flow: Flow<T>,
        collector: (T) -> Unit
    ) = launch { flow.collect(collector) }
}