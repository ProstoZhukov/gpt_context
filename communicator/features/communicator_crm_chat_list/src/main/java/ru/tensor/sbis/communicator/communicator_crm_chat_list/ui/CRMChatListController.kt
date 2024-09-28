package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.common.provider.BottomBarProvider
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCreationParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationOpenParams
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_CONSULTATION_CHANNEL_TYPE
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_CONSULTATION_CONTACT_ID
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_CONSULTATION_RESULT
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_ORIGIN_ID
import ru.tensor.sbis.communication_decl.crm.CrmChannelType
import ru.tensor.sbis.communicator.common.push.SubscribeOnNotification
import ru.tensor.sbis.communicator.common.push.UnsubscribeFromNotification
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListFilterHolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CRMChatFilterPreferences
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.crmFilterFlow
import ru.tensor.sbis.communicator.communicator_crm_chat_list.store.CRMChatListStore
import ru.tensor.sbis.communicator.communicator_crm_chat_list.store.CRMChatListStoreFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListFragment.Companion.REQUEST
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListFragment.Companion.RESULT_FILTER_MODEL
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListFragment.Companion.RESULT_FILTER_NAMES
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListView.Event
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListView.Model
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.router.CRMHostRouterImpl
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.CRMDeeplinkActionHandler
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.consultations.generated.ConsultationGroupType
import ru.tensor.sbis.deeplink.OpenCRMConversationDeepLinkAction
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.subscribe
import java.util.UUID

/**
 * Контроллер, обеспечивающий связку компонентов Android с компонентами MVI.
 *
 * @author da.zhukov
 */
