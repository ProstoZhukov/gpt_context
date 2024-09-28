package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.rx2.asFlow
import ru.tensor.sbis.clients_feature.ClientsViewParams
import ru.tensor.sbis.clients_feature.data.CRMClientAttribute
import ru.tensor.sbis.clients_feature.data.ClientsSearchColorType
import ru.tensor.sbis.clients_feature.data.NavigationDisplayMode
import ru.tensor.sbis.clients_registry.ClientsListConfig
import ru.tensor.sbis.clients_registry.CrmRootFolder
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin.clientsFeatureProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin.recipientSelectionFeatureProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.CRMConnectionListFragment
import ru.tensor.sbis.communicator.declaration.crm.model.CRMOpenableFilterType
import ru.tensor.sbis.design_selection.contract.listeners.SelectionDelegate
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase

/**
 * Роутер экрана фильтра.
 *
 * @author da.zhukov
 */
internal class CrmChatFilterRouter(
    private val crmChatFilterController: CrmChatFilterController
): FragmentRouter() {

    private val selectionFragment = recipientSelectionFeatureProvider.get().getRecipientSelectionFragment(
        RecipientSelectionConfig(
            useCase = RecipientSelectionUseCase.CRMEmployees,
            requestKey = CRM_FILTER_SELECTION_REQUEST_KEY,
            themeRes = R.style.CommunicatorCrmChatFilterSelectionTheme
        )
    )

    private val containerId = R.id.communicator_crm_chat_list_filter_container_id

    fun onOpen(type: CRMOpenableFilterType) = execute {
        val fragment = when (type) {
            CRMOpenableFilterType.RESPONSIBLE -> selectionFragment
            CRMOpenableFilterType.CLIENT -> {
                clientsFeatureProvider.get().getClientsListFragmentMultiSelection(
                    listConfig = ClientsListConfig(
                        rootFolder = CrmRootFolder.Clients,
                        shouldShowFiltering = false
                    ),
                    alreadySelectedClients = crmChatFilterController.getCurrentFilter().clientIds.first.map {
                        CRMClientAttribute.Uuid(it)
                    },
                    viewParams = ClientsViewParams(
                        navigationDisplayMode = NavigationDisplayMode.CURRENT_FOLDER,
                        needHideOnScrollSearchPanel = false,
                        searchColorType = ClientsSearchColorType.ADDITIONAL
                    ),
                    useCase = SelectionStatisticUseCase.CRM_CHAT_FILTER
                )
            }
            CRMOpenableFilterType.SOURCE -> {
                CRMConnectionListFragment.newInstance(crmChatFilterController.getCurrentFilter().sourceIds.first)
            }
            CRMOpenableFilterType.CHANNEL -> {
                CRMChannelsFragment.createCrmChannelListFragment(
                    CrmChannelListCase.CrmChannelFilterCase(
                        currentFilter = crmChatFilterController.getCurrentFilter().channelIds
                    )
                )
            }
        }
        addChildFragmentWithBackStack(fragment, containerId)
        if (type == CRMOpenableFilterType.RESPONSIBLE) {
            subscribeToSelectionResult(childFragmentManager)
        }
    }

    private fun subscribeToSelectionResult(fragmentManager: FragmentManager) {
        fragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    v: View,
                    savedInstanceState: Bundle?
                ) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                    if (f is SelectionDelegate.Provider) {
                        crmChatFilterController.subscribeToSelectionResult(f.getSelectionDelegate().selectedItemsWatcher.asFlow())
                    }
                }
            }, false
        )
    }

    fun back() = execute {
        if (!popBackStackChildFragmentIfNeed()) {
            (parentFragment as? ContainerMovableDialogFragment)?.dismissAllowingStateLoss()
        }
    }
}

private const val CRM_FILTER_SELECTION_REQUEST_KEY = "CRM_FILTER_SELECTION_REQUEST_KEY"