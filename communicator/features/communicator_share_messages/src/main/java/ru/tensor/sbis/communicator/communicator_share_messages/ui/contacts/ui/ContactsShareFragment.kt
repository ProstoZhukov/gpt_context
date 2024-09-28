package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.selection.SelectionDoneButtonVisibilityMode
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_share_messages.R
import ru.tensor.sbis.communicator.communicator_share_messages.ShareMessagesPlugin
import ru.tensor.sbis.communicator.communicator_share_messages.databinding.CommunicatorShareMessagesContactsBinding
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.di.ContactsShareComponent
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.di.DaggerContactsShareComponent
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.ContactsShareViewModel
import ru.tensor.sbis.communicator.communicator_share_messages.utils.ContactsInfoUtil
import ru.tensor.sbis.design.cloud_view.content.utils.DefaultMessageBlockTextHolder
import ru.tensor.sbis.design.cloud_view.model.DefaultCloudViewData
import ru.tensor.sbis.design_selection.contract.listeners.SelectionDelegate
import ru.tensor.sbis.design_selection.ui.main.utils.cloneWithSelectionTheme
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuContent
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuDelegate

/**
 * Экран шаринга недавним контактам в новый диалог.
 *
 * @author vv.chekurda
 */
internal class ContactsShareFragment : BaseFragment(),
    KeyboardEventListener,
    ShareMenuContent {

    companion object {

        /**
         * Создать новый инстанс экран шаринга недавним контактам.
         *
         * @param shareData данные, которыми делится пользователь.
         * @param quickShareKey ключ для быстрого шаринга.
         */
        fun newInstance(shareData: ShareData, quickShareKey: String? = null) =
            ContactsShareFragment().withArgs {
                putParcelable(CONTACTS_SHARE_DATA_KEY, shareData)
                putString(CONTACTS_QUICK_SHARE_KEY, quickShareKey)
            }
    }

    private lateinit var component: ContactsShareComponent
    private val viewModel: ContactsShareViewModel
        get() = component.viewModel

    private var _binding: CommunicatorShareMessagesContactsBinding? = null
    private val binding: CommunicatorShareMessagesContactsBinding
        get() = requireNotNull(_binding)

    private var isKeyboardShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        component = createComponent()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && component.quickShareKey == null) {
            showRecipientSelection()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        CommunicatorShareMessagesContactsBinding.inflate(
            inflater.cloneWithSelectionTheme(requireContext()),
            container,
            false
        ).also {
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        if (component.shareData is ShareData.Contacts) checkContactsPermission()
        if (savedInstanceState == null && component.quickShareKey != null) {
            binding.communicatorContactsShareMessagePanel.showKeyboard()
        }
    }

    private fun initViews() {
        initSelectionPanel()
        initSelectedPerson()
        initMessagePanel()
        initRecipientSelection()
        initSendingMessageText()
    }

    private fun initSelectionPanel() {
        val selectionPanel = binding.communicatorContactsShareSelectionPanel
        selectionPanel.init(component.selectedItemsAdapter)
        viewModel.multiSelectedItems.collectOnViewScope { selectedItems ->
            selectionPanel.setSelectedItems(selectedItems)
        }
        viewModel.selectionPanelVisibility.collectOnViewScope { isVisible ->
            selectionPanel.isInvisible = !isVisible
        }
    }

    private fun initSelectedPerson() {
        val selectedPerson = binding.communicatorContactsShareSinglePerson
        selectedPerson.changeSelectButtonVisibility(isVisible = false)
        viewModel.selectedPerson.collectOnViewScope { selectedItem ->
            if (selectedItem != null) {
                selectedPerson.isVisible = true
                selectedPerson.setData(selectedItem)
            } else {
                selectedPerson.isVisible = false
            }
        }
    }

    private fun initMessagePanel() {
        val messagePanel = binding.communicatorContactsShareMessagePanel
        val controller = component.messagePanelInitializer.initMessagePanel(
            messagePanel,
            component.coreFactory.createCoreConversation(conversationUuid = viewModel.conversationUuid.value)
        )
        viewModel.setMessagePanelController(controller)
        viewModel.messagePanelVisibility.collectOnViewScope { isVisible ->
            messagePanel.isVisible = isVisible
        }
        viewModel.showKeyboard.collectOnViewScope {
            messagePanel.showKeyboard()
            messagePanel.requestFocus()
        }
        viewModel.hideKeyboard.collectOnViewScope {
            messagePanel.hideKeyboard()
        }
        viewModel.conversationUuid.collectOnViewScope { uuid ->
            val coreInfo = component.coreFactory.createCoreConversation(conversationUuid = uuid)
            controller.setConversationInfo(coreInfo)
        }
    }

    private fun initRecipientSelection() {
        viewModel.recipientSelectionVisibility.collectOnViewScope { visibility ->
            binding.communicatorContactsShareContainer.visibility = visibility
        }
        viewModel.bottomOffset.collectOnViewScope { offset ->
            binding.communicatorContactsShareContainer.updatePadding(bottom = offset)
        }
    }

    private fun initSendingMessageText() {
        val cloudView = binding.communicatorContactsShareSendingMessage
        cloudView.setTextHolder(DefaultMessageBlockTextHolder())
        cloudView.textMaxLines = SENDING_MESSAGE_MAX_LINES
        viewModel.sendingMessage.collectOnViewScope { text ->
            cloudView.data = DefaultCloudViewData(text)
            cloudView.isVisible = text.isNotEmpty()
        }
    }

    private fun checkContactsPermission() {
        val hasPermission = ContactsInfoUtil.checkContactsPermission(requireContext())
        if (!hasPermission) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (!granted) requireActivity().finishAndRemoveTask()
            }.also {
                it.launch(android.Manifest.permission.READ_CONTACTS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setShareMenuDelegate(delegate: ShareMenuDelegate) {
        viewModel.setShareMenuController(delegate)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        binding.communicatorContactsShareMessagePanel.onKeyboardOpenMeasure(keyboardHeight)
        childFragmentManager.fragments.forEach {
            it.castTo<KeyboardEventListener>()?.onKeyboardOpenMeasure(keyboardHeight - viewModel.bottomOffset.value)
        }
        isKeyboardShown = binding.communicatorContactsShareMessagePanel.isVisible
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        binding.communicatorContactsShareMessagePanel.onKeyboardCloseMeasure(0)
        childFragmentManager.fragments.forEach {
            it.castTo<KeyboardEventListener>()?.onKeyboardCloseMeasure(0)
        }
        isKeyboardShown = false
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_KEYBOARD_SHOWN_KEY, isKeyboardShown)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        isKeyboardShown = savedInstanceState?.getBoolean(IS_KEYBOARD_SHOWN_KEY) ?: false
    }

    override fun onResume() {
        super.onResume()
        if (isKeyboardShown) binding.communicatorContactsShareMessagePanel.showKeyboard()
    }

    override fun onBackPressed(): Boolean =
        viewModel.onBackPressed()

    private fun showRecipientSelection() {
        val fragment = createRecipientSelectionFragment()
        childFragmentManager.beginTransaction()
            .add(R.id.communicator_contacts_share_container, fragment)
            .commitNow()
        childFragmentManager.fragments
            .find { it is SelectionDelegate.Provider }
            ?.castTo<SelectionDelegate.Provider>()
            ?.also { viewModel.setSelectionDelegate(it.getSelectionDelegate()) }
    }

    private fun createRecipientSelectionFragment(): Fragment {
        val selectionProvider = ShareMessagesPlugin.recipientSelectionProvider.get()
        selectionProvider.getRecipientSelectionResultManager().clear()
        return selectionProvider.getRecipientSelectionFragment(
            RecipientSelectionConfig(
                useCase = RecipientSelectionUseCase.ContactsShare,
                requestKey = CONTACTS_SHARE_REQUEST_KEY,
                doneButtonMode = SelectionDoneButtonVisibilityMode.AT_LEAST_ONE,
                canShowPersonCards = false,
                closeOnComplete = false
            )
        )
    }

    @Suppress("DEPRECATION")
    private fun createComponent(): ContactsShareComponent =
        DaggerContactsShareComponent.factory()
            .create(
                fragment = this,
                shareData = requireArguments().getParcelable<ShareData>(CONTACTS_SHARE_DATA_KEY) as ShareData,
                quickShareKey = requireArguments().getString(CONTACTS_QUICK_SHARE_KEY)
            ).also {
                it.viewModel
            }

    private fun <T> Flow<T>.collectOnViewScope(collector: FlowCollector<T>) {
        viewLifecycleOwner.lifecycleScope.launch {
            collect(collector)
        }
    }
}

private const val CONTACTS_QUICK_SHARE_KEY = "CONTACTS_QUICK_SHARE_KEY"
private const val CONTACTS_SHARE_DATA_KEY = "CONTACTS_SHARE_DATA_KEY"
private const val SENDING_MESSAGE_MAX_LINES = 2
private const val IS_KEYBOARD_SHOWN_KEY = "IS_KEYBOARD_SHOWN_KEY"
internal const val CONTACTS_SHARE_REQUEST_KEY = "CONTACTS_SHARE_REQUEST_KEY"