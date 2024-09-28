package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.base_components.fragment.HideKeyboardOnScrollListener
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.getActivityAs
import ru.tensor.sbis.communicator.base_folders.keyboard.CommunicatorKeyboardMarginsHelper
import ru.tensor.sbis.communicator.base_folders.keyboard.CommunicatorKeyboardMarginsHelperImpl
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorDialogInformationRouter
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.di.DaggerDialogInformationComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.di.DialogInformationComponent
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_CONVERSATION_NAME
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_DIALOG_INFORMATION_PARTICIPANTS_UUIDS
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_DIALOG_UUID
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity.Companion.KEY_NEW_DIALOG
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsResultListener
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.ThemeParticipantsAdapter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SuccessButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.extentions.setHorizontalMargin
import ru.tensor.sbis.design.utils.preventViewFromDoubleClickWithDelay
import ru.tensor.sbis.design.view.input.text.TextInputView
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.mvp.fragment.BaseListFragmentWithTwoWayPagination
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager
import java.util.*
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Фрагмент информации о диалоге
 *
 * @author da.zhukov
 */
internal class DialogInformationFragment :
    BaseListFragmentWithTwoWayPagination<
        ThemeParticipantListItem,
        ThemeParticipantsAdapter,
        DialogInformationContract.View,
        DialogInformationContract.Presenter>(),
    DialogInformationContract.View,
    CommunicatorDialogInformationRouter by ThemesRegistryFacade.themesRegistryDependency.getCommunicatorDialogInformationRouter(),
    CommunicatorKeyboardMarginsHelper by CommunicatorKeyboardMarginsHelperImpl() {

    companion object {

        private const val ACTION_DONE_BUTTON_VISIBILITY = "action_done_button_visibility"

        /** @SelfDocumented */
        @JvmStatic
        fun newInstance(args: Bundle?): DialogInformationFragment {
            val fragment = DialogInformationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var toolbar: SbisTopNavigationView
    private lateinit var editDialogNameView: TextInputView
    private lateinit var divider: View
    private lateinit var listTopShadow: View

    private lateinit var personListTitleView: SbisTextView

    private var lastToast: Toast? = null

    private val dialogInformationComponent: DialogInformationComponent by lazy {
        val componentBuilder = DaggerDialogInformationComponent.builder()
            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
            .communicatorThemeRegistryDependency(ThemesRegistryFacade.themesRegistryDependency)

        arguments?.run {
            if (containsKey(KEY_DIALOG_UUID)) componentBuilder.conversationUuid(
                getSerializable(
                    KEY_DIALOG_UUID
                ) as UUID
            )
            if (containsKey(KEY_NEW_DIALOG)) componentBuilder.isNewDialog(
                getBoolean(
                    KEY_NEW_DIALOG
                )
            )
            if (containsKey(KEY_CONVERSATION_NAME)) componentBuilder.conversationName(
                getString(KEY_CONVERSATION_NAME, "")
            )
            if (containsKey(KEY_DIALOG_INFORMATION_PARTICIPANTS_UUIDS)) componentBuilder.videoCallParticipants(
                UUIDUtils.fromParcelUuids(getParcelableArrayList(KEY_DIALOG_INFORMATION_PARTICIPANTS_UUIDS)) as? ArrayList<UUID>
            )
        }

        componentBuilder.build()
    }

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCommunicatorRouter(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ACTION_DONE_BUTTON_VISIBILITY, toolbar.rightBtnContainer?.isVisible ?: false)
    }

    /** @SelfDocumented */
    @SuppressLint("MissingSuperCall")
    override fun restoreFromBundle(savedInstanceState: Bundle) {
        with(savedInstanceState) {
            toolbar.rightBtnContainer?.isVisible = getBoolean(ACTION_DONE_BUTTON_VISIBILITY)
        }
    }

    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        initEditDialogNameView(mainView)

        mSbisListView = mainView.findViewById(R.id.communicator_dialog_information_participant_list)
        mSbisListView?.let {
            it.setLayoutManager(PaginationLayoutManager(requireContext()))
            it.addOnScrollListener(DialogInformationScrollListener())
            it.setHasFixedSize(true)
            mAdapter.setNeedShowContactIcon(true)
            it.setAdapter(mAdapter)
        }
        listTopShadow = mainView.findViewById(R.id.communicator_dialog_information_participant_list_shadow)

        personListTitleView = mainView.findViewById(R.id.communicator_dialog_information_person_list_title)

        initToolbar(mainView)
        setListeners()
        savedInstanceState?.let {
            restoreFromBundle(it)
        }
    }

    private fun initEditDialogNameView(mainView: View) {
        editDialogNameView = mainView.findViewById(R.id.communicator_dialog_information_text_input)
        editDialogNameView.isClearVisible = editDialogNameView.value.isNotEmpty()
        divider = mainView.findViewById(R.id.communicator_dialog_information_divider)
    }

    private fun initToolbar(mainView: View) {
        val toolbarActionDoneButton = SbisRoundButton(requireContext()).apply {
            val horizontalMargin = requireContext().resources.getDimensionPixelSize(
                RCommunicatorDesign.dimen.communicator_dialog_information_action_done_margin_end
            )
            id = R.id.themes_registry_dialog_information_done_button
            style = SuccessButtonStyle
            size = SbisRoundButtonSize.S
            icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_checked.character.toString())
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setHorizontalMargin(horizontalMargin, horizontalMargin)
            setOnClickListener(
                preventViewFromDoubleClickWithDelay { presenter.onDoneButtonClicked() }
            )
        }

        toolbar = mainView.findViewById<SbisTopNavigationView?>(R.id.communicator_dialog_information_toolbar).apply {
            content = SbisTopNavigationContent.SmallTitle(
                PlatformSbisString.Res(
                    RCommunicatorDesign.string.communicator_dialog_information
                )
            )
            backBtn?.setOnClickListener(
                preventViewFromDoubleClickWithDelay {
                    view?.let { KeyboardUtils.hideKeyboard(it) }
                    requireActivity().onBackPressed()
                }
            )
            rightButtons = listOf(toolbarActionDoneButton)
            rightBtnContainer?.isVisible = false
        }
    }

    /** @SelfDocumented */
    override fun getLayoutRes(): Int = R.layout.communicator_fragment_dialog_information

    private fun setListeners() {
        editDialogNameView.imeOptions = EditorInfo.IME_ACTION_DONE
        editDialogNameView.onValueChanged = { _, value ->
            presenter.onDialogTitleChanged(value)
        }
    }

    /** @SelfDocumented */
    override fun setParticipants(participants: List<ThemeParticipantListItem.ThemeParticipant>) {
        mAdapter.setData(participants, 0)
    }

    /** @SelfDocumented */
    override fun finishWithUuidResult(profileUuid: UUID) {
        val activity = getActivityAs<ConversationParticipantsResultListener>()
        if (presenter.isNewDialog()) {
            activity?.onResultCancel()
        } else {
            activity?.onResultOk(profileUuid)
        }
    }

    /** @SelfDocumented */
    override fun openProfile(profileUuid: UUID) {
        showProfile(profileUuid)
    }

    /** @SelfDocumented */
    override fun startConversation(profileUuid: UUID) {
        startNewConversation(profileUuid)
    }

    /** @SelfDocumented */
    override fun startCall(profileUuid: UUID) = startVideoCall(profileUuid)

    /** @SelfDocumented */
    override fun setDialogTitle(title: String) {
        editDialogNameView.value = title
    }

    /** @SelfDocumented */
    override fun changeActionDoneButtonVisibility(isVisible: Boolean) {
        toolbar.rightBtnContainer?.isVisible = isVisible
    }

    /** @SelfDocumented */
    override fun changeClearTitleButtonVisibility(isVisible: Boolean) {
        editDialogNameView.isClearVisible = isVisible
    }

    /** @SelfDocumented */
    override fun finishWithStringResult(dialogTitle: String) {
        view?.let { KeyboardUtils.hideKeyboard(it) }
        val activity = getActivityAs<ConversationParticipantsResultListener>()
        activity?.onResultOk(dialogTitle)
    }

    /** @SelfDocumented */
    override fun showToast(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") stringRes: Int) {
        lastToast?.cancel()
        lastToast = SbisPopupNotification.pushToast(requireContext(), stringRes)
    }

    /** @SelfDocumented */
    override fun showToast(message: String) {
        lastToast?.cancel()
        lastToast = SbisPopupNotification.pushToast(requireContext(), message)
    }

    /** @SelfDocumented */
    override fun inject() {
        dialogInformationComponent.inject(this)
        mAdapter = ThemeParticipantsAdapter().apply {
            onItemClick = presenter::onItemClick
            onItemPhotoClick = presenter::onItemPhotoClick
            onStartConversationClick = presenter::onStartConversationClick
            onStartVideoCallClick = presenter::onStartVideoCallClick
        }
    }

    /** @SelfDocumented */
    override fun createPresenter(): DialogInformationContract.Presenter = dialogInformationComponent.getPresenter()

    /** @SelfDocumented */
    override fun getPresenterView(): DialogInformationContract.View = this

    override fun onDestroy() {
        compositeDisposable.dispose()
        detachCommunicatorRouter()
        super.onDestroy()
    }

    /** @SelfDocumented */
    override fun createEmptyViewContent(messageTextId: Int): Any =
        createEmptyViewContent(messageTextId, 0)

    /** @SelfDocumented */
    override fun createEmptyViewContent(messageTextId: Int, detailTextId: Int): Any =
        if (detailTextId == ru.tensor.sbis.common.R.string.common_no_network_available_check_connection) {
            StubViewCase.NO_CONNECTION
        } else {
            StubViewCase.NO_SEARCH_RESULTS
        }

    private inner class DialogInformationScrollListener : HideKeyboardOnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val firstPosition = recyclerView.layoutManager?.castTo<LinearLayoutManager>()
                ?.findFirstCompletelyVisibleItemPosition() ?: -1
            listTopShadow.isVisible = firstPosition > 0
        }
    }
}
