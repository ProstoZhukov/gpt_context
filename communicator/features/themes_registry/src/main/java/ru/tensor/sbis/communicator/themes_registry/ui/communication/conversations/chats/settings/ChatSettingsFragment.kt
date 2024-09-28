package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.theartofdev.edmodo.cropper.CropImage
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.base_components.BaseProgressDialogFragment
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.PreviewerUrlUtil
import ru.tensor.sbis.common.util.PreviewerUrlUtil.replacePreviewerUrlPartWithCheck
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.common.util.hasFragmentOrPendingTransaction
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.themes_registry.BuildConfig
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryPlugin
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.ChatRecipientSelectionActivity
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.ChatSelectionType
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsActivity.Companion.CHAT_SETTINGS_DRAFT_BOOLEAN
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsActivity.Companion.CHAT_SETTINGS_NEW_CHAT_BOOLEAN
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsActivity.Companion.CHAT_SETTINGS_UUID
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.ChatSettingsAdapter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsFooterItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsHeaderItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.model.ChatSettingsEditChatNameData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.CURRENT_CHAT_TYPE_REQUEST_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.CURRENT_CHAT_TYPE_RESULT
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.CURRENT_PARTICIPATION_CHAT_TYPE_REQUEST_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.CURRENT_PARTICIPATION_CHAT_TYPE_RESULT
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsParticipationTypeFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsParticipationTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsTypeFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.di.ChatSettingsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.di.DaggerChatSettingsComponent
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SuccessButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.files_picker.decl.CropAspectRatio
import ru.tensor.sbis.design.files_picker.decl.CropMinSizeLimit
import ru.tensor.sbis.design.files_picker.decl.CropParams
import ru.tensor.sbis.design.files_picker.decl.GallerySelectionMode
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPicker
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerEvent
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.extentions.setHorizontalMargin
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.design.utils.preventViewFromDoubleClickWithDelay
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design_dialogs.dialogs.container.ContentActionHandler
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.ContainerBottomSheet
import ru.tensor.sbis.design_dialogs.dialogs.content.BaseContentCreator
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.fresco_view.util.superellipse.SuperEllipsePostprocessor
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.mvp.fragment.BaseListFragmentWithTwoWayPagination
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import ru.tensor.sbis.viewer.decl.viewer.ImageUri
import ru.tensor.sbis.viewer.decl.viewer.ImageViewerArgs
import java.util.*
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.context_menu.R as RContextMenu
import ru.tensor.sbis.design.design_confirmation.R as RDesignConfirmation
import ru.tensor.sbis.design.profile.R as RDesignProfile

/**
 * Фрагмент настроек чата
 *
 * @author vv.chekurda
 */
