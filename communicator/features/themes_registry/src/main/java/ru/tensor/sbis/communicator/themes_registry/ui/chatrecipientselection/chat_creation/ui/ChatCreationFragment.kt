package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.ui

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.contract.ChatCreationPresenter
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.contract.ChatCreationView
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.di.ChatCreationComponent
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.di.DaggerChatCreationComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsFragment
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.presenter.CHAT_CREATION_RECIPIENT_SELECTION_REQUEST_KEY
import ru.tensor.sbis.deeplink.DeeplinkActionNode.Companion.EXTRA_DEEPLINK_ACTION
import ru.tensor.sbis.deeplink.OpenConversationDeeplinkAction
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.TabletContainerDialogFragment
import java.util.*

/**
 * Фрагмент создания нового чата.
 * @see ChatCreationView
 *
 * @author vv.chekurda
 */
internal class ChatCreationFragment : BasePresenterFragment<ChatCreationView, ChatCreationPresenter>(),
    ChatCreationView,
    ChatSettingsFragment.ResultListener,
    AdjustResizeHelper.KeyboardEventListener {

    companion object {

        /**@SelfDocumented*/
        fun newInstance(): Fragment = ChatCreationFragment()
    }

    private var component: ChatCreationComponent? = null

    private val isTablet
        get() = DeviceConfigurationUtils.isTablet(requireContext())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.communicator_fragment_chat_creation, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(presenter)
        savedInstanceState ?: showChatCreationRecipientSelection()
    }

    /**@SelfDocumented*/
    override fun closeChatCreation() {
        finish()
    }

    /**@SelfDocumented*/
    private fun showChatCreationRecipientSelection() {
        val config = RecipientSelectionConfig(
            useCase = RecipientSelectionUseCase.NewPrivateChat,
            requestKey = CHAT_CREATION_RECIPIENT_SELECTION_REQUEST_KEY,
            closeOnComplete = false
        )
        placeFragment(themesRegistryDependency.getRecipientSelectionFragment(config))
        prefetchFragment(
            ChatSettingsFragment.newInstance(true, null, true),
            CHAT_SETTINGS_FRAGMENT_TAG
        )
    }

    /**@SelfDocumented*/
    override fun showCreationChatSettings() {
        val chatSettingsFragment = childFragmentManager.findFragmentByTag(CHAT_SETTINGS_FRAGMENT_TAG)
        if (chatSettingsFragment != null) {
            showAddedFragment(chatSettingsFragment)
        } else {
            placeFragment(
                ChatSettingsFragment.newInstance(true, null, true)
            )
        }
    }

    private fun placeFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.communicator_fragment_container, fragment, fragment::class.java.simpleName)
            .commit()
    }

    private fun showAddedFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .show(fragment)
            .remove(childFragmentManager.fragments.first())
            .commit()
    }

    @Suppress("SameParameterValue")
    private fun prefetchFragment(fragment: Fragment, tag: String) {
        Looper.myQueue().addIdleHandler {
            childFragmentManager.beginTransaction()
                .add(R.id.communicator_fragment_container, fragment, tag)
                .hide(fragment)
                .commit()
            false
        }
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean =
        childFragmentManager.fragments
            .getOrNull(0)
            ?.castTo<AdjustResizeHelper.KeyboardEventListener>()
            ?.onKeyboardOpenMeasure(keyboardHeight)
            ?: true

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean =
        childFragmentManager.fragments
            .getOrNull(0)
            ?.castTo<AdjustResizeHelper.KeyboardEventListener>()
            ?.onKeyboardCloseMeasure(0)
            ?: true

    /**@SelfDocumented*/
    override fun onResultOk(uuid: UUID) {
        presenter.onResultOk(uuid)
    }

    /**@SelfDocumented*/
    override fun onResultCancel() {
        presenter.onResultCancel()
    }

    /**
     * Открыть приватный чат
     *
     * @param recipient uuid пользователя, с которым будем открыт личный чат
     */
    override fun openPrivateChat(recipient: UUID) {
        if (isTablet) {
            themesRegistryDependency.getMainActivityIntent()
                .putExtra(
                    EXTRA_DEEPLINK_ACTION,
                    OpenConversationDeeplinkAction(recipients = arrayListOf(recipient), isChat = true)
                )
                .let(::startActivity)
            finish()
        } else {
            placeFragment(
                createConversationFragment(participantsUuids = arrayListOf(recipient))
            )
        }
    }

    /**
     * Открыть новый групповой чат
     *
     * @param chatUuid uuid новосозданного чата
     */
    override fun openNewGroupChat(chatUuid: UUID) {
        if (isTablet) {
            themesRegistryDependency.getMainActivityIntent()
                .putExtra(
                    EXTRA_DEEPLINK_ACTION,
                    OpenConversationDeeplinkAction(dialogUuid = chatUuid, isChat = true)
                )
                .let(::startActivity)
            finish()
        } else {
            placeFragment(createConversationFragment(chatUuid = chatUuid))
        }
    }

    private fun createConversationFragment(
        chatUuid: UUID? = null,
        participantsUuids: ArrayList<UUID>? = null
    ) =
        themesRegistryDependency.getConversationFragment(
            dialogUuid = chatUuid,
            participantsUuids = participantsUuids,
            isChat = true,
            messageUuid = null,
            folderUuid = null,
            files = null,
            text = null,
            document = null,
            type = null,
            archivedDialog = false
        )

    private fun finish() {
        val safeContext = context ?: return
        if (DeviceConfigurationUtils.isTablet(safeContext)) {
            parentFragment?.castTo<TabletContainerDialogFragment>()?.closeContainer()
        } else {
            activity?.finish()
        }
    }

    override fun inject() {
        component = DaggerChatCreationComponent.builder()
            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
            .build()
    }

    override fun createPresenter(): ChatCreationPresenter =
        component!!.chatCreationPresenter

    override fun getPresenterView() = this

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(presenter)
        component = null
    }
}

private const val CHAT_SETTINGS_FRAGMENT_TAG = "CHAT_SETTINGS_FRAGMENT_TAG"