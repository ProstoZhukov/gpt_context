package ru.tensor.sbis.communicator.crm.conversation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationFeatureFacade.CRM_CONVERSATION_CHAT_PARAMS_KEY
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationFragment
import timber.log.Timber

/**
 * Activity чата CRM.
 *
 * @author da.zhukov
 */
class CRMConversationActivity : AdjustResizeActivity() {

    override fun getContentViewId(): Int {
        return R.id.communicator_crm_conversation_fragment_container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(ru.tensor.sbis.design.R.anim.nothing, ru.tensor.sbis.design.R.anim.nothing)
        setContentView(R.layout.communicator_crm_conversation_activity)
        if (savedInstanceState == null) {
            showConversationFragment(false)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            // Если бекстек опустошили - завершение
            if (!tryToSetLastFragmentBackSwipeAvailability(true)) {
                onViewGoneBySwipe()
            }
        }
    }

    override fun swipeBackEnabled(): Boolean = false

    private fun showConversationFragment(addToBackStack: Boolean) {
        @Suppress("DEPRECATION")
        val chatParams = intent.getParcelableExtra<CRMConsultationParams>(CRM_CONVERSATION_CHAT_PARAMS_KEY)
        if (chatParams == null) {
            Timber.e("Переписка открывается в обход API компонента CRMConversationActivity.")
            finish()
        } else {
            tryToSetLastFragmentBackSwipeAvailability(false)
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            if (addToBackStack) {
                fragmentTransaction.setCustomAnimations(
                    ru.tensor.sbis.design.R.anim.right_in,
                    ru.tensor.sbis.design.R.anim.right_out,
                    ru.tensor.sbis.design.R.anim.right_in,
                    ru.tensor.sbis.design.R.anim.right_out
                )
                    .addToBackStack(null)
            }
            fragmentTransaction.add(
                R.id.communicator_crm_conversation_fragment_container,
                CRMConversationFragment.createCRMConversationFragment(
                    chatParams
                ),
                CRMConversationFragment::class.java.simpleName
            ).commit()
        }
    }

    // Возвращает true, если удалось изменить свайпаемость у фрагмента в контейнере
    private fun tryToSetLastFragmentBackSwipeAvailability(isEnabled: Boolean): Boolean {
        val fragment: Fragment =
            supportFragmentManager.findFragmentById(R.id.communicator_crm_conversation_fragment_container) as? CRMConversationFragment
                ?: return false
        val conversationFragment: CRMConversationFragment = fragment as CRMConversationFragment
        conversationFragment.setBackSwipeAvailability(isEnabled)
        return true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        showConversationFragment(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            @Suppress("DEPRECATION")
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isRunning && !delegateBackPressToFragment()) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
        overridePendingTransition(ru.tensor.sbis.design.R.anim.nothing, ru.tensor.sbis.design.R.anim.right_out)
    }

    private fun delegateBackPressToFragment(): Boolean {
        val currentFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.communicator_crm_conversation_fragment_container)
        return currentFragment is FragmentBackPress && (currentFragment as? FragmentBackPress)?.onBackPressed() == true
    }
}