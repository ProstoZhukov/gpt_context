package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communicator.common.analytics.QuickReply
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.SEARCH_RESULT_QUICK_REPLY_KEY
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.SELECTED_QUICK_REPLY_KEY
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.QuickReplyFragment
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplySearchResult
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store.QuickReplyStore
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store.QuickReplyStore.Intent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store.QuickReplyStore.Label
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store.QuickReplyStoreFactory
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyView.Event
import ru.tensor.sbis.communicator.declaration.crm.model.QuickReplyParams
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore

/**
 * Связывает [QuickReplyFragment] и компоненты MVI.
 *
 * @author dv.baranov
 */
internal class QuickReplyController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted viewFactory: (View) -> QuickReplyView,
    quickReplyStoreFactory: QuickReplyStoreFactory,
    private val params: QuickReplyParams,
) {
    private val store = fragment.provideStore { quickReplyStoreFactory.create(it) }
    private val analyticsUtil = crmConversationDependency?.analyticsUtilProvider?.getAnalyticsUtil()

    init {
        with(fragment) {
            attachBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
                bind {
                    view.events.map(::toIntent) bindTo store
                    store.states.map(::toModel) bindTo view
                    store.labels bindTo { it.consume() }
                }
            }
        }
    }

    private fun toIntent(event: Event): Intent = when (event) {
        is Event.EnterSearchQuery -> Intent.EnterSearchQuery(event.query)
        is Event.FolderChanged -> Intent.FolderChanged(event.folderTitle, event.uuid)
        is Event.OnQuickReplyItemClicked -> {
            analyticsUtil?.sendAnalytics(
                QuickReply(
                    quickReplyListCalledByButton = !params.isEditSearch,
                    isPinned = event.isPinned,
                ),
            )
            setFragmentResult(
                Bundle().apply {
                    putString(SELECTED_QUICK_REPLY_KEY, event.replyText)
                },
            )
            Intent.NoAction
        }
        is Event.OnPinClick -> Intent.OnPinClick(event.uuid, event.isPinned)
        is Event.OnListElementsCountChanged -> {
            setFragmentResult(
                Bundle().apply {
                    val searchResult = if (event.count != 0) {
                        QuickReplySearchResult.LIST_HAS_ELEMENTS
                    } else {
                        QuickReplySearchResult.LIST_IS_EMPTY
                    }
                    putString(SEARCH_RESULT_QUICK_REPLY_KEY, searchResult.toString())
                },
            )
            Intent.NoAction
        }
    }

    private fun setFragmentResult(bundle: Bundle) {
        fragment.parentFragmentManager.setFragmentResult(
            params.resultKey,
            bundle,
        )
    }

    private fun toModel(storeState: QuickReplyStore.State): QuickReplyView.Model =
        QuickReplyView.Model(
            storeState.searchText,
            storeState.folderTitle,
        )

    private fun Label.consume() = Unit
}
