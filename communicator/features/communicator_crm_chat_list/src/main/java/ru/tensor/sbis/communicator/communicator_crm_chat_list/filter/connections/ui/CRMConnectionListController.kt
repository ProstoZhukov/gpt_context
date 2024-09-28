package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.CRMConnectionListFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.Intent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.Label
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.State
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStoreFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListView.Event
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListView.Model
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import java.util.UUID

/**
 * Контроллер, обеспечивающий связку компонентов Android с компонентами MVI.
 *
 * @author da.zhukov
 */
internal class CRMConnectionListController @AssistedInject constructor(
    @Assisted private val fragment: CRMConnectionListFragment,
    @Assisted viewFactory: (View) -> CRMConnectionListView,
    private val storeFactory: CRMConnectionListStoreFactory,
    private val selectedItems: MutableLiveData<List<UUID>>
) {
    private val store = fragment.provideStore {
        storeFactory.create(it)
    }

    init {
        fragment.attachBinder(
            BinderLifecycleMode.CREATE_DESTROY,
            viewFactory
        ) { view ->
            bind {
                store.states.map { it.toModel() } bindTo view
                view.events.map { it.toIntent() } bindTo store
                store.labels bindTo { it.consume() }
            }
        }
    }

    private fun Label.consume() = when (this) {
        is Label.ItemSelected -> {
            fragment.parentFragmentManager.setFragmentResult(
                CrmChatFilterFragment.REQUEST,
                bundleOf(
                    CrmChatFilterFragment.RESULT_UUIDS to arrayListOf(idAndLabel.first),
                    CrmChatFilterFragment.RESULT_NAMES to arrayListOf(idAndLabel.second)
                )
            )
        }
    }


    private fun Event.toIntent(): Intent {
        return when (this) {
            is Event.EnterSearchQuery -> Intent.SearchQuery(query)
            is Event.ItemSelected -> Intent.ItemSelected(idAndLabel)
        }
    }

    private fun State.toModel(): Model =
        Model(this.query)

    fun onResetButtonClick() {
        selectedItems.postValue(emptyList())
    }
}        