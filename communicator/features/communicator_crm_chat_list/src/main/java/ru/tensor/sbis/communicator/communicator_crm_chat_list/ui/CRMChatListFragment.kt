package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.crm.CRMChatListClientsParams
import ru.tensor.sbis.communication_decl.crm.CRMChatListDefaultParams
import ru.tensor.sbis.communication_decl.crm.CRMChatListFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMChatListHistoryParams
import ru.tensor.sbis.communication_decl.crm.CRMChatListParams
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_CONSULTATION_RESULT
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin.commonSingletonComponentProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.di.DaggerCRMChatListComponent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.router.CRM_CONVERSATION_FRAGMENT_TAG
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.CRMDeeplinkActionHandler
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.getCurrentFragmentManager
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.getLastOpenedFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.handleBackPress
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.popCrmConversationFragmentFromBackStack
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.design.buttons.base.models.style.NavigationButtonStyle
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.main_screen_decl.fab.IconFab
import ru.tensor.sbis.main_screen_decl.fab.addActionButtons
import ru.tensor.sbis.master_detail.SelectionHelper
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment
import java.util.UUID

/**
 * Фрагмент реестра чатов тех. поддержки - CRM.
 *
 * @author da.zhukov
 */
internal class CRMChatListFragment :
    BaseFragment(),
    FragmentBackPress,
    DeeplinkActionNode,
    CRMDeeplinkActionHandler {

    companion object : CRMChatListFragmentFactory {
        /**
         * Запрос результата.
         */
        const val REQUEST = "REQUEST_FILTER"

        /**
         * Ключ для доступа к результату.
         */
        const val RESULT_FILTER_MODEL = "RESULT_FILTER_MODEL"

        /**
         * Ключ для доступа к результату.
         */
        const val RESULT_FILTER_NAMES = "RESULT_FILTER_NAMES"

        private const val CRM_CHAT_LIST_PARAMS_KEY = "CRM_CHAT_LIST_PARAMS_KEY"

        override fun createCRMChatListFragment(crmChatListParams: CRMChatListParams): Fragment {
            return CRMChatListFragment().withArgs {
                putSerializable(CRM_CHAT_LIST_PARAMS_KEY, crmChatListParams)
            }
        }
    }

    override val deeplinkActionFlow: MutableSharedFlow<DeeplinkAction> = MutableSharedFlow()

    private val chatParams: CRMChatListParams
        get() = arguments?.getSerializable(CRM_CHAT_LIST_PARAMS_KEY) as CRMChatListParams

    private val isHistoryMode: Boolean
        get() = chatParams is CRMChatListHistoryParams

    private val isClientsMode: Boolean
        get() = chatParams is CRMChatListClientsParams

    private val consultationUuid: UUID?
        get() = if (chatParams is CRMChatListDefaultParams) {
            (chatParams as CRMChatListDefaultParams).consultationUuid
        } else {
            null
        }

    private var controller: CRMChatListController? = null
    private var listComponentView: ListComponentView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerCRMChatListComponent.factory().create(
            scope = this.lifecycleScope,
            listDateFormatter = ListDateFormatter.DateTimeWithoutTodayStandard(requireContext()),
            viewModelStoreOwner = this,
            crmChatListNotificationHelper = CRMChatListNotificationHelper(requireContext()),
            crmChatListParams = chatParams,
            commonSingletonComponent = commonSingletonComponentProvider.get()
        ).also {
            controller = it.injector().inject(this, it.viewFactory, this, isHistoryMode, consultationUuid)
            if (!isHistoryMode) {
                parentFragment?.getCurrentFragmentManager()?.setFragmentResultListener(
                    REQUEST,
                    this@CRMChatListFragment,
                    controller!!
                )
            }
            requireActivity().supportFragmentManager.setFragmentResultListener(
                CRM_CHANNEL_CONSULTATION_RESULT,
                this@CRMChatListFragment,
                controller!!
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView = inflater.setTheme(isHistoryMode).inflate(
            R.layout.communicator_crm_chat_list_fragment,
            container,
            false
        )
        listComponentView = mainView.findViewById(R.id.communicator_crm_chat_list)

        initTopNavigationView(mainView)
        prepareSearchView(mainView)
        initFab(mainView.context)

        return mainView
    }

    private fun initTopNavigationView(mainView: View) {
        val topNavigationView = mainView.findViewById<SbisTopNavigationView>(R.id.communicator_crm_chat_list_top_navigation_view)
        when {
            isClientsMode -> {
                topNavigationView.apply {
                    val title = (chatParams as CRMChatListClientsParams).clientName ?: StringUtils.EMPTY
                    content = SbisTopNavigationContent.SmallTitle(
                        title = PlatformSbisString.Value(title),
                        subtitle = PlatformSbisString.Res(R.string.communicator_crm_chats_tab_title)
                    )
                    showBackButton = true
                    isEditingEnabled = false
                    smallTitleMaxLines = TOP_NAVIGATION_SMALL_TITLE_MAX_LINE
                    backBtn?.setOnClickListener {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
            isHistoryMode -> {
                topNavigationView.isVisible = false
            }
            else -> {
                topNavigationView.apply {
                    content = SbisTopNavigationContent.LargeTitle(
                        PlatformSbisString.Res(R.string.communicator_crm_chats_tab_title),
                        navxId = NavxId.CLAIM_CHATS
                    )
                    showBackButton = false
                }
            }
        }
    }

    private fun prepareSearchView(mainView: View) {
        val searchView = mainView.findViewById<SearchInput>(R.id.communicator_crm_chat_list_search_input)
        when {
            isClientsMode -> searchView.setHasFilter(false)
            isHistoryMode -> searchView.isVisible = false
        }
    }

    private fun initFab(context: Context) {
        if (isHistoryMode) return
        addActionButtons(
            fragment = this,
            IconFab(
                icon = TakeOldestDrawable(context),
                style = NavigationButtonStyle
            ) {
                controller?.takeOldestConsultation()
            },
            isSplitViewOnTablet = isTablet
        )
    }

    override fun onStop() {
        if (!isClientsMode) controller?.saveFilterState()
        super.onStop()
    }

    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        lifecycleScope.launch {
            deeplinkActionFlow.emit(args)
        }
    }

    override fun onBackPressed(): Boolean {
        val currentFragmentManager = getCurrentFragmentManager()
        val lastOpenedFragment = currentFragmentManager.getLastOpenedFragment()
        return when {
            lastOpenedFragment is CRMChatListFragment || lastOpenedFragment == parentFragment -> false
            lastOpenedFragment?.castTo<FragmentBackPress>()?.onBackPressed() == true -> true
            lastOpenedFragment is ContainerMovableDialogFragment -> lastOpenedFragment.handleBackPress()
            currentFragmentManager.backStackEntryCount == 0 -> false
            currentFragmentManager.backStackEntryCount > 1 &&
                lastOpenedFragment?.tag == CRM_CONVERSATION_FRAGMENT_TAG -> {
                currentFragmentManager.popCrmConversationFragmentFromBackStack()
                true
            }
            isTablet && lastOpenedFragment?.tag == CRM_CONVERSATION_FRAGMENT_TAG -> false
            else -> currentFragmentManager.popBackStackImmediate()
        }
    }

    private fun LayoutInflater.setTheme(isHistoryMode: Boolean): LayoutInflater {
        val themeWrapper = if (isHistoryMode) {
            ContextThemeWrapper(activity, R.style.CRMChatListHistoryListTheme)
        } else {
            ContextThemeWrapper(activity, R.style.CRMChatListDefaultListTheme)
        }
        return cloneInContext(themeWrapper)
    }
}
private const val TOP_NAVIGATION_SMALL_TITLE_MAX_LINE = 1
