package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilterableContent
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_files.ui.ConversationInformationFilesContent
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.fab_options.ConversationInformationFabOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.LinkAdditionFragmentCreator
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.files_filter.ConversationInformationFilesFilterFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.files_filter.FILTER_SELECTION_RESULT_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.files_filter.SELECTED_FILTER_TYPES
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils.ConversationInformationMenuOptionHelper
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.createFragmentContainer
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenVerticalLocator
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.Item
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPicker
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.getThemeDimension
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.toast.showToast
import ru.tensor.sbis.folderspanel.PickNameDialogFragment
import ru.tensor.sbis.message_panel.helper.canAddRecipientForConversation
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment
import ru.tensor.sbis.mvi_extension.router.Router
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider
import java.util.UUID
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.design.R as RDesign

/**
 * Интерфейс роутера экрана .
 *
 * @author dv.baranov
 */
internal interface ConversationInformationRouter : Router<Fragment> {

    fun changeTransactionsAvailability(isAvailable: Boolean)

    fun navigateBack()

    fun openMenu(onOptionSelected: (option: ConversationInformationOption) -> Unit)

    fun changeTabContent(fragment: Fragment)

    fun clearTabContent()

    fun openFilterSelection(
        currentFilter: List<ConversationInformationFilter>,
        filterSelectionResultListener: (List<ConversationInformationFilter>) -> Unit
    )

    fun createFolder(folderName: String)

    fun startCall(participants: List<UUID>, isVideo: Boolean)

    fun copyLink(url: String)

    fun openParticipantSelection(recipients: List<UUID>)

    fun showProfile(profileUuid: UUID)

    fun showFabMenu(optionAction: (option: ConversationInformationFabOption) -> Unit)

    fun showFolderCreationDialog()

    fun showFilesPicker()

    fun openLinkAddition(themeUuid: UUID)

    fun addFiles(selectedFiles: List<SbisPickedItem>, compressImages: Boolean)
}

/**
 * Реализация роутера экрана .
 *
 * @author dv.baranov
 */