internal class ChatSettingsFragment :
    BaseListFragmentWithTwoWayPagination<ChatSettingsItem, ChatSettingsAdapter, ChatSettingsContract.View,
        ChatSettingsContract.Presenter>(),
    ChatSettingsContract.View,
    AdjustResizeHelper.KeyboardEventListener,
    ContentActionHandler,
    PopupConfirmation.DialogYesNoWithTextListener {

    companion object {
        /** @SelfDocumented */
        @JvmStatic
        fun newInstance(newChat: Boolean, uuid: UUID?, draftChat: Boolean): ChatSettingsFragment {
            val fragment = ChatSettingsFragment()
            fragment.arguments = createArguments(newChat, uuid, draftChat)
            return fragment
        }

        private fun createArguments(isNewChat: Boolean, uuid: UUID?, draftChat: Boolean): Bundle {
            val args = Bundle()
            args.putBoolean(CHAT_SETTINGS_NEW_CHAT_BOOLEAN, isNewChat)
            args.putSerializable(CHAT_SETTINGS_UUID, uuid)
            args.putBoolean(CHAT_SETTINGS_DRAFT_BOOLEAN, draftChat)
            return args
        }
    }

    private lateinit var topNavigationView: SbisTopNavigationView
    private var contentContainer: ViewGroup? = null
    private var avatarSideSize: Int = 0

    private var postprocessor: SuperEllipsePostprocessor? = null
    private var chatComponent: ChatSettingsComponent? = null

    private var filesPicker: SbisFilesPicker? = null

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var resultListener: ResultListener? = null

    private var lastToast: Toast? = null
    private var progressDialogFragment: BaseProgressDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            progressDialogFragment =
                childFragmentManager.findFragmentByTag(PROGRESS_DIALOG_FRAGMENT_TAG) as BaseProgressDialogFragment?
        }
        postprocessor = SuperEllipsePostprocessor(
            requireContext(),
            RDesign.drawable.super_ellipse_mask
        )
        avatarSideSize = resources.getDimensionPixelSize(RDesignProfile.dimen.design_profile_person_photo_view_size)
    }

    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        contentContainer = mainView.findViewById(R.id.communicator_chat_settings_content_container)
        setListeners()
        mSbisListView = mainView.findViewById(R.id.communicator_participant_list)
        mSbisListView!!.run {
            setSwipeColorSchemeResources(
                context.getThemeColor(RDesign.attr.primaryActiveColor),
            )
            setLayoutManager(PaginationLayoutManager(requireContext()))
            setAdapter(mAdapter)
        }

        initTopNavigationView(mainView)

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState)
        }
    }

    override fun getLayoutRes(): Int = R.layout.communicator_fragment_chat_settings

    private fun setListeners() {
        val fragment = getFragment()
        fragment?.viewLifecycleOwner?.lifecycleScope?.launch {
            filesPicker?.events?.collect { event ->
                if (event is SbisFilesPickerEvent.OnItemsSelected) {
                    when (val pickedPhoto = event.selectedItems.firstOrNull()) {
                        is SbisPickedItem.LocalFile -> handleFilesPickerResult(pickedPhoto.uri)
                        else -> illegalState { "Unexpected type - ${pickedPhoto!!::class.java.simpleName}" }
                    }
                }
            }
        }
    }

    private fun handleCroppedResult(data: Intent?) {
        val result = CropImage.getActivityResult(data)
        result?.uri?.toString()?.also {
            presenter.handleNewAvatar(it)
        } ?: result.error?.localizedMessage?.also(::showToast)
    }

    private fun handleFilesPickerResult(uri: String) {
        presenter.handleNewAvatar(uri)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) return
        if (resultCode == Activity.RESULT_OK || resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            handleCroppedResult(data)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        resultListener = parentFragment?.castTo()
    }

    override fun onDestroyView() {
        if (!activity?.isChangingConfigurations!!) lastToast?.cancel()
        contentContainer = null
        compositeDisposable.dispose()
        super.onDestroyView()
    }

    private fun initTopNavigationView(mainView: View) {
        val actionDoneButton = SbisRoundButton(requireContext()).apply {
            val horizontalMargin = requireContext().resources.getDimensionPixelSize(
                RCommunicatorDesign.dimen.communicator_dialog_information_action_done_margin_end
            )
            id = R.id.themes_registry_chat_settings_done_button
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
            visibility = View.VISIBLE
        }
        topNavigationView = mainView.findViewById<SbisTopNavigationView?>(R.id.communicator_chat_settings_toolbar).apply {
            rightButtons = listOf(actionDoneButton)
            rightBtnContainer?.isVisible = false
        }
    }

    override fun getPresenterView(): ChatSettingsContract.View {
        return this
    }

    override fun createPresenter(): ChatSettingsContract.Presenter {
        chatComponent = chatComponent ?: createComponent()
        return chatComponent!!.getChatSettingsPresenter()
    }

    override fun inject() {
        chatComponent = (chatComponent ?: createComponent()).also {
            it.inject(this)
            mAdapter = ChatSettingsAdapter(
                createChatSettingsHeaderItem(),
                createChatSettingsFooterItem(),
                presenter::onItemClick,
                presenter::onRemoveAdminClick,
                presenter.isSwipeEnabled
            )
        }
    }

    private fun createComponent(): ChatSettingsComponent =
        DaggerChatSettingsComponent.builder()
            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
            .communicatorDialogChatDependency(themesRegistryDependency)
            .setFragment(this)
            .newChat(arguments?.getBoolean(CHAT_SETTINGS_NEW_CHAT_BOOLEAN) ?: true)
            .chatUuid(arguments?.getSerializable(CHAT_SETTINGS_UUID) as UUID?)
            .draftChat(arguments?.getBoolean(CHAT_SETTINGS_DRAFT_BOOLEAN) ?: true)
            .build().apply {
                filesPicker = getFragment()?.let {
                    ThemesRegistryPlugin.filesPickerFactoryProvider.get().createSbisFilesPicker(it)
                }
            }

    private fun createChatSettingsHeaderItem(): ChatSettingsHeaderItem = ChatSettingsHeaderItem(
        avatarUrl = null,
        onAvatarViewClick = { presenter.onAvatarClick() },
        onAvatarViewLongClick = { presenter.onAvatarLongClick() },
        chatSettingsEditChatNameData = ChatSettingsEditChatNameData(
            value = presenter.getChatName(),
            onValueChanged = { name: String ->
                presenter.onChatNameChanged(name)
            },
        ),
        personListTitleViewTextRes = RCommunicatorDesign.string.communicator_chat_participants,
        onAddButtonClick = { presenter.onAddPersonButtonClicked() },
        isAddButtonVisible = false,
        onRecycleHeaderItem = {
            view?.let { KeyboardUtils.hideKeyboard(it) }
        },
    )

    private fun createChatSettingsFooterItem(): ChatSettingsFooterItem = ChatSettingsFooterItem(
        onCollapseButtonClick = { mAdapter?.onCollapseButtonClick() },
        onChangeChatTypeButtonClick = { presenter.onChangeChatTypeClicked() },
        onChangeParticipationTypeButtonClick = { presenter.onChangeParticipationTypeClicked() },
        changeNotificationOptions = { options: ChatNotificationOptions ->
            presenter.changeNotificationOptions(
                options.notificationsTurnedOff,
                options.notificationsPrivateEvents,
                options.notificationsAdminEvents,
            )
        },
        changeActionDoneButtonVisibility = { isVisible: Boolean -> changeActionDoneButtonVisibility(isVisible) },
        onCloseChannelButtonClick = { presenter.closeChat() },
        isNewChat = arguments?.getBoolean(CHAT_SETTINGS_NEW_CHAT_BOOLEAN) ?: true,
        onRecycleFooterItem = { presenter.saveNotificationOptions() },
    )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mAdapter?.onSavedInstanceState(outState)
    }

    override fun restoreFromBundle(savedInstanceState: Bundle) {
        super.restoreFromBundle(savedInstanceState)
        mAdapter?.onRestoreInstanceState(savedInstanceState)
    }

    override fun showAvatarChangeDialog() {
        val imageCropParams = CropParams(
            aspectRatio = CropAspectRatio(1, 1),
            minSizeLimit = CropMinSizeLimit(AVATAR_MIN_SIZE, AVATAR_MIN_SIZE)
        )
        filesPicker?.show(
            childFragmentManager,
            setOf(
                SbisFilesPickerTab.Gallery(
                    selectionMode = GallerySelectionMode.Single(imageCropParams),
                    needOnlyImages = true
                )
            )
        )
    }

    override fun showChatAvatar(photoUrl: String) {
        // согласованный размер картинки при открытии в просмотрщике
        val androidAndIosSize = 60
        val avatarSize = androidAndIosSize * resources.displayMetrics.density.toInt()
        val photoUrlWithSize = replacePreviewerUrlPartWithCheck(
            photoUrl,
            avatarSize,
            avatarSize,
            PreviewerUrlUtil.ScaleMode.RESIZE
        )
        val args = ViewerSliderArgs(ImageViewerArgs(ImageUri(photoUrlWithSize)))
        val context = requireContext()
        val intent = themesRegistryDependency
            .createViewerSliderIntent(context, args)
        this.startActivity(intent)
    }

    override fun updateAvatar(dataString: String?) {
        mAdapter?.updateAvatar(dataString)
    }

    override fun setPersonListTitle(titleRes: Int) {
        mAdapter?.updatePersonListTitleViewText(titleRes)
    }

    override fun showCloseChatButton(show: Boolean) {
        mAdapter?.changeCloseChatButtonVisibility(show)
    }

    override fun setChatNameEditable(editable: Boolean) {
        mAdapter?.changeEditChatNameIsEnabledValue(editable)
    }

    override fun setChatName(name: String, needUpdate: Boolean) {
        mAdapter?.changeEditChatNameValue(name, needUpdate)
    }

    override fun updateCheckboxAndSwitch(
        options: ChatNotificationOptions,
        skipSwitchAnimation: Boolean,
        needUpdate: Boolean,
    ) {
        mAdapter?.updateCheckboxesAndSwitch(options, skipSwitchAnimation, needUpdate)
    }

    override fun updateChatTypeButtonsState(
        currentType: ChatSettingsTypeOptions,
        currentParticipationType: ChatSettingsParticipationTypeOptions,
    ) {
        mAdapter?.updateChatTypes(
            currentType,
            currentParticipationType,
        )
    }

    override fun showOnlyEmployeesTypeConfirmation() {
        PopupConfirmation.newSimpleInstance(CODE_CONFIRM_ONLY_EMPLOYEES).also {
            it.requestTitle(getString(RCommunicatorDesign.string.communicator_only_employees_confirmation_text))
            it.requestPositiveButton(getString(RDesignConfirmation.string.design_confirmation_dialog_button_yes))
            it.requestNegativeButton(getString(RDesignConfirmation.string.design_confirmation_dialog_button_no))
            it.setEventProcessingRequired(true)
        }.show(childFragmentManager, PopupConfirmation::class.simpleName)
    }

    override fun onChangeChatTypeClicked(
        options: List<ChatSettingsTypeOptions>,
        currentType: ChatSettingsTypeOptions,
    ) {
        if (isTablet) {
            val anchor =
                mSbisListView?.findViewById<SbisTextView>(R.id.communicator_chat_settings_footer_change_chat_type)
            val sbisMenu = SbisMenu(
                children = options.map { getOptionChatTypeMenuItem(it, currentType == it) },
            )
            sbisMenu.showMenu(anchor!!)
        } else {
            showTypeBottomOptions(
                ChatSettingsTypeFragment.Creator(currentType),
                CHANGE_CHAT_TYPE_TAG,
            )
        }
    }

    override fun onChangeParticipationTypeClicked(
        options: List<ChatSettingsParticipationTypeOptions>,
        currentType: ChatSettingsParticipationTypeOptions,
    ) {
        if (isTablet) {
            val anchor =
                mSbisListView?.findViewById<SbisTextView>(R.id.communicator_chat_settings_footer_change_participation_type)
            val sbisMenu = SbisMenu(
                children = options.map { getOptionChatParticipationTypeMenuItem(it, currentType == it) },
            )
            sbisMenu.showMenu(anchor!!)
        } else {
            showTypeBottomOptions(
                ChatSettingsParticipationTypeFragment.Creator(currentType),
                CHANGE_CHAT_PARTICIPATION_TYPE_TAG,
            )
        }
    }

    override fun showAvatarOptionMenu() {
        val anchor = mSbisListView?.findViewById<View>(R.id.communicator_chat_settings_header_avatar_photo)
        val sbisMenu = SbisMenu(
            children = getOptions(),
        )
        anchor?.let { sbisMenu.showMenu(it) }
    }

    private fun getOptions(): List<MenuItem> = getAllOptions().map {
        MenuItem(
            title = PlatformSbisString.Res(it.textRes),
            image = it.iconRes,
            destructive = it.destructive,
            imageAlignment = HorizontalPosition.LEFT
        ) {
            presenter.handleAvatarOption(it)
        }
    }

    private fun SbisMenu.showMenu(anchor: View) {
        this.showMenuWithLocators(
            fragmentManager = childFragmentManager,
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.BOTTOM,
                force = false,
                offsetRes = RContextMenu.dimen.context_menu_anchor_margin,
            ).apply { anchorView = anchor },
            horizontalLocator = AnchorHorizontalLocator(
                alignment = HorizontalAlignment.LEFT,
                force = false,
                innerPosition = true,
            ).apply { anchorView = anchor },
            dimType = DimType.SOLID,
        )
    }

    private fun getOptionChatTypeMenuItem(
        option: ChatSettingsTypeOptions,
        isCurrent: Boolean,
    ) = MenuItem(
        title = requireContext().getString(option.titleRes),
        discoverabilityTitle = requireContext().getString(option.discoverabilityTitleRes),
        state = if (isCurrent) MenuItemState.ON else MenuItemState.MIXED,
    ) {
        mAdapter?.updateChatTypes(option, null)
        presenter.onChatTypeSelected(option)
    }

    private fun getOptionChatParticipationTypeMenuItem(
        option: ChatSettingsParticipationTypeOptions,
        isCurrent: Boolean,
    ) = MenuItem(
        title = requireContext().getString(option.titleRes),
        state = if (isCurrent) MenuItemState.ON else MenuItemState.MIXED,
    ) {
        mAdapter?.updateChatTypes(null, option)
        presenter.onChatParticipationTypeSelected(option)
    }

    private fun showTypeBottomOptions(creator: BaseContentCreator, tag: String) {
        val fragmentManager = childFragmentManager
        if (!fragmentManager.hasFragmentOrPendingTransaction(CHANGE_CHAT_TYPE_TAG) &&
            !fragmentManager.hasFragmentOrPendingTransaction(CHANGE_CHAT_PARTICIPATION_TYPE_TAG)
        ) {
            val dialogContainer = ContainerBottomSheet()
            fragmentManager
                .beginTransaction()
                .add(dialogContainer.setContentCreator(creator), tag)
                .commitAllowingStateLoss()
        }
    }

    override fun onContentAction(actionId: String, data: Bundle?) {
        when (actionId) {
            CURRENT_CHAT_TYPE_REQUEST_KEY -> {
                val result = data?.get(CURRENT_CHAT_TYPE_RESULT)?.castTo<ChatSettingsTypeOptions>()
                result?.let {
                    mAdapter?.updateChatTypes(result, null)
                    presenter.onChatTypeSelected(result)
                }
            }
            CURRENT_PARTICIPATION_CHAT_TYPE_REQUEST_KEY -> {
                val result =
                    data?.get(CURRENT_PARTICIPATION_CHAT_TYPE_RESULT)?.castTo<ChatSettingsParticipationTypeOptions>()
                result?.let {
                    mAdapter?.updateChatTypes(null, result)
                    presenter.onChatParticipationTypeSelected(result)
                }
            }
        }
    }

    override fun showProgressDialog(@StringRes textResId: Int) {
        if (progressDialogFragment == null) {
            progressDialogFragment = BaseProgressDialogFragment.newInstance(true)
        }

        progressDialogFragment?.init(null, getString(textResId))
        progressDialogFragment?.showNow(childFragmentManager, PROGRESS_DIALOG_FRAGMENT_TAG)
    }

    override fun hideProgressDialog() {
        progressDialogFragment?.let {
            if (it.isAdded) {
                it.dismiss()
                progressDialogFragment = null
            }
        } ?: if (BuildConfig.DEBUG) {
            throw IllegalStateException("Progress dialog is not shown")
        } else {
        }
    }

    override fun finish(chatUuid: UUID) {
        view?.let { KeyboardUtils.hideKeyboard(it) }
        resultListener?.onResultOk(chatUuid)
            ?: if (activity is ChatSettingsActivity) requireActivity().run {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
            }
    }

    override fun cancel() {
        view?.let { KeyboardUtils.hideKeyboard(it) }
        resultListener?.onResultCancel()
            ?: if (activity is ChatSettingsActivity) requireActivity().finish() else {
            }
    }

    override fun openProfile(profileUuid: UUID) {
        val context = context
        if (context != null) {
            val intent = themesRegistryDependency.createPersonCardIntent(context, profileUuid)
            context.startActivity(intent)
        }
    }

    override fun setToolbarData(creatorName: String, newChat: Boolean, timestamp: Long) {
        topNavigationView.run {
            content = SbisTopNavigationContent.SmallTitle(
                title = PlatformSbisString.Res(
                    if (newChat) RCommunicatorDesign.string.communicator_new_channel_title else RCommunicatorDesign.string.communicator_channel_settings_toolbar
                ),
                subtitle = if (!newChat) {
                    PlatformSbisString.Value(
                        "${getString(RCommunicatorDesign.string.communicator_chat_toolbar_subtitle)} ${
                            DateFormatUtils.formatAsBirthday(
                                timestamp
                            )
                        } $creatorName"
                    )
                } else null
            )
            setBackBtnClickListener()
        }
    }

    private fun SbisTopNavigationView.setBackBtnClickListener() {
        backBtn?.setOnClickListener {
            view?.let { KeyboardUtils.hideKeyboard(it) }
            requireActivity().onBackPressed()
        }
    }

    @SuppressLint("ShowToast")
    override fun showToast(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") stringRes: Int) {
        lastToast?.cancel()
        lastToast = SbisPopupNotification.pushToast(requireContext(), stringRes)
    }

    override fun showToast(message: String) {
        lastToast?.cancel()
        lastToast = SbisPopupNotification.pushToast(requireContext(), message)
    }

    override fun setSwipeEnabled(isEnabled: Boolean) {
        mAdapter?.setSwipeEnabled(isEnabled)
    }

    override fun updateDataList(dataList: MutableList<ChatSettingsItem>?, offset: Int) {
        super.updateDataList(dataList, offset)
    }

    override fun changeAddPersonsButtonVisibility(isVisible: Boolean) {
        mAdapter?.changeAddButtonVisibility(isVisible)
    }

    override fun changeActionDoneButtonVisibility(isVisible: Boolean) {
        topNavigationView.rightBtnContainer?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun showChoosingRecipients(chatUuid: UUID?, isForAdmins: Boolean) {
        if (isForAdmins) {
            startActivity(
                ChatRecipientSelectionActivity.newIntent(
                    requireContext(),
                    ChatSelectionType.AdminSelection(chatUuid!!)
                )
            )
        } else {
            val intent = themesRegistryDependency.getRecipientSelectionIntent(
                requireContext(),
                RecipientSelectionConfig(
                    useCase = if (chatUuid == null) {
                        RecipientSelectionUseCase.NewChat
                    } else {
                        RecipientSelectionUseCase.AddChatParticipants(chatUuid)
                    }
                )
            )
            startActivity(intent)
        }
    }

    override fun setEditNameViewBackgroundColor(isEditNameTextEmpty: Boolean) {
        val validationsStatus = if (isEditNameTextEmpty) {
            ValidationStatus.Error(requireContext().getString(RCommunicatorDesign.string.communicator_warning_enter_channel_name))
        } else {
            ValidationStatus.Default(EMPTY)
        }
        mAdapter
            ?.changeEditChatNameValidationStatus(validationsStatus)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        contentContainer?.updatePadding(bottom = keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        contentContainer?.updatePadding(bottom = 0)
        return true
    }

    /** @SelfDocumented */
    interface ResultListener {

        fun onResultOk(uuid: UUID)

        fun onResultCancel()
    }

    override fun onYes(requestCode: Int, text: String?) {
        doIf (requestCode == CODE_CONFIRM_ONLY_EMPLOYEES) {
            presenter.updateChat()
        }
    }

    override fun onNo(requestCode: Int, text: String?) {
        doIf (requestCode == CODE_CONFIRM_ONLY_EMPLOYEES) {
            mAdapter?.updateChatTypes(null, ChatSettingsParticipationTypeOptions.FOR_ALL)
            presenter.onChatParticipationTypeSelected(ChatSettingsParticipationTypeOptions.FOR_ALL)
        }
    }
}

private val PROGRESS_DIALOG_FRAGMENT_TAG = ChatSettingsFragment::class.java.simpleName + ".progress_dialog_fragment"
private const val AVATAR_MIN_SIZE = 200
private const val CHANGE_CHAT_TYPE_TAG = "CHANGE_CHAT_TYPE_TAG"
private const val CHANGE_CHAT_PARTICIPATION_TYPE_TAG = "CHANGE_CHAT_PARTICIPATION_TYPE_TAG"
private const val CODE_CONFIRM_ONLY_EMPLOYEES = 1