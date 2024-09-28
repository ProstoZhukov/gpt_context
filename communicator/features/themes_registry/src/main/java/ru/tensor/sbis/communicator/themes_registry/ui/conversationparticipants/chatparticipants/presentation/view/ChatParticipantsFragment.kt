package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.view

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.base_components.fragment.HideKeyboardOnScrollListener
import ru.tensor.sbis.common.util.getActivityAs
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationSearchableContent
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_INFO_SELECTION_RESULT_KEY
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_INFO_SELECTION_RESULT_UUID_KEY
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_CHAT_PERMISSION_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_UUID_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.KEY_NEW_INFORMATION_CONVERSATION_SCREEN
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_CHAT_PERMISSIONS
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_DIALOG_UUID
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsResultListener
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.ThemeParticipantsAdapter
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract.ChatParticipantsViewContract
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.di.ChatParticipantsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.di.DaggerChatParticipantsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.router.ChatParticipantsRouterImpl
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.viewmodel.ChatParticipantsViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.getViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.PinnedHeaderViewHelper
import ru.tensor.sbis.design.utils.extentions.updateTopMargin
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager
import ru.tensor.sbis.mvp.search.BaseSearchableView
import java.util.UUID
import javax.inject.Inject
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.view.input.R as RInputView

/**
 * Фрагмент участников чата
 */
