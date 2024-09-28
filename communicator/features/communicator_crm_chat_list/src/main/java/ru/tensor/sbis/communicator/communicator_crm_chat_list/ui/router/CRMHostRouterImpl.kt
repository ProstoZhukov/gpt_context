package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.router

import android.content.Intent
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin.crmConversationFeature
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.getCurrentFragmentManager
import ru.tensor.sbis.communicator.declaration.crm.CRMHostRouter
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.master_detail.DetailFragmentManager
import ru.tensor.sbis.mvi_extension.router.Router
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import ru.tensor.sbis.common.R as RCommon

/**
 * Хост роутер для чатов тех. поддержки.
 *
 * @author dv.baranov
 */
internal class CRMHostRouterImpl :
    CRMHostRouter,
    FragmentRouter(),
    Router<Fragment> {

    companion object : CRMHostRouter.Provider {

        override fun getCRMHostRouter() = CRMHostRouterImpl()
    }

    private val crmConversationFragmentFactory: CRMConversationFragmentFactory = crmConversationFeature.get()
    private var isMaxCountOfTransitions: Boolean = false
    private val overlayDetailContainerId: Int = RCommon.id.overlay_container
    private val masterContainerId: Int = R.id.crm_communicator_master_fragment_container

    override fun openCRMConversation(params: CRMConsultationParams) {
        val fragment = crmConversationFragmentFactory.createCRMConversationFragment(params)
        showChildFragment(fragment, CRM_CONVERSATION_FRAGMENT_TAG)
    }

    private fun showChildFragment(fragment: Fragment, tag: String) = execute {
        if (isTablet) {
            showChildFragmentInDetailsContainer(fragment, tag)
        } else {
            showChildFragmentOverlay(fragment, tag)
        }
    }

    private fun showChildFragmentInDetailsContainer(fragment: Fragment, tag: String) = execute {
        popBackStackIfNeed(getCurrentFragmentManager(), tag)
        this.castTo<DetailFragmentManager>()?.apply {
            showDetailFragment(fragment, tag = tag, popPreviousFromBackStack = false)
        } ?: showDetailFragmentWithNoSpecifiedContainer(fragment, tag)
    }

    // В сценариях, где мы открываем реестр чатов на планшете в detail container-е, приходится искать контейнер,
    // в котором он открылся, чтобы там же открывать остальные экраны (переписка, переназначения и т.д.).
    private fun showDetailFragmentWithNoSpecifiedContainer(fragment: Fragment, tag: String) = execute {
        val crmChatListFragment = getCurrentFragmentManager().fragments.find { it is CRMChatListFragment }
        crmChatListFragment?.let {
            it.view?.parent?.castTo<ViewGroup>()?.id?.let { containerId ->
                addChildFragmentWithBackStack(fragment, containerId, tag)
            }
        }
    }

    private fun showChildFragmentOverlay(fragment: Fragment, tag: String) = execute {
        val container = this.activity?.findViewById<FragmentContainerView>(overlayDetailContainerId)
        val supportFragmentManager = this.activity?.supportFragmentManager
        if (container != null && supportFragmentManager != null) {
            popBackStackIfNeed(supportFragmentManager, tag)
            supportFragmentManager
                .beginTransaction()
                .add(overlayDetailContainerId, fragment, tag)
                .addToBackStack(tag)
                .commit()
        } else {
            popBackStackIfNeed(getCurrentFragmentManager(), tag)
            addChildFragmentWithBackStack(fragment, masterContainerId, tag)
        }
    }

    override fun initRouter(fragment: Fragment, isHistoryMode: Boolean) {
        val hostFragment = fragment.getHostFragment(isHistoryMode)
        val navigatorFragment = hostFragment ?: fragment
        attachNavigator(WeakLifecycleNavigator(navigatorFragment))
        this.isMaxCountOfTransitions = isHistoryMode
    }

    private fun Fragment.getHostFragment(isHistoryMode: Boolean): Fragment? = when {
        isTablet && isHistoryMode -> parentFragment?.parentFragment
        isTablet -> parentFragment
        else -> null
    }

    private fun popBackStackIfNeed(fragmentManager: FragmentManager, tag: String) {
        if (tag != CRM_CONVERSATION_FRAGMENT_TAG) return
        // Мы можем открывать консультации из шторки и из обычного реестра.
        // Для шторки нам нужно хранить фрагмент корневой консультации, к которой мы вернемся
        // в случае обработки onBackPressed.
        val maxFragmentsCount = if (isMaxCountOfTransitions) {
            MAX_COUNT_OF_TRANSITIONS_IN_HISTORY_VIEW
        } else {
            DEFAULT_COUNT_OF_TRANSITIONS
        }
        // Необходимо для возвращения к первой открытой консультации(из реестра чатов),
        // после навигации в другие консультации через шторку истории
        var countOfOpenedConversations = 0
        for (i in 0 until fragmentManager.backStackEntryCount) {
            if (fragmentManager.getBackStackEntryAt(i).name == CRM_CONVERSATION_FRAGMENT_TAG) {
                countOfOpenedConversations += 1
            }
        }
        for (i in 0..countOfOpenedConversations - maxFragmentsCount) {
            fragmentManager.popBackStack()
        }
    }

    override fun openNextConsultation(chatParams: CRMConsultationParams) = execute {
        openCRMConversation(chatParams)
    }

    override fun openFilters(filterModel: CRMChatFilterModel) = execute {
        CrmChatFilterFragment.newInstance(filterModel)
            .show(getCurrentFragmentManager(), CRM_FILTER_FRAGMENT_TAG)
    }

    override fun openContentScreen(fragment: Fragment, tag: String) {
        showChildFragment(fragment, tag)
    }

    override fun openContentScreenInActivity(intent: Intent) = execute {
        requireContext().startActivity(intent)
    }
}

internal const val CRM_CONVERSATION_FRAGMENT_TAG = "CRM_CONVERSATION_FRAGMENT_TAG"
private const val CRM_FILTER_FRAGMENT_TAG = "CRM_FILTER_FRAGMENT_TAG"
private const val DEFAULT_COUNT_OF_TRANSITIONS = 1
private const val MAX_COUNT_OF_TRANSITIONS_IN_HISTORY_VIEW = 2
