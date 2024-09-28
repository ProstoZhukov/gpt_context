package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.hostfragment

import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.crm.CRMChatListDefaultParams
import ru.tensor.sbis.communication_decl.crm.CRMChatListHostFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMChatListParams
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.router.CRM_CONVERSATION_FRAGMENT_TAG
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.getCurrentFragmentManager
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.getLastOpenedFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.handleBackPress
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.popCrmConversationFragmentFromBackStack
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.master_detail.MasterDetailFragment
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment

/**
 * Фрагмент для хостинга реестра чатов тех. поддержки.
 * Содержит в планшетной верстке details контейнер для отображения конкретного чата.
 *
 * @author dv.baranov
 */
internal class CRMChatListHostFragment :
    MasterDetailFragment(),
    FragmentBackPress,
    AdjustResizeHelper.KeyboardEventListener,
    DeeplinkActionNode {

    companion object : CRMChatListHostFragmentFactory {

        /**
         * Идентификатор переписки.
         * Передается, если при создании требуется открыть переписку.
         */
        private const val CRM_CHAT_LIST_PARAMS = "CRM_CHAT_LIST_PARAMS"

        override fun createCRMChatListHostFragment(crmChatListParams: CRMChatListParams): Fragment =
            CRMChatListHostFragment().withArgs {
                putSerializable(CRM_CHAT_LIST_PARAMS, crmChatListParams)
            }
    }

    private val crmChatListParams: CRMChatListParams by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(
                CRM_CHAT_LIST_PARAMS,
                CRMChatListParams::class.java
            ) ?: CRMChatListDefaultParams()
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getSerializable(CRM_CHAT_LIST_PARAMS) as CRMChatListParams
        }
    }

    private val scrollHelper by lazy { CRMChatListPlugin.commonSingletonComponentProvider.get().scrollHelper }

    override fun createMasterFragment(): Fragment = CRMChatListFragment.createCRMChatListFragment(crmChatListParams)

    override val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (!onBackPressed()) { activity?.finish() }
        }
    }

    override fun onBackPressed(): Boolean {
        val currentFragmentManager = getCurrentFragmentManager()
        val lastOpenedFragment = currentFragmentManager.getLastOpenedFragment()
        return when {
            lastOpenedFragment is CRMChatListHostFragment || lastOpenedFragment is CRMChatListFragment -> false
            lastOpenedFragment?.castTo<FragmentBackPress>()?.onBackPressed() == true -> true
            lastOpenedFragment is ContainerMovableDialogFragment -> lastOpenedFragment.handleBackPress()
            currentFragmentManager.backStackEntryCount == 0 -> false
            currentFragmentManager.backStackEntryCount > 1 &&
                lastOpenedFragment?.tag == CRM_CONVERSATION_FRAGMENT_TAG -> {
                currentFragmentManager.popCrmConversationFragmentFromBackStack()
                // для скрытия ННП при возвращении к первой открытой консультации(из реестра чатов).
                scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_DOWN_FAKE)
                true
            }
            isTablet && lastOpenedFragment?.tag == CRM_CONVERSATION_FRAGMENT_TAG -> false
            else -> currentFragmentManager.popBackStackImmediate()
        }
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        getFragmentsForKeyboardMeasure().forEach {
            (it as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardOpenMeasure(keyboardHeight)
        }
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        getFragmentsForKeyboardMeasure().forEach {
            (it as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardCloseMeasure(keyboardHeight)
        }
        return true
    }

    private fun getFragmentsForKeyboardMeasure(): List<Fragment> {
        val activityFragments = activity?.supportFragmentManager?.fragments
        val fragmentWithKeyboardEventListener = activityFragments?.findLast {
            (it as? AdjustResizeHelper.KeyboardEventListener) != null
        }
        return if (fragmentWithKeyboardEventListener != null) {
            activityFragments
        } else {
            getCurrentFragmentManager().fragments
        }
    }

    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        getCurrentFragmentManager().getLastOpenedFragment()?.castTo<DeeplinkActionNode>()?.onNewDeeplinkAction(args)
    }
}