internal class ChatParticipantsFragment :
    BaseSearchableView<ThemeParticipantListItem,
        ThemeParticipantsAdapter,
        ChatParticipantsViewContract.View,
        ChatParticipantsViewContract.Presenter>(),
    ChatParticipantsViewContract.View,
    ConversationInformationSearchableContent {

    companion object {
        fun newInstance(args: Bundle?, folder: ThemeParticipantListItem.ThemeParticipantFolder? = null): ChatParticipantsFragment {
            val fragment = ChatParticipantsFragment()
            fragment.arguments = (args?.let { Bundle(it) } ?: Bundle()).apply {
                folder?.let {
                    putSerializable(OPENED_FOLDER_UUID_KEY, folder.uuid)
                    putString(OPENED_FOLDER_NAME_KEY, folder.name)
                }
            }
            return fragment
        }
    }

    private lateinit var sbisToolbar: SbisTopNavigationView

    private val isFromConversationInfo: Boolean
        get() = arguments?.getBoolean(KEY_NEW_INFORMATION_CONVERSATION_SCREEN, false) ?: false

    private val component: ChatParticipantsComponent by lazy {
        val componentBuilder = DaggerChatParticipantsComponent.builder()
            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
            .communicatorDialogChatDependency(themesRegistryDependency)
            .viewModel(
                getViewModel<ChatParticipantsViewModel>().apply {
                    folderUUID = arguments?.getSerializable(OPENED_FOLDER_UUID_KEY) as? UUID
                }
            )

        arguments?.run {
            if (containsKey(KEY_DIALOG_UUID)) componentBuilder.conversationUuid(getSerializable(KEY_DIALOG_UUID) as UUID)
            if (containsKey(KEY_CHAT_PERMISSIONS)) componentBuilder.chatPermissions(getParcelable(KEY_CHAT_PERMISSIONS)!!)
            if (containsKey(CONVERSATION_UUID_KEY)) {
                componentBuilder.conversationUuid(getSerializable(CONVERSATION_UUID_KEY) as UUID)
            }
            if (containsKey(CONVERSATION_CHAT_PERMISSION_KEY)) {
                componentBuilder.chatPermissions(getParcelable(CONVERSATION_CHAT_PERMISSION_KEY)!!)
            }
        }

        componentBuilder.router(ChatParticipantsRouterImpl(this.parentFragmentManager, R.id.communicator_chat_participants_content_container))

        componentBuilder.build()
    }

    private var keyboardHeight: Int = 0

    /** @SelfDocumented */
    override fun getLayoutRes(): Int = R.layout.communicator_fragment_conversation_participants_list

    /** @SelfDocumented */
    override fun getPresenterView(): ChatParticipantsViewContract.View = this

    /** @SelfDocumented */
    override fun createPresenter(): ChatParticipantsViewContract.Presenter = component.getPresenter()

    /** @SelfDocumented */
    override fun inject() = component.inject(this)

    /** @SelfDocumented */
    fun refreshParticipantsList() {
        presenter.onRefresh()
    }

    /** @SelfDocumented */
    @Inject
    fun setAdapter(adapter: ThemeParticipantsAdapter) {
        mAdapter = adapter.apply {
            onItemClick = presenter::onItemClick
            onItemPhotoClick = presenter::onItemPhotoClick
            onChangeAdminStatusClick = presenter::onChangeAdminStatusClick
            onRemoveParticipantClick = presenter::onRemoveParticipantClick
            onFolderItemClick = presenter::onFolderClick
        }
    }

    /** @SelfDocumented */
    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        super.initViews(mainView, savedInstanceState)

        mSearchPanel.isVisible = !isFromConversationInfo

        initToolbar(mainView)
        initFolderView(mainView)
        initList(mainView)
    }

    override fun showControls() {
        // ignore
    }

    override fun hideControls() = presenter.onKeyboardClosed(true)

    private fun initToolbar(mainView: View) {
        val rightButton = SbisButton(mainView.context).apply {
            size = SbisButtonSize.S
            style = SbisButtonCustomStyle(
                iconStyle = SbisButtonIconStyle(
                    ColorStateList.valueOf(
                        context.getThemeColorInt(RDesign.attr.secondaryIconColor)
                    )
                )
            )
            setIcon(SbisMobileIcon.Icon.smi_navBarPlus)
            backgroundType = SbisButtonBackground.Transparent
            setOnClickListener { presenter.onAddClick() }
        }
        sbisToolbar = mainView.findViewById<SbisTopNavigationView>(R.id.communicator_dialog_participants_toolbar).apply {
            content = SbisTopNavigationContent.SmallTitle(
                PlatformSbisString.Res(
                    RCommunicatorDesign.string.communicator_channel_conversation_participants
                )
            )
            backBtn?.apply {
                setText(RDesign.string.design_mobile_icon_toolbar_close)
                setOnClickListener { requireActivity().finish() }
            }
            rightItems = listOf(rightButton)
            isVisible = !isFromConversationInfo
        }
    }

    private fun initFolderView(mainView: View) {
        val folderView = mainView.findViewById<CurrentFolderView>(R.id.communicator_dialog_participants_folder)
        val appBar = mainView.findViewById<AppBarLayout>(R.id.communicator_dialog_participants_appbar)
        if (component.getViewModel().folderUUID != null) {
            folderView.isVisible = true
            folderView.setTitle(this.arguments?.getString(OPENED_FOLDER_NAME_KEY) ?: "")
            folderView.setOnClickListener {
                backFromFolder()
            }
            appBar.addOnOffsetChangedListener(
                PinnedHeaderViewHelper(
                    folderView,
                    null,
                    { margin -> mSbisListView?.updateTopMargin(margin) },
                    null
                )
            )
        } else {
            folderView.isVisible = false
        }
    }

    private fun backFromFolder(): Boolean {
        updateDataListOfChatParticipantsFragments()
        return component.getRouter().back()
    }

    private fun updateDataListOfChatParticipantsFragments() {
        parentFragmentManager.fragments.filterIsInstance<ChatParticipantsFragment>().forEach {
            it.presenter.onRefresh()
        }
    }

    private fun initList(mainView: View) {
        mSbisListView = mainView.findViewById(R.id.communicator_dialog_participants_list)
        mSbisListView?.run {
            setSwipeColorSchemeResources(context.getThemeColor(androidx.appcompat.R.attr.colorAccent))
            setHasFixedSize(true)
            setSwipeProgressViewOffset(true, 0, resources.getDimensionPixelSize(RInputView.dimen.input_view_search_padding_top))
            setLayoutManager(PaginationLayoutManager(requireContext()))
            addOnScrollListener(HideKeyboardOnScrollListener())
            mAdapter.setSwipePossible(true)
            setAdapter(mAdapter)
            recyclerView.itemAnimator?.apply {
                addDuration = 0
                changeDuration = 0
                moveDuration = 0
                castTo<SimpleItemAnimator>()?.supportsChangeAnimations = false
            }
        }
    }

    /** @SelfDocumented */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(component.getViewModel()) {
            currentUserUuid.observe(viewLifecycleOwner) { mAdapter.setCurrentUserUuid(it!!) }

            chatPermissions.observe(viewLifecycleOwner) {
                sbisToolbar.rightBtnContainer?.isVisible = it!!.canAddParticipant
                mAdapter.setSwipeEnabled(it.canChangeAdministrators)
            }

            isSwipeEnabled.observe(viewLifecycleOwner) { mAdapter.setSwipeEnabled(it!!) }
        }
    }

    /** @SelfDocumented */
    override fun onResume() {
        super.onResume()
        KeyboardUtils.hideKeyboard(mSearchPanel)
    }

    /** @SelfDocumented */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mAdapter.onSavedInstanceState(outState)
    }

    /** @SelfDocumented */
    override fun restoreStateFromBundle(savedInstanceState: Bundle?) {
        super.restoreStateFromBundle(savedInstanceState)
        mAdapter.onRestoreInstanceState(savedInstanceState!!)
    }

    /** @SelfDocumented */
    override fun openProfile(profileUuid: UUID) {
        safeKeyboard { context ->
            val intent = themesRegistryDependency
                .createPersonCardIntent(context, profileUuid)
            context.startActivity(intent)
        }
    }

    /** @SelfDocumented */
    override fun showRecipientsSelection(uuid: UUID) {
        safeKeyboard { context ->
            val intent = themesRegistryDependency.getRecipientSelectionIntent(
                context,
                RecipientSelectionConfig(
                    useCase = RecipientSelectionUseCase.AddChatParticipants(uuid)
                )
            )
            startActivity(intent)
        }
    }

    private fun safeKeyboard(action: (context: Context) -> Unit) {
        if (keyboardHeight > 0) {
            hideKeyboard()
            view?.postDelayed({
                action(context ?: return@postDelayed)
            }, SAFE_KEYBOARD_NAVIGATION_DELAY_MS)
        } else {
            action(requireContext())
        }
    }

    /** @SelfDocumented */
    override fun finishWithUuidResult(profileUuid: UUID) {
        val activity = getActivityAs<ConversationParticipantsResultListener>()
        when {
            activity != null -> activity.onResultOk(profileUuid)
            parentFragment != null -> {
                val bundle = Bundle().apply {
                    putSerializable(CONVERSATION_INFO_SELECTION_RESULT_UUID_KEY, profileUuid)
                }
                requireParentFragment().parentFragmentManager.setFragmentResult(
                    CONVERSATION_INFO_SELECTION_RESULT_KEY,
                    bundle
                )
            }
            else -> throw ClassCastException(
                context.toString() + " should implement " + ConversationParticipantsResultListener::class.java.simpleName
            )
        }
    }

    /** @SelfDocumented */
    override fun showToast(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") @StringRes message: Int) {
        showToast(message, Toast.LENGTH_LONG)
    }

    override fun onFolderClick(folder: ThemeParticipantListItem.ThemeParticipantFolder) {
        component.getRouter().openFolder(requireParentFragment().arguments, folder)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        val statusBarHeight = StatusBarHelper.getStatusBarHeight(requireContext())
        mSbisListView?.setPadding(0, 0, 0, keyboardHeight - statusBarHeight)
        this.keyboardHeight = keyboardHeight
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        mSbisListView?.setPadding(0, 0, 0, 0)
        this.keyboardHeight = 0
        return true
    }

    override fun createEmptyViewContent(messageTextId: Int): Any =
        createEmptyViewContent(messageTextId, 0)

    override fun createEmptyViewContent(messageTextId: Int, detailTextId: Int): Any =
        if (detailTextId == RCommon.string.common_no_network_available_check_connection) {
            StubViewCase.NO_CONNECTION
        } else {
            StubViewCase.NO_SEARCH_RESULTS
        }

    override fun onBackPressed(): Boolean = if (component.getViewModel().folderUUID != null) {
        backFromFolder()
    } else {
        component.getRouter().back()
    }

    override fun setSearchQuery(query: String) {
        mSearchPanel.setSearchText(query)
    }
}

private const val SAFE_KEYBOARD_NAVIGATION_DELAY_MS = 70L
private const val OPENED_FOLDER_UUID_KEY = "OPENED_FOLDER_ID_KEY"
private const val OPENED_FOLDER_NAME_KEY = "OPENED_FOLDER_NAME_KEY"