internal class ConversationInformationRouterImpl(
    private val recipientSelectionResultManager: RecipientSelectionResultManager,
    private val conversationInformationData: ConversationInformationData,
    private val personCardProvider: PersonCardProvider,
    private val filesPicker: SbisFilesPicker
) : FragmentRouter(), ConversationInformationRouter {

    private val containerId = R.id.communicator_conversation_information_tabs_content_container
    private val menuOptionHelper by lazy { ConversationInformationMenuOptionHelper(conversationInformationData) }

    private var missedTabSelection: Fragment? = null
    private var isTransactionsDisabled = false

    override fun changeTransactionsAvailability(isAvailable: Boolean) {
        isTransactionsDisabled = !isAvailable
        if (isAvailable) {
            missedTabSelection?.also {
                changeTabContent(it)
                missedTabSelection = null
            }
        }
    }

    override fun navigateBack() = execute {
        if (parentFragmentManager.backStackEntryCount > 0 &&
            parentFragmentManager.fragments.lastOrNull() is ConversationInformationFragment
        ) {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    override fun openMenu(onOptionSelected: (option: ConversationInformationOption) -> Unit) = execute {
        val toolbar = view?.findViewById<SbisTopNavigationView>(R.id.communicator_conversation_information_toolbar)
        val anchor = toolbar?.rightBtnContainer?.children?.lastOrNull()?.castTo<ViewGroup>()?.children?.firstOrNull()
            ?: toolbar?.rightBtnContainer ?: toolbar
        anchor?.let {
            showMenu(
                anchor = it,
                menuItems = menuOptionHelper.getMoreBtnOptions(requireContext(), onOptionSelected),
                verticalAlignment = VerticalAlignment.BOTTOM
            )
        }
    }

    override fun showFabMenu(optionAction: (option: ConversationInformationFabOption) -> Unit) = execute {
        val anchor = view?.findViewById<SbisRoundButton>(R.id.communicator_conversation_information_button_add)
        anchor?.let {
            showMenu(
                anchor = it,
                menuItems = menuOptionHelper.getFabBtnOptions(requireContext(), optionAction),
                verticalAlignment = VerticalAlignment.TOP
            )
        }
    }

    override fun showFolderCreationDialog() = execute {
        val dialogFragment = PickNameDialogFragment.newInstance(
            getString(RCommon.string.common_folder_new_dialog_title),
            StringUtils.EMPTY,
            getString(RCommon.string.common_folder_edit_dialog_edittext_hint)
        )
        dialogFragment.show(childFragmentManager, PickNameDialogFragment::class.java.canonicalName)
    }

    override fun showFilesPicker() = execute {
        filesPicker.show(
            childFragmentManager, setOf(
                SbisFilesPickerTab.Gallery(),
                SbisFilesPickerTab.Files(),
                SbisFilesPickerTab.Scanner()
            )
        )
    }

    override fun openLinkAddition(themeUuid: UUID) = execute {
        val horizontalLocator = ScreenHorizontalLocator()
        val verticalLocator = ScreenVerticalLocator()
        createFragmentContainer(
            LinkAdditionFragmentCreator(themeUuid)
        ).show(childFragmentManager, horizontalLocator, verticalLocator)
    }

    private fun Fragment.showMenu(
        anchor: View,
        menuItems: Iterable<Item>,
        verticalAlignment: VerticalAlignment
    ) {
        val sbisMenu = SbisMenu(children = menuItems)
        sbisMenu.showMenuWithLocators(
            fragmentManager = childFragmentManager,
            verticalLocator = AnchorVerticalLocator(
                alignment = verticalAlignment,
                force = false,
                offsetRes = requireContext().getThemeDimension(RDesign.attr.offset_l)
            ).apply { anchorView = anchor },
            horizontalLocator = AnchorHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT,
                force = false,
                innerPosition = true,
                offsetRes = requireContext().getThemeDimension(RDesign.attr.offset_m)
            ).apply { anchorView = anchor },
            dimType = DimType.SOLID
        )
    }

    override fun changeTabContent(fragment: Fragment) = execute {
        if (isTransactionsDisabled) {
            missedTabSelection = fragment
            return@execute
        }
        childFragmentManager.beginTransaction()
            .replace(containerId, fragment, CONVERSATION_INFORMATION_TAB_CONTENT_TAG)
            .commitNowAllowingStateLoss()
    }

    override fun clearTabContent() = execute {
        val fragment = childFragmentManager.findFragmentById(containerId) ?: return@execute
        childFragmentManager.beginTransaction()
            .remove(fragment)
            .commitNowAllowingStateLoss()
    }

    override fun openFilterSelection(
        currentFilter: List<ConversationInformationFilter>,
        filterSelectionResultListener: (List<ConversationInformationFilter>) -> Unit
    ) = execute {
        childFragmentManager.setFragmentResultListener(FILTER_SELECTION_RESULT_KEY, this) { _, result ->
            val filters = result.getParcelableArrayList<ConversationInformationFilter>(SELECTED_FILTER_TYPES)
            filters?.let {
                filterSelectionResultListener(it)
                view?.findViewById<FragmentContainerView>(containerId)
                    ?.getFragment<Fragment>()
                    ?.castTo<ConversationInformationFilterableContent>()
                    ?.setFilter(it)
            }
        }
        val contentCreator = ConversationInformationFilesFilterFragment.Creator(currentFilter)
        val fragment = getMovableDialogFragment(contentCreator, requireContext())
        fragment.showNow(childFragmentManager, CONVERSATION_INFORMATION_FILES_FILTERS_TAG)
    }

    override fun createFolder(folderName: String) = execute {
        view?.findViewById<FragmentContainerView>(containerId)
            ?.getFragment<Fragment>()
            ?.castTo<ConversationInformationFilesContent>()
            ?.createFolder(folderName)
    }

    override fun addFiles(selectedFiles: List<SbisPickedItem>, compressImages: Boolean) = execute {
        view?.findViewById<FragmentContainerView>(containerId)
            ?.getFragment<Fragment>()
            ?.castTo<ConversationInformationFilesContent>()
            ?.addFiles(selectedFiles, compressImages)
    }

    private fun getMovableDialogFragment(creator: ContentCreatorParcelable, context: Context) =
        ContainerMovableDialogFragment.Builder()
            .setContentCreator(creator)
            .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
            .setDefaultHeaderPaddingEnabled(false)
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            .setContainerBackgroundColor(BackgroundColor.DEFAULT.getValue(context))
            .setCloseOnShadowClick(true)
            .build()

    override fun startCall(participants: List<UUID>, isVideo: Boolean) = execute {
        context?.let {
            if (!NetworkUtils.isConnected(it)) {
                SbisPopupNotification.pushToast(
                    it,
                    ru.tensor.sbis.common.R.string.common_video_call_no_internet
                )
                return@let
            }
            when {
                participants.size == 1 -> {
                    themesRegistryDependency.callActivityProviderFeature?.performOutgoingCall(
                        participants.first().toString(),
                        isVideo
                    )
                }
                participants.size > 1 -> {
                    themesRegistryDependency.callActivityProviderFeature?.performMeetingOutgoingCall(
                        it,
                        conversationInformationData.conversationUuid.toString(),
                        isVideo,
                        participantsUuids = participants.map { uuid -> uuid.toString() }
                    )
                }
            }
        }
    }

    override fun copyLink(url: String) = execute {
        ClipboardManager.copyToClipboard(requireContext(), url)
        showToast(ru.tensor.sbis.communicator.design.R.string.communicator_link_copied, Toast.LENGTH_LONG)
    }

    override fun openParticipantSelection(recipients: List<UUID>) = execute {
        recipientSelectionResultManager.preselect(recipients)
        val recipientSelectionUseCase: RecipientSelectionUseCase = getSelectionUseCase(
            conversationUuid = conversationInformationData.conversationUuid,
            isNewConversation = conversationInformationData.isNewConversation,
            isChat = conversationInformationData.isChat,
            canAddParticipant = canAddRecipientForConversation(
                conversationInformationData.isChat,
                conversationInformationData.chatPermissions
            )
        )
        val intent = themesRegistryDependency.getRecipientSelectionIntent(
            requireContext(),
            RecipientSelectionConfig(recipientSelectionUseCase, requestKey = CONVERSATION_INFORMATION_SELECTION_RESULT)
        )
        startActivity(intent)
    }

    override fun showProfile(profileUuid: UUID) = execute {
        startActivity(personCardProvider.createPersonCardIntent(requireContext(), profileUuid))
    }

    private fun getSelectionUseCase(
        conversationUuid: UUID?,
        isNewConversation: Boolean,
        isChat: Boolean,
        canAddParticipant: Boolean,
        conversationType: ConversationType? = null,
        documentUuid: UUID? = null
    ): RecipientSelectionUseCase =
        when {
            isNewConversation || conversationType == ConversationType.VIDEO_CONVERSATION ||
                conversationUuid == null -> {
                RecipientSelectionUseCase.NewDialog
            }

            !isChat -> {
                RecipientSelectionUseCase.Dialog(
                    dialogUuid = conversationUuid,
                    documentUuid = documentUuid
                )
            }

            !canAddParticipant -> {
                RecipientSelectionUseCase.ChatParticipants(chatUuid = conversationUuid)
            }

            else -> {
                RecipientSelectionUseCase.Chat(chatUuid = conversationUuid)
            }
        }
}

private const val CONVERSATION_INFORMATION_TAB_CONTENT_TAG = "CONVERSATION_INFORMATION_TAB_CONTENT_TAG"
private const val CONVERSATION_INFORMATION_FILES_FILTERS_TAG = "CONVERSATION_INFORMATION_FILES_FILTERS_TAG"
internal const val CONVERSATION_INFORMATION_SELECTION_RESULT = "CONVERSATION_INFORMATION_SELECTION_RESULT"
