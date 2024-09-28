package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.presentation.view

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.base_components.fragment.HideKeyboardOnScrollListener
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.getActivityAs
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationSearchableContent
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_INFO_SELECTION_RESULT_KEY
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_INFO_SELECTION_RESULT_UUID_KEY
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorDialogInformationRouter
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_IS_NEW_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_UUID_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.KEY_NEW_INFORMATION_CONVERSATION_SCREEN
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_DIALOG_INFORMATION_PARTICIPANTS_UUIDS
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_DIALOG_UUID
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_IS_FROM_COLLAGE
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_NEW_DIALOG
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsResultListener
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.ThemeParticipantsAdapter
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract.DialogParticipantsViewContract
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.di.DaggerDialogParticipantsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.di.DialogParticipantsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.extentions.setTopPadding
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager
import ru.tensor.sbis.mvp.search.BaseSearchableView
import java.util.UUID
import javax.inject.Inject
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.view.input.R as RInputView

/**
 * Фрагмент участников диалога
 */
internal class DialogParticipantsFragment :
    BaseSearchableView<ThemeParticipantListItem,
        ThemeParticipantsAdapter,
        DialogParticipantsViewContract.View,
        DialogParticipantsViewContract.Presenter>(),
    DialogParticipantsViewContract.View,
    ConversationInformationSearchableContent,
    CommunicatorDialogInformationRouter by themesRegistryDependency.getCommunicatorDialogInformationRouter() {

    companion object {
        fun newInstance(args: Bundle?): DialogParticipantsFragment {
            val fragment = DialogParticipantsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var topNavigationView: SbisTopNavigationView
    private val isFromCollage: Boolean
        get() = arguments?.getBoolean(KEY_IS_FROM_COLLAGE, false) ?: false

    private val isFromConversationInfo: Boolean
        get() = arguments?.getBoolean(KEY_NEW_INFORMATION_CONVERSATION_SCREEN, false) ?: false

    private val component: DialogParticipantsComponent by lazy {
        val componentBuilder = DaggerDialogParticipantsComponent.builder()
            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
            .communicatorDialogChatDependency(themesRegistryDependency)

        arguments?.run {
            if (containsKey(KEY_DIALOG_UUID)) componentBuilder.conversationUuid(getSerializable(KEY_DIALOG_UUID) as UUID)
            if (containsKey(KEY_NEW_DIALOG)) componentBuilder.isNewDialog(getBoolean(KEY_NEW_DIALOG))
            if (containsKey(KEY_DIALOG_INFORMATION_PARTICIPANTS_UUIDS)) {
                componentBuilder.videoCallParticipants(
                    UUIDUtils.fromParcelUuids(getParcelableArrayList(KEY_DIALOG_INFORMATION_PARTICIPANTS_UUIDS)) as? ArrayList<UUID>
                )
            } else {
                componentBuilder.videoCallParticipants(arrayListOf())
            }
            componentBuilder.isFromCollage(isFromCollage)
            if (containsKey(CONVERSATION_UUID_KEY)) {
                componentBuilder.conversationUuid(getSerializable(CONVERSATION_UUID_KEY) as UUID)
            }
            if (containsKey(CONVERSATION_IS_NEW_KEY)) {
                componentBuilder.isNewDialog(getBoolean(CONVERSATION_IS_NEW_KEY))
            }
        }

        componentBuilder.build()
    }

    /** @SelfDocumented */
    override fun getLayoutRes(): Int = R.layout.communicator_fragment_conversation_participants_list

    /** @SelfDocumented */
    override fun getPresenterView(): DialogParticipantsViewContract.View = this

    /** @SelfDocumented */
    override fun createPresenter(): DialogParticipantsViewContract.Presenter = component.getPresenter()

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
            onStartConversationClick = presenter::onStartConversationClick
            onStartVideoCallClick = presenter::onStartVideoCallClick
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCommunicatorRouter(this)
    }

    /** @SelfDocumented */
    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        super.initViews(mainView, savedInstanceState)
        mSearchPanel.isVisible = false
        initTopNavigationView(mainView)
        initList(mainView)
    }

    private fun initTopNavigationView(mainView: View) {
        topNavigationView = mainView.findViewById<SbisTopNavigationView?>(R.id.communicator_dialog_participants_toolbar).apply {
            val titleStringRes = when {
                isFromCollage -> RCommunicatorDesign.string.communicator_dialog_recipients_list_title
                else -> RCommunicatorDesign.string.communicator_dialog_conversation_members
            }
            content = SbisTopNavigationContent.SmallTitle(
                title = PlatformSbisString.Res(titleStringRes),
            )
            backBtn?.setOnClickListener { requireActivity().onBackPressed() }
            isVisible = !isFromConversationInfo
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
            mAdapter.setNeedShowContactIcon(isFromConversationInfo)
            setAdapter(mAdapter)
            recyclerView.itemAnimator?.apply {
                addDuration = 0
                changeDuration = 0
                moveDuration = 0
                castTo<SimpleItemAnimator>()?.supportsChangeAnimations = false
            }
            if (isFromConversationInfo) {
                mainView.findViewById<AppBarLayout>(R.id.communicator_dialog_participants_appbar)?.isVisible = false
            }
        }
    }

    /** @SelfDocumented */
    override fun setParticipants(participants: List<ThemeParticipantListItem.ThemeParticipant>) {
        mAdapter.setData(participants, 0)
    }

    override fun startConversation(profileUuid: UUID) {
        startNewConversation(profileUuid)
    }

    override fun startCall(profileUuid: UUID) {
        startVideoCall(profileUuid)
    }

    override fun updateListPaddingsIfNeed() {
        if (isFromConversationInfo) {
            mSbisListView?.run {
                recyclerView.setTopPadding(Offset.M.getDimenPx(context))
                recyclerViewBottomPadding = InlineHeight.XL.getDimenPx(context)
            }
        }
    }

    /** @SelfDocumented */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mAdapter.onSavedInstanceState(outState)
    }

    /** @SelfDocumented */
    override fun restoreFromBundle(savedInstanceState: Bundle) {
        super.restoreFromBundle(savedInstanceState)
        mAdapter.onRestoreInstanceState(savedInstanceState)
    }

    /** @SelfDocumented */
    override fun openProfile(profileUuid: UUID) {
        val intent = themesRegistryDependency.createPersonCardIntent(requireContext(), profileUuid)
        requireContext().startActivity(intent)
    }

    /** @SelfDocumented */
    override fun finishWithUuidResult(profileUuid: UUID) {
        val activity = getActivityAs<ConversationParticipantsResultListener>()
        if (activity != null) {
            if (presenter.isNewDialog()) {
                activity.onResultCancel()
            } else {
                activity.onResultOk(profileUuid)
            }
        } else {
            val bundle = Bundle().apply {
                putSerializable(CONVERSATION_INFO_SELECTION_RESULT_UUID_KEY, profileUuid)
            }
            parentFragmentManager.setFragmentResult(CONVERSATION_INFO_SELECTION_RESULT_KEY, bundle)
        }
    }

    override fun createEmptyViewContent(messageTextId: Int): Any =
        createEmptyViewContent(messageTextId, 0)

    override fun createEmptyViewContent(messageTextId: Int, detailTextId: Int): Any =
        if (detailTextId == RCommon.string.common_no_network_available_check_connection) {
            StubViewCase.NO_CONNECTION
        } else {
            StubViewCase.NO_SEARCH_RESULTS
        }

    override fun setSearchQuery(query: String) {
        mSearchPanel.setSearchText(query)
    }
}