internal class CRMChatListController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted viewFactory: (View) -> CRMChatListView,
    @Assisted private val deeplinkActionHandler: CRMDeeplinkActionHandler,
    @Assisted private val isHistoryMode: Boolean,
    @Assisted private val consultationUuid: UUID?,
    private val crmChatListStoreFactory: CRMChatListStoreFactory,
    private val crmChatListNotificationHelper: CRMChatListNotificationHelper,
    private val crmChatFilterPreferences: CRMChatFilterPreferences,
    private val filterHolder: CRMChatListFilterHolder
) : FragmentResultListener {

    private val router = CRMHostRouterImpl()
    private val store = fragment.provideStore { crmChatListStoreFactory.create(it) }
    private val messagesPushManager = CRMChatListPlugin.messagesPushManagerProvider?.get()?.messagesPushManager
    private val isTablet = fragment.isTablet

    init {
        router.initRouter(fragment, isHistoryMode)
        consultationUuid?.let {
            router.openCRMConversation(
                CRMConsultationOpenParams(
                    needBackButton = !isTablet,
                    crmConsultationCase = CRMConsultationCase.Operator(
                        originUuid = it,
                        viewId = filterHolder.viewId
                    ),
                ),
            )
        }
        with(fragment) {
            attachBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
                bind {
                    view.events.map(::toIntent) bindTo store
                    store.states.map(::toModel) bindTo view
                    store.labels bindTo { it.consume() }
                }
            }

            lifecycle.subscribe(
                onResume = {
                    updateShowingPushNotification(false)
                    if (!isHistoryMode) {
                        checkShowTakeOldestFab()
                    }
                },
                onPause = { if (!isHistoryMode) updateShowingPushNotification(true) }
            )

            lifecycleScope.launchWhenStarted {
                launch {
                    deeplinkActionHandler.deeplinkActionFlow.collect {
                        if (it is OpenCRMConversationDeepLinkAction) {
                            store.accept(
                                CRMChatListStore.Intent.OpenConsultation(
                                    CRMConsultationOpenParams(
                                        needBackButton = !isTablet,
                                        crmConsultationCase = CRMConsultationCase.Operator(
                                            originUuid = it.dialogUuid,
                                            viewId = filterHolder.viewId
                                        ),
                                    ),
                                ),
                            )
                        }
                    }
                }
                launch {
                    crmFilterFlow.collect {
                        store.accept(
                            CRMChatListStore.Intent.ApplyFilter(it.first, it.second)
                        )
                    }
                }
            }
        }
    }

    private fun toIntent(event: Event): CRMChatListStore.Intent = when (event) {
        is Event.EnterSearchQuery -> CRMChatListStore.Intent.SearchQuery(event.query)
        is Event.FolderChanged -> CRMChatListStore.Intent.ChangeCurrentFolder(event.groupType, event.folderTitle)
        is Event.SwipeMenuItemClicked -> CRMChatListStore.Intent.HandleSwipeMenuItemClick(event.menuItem)
        is Event.OpenConsultation -> CRMChatListStore.Intent.OpenConsultation(event.consultationParams)
        is Event.OpenSearchPanel -> CRMChatListStore.Intent.OpenSearchPanel
        is Event.ClickFilterIcon -> CRMChatListStore.Intent.OpenFilters(filterHolder.getCurrentFilterModel())
        Event.CheckShowTakeOldestFab -> CRMChatListStore.Intent.CheckShowTakeOldestFab(isTablet)
        is Event.TakeOldestConsultation -> CRMChatListStore.Intent.TakeOldestConsultation(!isTablet)
        is Event.TakeOldestFabVisibilityChanged ->
            CRMChatListStore.Intent.TakeOldestFabVisibilityChanged(event.isVisible)
        is Event.ShowInformer -> CRMChatListStore.Intent.ShowInformer(event.msg, event.style, event.icon)
    }

    private fun toModel(state: CRMChatListStore.State) =
        Model(
            query = state.query,
            groupType = state.groupType,
            currentFolderViewIsVisible = state.groupType != ConsultationGroupType.UNKNOWN,
            folderTitle = state.folderTitle,
            searchPanelIsOpen = state.searchPanelIsOpen,
            filters = state.filters,
            fabVisible = state.fabVisible
        )

    private fun CRMChatListStore.Label.consume() = when (this) {
        is CRMChatListStore.Label.ShowInformer -> crmChatListNotificationHelper.showSbisPopupNotification(
            message = this.msg,
            type = this.style,
            icon = this.icon
        )
        is CRMChatListStore.Label.OpenConsultation -> { router.openCRMConversation(this.consultationParams) }
        is CRMChatListStore.Label.CreateConsultation -> { router.openCRMConversation(this.consultationParams) }
        is CRMChatListStore.Label.OpenFilters -> { router.openFilters(this.filterModel) }
        is CRMChatListStore.Label.ChangeTakeOldestFabVisibility -> {
            if (!isHistoryMode) {
                swapFabVisibility(isVisible)
            }
            Unit
        }
    }

    private fun swapFabVisibility(show: Boolean) {
        fragment.activity?.castTo<BottomBarProvider>()?.swapFabButton(show)
    }

    private fun updateShowingPushNotification(needShow: Boolean) {
        messagesPushManager?.let {
            if (needShow) {
                it.executeAction(SubscribeOnNotification())
            } else {
                it.executeAction(UnsubscribeFromNotification())
            }
        }
    }

    private fun checkShowTakeOldestFab() {
        store.accept(CRMChatListStore.Intent.CheckShowTakeOldestFab(isTablet))
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            REQUEST -> {
                val model = result.getParcelableUniversally<CRMChatFilterModel>(RESULT_FILTER_MODEL)
                val names = result.getSerializableUniversally<ArrayList<String>>(RESULT_FILTER_NAMES)
                fragment.lifecycleScope.launch {
                    store.accept(
                        CRMChatListStore.Intent.ApplyFilter(model!!, names!!)
                    )
                }
            }
            CRM_CHANNEL_CONSULTATION_RESULT -> {
                val originUuid = result.getSerializableUniversally<UUID>(CRM_CHANNEL_ORIGIN_ID) as UUID
                val contactId = result.getSerializableUniversally<UUID>(CRM_CHANNEL_CONSULTATION_CONTACT_ID) as UUID
                val type = result.getSerializableUniversally<CrmChannelType>(CRM_CHANNEL_CONSULTATION_CHANNEL_TYPE) as CrmChannelType
                fragment.lifecycleScope.launch {
                    store.accept(
                        CRMChatListStore.Intent.CreateConsultation(
                            CRMConsultationCreationParams(
                                crmConsultationCase = CRMConsultationCase.Operator(
                                    originUuid = originUuid,
                                    viewId = filterHolder.viewId,
                                    isForReclamation = false,
                                    contactId = contactId,
                                    channelType = type
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    fun saveFilterState() {
        fragment.lifecycleScope.launch {
            crmChatFilterPreferences.saveState(filterHolder.getCurrentFilterModel(), filterHolder.getCurrentFilters())
        }
    }

    fun takeOldestConsultation() {
        store.accept(
            CRMChatListStore.Intent.TakeOldestConsultation(!isTablet || isHistoryMode)
        )
    }
}
