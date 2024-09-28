package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_CONSULTATION_CONTACT_ID
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_CONSULTATION_CHANNEL_TYPE
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_ORIGIN_ID
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_CONSULTATION_RESULT
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_NAME
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_OPERATOR_RESULT
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store.CRMChannelsStore
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store.CRMChannelsStoreFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsView.Event
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsView.Model
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import java.util.UUID

/**
 * Контроллер, обеспечивающий связку компонентов Android с компонентами MVI.
 *
 * @author da.zhukov
 */
internal class CRMChannelsController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted viewFactory: (View) -> CRMChannelsView,
    private val crmChannelsStoreFactory: CRMChannelsStoreFactory,
    private val selectedItems: MutableLiveData<List<UUID>>
) {

    private val store = fragment.provideStore { crmChannelsStoreFactory.create(it) }
    private val backButtonClick: () -> Unit = {
        fragment.requireActivity().onBackPressedDispatcher.onBackPressed()
    }

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

    private fun toIntent(event: Event): CRMChannelsStore.Intent = when (event) {
        is Event.EnterSearchQuery -> CRMChannelsStore.Intent.SearchQuery(event.query)
        is Event.OnItemSuccessClick -> CRMChannelsStore.Intent.OnItemSuccessClick(event.id, event.parentId, event.channelName)
        is Event.OnItemCheckClick -> CRMChannelsStore.Intent.OnItemCheckClick(event.result)
        is Event.OnItemClick -> CRMChannelsStore.Intent.OnItemClick(
            event.id,
            event.channelId,
            event.operatorGroupId,
            event.name,
            event.needOpenFolder,
            event.itemType,
            event.groupType
        )

        is Event.UpdateCurrentFolderView -> CRMChannelsStore.Intent.UpdateCurrentFolderView(
            event.folderTitle,
            event.currentFolderViewIsVisible
        )

        is Event.BackButtonClick -> CRMChannelsStore.Intent.BackButtonClick
        is Event.CurrentFolderViewClick -> CRMChannelsStore.Intent.CurrentFolderViewClick(
            event.parentId,
            event.parentName,
            event.groupType,
            event.needShowFolder
        )
    }

    private fun toModel(state: CRMChannelsStore.State) =
        Model(
            query = state.query,
            currentFolderViewIsVisible = state.currentFolderViewIsVisible,
            folderTitle = state.folderTitle,
        )

    private fun CRMChannelsStore.Label.consume() = when (this) {
        is CRMChannelsStore.Label.BackButtonClick -> {
            backButtonClick.invoke()
        }

        is CRMChannelsStore.Label.OnItemCheckClick -> {
            fragment.parentFragmentManager.setFragmentResult(
                CrmChatFilterFragment.REQUEST,
                bundleOf(
                    CrmChatFilterFragment.RESULT_UUIDS to arrayListOf(result.first),
                    CrmChatFilterFragment.RESULT_NAMES to arrayListOf(result.second)
                )
            )
        }

        is CRMChannelsStore.Label.OnChannelConsultationItemClick -> {
            fragment.requireActivity().supportFragmentManager.setFragmentResult(
                CRM_CHANNEL_CONSULTATION_RESULT,
                bundleOf(
                    CRM_CHANNEL_ORIGIN_ID to result.first,
                    CRM_CHANNEL_CONSULTATION_CONTACT_ID to result.second,
                    CRM_CHANNEL_CONSULTATION_CHANNEL_TYPE to result.third
                )
            )
            backButtonClick.invoke()
        }

        is CRMChannelsStore.Label.OnChannelOperatorItemClick -> {
            fragment.parentFragmentManager.setFragmentResult(
                CRM_CHANNEL_OPERATOR_RESULT,
                bundleOf(
                    CRM_CHANNEL_ORIGIN_ID to channelId,
                    CRM_CHANNEL_NAME to channelName
                )
            )
            backButtonClick.invoke()
        }
    }

    fun onNotChoseClick() {
        fragment.parentFragmentManager.setFragmentResult(
            CRM_CHANNEL_OPERATOR_RESULT,
            bundleOf()
        )
        backButtonClick.invoke()
    }

    fun onResetButtonClick() {
        selectedItems.postValue(emptyList())
    }
}