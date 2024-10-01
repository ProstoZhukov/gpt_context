package ru.tensor.sbis.communicator.sbis_conversation.ui

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ParcelUuid
import android.text.InputType
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConversationLayoutManager
import androidx.tracing.Trace
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.android_ext_decl.getParcelableArrayListUniversally
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.attachment.signing.decl.AttachmentsSigningProcessListener
import ru.tensor.sbis.base_components.BaseProgressDialogFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.getActivityAs
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.common.util.toUuid
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationFromRegistryParams
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationOpenParams
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider
import ru.tensor.sbis.communication_decl.communicator.ui.DialogCreationParams
import ru.tensor.sbis.communication_decl.communicator.ui.DialogCreationWithParticipantsParams
import ru.tensor.sbis.communication_decl.communicator.ui.DocInfo
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communication_decl.selection.SelectionMenu
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarTitleEditingState
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.mapper.cloudSenderPersonModel
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.mapper.dateOrTimeString
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.BaseConversationFragment
import ru.tensor.sbis.communicator.base.conversation.utils.ConversationSubtitleExtension
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_ARCHIVED_CONVERSATION
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_CHATS_REGISTRY_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_CHAT_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_CONVERSATION_ARG
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_FOLDER_UUID_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_FROM_PARENT_THREAD
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_HIGHLIGHT_MESSAGE
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_IS_GROUP_CONVERSATION
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_MESSAGE_UUID_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_PARTICIPANTS_UUIDS_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_SENDER_UUID_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_THREAD_INFO
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_TOOLBAR_DIALOG_TITLE
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_TOOLBAR_PHOTO_ID
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_TOOLBAR_TITLE
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_TOOLBAR_VIEW_DATA
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_DIALOG_UUID_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CHAT_SETTINGS_REQUEST_CODE
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_INFO_SELECTION_RESULT_KEY
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_INFO_SELECTION_RESULT_UUID_KEY
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_VIEWER_SLIDER_CODE
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.DIALOG_PARTICIPANTS_ACTIVITY_CODE
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.MessageConversationPreviewMenuAction
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.common.data.ThreadInfo
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.communicator.common.navigation.contract.ConversationScreen
import ru.tensor.sbis.communicator.common.navigation.data.CommunicatorArticleDiscussionParams
import ru.tensor.sbis.communicator.common.themes_registry.dialog_info.document_plate_view.DocumentPlateView
import ru.tensor.sbis.communicator.common.themes_registry.dialog_info.document_plate_view.DocumentPlateViewModel
import ru.tensor.sbis.communicator.common.util.ThrottleActionHandler
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.message_search.ThemeMessageSearchApi
import ru.tensor.sbis.communicator.common.util.message_search.ThemeSearchMessagesSelectionResultListener
import ru.tensor.sbis.communicator.common.util.result_mediator.MessageUuidMediator
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsDispatcher
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsType
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.generated.DocumentAccessType
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.communicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.ConversationActivity
import ru.tensor.sbis.communicator.sbis_conversation.DialogCreationActivity
import ru.tensor.sbis.communicator.sbis_conversation.R
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessagesListAdapter
import ru.tensor.sbis.communicator.sbis_conversation.conversation.ConversationRouterImpl
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.di.conversation.DaggerConversationComponent
import ru.tensor.sbis.communicator.sbis_conversation.preview.CONVERSATION_CLICKED
import ru.tensor.sbis.communicator.sbis_conversation.preview.ConversationPreviewActionListener
import ru.tensor.sbis.communicator.sbis_conversation.preview.ConversationPreviewAnimatorUtils
import ru.tensor.sbis.communicator.sbis_conversation.preview.ConversationPreviewDialogFragment
import ru.tensor.sbis.communicator.sbis_conversation.preview.ConversationPreviewOnTouchListener
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationContract.ConversationPresenter
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationContract.ConversationView
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption
import ru.tensor.sbis.communicator.sbis_conversation.ui.document_sign.DocumentSigningOption
import ru.tensor.sbis.communicator.sbis_conversation.ui.document_sign.getOptions
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.DOTS_FOR_UNLOAD_SUBTITLE
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel
import ru.tensor.sbis.communicator.sbis_conversation.utils.ConversationViewPool
import ru.tensor.sbis.communicator.sbis_conversation.utils.ListUpdateHelper
import ru.tensor.sbis.communicator.sbis_conversation.utils.ListViewContract
import ru.tensor.sbis.communicator.sbis_conversation.utils.MessageSearchHelper
import ru.tensor.sbis.communicator.sbis_conversation.utils.PhoneNumberActionHelper
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisArrowButton
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.NavigationButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SuccessButtonStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.DefaultItem
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.context_menu.R.dimen
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.AudioRecorderDelegate
import ru.tensor.sbis.design.message_panel.audio_recorder.view.AudioRecordView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.emotion_picker.MessageEmotionPicker
import ru.tensor.sbis.design.message_panel.video_recorder.integration.contract.VideoRecorderDelegate
import ru.tensor.sbis.design.message_panel.video_recorder.view.VideoRecordView
import ru.tensor.sbis.design.person_suggest.input.PersonInputLayout
import ru.tensor.sbis.design.person_suggest.input.contract.PersonInputLayoutListener
import ru.tensor.sbis.design.person_suggest.suggest.PersonSuggestView
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.util.dpToPx
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.design_dialogs.fragment.AlertDialogFragment
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.integration.CommunicatorMessagePanelController
import ru.tensor.sbis.message_panel.view.MessagePanel
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.common.R as RCommunicatorCommon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.message_panel.R as RMessagePanel
import ru.tensor.sbis.modalwindows.R as RModalWindows

/**
 * Фабрика для создания фрагмента реестра сообщений.
 *
 * @author vv.chekurda
 */
interface ConversationFragmentFactory {

    /**
     * Идентификатор лэйаута фрагмента.
     * Используется для пула.
     */
    @get:LayoutRes
    val layoutId: Int

    /**
     * Создать фрагмент реестра сообщений.
     */
    fun createConversationFragment(
        dialogUuid: UUID? = null,
        messageUuid: UUID? = null,
        folderUuid: UUID? = null,
        participantsUuids: ArrayList<UUID>?,
        file: ArrayList<Uri>? = null,
        text: String? = null,
        document: Document? = null,
        type: ConversationType? = ConversationType.REGULAR,
        isChat: Boolean = false,
        archivedConversation: Boolean = false
    ): Fragment

    /**
     * Создать фрагмент реестра сообщений по аргументам Activity.
     */
    fun createConversationFragment(arguments: Bundle): Fragment

    /**
     * Создать фрагмент реестра сообщений по параметрам ConversationParams.
     */
    fun createConversationFragment(arg: ConversationParams): Fragment
}

/**
 * Фрагмент реестра сообщений на crud коллекции.
 *
 * @author vv.chekurda
 */
internal class ConversationFragment
    : BaseConversationFragment<
        ConversationMessage,
        MessagesListAdapter,
        ConversationView,
        ConversationPresenter
        >(),
    ConversationView,
    AlertDialogFragment.YesNoNeutralListener,
    PopupConfirmation.DialogYesNoWithTextListener,
    AttachmentsSigningProcessListener,
    CommunicatorConversationRouter,
    ListViewContract<ConversationMessage>,
    ConversationScreen,
    ThemeSearchMessagesSelectionResultListener {

    companion object : ConversationFragmentFactory {

        @get:LayoutRes
        override val layoutId = R.layout.communicator_fragment_conversation

        override fun createConversationFragment(arguments: Bundle): Fragment =
            ConversationFragment().withArgs { putAll(arguments) }

        override fun createConversationFragment(arg: ConversationParams): Fragment {
            return ConversationFragment().withArgs {
                putSerializable(CONVERSATION_ACTIVITY_CONVERSATION_ARG, arg)
            }
        }

        override fun createConversationFragment(
            dialogUuid: UUID?,
            messageUuid: UUID?,
            folderUuid: UUID?,
            participantsUuids: ArrayList<UUID>?,
            file: ArrayList<Uri>?,
            text: String?,
            document: Document?,
            type: ConversationType?,
            isChat: Boolean,
            archivedConversation: Boolean,
        ): Fragment =
            ConversationFragment().withArgs {
                putSerializable(EXTRA_CONVERSATION_ACTIVITY_DIALOG_UUID_KEY, dialogUuid)
                putSerializable(CONVERSATION_ACTIVITY_MESSAGE_UUID_KEY, messageUuid)
                putSerializable(CONVERSATION_ACTIVITY_FOLDER_UUID_KEY, folderUuid)
                putParcelableArrayList(
                    CONVERSATION_ACTIVITY_PARTICIPANTS_UUIDS_KEY,
                    UUIDUtils.toParcelUuids(participantsUuids) as ArrayList<ParcelUuid>
                )
                putParcelableArrayList(Intent.EXTRA_STREAM, file)
                putString(Intent.EXTRA_TEXT, text)
                putParcelable(EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY, document)
                putSerializable(EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY, type)
                putBoolean(CONVERSATION_ACTIVITY_CHAT_KEY, isChat)
                putBoolean(CONVERSATION_ACTIVITY_ARCHIVED_CONVERSATION, archivedConversation)
            }
    }

    @Suppress("DEPRECATION")
    private val viewModel by lazy { ViewModelProviders.of(this)[ConversationViewModel::class.java] }

    private val communicatorConversationRouter: CommunicatorConversationRouter by lazy {
        singletonComponent.dependency.getCommunicatorConversationRouter()
    }

    private var progressDialogFragment: BaseProgressDialogFragment? = null

    private var isTablet: Boolean = false
    private var landscape: Boolean = false
    private var isNewConversation: Boolean = false

    private var documentPlateView: DocumentPlateView? = null
    private var pinnedChatMessageView: View? = null
    private var recipientsPanelView: View? = null
    private var audioRecorder: AudioRecorderDelegate? = null
    private var videoRecorder: VideoRecorderDelegate? = null
    private var editTitleDoneButton: View? = null

    private var disposer = CompositeDisposable()

    private val phoneNumberActionHelper = PhoneNumberActionHelper(this)
    private val messageSearchHelper: MessageSearchHelper by lazy {
        viewModel.messageSearchHelper
    }

    private var personInputLayout: PersonInputLayout? = null
    private val personSuggestView: PersonSuggestView by lazy {
        mainLayout?.findViewById(R.id.communicator_conversation_person_suggest_view)!!
    }
    private val buttonPrevious: SbisArrowButton by lazy {
        SbisArrowButton(requireContext()).apply {
            id = R.id.communicator_conversation_search_message_previous_button
            setIconChar(SbisMobileIcon.Icon.smi_ArrowNarrowUp.character)
            style = NavigationButtonStyle
            size = SbisRoundButtonSize.M
            setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    messageSearchHelper.navigateToPreviousFoundMessage()
                }
            }
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val marginEnd = context.getDimenPx(RDesign.attr.offset_m)
                val marginBottom = context.getDimenPx(RDesign.attr.offset_m)
                setMargins(0, 0, marginEnd, marginBottom)

                addRule(RelativeLayout.ALIGN_PARENT_END)
                addRule(RelativeLayout.ABOVE, buttonNext.id)
            }
        }
    }
    private val buttonNext: SbisArrowButton by lazy {
        SbisArrowButton(requireContext()).apply {
            id = R.id.communicator_conversation_search_message_next_button
            setIconChar(SbisMobileIcon.Icon.smi_ArrowNarrowDown.character)
            style = NavigationButtonStyle
            size = SbisRoundButtonSize.M
            setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    messageSearchHelper.navigateToNextFoundMessage()
                }
            }
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val marginEnd = context.getDimenPx(RDesign.attr.offset_m)
                val marginBottom = context.getDimenPx(RDesign.attr.offset_s)
                setMargins(0, 0, marginEnd, marginBottom)

                addRule(RelativeLayout.ALIGN_PARENT_END)
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
        }
    }
    private val buttonSwitchViewMode: SbisButton by lazy {
        SbisButton(requireContext()).apply {
            id = R.id.communicator_conversation_search_message_switch_mode_button
            model = SbisButtonModel(
                icon = SbisButtonTextIcon(
                    fontIcon = SbisMobileIcon.Icon.smi_search,
                    size = SbisButtonIconSize.XL
                ),
                title = SbisButtonTitle(getString(RCommon.string.common_reset_button_label)),
                backgroundType = SbisButtonBackground.Contrast,
                clickListener = { messageSearchHelper.switchViewMode() }
            )
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val marginEnd = context.getDimenPx(RDesign.attr.offset_m)
                val marginBottom = context.getDimenPx(RDesign.attr.offset_m)
                setMargins(0, 0, marginEnd, marginBottom)

                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
        }
    }

    private var listUpdateHelper = ListUpdateHelper(this)
    private val viewPoolsHolder: ConversationViewPool by lazy {
        singletonComponent.conversationViewPoolController
            .getViewPoolsHolder(requireContext())
            .apply { prepareViewPools() }
    }
    private var conversationView: View? = null
        set(value) {
            field?.let(viewPoolsHolder::recycleConversationView)
            field = value
        }
        get() = field
            ?: viewPoolsHolder.conversationView.also {
                field = it
                adapter = it
                    .findViewById<AbstractListView<*, *>>(R.id.communicator_conversation_fragment_sbis_list_view)
                    ?.recyclerView
                    ?.adapter
                    ?.castTo()
                    ?: MessagesListAdapter(
                        viewPoolsHolder,
                        conversationComponent.listDateViewUpdater,
                        isFullViewMode
                    )
                val listener = if (isFullViewMode) {
                    presenter
                } else {
                    ConversationPreviewActionListener(this, presenter)
                }
                adapter?.actionsListener?.init(listener)
            }

    private val conversationComponent by lazy {
        val conversationInfo = conversationInfoFromArguments()
        DaggerConversationComponent.builder()
            .viewModelStoreOwner(this)
            .sbisConversationSingletonComponent(singletonComponent)
            .conversationOpenData(conversationInfo)
            .viewModel(viewModel)
            .build()
    }

    override val conversationUuid: UUID?
        get() = presenter.conversationUuid

    override fun onCreate(savedInstanceState: Bundle?) {
        Trace.beginAsyncSection("ConversationFragment.onCreate", 0)
        super.onCreate(savedInstanceState)
        initMediaPlayerSessionHelper()
        isTablet = DeviceConfigurationUtils.isTablet(requireContext())
        landscape = DeviceConfigurationUtils.isLandscape(requireContext())
        listUpdateHelper.init(savedInstanceState == null && !isNewConversation && activity !is ConversationActivity)

        savedInstanceState?.let {
            progressDialogFragment = childFragmentManager.findFragmentByTag(PROGRESS_DIALOG_FRAGMENT_TAG) as BaseProgressDialogFragment?
            presenter.restorePopupMenuVisibility(it.getBoolean(WAS_POPUP_MENU_VISIBLE))
        }
        MessageUuidMediator().setResultListener(this) {
            presenter.onQuoteClicked(it)
            onBackPressed()
        }
        Trace.endAsyncSection("ConversationFragment.onCreate", 0)
    }

    private lateinit var router: ConversationRouter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Trace.beginAsyncSection("ConversationFragment.onCreateView", 0)
        initCommunicatorRouter(this)
        router = ConversationRouterImpl(
            this,
            R.id.communicator_conversation_fragment_swipe_back_container,
            communicatorConversationRouter,
            phoneNumberActionHelper
        )
        presenter.setRouter(
            router
        )
        if (savedInstanceState == null && isFirstActivityLaunch) {
            needToShowKeyboard = arguments?.getBoolean(IntentAction.Extra.NEED_TO_SHOW_KEYBOARD, false) ?: false
        } else {
            restoreStateFromBundle(savedInstanceState)
        }
        val mainView = conversationView!!
        initViews(mainView, savedInstanceState)
        initViewListeners()
        Trace.endAsyncSection("ConversationFragment.onCreateView", 0)
        return addToSwipeBackLayout(mainView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Trace.beginAsyncSection("ConversationFragment.onViewCreated", 0)
        // Подписку необходимо выполнить до onViewCreated, в котором вызовется attachView на презентере.
        subscribeOnViewModel()
        subscribeOnMessageSearchHelper()
        super.onViewCreated(view, savedInstanceState)
        updateSwipeBack()
        childFragmentManager.addOnBackStackChangedListener(this::updateSwipeBack)
        childFragmentManager.setFragmentResultListener(CONVERSATION_INFO_SELECTION_RESULT_KEY, this) { _, bundle ->
            presenter.onParticipantsScreenClosed()
            val selectedPersonUuid = bundle.getSerializable(CONVERSATION_INFO_SELECTION_RESULT_UUID_KEY) as? UUID
            selectedPersonUuid?.let {
                presenter.onDialogParticipantChoosed(it)
                showKeyboard()
            }
        }
        if (isFullViewMode) {
            requireActivity().supportFragmentManager.setFragmentResultListener(CONVERSATION_PREVIEW_RESULT, this) { _, result ->
                presenter.handleConversationPreviewAction(
                    result.getSerializableUniversally<MessageConversationPreviewMenuAction>(CONVERSATION_PREVIEW_RESULT)!!
                )
            }
        }
        Trace.endAsyncSection("ConversationFragment.onViewCreated", 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        phoneNumberActionHelper.onRequestPermissionsResult(requestCode, grantResults) {
            showWarningPopup(RCommon.string.common_no_permission_error)
        }
    }

    override fun showPhoneNumberActionsList(messageUuid: UUID?, actions: List<Int>) {
        messageUuid ?: return
        if (presenter.actionsMenuShown) return
        val rootView = view ?: return

        if (currentKeyboardOffset != 0) {
            if (needToShowKeyboard) hideKeyboard()
            rootView.postDelayed({ showPhoneNumberActionsList(messageUuid, actions) }, 70)
            return
        }

        val recyclerView = preparedRecyclerView() ?: return
        val messagePosition = adapter!!.getPositionForMessageByUuid(messageUuid)

        val (anchor, alignment) = getAnchorAndAlignment(messagePosition, recyclerView)

        if (anchor != null && alignment != null) {
            val sbisMenu = SbisMenu(children = actions.mapIndexed { index, action -> getPhoneNumberActionMenu(action, index) })
            showMenu(recyclerView, sbisMenu, anchor, alignment, DimType.SOLID)
        }
    }

    private fun getPhoneNumberActionMenu(action: Int, actionIndex: Int): DefaultItem {
        val icon = when (action) {
            RCommunicatorDesign.string.communicator_selected_phone_number_action_copy -> SbisMobileIcon.Icon.smi_copy
            RCommunicatorDesign.string.communicator_selected_phone_number_action_call -> SbisMobileIcon.Icon.smi_phone
            RCommunicatorDesign.string.communicator_selected_phone_number_action_add -> SbisMobileIcon.Icon.smi_workers
            else -> null
        }
        return DefaultItem(title = getString(action), image = icon) {
            presenter.onPhoneNumberActionClick(actionIndex)
        }
    }

    private fun subscribeOnViewModel() {
        if (disposer.isDisposed) disposer = CompositeDisposable()
        disposer += viewModel.menuIconObservable.subscribe { isVisible ->
            if (editTitleDoneButton?.isVisible == true || !isFullViewMode) return@subscribe
            toolbarMoreButton!!.isVisible = isVisible
        }
        disposer += viewModel.documentNameObservable.subscribe { documentPlateView?.isVisible = it }
        disposer += viewModel.enableInputObservable.subscribe(::changeMessagePanelVisibility)
        disposer += viewModel.showKeyboardOnFirstLaunch.subscribe { if (it) showKeyboard() }
        disposer += viewModel.recipientsPanelVisibility.subscribe { recipientsPanelView?.isVisible = it }
        viewModel.requireKeyboardOnFirstLaunch.onNext(isNewConversation && keyboardShowingRule)
        disposer += viewModel.currentStubContent.subscribe { showStubView(it) }
        disposer += viewModel.stubIsVisible.subscribe { isVisible ->
            if (isVisible) {
                viewModel.currentStub.value?.let { showStubView(it) }
            } else {
                hideStubView()
            }
        }
    }

    private fun subscribeOnMessageSearchHelper() {
        viewLifecycleOwner.lifecycleScope.launch {
            messageSearchHelper.isSearchModeActive.collect { isActive ->
                if (isActive) {
                    messageSearchHelper.initThemeMessageSearchApi(presenter.prepareSearchMode())
                    subscribeAfterActivatedSearchMode()
                    activateSearchMode()
                    topNavigation?.backBtn?.setOnClickListener {
                        messageSearchHelper.deactivateSearchMode()
                    }
                } else {
                    deactivateSearchMode()
                    topNavigation?.backBtn?.setOnClickListener {
                        hideKeyboard()
                        requireActivity().onBackPressed()
                    }
                    personInputLayout?.searchInput?.clearSearch()
                }
            }
        }
    }

    private fun subscribeAfterActivatedSearchMode() {
        viewLifecycleOwner.lifecycleScope.launch {
            messageSearchHelper.suggestedPersons.collect { persons ->
                if (persons.isNotEmpty()) {
                    personSuggestView.init {
                        messageSearchHelper.onPersonSelected(it)
                        personInputLayout?.personFilter = it
                    }
                    personSuggestView.data = persons
                    personSuggestView.visibility = View.VISIBLE
                } else {
                    personSuggestView.visibility = View.GONE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            messageSearchHelper.navigateToMessageIdEvent.collect { messageId ->
                presenter.onQuoteClicked(messageId)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            messageSearchHelper.isMessagePanelVisible.collect { isVisible ->
                viewModel.showMessagePanel.onNext(isVisible)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            messageSearchHelper.isNavigationButtonsVisible.collect { isVisible ->
                val visibility = if (isVisible) View.VISIBLE else View.GONE
                buttonPrevious.visibility = visibility
                buttonNext.visibility = visibility
                buttonSwitchViewMode.visibility = visibility
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            messageSearchHelper.isListViewMode.collect { isListViewMode ->
                if (isListViewMode) {
                    //foundMessagesRecyclerView.visibility = View.VISIBLE
                    //  messagesRecyclerView.visibility = View.GONE
                } else {
                    // foundMessagesRecyclerView.visibility = View.GONE
                    //  messagesRecyclerView.visibility = View.VISIBLE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            messageSearchHelper.currentFoundMessageIndex.collect {
                val messageIds = messageSearchHelper.foundMessageIds.firstOrNull() ?: emptyList()
                buttonPrevious.isEnabled = messageIds.isNotEmpty()
                buttonNext.isEnabled = messageIds.isNotEmpty()
            }
        }
    }

    /**
     * Изменить видимость панели сообщений. Применим для закрытых каналов.
     * Метод изменяет видимость панели сообщений, крепит кнопку скролла к нужной границе
     * и меняет нижние отступы списка.
     */
    private fun changeMessagePanelVisibility(isVisible: Boolean) {
        if (messagePanel?.isVisible == isVisible || !isFullViewMode) return
        messagePanel?.isVisible = isVisible
        val messagePanelHeight = messagePanel?.measuredHeight?.takeIf { isVisible } ?: 0
        sbisListView?.recyclerViewBottomPadding = defaultRecyclerBottomPadding + keyboardHeight + messagePanelHeight
        sbisButtonContainer?.updateLayoutParams<RelativeLayout.LayoutParams> {
            if (isVisible) {
                addRule(RelativeLayout.ABOVE, messagePanel!!.id)
            } else {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? =
        when {
            nextAnim == R.anim.conversation_fade_in_scale_up -> {
                super.onCreateAnimation(transit, enter, nextAnim)
            }
            !isNewConversation -> {
                listUpdateHelper.onCreateAnimation(enter, nextAnim)
                    ?: super.onCreateAnimation(transit, enter, nextAnim)
            }
            else -> super.onCreateAnimation(transit, enter, nextAnim)
        }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? =
        when (nextAnim) {
            R.anim.conversation_fade_in_scale_up -> {
                ConversationPreviewAnimatorUtils.createAnimator(requireView()).apply {
                    addListener(
                        object : Animator.AnimatorListener {
                            override fun onAnimationEnd(animation: Animator) = Unit
                            override fun onAnimationCancel(animation: Animator) = Unit
                            override fun onAnimationRepeat(animation: Animator) = Unit

                            override fun onAnimationStart(animation: Animator) {
                                closePreviewFragment()
                            }


                            private fun closePreviewFragment() {
                                val activity = activity ?: return
                                val conversationPreviewFragment = activity.supportFragmentManager.findFragmentByTag(
                                    ConversationPreviewDialogFragment::class.java.simpleName
                                ) as? DialogFragment
                                conversationPreviewFragment?.dismissAllowingStateLoss()
                            }
                        }
                    )
                }
            }
            else -> super.onCreateAnimator(transit, enter, nextAnim)
        }

    private fun updateSwipeBack() {
        val firstChildFragment = childFragmentManager.fragments.lastOrNull()
        // Включение свайпа только если нет фрагментов в контейнере, текущий сверху
        val backSwipeAvailable = !isTablet && (firstChildFragment == null || firstChildFragment is DialogFragment || firstChildFragment is SelectionMenu)
        setBackSwipeAvailability(backSwipeAvailable)
        // Не забываем про активити для создания нового диалога
        activity?.castTo<DialogCreationActivity>()?.swipeBackLayout?.isEnabled = backSwipeAvailable
    }

    override fun onResume() {
        if (isFirstActivityLaunch) finishConversationOpenMetric()
        super.onResume()
    }

    override val keyboardShowingRule: Boolean
        get() = (super.keyboardShowingRule
                && presenter.isRecipientSelectionClosed()
                //условие для отложенного показа клавиатуры на момент отображения данных
                && notShowKeyboardOnResumeForTabletNewConversation)

    private val notShowKeyboardOnResumeForTabletNewConversation
        get() = !(isFirstActivityLaunch && isNewConversation && isTablet)

    private fun finishConversationOpenMetric() {
        Observable.timer(resources.getInteger(RDesign.integer.animation_activity_translate_duration).toLong(), TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { MetricsDispatcher.stopTrace(MetricsType.FIREBASE_OPEN_DIALOG) }
            .storeIn(disposer)
    }

    override fun onDestroyView() {
        swipeBackLayout?.changeSwipeBackAvailability(true)
        clearPinnedChatMessageView()
        presenter.setRouter(null)
        translateMessagePanel(0f)
        keyboardAnimator?.cancel()
        conversationView = null
        documentPlateView = null
        pinnedChatMessageView = null
        recipientsPanelView = null
        audioRecorder = null
        videoRecorder = null
        changeMessagePanelAlpha(isVisible = true)
        disposer.dispose()
        removeEditTitleDoneButton()
        viewLifecycleOwner.lifecycleScope.coroutineContext.cancelChildren()
        super.onDestroyView()
    }

    override fun onDestroy() {
        progressDialogFragment = null
        detachCommunicatorRouter()
        adapter?.actionsListener?.clear()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(MESSAGE_PANEL_ALPHA_KEY, messagePanel?.alpha ?: 1f)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DIALOG_PARTICIPANTS_ACTIVITY_CODE -> {
                presenter.onParticipantsScreenClosed()
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.extras?.let {
                        when {
                            it.containsKey(ConversationProvider.CONVERSATION_PARTICIPANTS_ACTIVITY_EXTRA_UUID_KEY) -> {
                                presenter.onDialogParticipantChoosed(it.getSerializable(ConversationProvider.CONVERSATION_PARTICIPANTS_ACTIVITY_EXTRA_UUID_KEY) as UUID)
                                showKeyboard()
                            }
                        }
                    }
                }
            }
            CHAT_SETTINGS_REQUEST_CODE        -> if (resultCode == Activity.RESULT_OK) {
                presenter.onChatCreatedFromDialog()
            }
            CONVERSATION_VIEWER_SLIDER_CODE   -> presenter.onViewerSliderClosed()
        }
    }

    @LayoutRes
    override fun getLayoutRes(): Int = R.layout.communicator_fragment_conversation

    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        super.initViews(mainView, savedInstanceState)
        mainLayout = mainView.findViewById(R.id.communicator_layout_main)
        sbisListView = mainView.findViewById(R.id.communicator_conversation_fragment_sbis_list_view)
        pinnedChatMessageView = mainView.findViewById(R.id.communicator_pinned_chat_message_holder)
        headerDateView = mainView.findViewById(R.id.communicator_conversation_header_date_view)
        messagePanelKeyboardOffsetView = mainView.findViewById(R.id.communicator_message_panel_keyboard_offset)
        messagePanelMovablePanelContainer = mainView.findViewById(R.id.communicator_conversation_message_panel_movable_panel_container)
        documentPlateView = mainView.findViewById<DocumentPlateView>(RCommunicatorCommon.id.communicator_document_plate_view_id)
            .apply {
                if (isFullViewMode) {
                    setOnClickListener {
                        ThrottleActionHandler.INSTANCE.handle {
                            forceHideKeyboard()
                            view?.postDelayed({ presenter.openDocument() }, MS_DELAY_FOR_CLOSING_THE_KEYBOARD)
                        }
                    }
                }
            }

        initTopNavigation(mainView)
        initMessagePanel(mainView, savedInstanceState)
        initScrollButton(mainView)

        layoutManager = ConversationLayoutManager(
            requireContext(),
            laidOutItemsListener
        ).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        defaultRecyclerBottomPadding = resources.getDimensionPixelOffset(
            RCommunicatorDesign.dimen.communicator_messages_list_bottom_padding
        )
        sbisListView?.apply {
            recyclerViewBottomPadding = defaultRecyclerBottomPadding + (messagePanel?.measuredHeight?.takeIf { isFullViewMode } ?: 0)
            setLayoutManager(this@ConversationFragment.layoutManager!!)
            setRecyclerViewVerticalScrollbarEnabled(true)
            recyclerView.also {
                it.id = R.id.communicator_conversation_fragment_recycler_view_id
                it.itemAnimator = null
                it.fitsSystemWindows = false
                it.setHasFixedSize(true)
                it.stopScroll()
                if (it.adapter == null) setAdapter(adapter)
                conversationComponent.listDateViewUpdater.bind(it, headerDateView!!)
                if (!isFullViewMode) {
                    it.setOnTouchListener(ConversationPreviewOnTouchListener {
                        parentFragmentManager.setFragmentResult(
                            CONVERSATION_CLICKED,
                            Bundle()
                        )
                    })
                }
            }
        }
    }

    private fun initTopNavigation(rootView: View) {
        val topNavigation = rootView.findViewById<SbisTopNavigationView>(R.id.communicator_conversation_fragment_top_navigation)!!
        this.topNavigation = topNavigation
        initMoreToolbarButton()
        initPersonInputLayout()
        topNavigation.apply {
            smallTitleMaxLines = if (isEditingEnabled) EDIT_TITLE_MAX_LINES else 1
            subtitleView!!.setExtension(ConversationSubtitleExtension())
            initPersonView()
            if (isFullViewMode) setOnClickListener { onToolbarClick() }
            initBackButton()
        }
    }

    private fun initPersonInputLayout() {
        val searchInput = SearchInput(ContextThemeWrapper(this.context, R.style.CommunicatorSearchInputTheme)).apply {
            isRoundSearchInputBackground = true
        }
        viewLifecycleOwner.lifecycleScope.launch {
            searchInput.searchQueryChangedObservable().asFlow().collect {
                messageSearchHelper.onSearchQueryChanged(it)
            }
        }

        personInputLayout = PersonInputLayout(requireContext()).apply {
            id = R.id.communicator_conversation_search_message_person_input_layout
            addView(searchInput, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            listener = object : PersonInputLayoutListener {
                override fun onPersonClick(personUuid: UUID) {
                    communicatorConversationRouter.showProfile(personUuid)
                }

                override fun onCancelPersonFilterClick() {
                    messageSearchHelper.onPersonSelected(null)
                }
            }
        }
        topNavigation?.leftCustomViewContainer?.addView(personInputLayout)
        topNavigation?.leftCustomViewContainer?.isVisible = false
    }

    private fun activateSearchMode() {
        if (buttonPrevious.parent == null) {
            mainLayout?.addView(buttonPrevious)
            mainLayout?.addView(buttonNext)
            mainLayout?.addView(buttonSwitchViewMode)
        }
        topNavigation?.personView?.isVisible = false
        topNavigation?.subtitleView?.isVisible = false
        topNavigation?.titleView?.isVisible = false
        topNavigation?.rightBtnContainer?.isVisible = false
        topNavigation?.leftCustomViewContainer?.isVisible = true
    }

    private fun deactivateSearchMode() {
        if (buttonPrevious.parent != null) {
            mainLayout?.removeView(buttonPrevious)
            mainLayout?.removeView(buttonNext)
            mainLayout?.removeView(buttonSwitchViewMode)
        }
        topNavigation?.personView?.isVisible = true
        topNavigation?.subtitleView?.isVisible = true
        topNavigation?.titleView?.isVisible = true
        topNavigation?.rightBtnContainer?.isVisible = true
        topNavigation?.leftCustomViewContainer?.isVisible = false
    }

    private fun initMoreToolbarButton() {
        val moreToolbarButton = topNavigation!!.findViewById<View>(R.id.communicator_conversation_toolbar_icon)!!
        this.toolbarMoreButton = moreToolbarButton
        val hideToolbarButtonAndDeleteAction = arguments?.getBoolean(
            IntentAction.Extra.HIDE_TOOLBAR_BUTTON_AND_DELETE_ACTION,
            false
        ) ?: false
        moreToolbarButton.isVisible = !hideToolbarButtonAndDeleteAction && isFullViewMode
        moreToolbarButton.setOnClickListener {
            hideKeyboard()
            it.post { presenter.onToolbarMenuIconClicked() }
        }
    }

    private fun SbisTopNavigationView.initPersonView() {
        personView!!.isVisible = true
        personView!!.setOnClickListener {
            if (isFullViewMode) {
                ThrottleActionHandler.INSTANCE.handle {
                    forceHideKeyboard()
                    view?.postDelayed({ presenter.onTitlePhotoClick() }, MS_DELAY_FOR_CLOSING_THE_KEYBOARD)
                }
            }
        }
    }

    private fun SbisTopNavigationView.initBackButton() {
        // Флаг для отображения кнопки "назад" в тулбаре. Охватывает кейсы, когда на планшете
        // на некоторых экранах нужно "провалиться" в фрагмент диалога и затем вернуться обратно
        val needBackButton = arguments?.getBoolean(IntentAction.Extra.NEED_TO_ADD_FRAGMENT_TO_BACKSTACK, false)
            ?: false
        showBackButton = (!isTablet || needBackButton) && isFullViewMode
        backBtn!!.setOnClickListener {
            hideKeyboard()
            requireActivity().onBackPressed()
        }
    }

    private fun onToolbarClick() {
        ThrottleActionHandler.INSTANCE.handle {
            presenter.beforeToolbarClick()
            view?.postDelayed(
                { presenter.onToolbarClick()  },
                MS_DELAY_FOR_CLOSING_THE_KEYBOARD
            )
        }
    }

    private fun initMessagePanel(rootView: View, savedInstanceState: Bundle?) {
        messagePanel = rootView.findViewById<MessagePanel>(R.id.communicator_conversation_fragment_message_panel).apply {
            alpha = savedInstanceState?.getFloat(MESSAGE_PANEL_ALPHA_KEY) ?: 1f
            recipientsPanelView = findViewById(RMessagePanel.id.message_panel_recipients_view)
            isVisible = isFullViewMode
        }
        topNavigation?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            messagePanel?.setTopOffset(topNavigation?.bottom ?: 0)
        }
        if (savedInstanceState == null) {
            configureMessagePanel()
        }
    }

    private fun configureMessagePanel() {
        if (conversationComponent.initialCoreConversationInfo.conversationUuid == null) {
            messagePanel?.isSendButtonEnabled = false
        }
    }

    override fun initMessagePanelController(
        coreConversationInfo: ru.tensor.sbis.message_panel.model.CoreConversationInfo?,
        viewerSliderArgsFactory: ViewerSliderArgsFactory
    ): CommunicatorMessagePanelController {
        val controller = super.initMessagePanelController(coreConversationInfo, viewerSliderArgsFactory)
        initAudioRecorderDelegate(controller)
        initVideoRecorderDelegate(controller)
        return controller
    }

    override fun onMessageSelected(messageUUID: UUID) {
        viewLifecycleOwner.lifecycleScope.launch {
           messageSearchHelper.onMessageClicked(messageUUID)
        }
    }

    override fun setToolbarData(toolbarData: ToolbarData) {
        super.setToolbarData(toolbarData)
        setEditToolbarData(toolbarData)
    }

    override fun showStubView(stubContent: StubViewContent) {
        val currentStub = viewModel.currentStub
        if (currentStub.value != stubContent) {
            currentStub.onNext(stubContent)
        }
        sbisListView?.showInformationViewData(stubContent)
    }

    private fun setEditToolbarData(toolbarData: ToolbarData) {
        topNavigation?.apply {
            when {
                // Состояние активированной редакции.
                toolbarData.editingState == ToolbarTitleEditingState.ENABLED && !isEditingEnabled -> {
                    startEditTitle(toolbarData.conversationName)
                }
                // Состояние подтвержденной редакции.
                isEditingEnabled && toolbarData.editingState == ToolbarTitleEditingState.COMPLETED -> {
                    completeEditTitle()
                }
                // Состояние оконченной редакции.
                editTitleDoneButton != null && toolbarData.editingState == ToolbarTitleEditingState.DISABLED -> {
                    finishEditTitle()
                }
            }
        }
    }

    private fun startEditTitle(conversationName: String? = null) {
        topNavigation?.apply {
            isEditingEnabled = true
            smallTitleMaxLines = EDIT_TITLE_MAX_LINES
            titleView!!.placeholder = resources.getString(RCommunicatorDesign.string.communicator_dialog_name)
            subtitleView?.isVisible = false
            titleView!!.value = conversationName ?: StringUtils.EMPTY
            titleView!!.onValueChanged = { _, value ->
                changeEditTitleDoneButtonVisibility(isVisible = value.isNotEmpty())
            }

            if (editTitleDoneButton == null) addEditTitleDoneButton()
            changeEditTitleDoneButtonVisibility(isVisible = titleView!!.value.isNotEmpty())
        }
    }

    /**
     * Подтверить редакцию темы в заголовке, но не завершать.
     */
    private fun completeEditTitle() {
        topNavigation?.apply {
            isEditingEnabled = false
            smallTitleMaxLines = 1
            titleView!!.placeholder = StringUtils.EMPTY
            titleView!!.onValueChanged = null
            changeEditTitleDoneButtonVisibility(isVisible = false)
        }
    }

    /**
     * Завершить редакцию темы в заголовке.
     */
    private fun finishEditTitle() {
        topNavigation?.apply {
            completeEditTitle()
            removeEditTitleDoneButton()
            subtitleView!!.isVisible = true
        }
    }

    private fun addEditTitleDoneButton() {
        val doneButton = SbisRoundButton(requireContext()).apply {
            id = R.id.communicator_conversation_edit_title_done_button
            icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_checked.character.toString())
            style = SuccessButtonStyle
            size = SbisRoundButtonSize.S
            setPadding(context.dpToPx(10))
        }
        editTitleDoneButton = doneButton
        doneButton.preventDoubleClickListener {
            presenter.onCompleteTitleEditClicked(topNavigation?.titleView?.value ?: StringUtils.EMPTY)
        }
        topNavigation!!.rightBtnContainer!!.addView(doneButton)
    }

    private fun removeEditTitleDoneButton() {
        changeEditTitleDoneButtonVisibility(false)
        editTitleDoneButton?.also {
            (it.parent as ViewGroup).removeView(it)
            editTitleDoneButton = null
        }
    }

    private fun changeEditTitleDoneButtonVisibility(isVisible: Boolean) {
        editTitleDoneButton?.isVisible = isVisible
        toolbarMoreButton?.isVisible = !isVisible
    }

    override fun changeRecordEnable(isAudioEnabled: Boolean, isVideoEnabled: Boolean) {
        audioRecorder?.isEnabled = isAudioEnabled
        videoRecorder?.isEnabled = isVideoEnabled
    }

    private fun initAudioRecorderDelegate(controller: MessagePanelController<*, *, *>) {
        val audioRecordView = requireView().findViewById<AudioRecordView>(R.id.communicator_conversation_audio_record_view)
        val emotionPicker = requireView().findViewById<MessageEmotionPicker>(R.id.communicator_conversation_emotion_picker)
        audioRecorder = singletonComponent.dependency.audioRecorderDelegateFactory
            .createRecorderDelegate(
                fragment = this,
                audioRecordView = audioRecordView,
                messagePanel = messagePanel!!,
                messagePanelController = controller,
                emotionPicker = emotionPicker
            ).apply {
                setRecordResultListener { presenter.onRecordCompleted() }
                setRecordStateChangedListener(presenter::onAudioRecordStateChanged)
            }
    }

    private fun initVideoRecorderDelegate(controller: MessagePanelController<*, *, *>) {
        val videoRecordView = requireView().findViewById<VideoRecordView>(R.id.communicator_conversation_video_record_view)
        videoRecorder = singletonComponent.dependency.videoRecorderDelegateFactory
            .createRecorderDelegate(
                fragment = this,
                messagePanel = messagePanel!!,
                messagePanelController = controller,
                videoRecordView = videoRecordView,
                headerDateView = headerDateView
            ).apply {
                setRecordResultListener { presenter.onRecordCompleted() }
                setRecordStateChangedListener(presenter::onVideoRecordStateChanged)
                setOnRecordClickListener { audioRecorder?.hideEmotionPicker() }
            }
    }

    private fun initMediaPlayerSessionHelper() {
        singletonComponent.dependency
            .mediaPlayerFeature
            ?.getMediaPlayerSessionHelper()
            ?.init(this)
    }

    private fun changeMessagePanelAlpha(isVisible: Boolean) {
        messagePanel?.apply {
            alpha = if (isVisible) 1f else 0f
            isInputLocked = !isVisible
        }
    }

    /** @SelfDocumented */
    override fun swipeBackEnabled(): Boolean =
        !(isTablet && getActivityAs<ConversationActivity>() == null) && isFullViewMode

    /** Действие на кнопку *назад*, спрятать клавиатуру */
    override fun onBackPressed(): Boolean {
        hideKeyboard()
        return childFragmentManager.run {
            (fragments.lastOrNull()?.castTo<FragmentBackPress>()?.onBackPressed() ?: false) ||
                popBackStackImmediate() || presenter.onBackPressed() || super.onBackPressed()
        }
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        if (this.keyboardHeight != keyboardHeight) {
            this.keyboardHeight = keyboardHeight
            keyboardAnimator?.cancel()
            if (animateKeyboardShowing) {
                keyboardAnimator = getKeyboardAnimator(keyboardHeight).also {
                    additionalOffset = 0
                    it.start()
                }
                /*
                Из-за мода MainActivity windowSoftInputMode = adjustPan - вынужденная мера мгновенно поднимать панель,
                чтобы не загараживать ее, иначе система поднимет экран.
                 */
                if (keyboardHeight > currentKeyboardOffset) {
                    messagePanel?.onKeyboardOpenMeasure(keyboardHeight)
                    translateMessagePanel(-keyboardHeight.toFloat())
                }
                animateKeyboardShowing = false
            } else {
                currentKeyboardOffset = keyboardHeight
                presenter.onKeyboardAppears(keyboardHeight)
                setListViewBottomPadding(keyboardHeight)
                scrollListView(keyboardHeight, false)
                translateMessagePanel(-keyboardHeight.toFloat())
                messagePanel?.onKeyboardOpenMeasure(keyboardHeight)
            }
            needToShowKeyboard = true
        }
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        if (this.keyboardHeight != 0) {
            this.keyboardHeight = 0
            keyboardAnimator?.cancel()
            /*
            Если клавиатура поднялась хотя бы на половину - анимируем опускание, иначе мгновенно все опускаем.
            Такое поведение предотвращает анимации на доли секунды, когда закрывается клавиатура при уходе с экрана
            уровнем выше, текущий фрагмент уже в состоянии Resumed и до него долетают колбэки onKeyboardOpen и CloseMeasure.
             */
            if (currentKeyboardOffset >= keyboardHeight / 2 &&
                audioRecorder?.state?.isRecording != true &&
                videoRecorder?.state?.isRecording != true &&
                !listUpdateHelper.isAnimationRunning
            ) {
                keyboardAnimator = getKeyboardAnimator(0).also {
                    it.start()
                }
            } else {
                onKeyboardClosed()
            }
            needToShowKeyboard = false
        }
        return true
    }

    override fun forceHideKeyboard() {
        if (keyboardHeight == 0) return
        hideKeyboard()
        onKeyboardClosed()
    }

    /**
     * Привести View к состоянию полного закрытия клавиатуры.
     */
    private fun onKeyboardClosed() {
        keyboardAnimator?.cancel()
        presenter.onKeyboardDisappears(currentKeyboardOffset)
        setListViewBottomPadding(0)
        scrollListView(-currentKeyboardOffset, false)
        translateMessagePanel(0f)
        messagePanel?.onKeyboardCloseMeasure(0)

        keyboardHeight = 0
        currentKeyboardOffset = 0
    }

    /** @SelfDocumented */
    override fun showKeyboard() {
        super.showKeyboard()
    }

    override fun focusMessagePanel() {
        messagePanel?.requestFocus()
    }

    override fun focusEditTitle() {
        KeyboardUtils.showKeyboard(topNavigation?.titleView!!)
    }

    override fun cancelMessageRecording() {
        audioRecorder?.cancelRecording()
        videoRecorder?.cancelRecording()
    }

    /** @SelfDocumented */
    override fun onYes(requestCode: Int, text: String?) {
        if (requestCode == DIALOG_CODE_CHANGE_TOPIC && text != null) presenter.setDialogTitle(text)
        onYes(requestCode)
    }

    override fun onYes(requestCode: Int) {
        when (requestCode) {
            DIALOG_CODE_CONFIRM_DELETE_DIALOG -> presenter.onDialogDeletingClicked()
            DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL -> presenter.onDialogDeletingConfirmed()
            DIALOG_CODE_CONFIRM_HIDE_CHAT -> presenter.onHideChatConfirmed()
            DIALOG_CODE_DELETED_DIALOG -> presenter.close()
            DIALOG_CODE_CONFIRM_DELETE_MESSAGE_FOR_ME -> presenter.deleteMessageOnlyForMe()
            DIALOG_CODE_CONFIRM_CANCEL_RECORDING -> presenter.onCancelRecordingDialogResult(isConfirmed = true)
            else -> super.onYes(requestCode)
        }
    }

    override fun onNo(requestCode: Int, text: String?) {
        onNo(requestCode)
    }

    override fun onNo(requestCode: Int) {
        if (requestCode == DIALOG_CODE_CONFIRM_CANCEL_RECORDING) {
            presenter.onCancelRecordingDialogResult(isConfirmed = false)
        }
    }

    override fun onNeutral(dialogCode: Int) = Unit

    override fun onItem(dialogCode: Int, option: Int) = Unit

    override fun onItemClicked(requestCode: Int, itemValue: String?) {
        when (requestCode) {
            DIALOG_CODE_CONFIRM_QUIT_CHAT -> chatLeaveConfirmation(requestCode, itemValue)
            DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL -> deleteMyDialogOrOutgoingMessage(requestCode, itemValue)
            else -> super.onItemClicked(requestCode, itemValue)
        }
    }

    private fun chatLeaveConfirmation(requestCode: Int, itemValue: String?) {
        when (itemValue) {
            getString(RCommunicatorDesign.string.communicator_chat_leave_confirmation_yes) -> {
                presenter.onQuitChatConfirmed()
            }
            getString(RCommunicatorDesign.string.communicator_channel_leave_confirmation_yes_and_remove) -> {
                presenter.onQuitAndHideChatConfirmed()
            }
            getString(RCommunicatorDesign.string.communicator_chat_leave_confirmation_no) -> {
                super.onItemClicked(requestCode, itemValue)
            }
        }
    }

    //region View

    override fun showProgressDialog(@StringRes textResId: Int, cancellable: Boolean) {
        if (progressDialogFragment == null) {
            progressDialogFragment = BaseProgressDialogFragment.newInstance(cancellable)
        }

        progressDialogFragment!!.init(null, getString(textResId))
        progressDialogFragment!!.showNow(childFragmentManager, PROGRESS_DIALOG_FRAGMENT_TAG)
    }

    override fun hideProgressDialog() {
        progressDialogFragment?.let {
            if (it.isAdded) {
                it.dismiss()
                progressDialogFragment = null
            }
        }
    }

    override fun showSigningActionsMenu(onlySignButton: Boolean) {
        messagePanel?.let { panel ->
            forceHideKeyboard()
            val recyclerView = preparedRecyclerView() ?: return
            val sbisMenu = SbisMenu(
                children = getOptions(onlySignButton).map { getSigningOptionMenuItem(it) },
            )
            val anchor = panel.findViewById<View>(RMessagePanel.id.message_panel_sign_button) ?: panel
            showMenu(recyclerView, sbisMenu, anchor, HorizontalAlignment.RIGHT, DimType.SOLID)
        }
    }

    private fun getSigningOptionMenuItem(option: DocumentSigningOption) = MenuItem(
        title = requireContext().getString(option.textRes),
        destructive = option.destructive,
        state = MenuItemState.OFF,
    ) {
        when (option) {
            DocumentSigningOption.SIGN -> presenter.onSignMenuItemClicked()
            DocumentSigningOption.REQUEST -> presenter.onRequestSignatureMenuItemClicked()
            DocumentSigningOption.SIGN_AND_REQUEST -> presenter.onSignAndRequestMenuItemClicked()
            DocumentSigningOption.CANCEL -> Unit
        }
    }

    override fun showAttachmentsSigning(attachmentsUuids: List<UUID>) {
        singletonComponent.dependency.attachmentsSigningProvider?.let { signingProvider ->
            val signingFragment = signingProvider
                .getAttachmentsSigningFragment(
                    UrlUtils.FILE_SD_OBJECT,
                    attachmentsUuids.map { it.toString() },
                    needShowAlreadySignedError = false
                )
                .also { it.setTargetFragment(this, -1) }

            parentFragmentManager
                .beginTransaction()
                .add(R.id.communicator_layout_main, signingFragment)
                .commit()
        }
    }

    override fun onAttachmentsSignDone() {
        presenter.messageFileSigningSuccess()
    }

    override fun onAttachmentsSignFailed() {
        presenter.messageFileSigningFailure()
    }

    override fun setDocumentPlateData(data: DocumentPlateViewModel) {
        documentPlateView?.bindData(data)
    }

    override fun showUnattachedPhoneError(errorText: String?) {
        val isKeyboardClosed = safeOnClosedKeyboard { showUnattachedPhoneError(errorText) }
        if (!isKeyboardClosed) return
        presenter.onPhoneVerificationRequired(errorText)
    }

    override fun showCancelRecordingConfirmationDialog() {
        singletonComponent.dependency.getRecordCancelConfirmationDialog(
            requireContext(),
            DIALOG_CODE_CONFIRM_CANCEL_RECORDING
        ).show(childFragmentManager, PopupConfirmation::class.simpleName)
    }

    override fun showDeletingConfirmationDialog(@StringRes deleteConfirmationString: Int) {
        PopupConfirmation.newMessageInstance(
            DIALOG_CODE_CONFIRM_DELETE_DIALOG,
            getString(RCommunicatorDesign.string.communicator_delete_dialog_forever_message)
        ).also {
            it.requestTitle(getString(deleteConfirmationString))
            it.requestTitleMaxLines(3)
            it.requestPositiveButton(getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog), true)
            it.requestNegativeButton(getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_negative))
            it.setEventProcessingRequired(true)
        }.show(childFragmentManager, PopupConfirmation::class.simpleName)
    }

    override fun showComplainDialogFragment(complainUseCase: ComplainUseCase) {
        communicatorSbisConversationDependency?.complainFragmentFeature?.showComplainDialogFragment(
            childFragmentManager,
            complainUseCase
        )
    }

    override fun showDialogTopicInput(dialogTheme: String?) {
        PopupConfirmation.newEditTextInstance(
            requestCode = DIALOG_CODE_CHANGE_TOPIC,
            hint = getString(RCommunicatorDesign.string.communicator_enter_dialog_name),
            maxLength = MAX_LENGTH,
            inputType = InputType.TYPE_CLASS_TEXT,
            canNotBeBlank = dialogTheme.isNullOrEmpty(),
            mustChangeInitialText = false,
            initialText = dialogTheme,
            isClearVisible = true
        ).also {
            it.requestPositiveButton(getString(RCommunicatorDesign.string.communicator_enter_dialog_name_positive_btn))
            it.requestNegativeButton(getString(RCommunicatorDesign.string.communicator_enter_dialog_name_negative_btn))
            it.setEventProcessingRequired(true)
        }.show(this.childFragmentManager, this.javaClass.canonicalName)
    }

    override fun showPopupDeleteDialogForAll() {
        showPopupWithManyButtons(
            DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL,
            RCommunicatorDesign.string.communicator_delete_dialog_forever_message,
            RMessagePanel.string.message_panel_delete_dialog_forever,
            arrayListOf(
                getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_for_all),
                getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_for_me),
                getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_negative)
            ),
            hashMapOf(Pair(getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_for_all), RModalWindows.style.ModalWindowsAlertDialogButtonRemoval))
        )
    }

    override fun showPopupDeleteMessageForMe() {
        PopupConfirmation.newSimpleInstance(
            DIALOG_CODE_CONFIRM_DELETE_MESSAGE_FOR_ME,
        ).also {
            it.requestTitle(getString(RCommunicatorDesign.string.communicator_delete_message_forever))
            it.requestPositiveButton(getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog), true)
            it.requestNegativeButton(getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_negative))
            it.setEventProcessingRequired(true)
        }
            .show(childFragmentManager, PopupConfirmation::class.simpleName)
    }

    override fun notifyDialogRemoved() {
        if (childFragmentManager.findFragmentByTag(NOTIFY_DIALOG_REMOVED_TAG) != null) return

        val popup = PopupConfirmation.newSimpleInstance(
            DIALOG_CODE_DELETED_DIALOG,
        ).also {
            it.requestTitle(getString(RCommunicatorDesign.string.communicator_alert_dialog_title_dialog_removed))
            it.requestPositiveButton(getString(RCommunicatorDesign.string.communicator_alert_info_continue))
            it.setEventProcessingRequired(true)
            it.isCancelable = false
        }
        try {
            popup.show(childFragmentManager, NOTIFY_DIALOG_REMOVED_TAG)
        } catch (ex: IllegalStateException) {
            Timber.w(ex, "ConversationFragment.notifyDialogRemoved error: conversation was already closed.")
        }
    }

    override fun showLeaveChatConfirmationDialog() {
        showPopupWithManyButtons(
            DIALOG_CODE_CONFIRM_QUIT_CHAT,
            RCommunicatorDesign.string.communicator_channel_leave_confirmation_subtitle,
            RCommunicatorDesign.string.communicator_channel_leave_confirmation_title,
            arrayListOf(
                getString(RCommunicatorDesign.string.communicator_chat_leave_confirmation_yes),
                getString(RCommunicatorDesign.string.communicator_channel_leave_confirmation_yes_and_remove),
                getString(RCommunicatorDesign.string.communicator_chat_leave_confirmation_no)
            )
        )
    }

    override fun showHideChatConfirmation() {
        PopupConfirmation.newSimpleInstance(DIALOG_CODE_CONFIRM_HIDE_CHAT).also {
            it.requestTitle(getString(RCommunicatorDesign.string.communicator_theme_remove_channel_alert_title))
            it.requestPositiveButton(getString(RCommon.string.dialog_button_ok))
            it.requestNegativeButton(getString(RDesign.string.design_undo))
            it.setEventProcessingRequired(true)
        }.show(childFragmentManager, PopupConfirmation::class.simpleName)
    }

    override fun showDeleteConversationConfirmation() {
        presenter.onDeleteDialog()
    }

    override fun copyLink(url: String) {
        ClipboardManager.copyToClipboard(requireContext(), url)
        showToast(RCommunicatorDesign.string.communicator_link_copied, Toast.LENGTH_LONG)
    }

    override fun showOpenDocumentErrorNotification() {
        val message = resources.getString(RCommunicatorDesign.string.communicator_opening_document_access_error)
        val icon = SbisMobileIcon.Icon.smi_information.character.toString()
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR, message, icon)
    }

    override fun showRecordError(errorRes: Int) {
        showToast(errorRes, Toast.LENGTH_LONG)
    }

    override fun showRecordInfoPopup(@StringRes infoRes: Int) {
        PopupConfirmation.newSimpleInstance(DIALOG_CODE_RECORD_MEDIA_INFO_POPUP).also {
            it.requestTitle(getString(infoRes))
            it.requestPositiveButton(getString(RCommon.string.dialog_button_ok))
            it.setEventProcessingRequired(false)
        }.show(childFragmentManager, PopupConfirmation::class.simpleName)
    }

    override fun showErrorPopup(textId: Int, icon: String?) {
        val message = getString(textId)
        showErrorPopup(message, icon)
    }

    override fun showErrorPopup(text: String?, icon: String?) {
        val message = text ?: getString(RCommunicatorDesign.string.communicator_conversation_audio_message_playback_error)
        // Это не popup, а панель информер!
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR, message, icon)
    }

    override fun showSuccessPopup(textId: Int) {
        SbisPopupNotification.push(
            context = requireContext(),
            type = SbisPopupNotificationStyle.SUCCESS,
            message = getString(textId)
        )
    }

    private fun showWarningPopup(textId: Int) {
        SbisPopupNotification.push(
            context = requireContext(),
            type = SbisPopupNotificationStyle.WARNING,
            message = getString(textId)
        )
    }

    override fun getStringRes(stringId: Int) = getString(stringId)

    /** @SelfDocumented */
    override fun changeToolbarCollageEnable(isEnabled: Boolean) {
        topNavigation?.personView?.isEnabled = isEnabled
    }

    /** @SelfDocumented */
    override fun changeSwipeBackAvailability(isAvailable: Boolean) {
        swipeBackLayout?.changeSwipeBackAvailability(isAvailable)
    }

    /** @SelfDocumented */
    override fun hideTitle() {
        topNavigation?.apply {
            titleView?.isInvisible = true
            subtitleView?.isInvisible = true
            personView?.isInvisible = true
        }
    }
    //endregion

    //region BasePresenterFragment
    override fun getPresenterView(): ConversationView = this

    override fun createPresenter(): ConversationPresenter =
        conversationComponent.conversationPresenter

    //endregion

    override fun showPinnedChatMessage(message: Message?, canUnpin: Boolean) {
        pinnedChatMessageView?.apply {
            isVisible = message != null
            setOnClickListener { message?.uuid?.run(presenter::onQuoteClicked) }
            findViewById<SbisTextView>(R.id.communicator_pinned_chat_message_author).text =
                message?.cloudSenderPersonModel?.name
            findViewById<SbisTextView>(R.id.communicator_pinned_chat_message_time).text = message?.dateOrTimeString
            findViewById<SbisTextView>(R.id.communicator_pinned_chat_message_text).text = message?.messageText?.toString()
            findViewById<SbisTextView>(R.id.communicator_pinned_chat_message_clear_button).apply {
                isVisible = canUnpin && message != null
                setOnClickListener { message?.let { presenter.unpinChatMessage() }  }
            }
            setOnLongClickListener {
                message?.uuid?.let { presenter.onQuoteOrPinnedMessageLongClicked(it) }
                true
            }
        }
    }

    /**
     * Очистить view закрепленного сообщения чата.
     * Необходимо вызывать на destroy фрагмента для переиспользования view
     */
    private fun clearPinnedChatMessageView() {
        showPinnedChatMessage(null, false)
    }

    override fun showGrantAccessMenu(message: Message, messagePosition: Int, sender: View) {
        val recyclerView = preparedRecyclerView() ?: return
        val sbisMenu = SbisMenu(
            children = listOf(
                DefaultItem(title = getString(RCommunicatorDesign.string.communicator_grant_access_view), handler = { presenter.acceptAccessRequest(message, messagePosition, DocumentAccessType.VIEW) }),
                DefaultItem(title = getString(RCommunicatorDesign.string.communicator_grant_access_change), handler = { presenter.acceptAccessRequest(message, messagePosition, DocumentAccessType.CHANGE) }),
                DefaultItem(title = getString(RCommunicatorDesign.string.communicator_grant_access_change_plus), handler = { presenter.acceptAccessRequest(message, messagePosition, DocumentAccessType.CHANGE_PLUS) })
            )
        )
        showMenu(recyclerView, sbisMenu, sender, HorizontalAlignment.CENTER, DimType.SOLID)
    }

    override fun showProgressInRejectButton(show: Boolean, messagePosition: Int) {
        adapter?.changeProgressInRejectButton(show, messagePosition)
    }

    override fun showProgressInAcceptButton(show: Boolean, messagePosition: Int) {
        adapter?.changeProgressInAcceptButton(show, messagePosition)
    }

    /** Завершить диалог с ошибкой, скрыть клавиатуру */
    override fun finishConversationActivityWithCommonError() {
        hideKeyboard()
        showToast(RCommon.string.common_loading_error, Toast.LENGTH_LONG)
        activity?.finish()
    }

    // region NavigationDelegate
    override fun initCommunicatorRouter(fragment: Fragment) {
        communicatorConversationRouter.initCommunicatorRouter(fragment)
    }

    override fun detachCommunicatorRouter() {
        communicatorConversationRouter.detachCommunicatorRouter()
    }

    override fun showConversationDetailsScreen(params: ConversationDetailsParams) {
        communicatorConversationRouter.showConversationDetailsScreen(params)
    }

    /** @SelfDocumented */
    override fun showNewsDetails(
        onNewScreen: Boolean,
        documentUuid: String?,
        messageUuid: UUID?,
        dialogUuid: UUID?,
        isReplay: Boolean,
        showComments: Boolean
    ) {
        communicatorConversationRouter.showNewsDetails(
            onNewScreen,
            documentUuid,
            messageUuid,
            dialogUuid,
            isReplay,
            showComments
        )
    }

    override fun showTask(documentUuid: String?) {
        communicatorConversationRouter.showTask(documentUuid)
    }

    override fun showProfile(uuid: UUID) {
        communicatorConversationRouter.showProfile(uuid)
    }

    override fun showViolationDetails(documentUuid: UUID?) {
        communicatorConversationRouter.showViolationDetails(documentUuid)
    }

    override fun showLinkInWebView(url: String, title: String?) {
        communicatorConversationRouter.showLinkInWebView(url, title)
    }

    override fun showVerificationFragment(registryContainerId: Int) {
        communicatorConversationRouter.showVerificationFragment(registryContainerId)
    }

    override fun changeRegistry(registryType: CommunicatorRegistryType) {
        communicatorConversationRouter.changeRegistry(registryType)
    }

    override fun changeNavigationSelectedItem(registryType: CommunicatorRegistryType) {
        communicatorConversationRouter.changeRegistry(registryType)
    }

    override fun openScreen(
        intent: Intent,
        useOverlayDetailContainer: Boolean,
        onCloseCallback: (() -> Unit)?,
        fragmentProvider: () -> Fragment?
    ) {
        communicatorConversationRouter.openScreen(
            intent,
            useOverlayDetailContainer,
            fragmentProvider = fragmentProvider
        )
    }

    override fun setSubContent(fragment: Fragment) {
        communicatorConversationRouter.setSubContent(fragment)
    }

    override fun removeSubContent() {
        communicatorConversationRouter.removeSubContent()
    }

    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        communicatorConversationRouter.onNewDeeplinkAction(args)
    }

    override fun handleDeeplinkAction(args: DeeplinkAction) {
        communicatorConversationRouter.handleDeeplinkAction(args)
    }

    override fun showArticleDiscussion(params: CommunicatorArticleDiscussionParams) {
        communicatorConversationRouter.showArticleDiscussion(params)
    }

    override fun popBackStack(): Boolean =
        communicatorConversationRouter.popBackStack()

    override fun getTopSubContent(): Fragment? =
        communicatorConversationRouter.getTopSubContent()

    override fun changeRegistrySelectedItem(uuid: UUID) {
        communicatorConversationRouter.changeRegistrySelectedItem(uuid)
    }

    override fun updateDataList(dataList: List<ConversationMessage>?, offset: Int) {
        listUpdateHelper.updateDataList(dataList, offset)
    }

    override fun updateDataListWithoutNotification(dataList: List<ConversationMessage>?, offset: Int) {
        listUpdateHelper.updateDataListWithoutNotification(dataList, offset)
    }

    override fun notifyItemsChanged(position: Int, count: Int, payLoad: Any?) {
        listUpdateHelper.notifyItemsChanged(position, count, payLoad)
    }

    override fun notifyItemsChanged(position: Int, count: Int) {
        listUpdateHelper.notifyItemsChanged(position, count)
    }

    override fun notifyDataSetChanged() {
        listUpdateHelper.notifyDataSetChanged()
    }

    override fun notifyItemsInserted(position: Int, count: Int) {
        listUpdateHelper.notifyItemsInserted(position, count)
    }

    override fun notifyItemsRemoved(position: Int, count: Int) {
        listUpdateHelper.notifyItemsRemoved(position, count)
    }

    override fun showOlderLoadingError() {
        listUpdateHelper.showOlderLoadingError()
    }

    override fun showNewerLoadingError() {
        listUpdateHelper.showNewerLoadingError()
    }

    override fun showOlderLoadingProgress(show: Boolean) {
        listUpdateHelper.showOlderLoadingProgress(show)
    }

    override fun showNewerLoadingProgress(show: Boolean) {
        listUpdateHelper.showNewerLoadingProgress(show)
    }

    override fun setRelevantMessagePosition(position: Int) {
        listUpdateHelper.setRelevantMessagePosition(position)
    }

    override fun scrollToBottom(skipScrollToPosition: Boolean, withHide: Boolean) {
        listUpdateHelper.scrollToBottom(skipScrollToPosition, withHide)
    }

    override fun scrollToPosition(position: Int) {
        listUpdateHelper.scrollToPosition(position)
    }

    override fun internalUpdateDataList(dataList: List<ConversationMessage>?, offset: Int) {
        Trace.beginAsyncSection("ConversationFragment.internalUpdateDataList, empty ${dataList?.isEmpty()}", 0)
        super.updateDataList(dataList, offset)
        Trace.endAsyncSection("ConversationFragment.internalUpdateDataList, empty ${dataList?.isEmpty()}", 0)
    }

    override fun internalUpdateDataListWithoutNotification(dataList: List<ConversationMessage>?, offset: Int) {
        super.updateDataListWithoutNotification(dataList, offset)
    }

    override fun internalNotifyItemsChanged(position: Int, count: Int, payLoad: Any?) {
        super.notifyItemsChanged(position, count, payLoad)
    }

    override fun internalNotifyItemsChanged(position: Int, count: Int) {
        super.notifyItemsChanged(position, count)
    }

    override fun internalNotifyDataSetChanged() {
        super.notifyDataSetChanged()
    }

    override fun internalNotifyItemsInserted(position: Int, count: Int) {
        super.notifyItemsInserted(position, count)
    }

    override fun internalNotifyItemsRemoved(position: Int, count: Int) {
        super.notifyItemsRemoved(position, count)
    }

    override fun internalShowOlderLoadingError() {
        super.showOlderLoadingError()
    }

    override fun internalShowNewerLoadingError() {
        super.showNewerLoadingError()
    }

    override fun internalShowOlderLoadingProgress(show: Boolean) {
        super.showOlderLoadingProgress(show)
    }

    override fun internalShowNewerLoadingProgress(show: Boolean) {
        super.showNewerLoadingProgress(show)
    }

    override fun internalSetRelevantMessagePosition(position: Int) {
        super.setRelevantMessagePosition(position)
    }

    override fun internalScrollToBottom(skipScrollToPosition: Boolean, withHide: Boolean) {
        super.scrollToBottom(skipScrollToPosition, withHide)
    }

    override fun internalScrollToPosition(position: Int) {
        super.scrollToPosition(position)
    }

    override fun showConversationOptionsMenu(options: List<ConversationOption>) {
        val anchor = toolbarMoreButton!!
        val sbisMenu = SbisMenu(
            children = options.map { getOptionMenuItem(it) }
        )
        sbisMenu.showMenuWithLocators(
            fragmentManager = childFragmentManager,
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.BOTTOM,
                force = false,
                offsetRes = dimen.context_menu_anchor_margin
            ).apply { anchorView = anchor },
            horizontalLocator = AnchorHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT,
                force = false,
                innerPosition = true,
                offsetRes = dimen.context_menu_horizontal_margin
            ).apply { anchorView = anchor },
            dimType = DimType.SOLID
        )
    }
    // endregion

    private fun getOptionMenuItem(option: ConversationOption) =
        DefaultItem(title = getString(option.textRes), image = option.iconRes, destructive = option.destructive) {
            presenter.onConversationOptionSelected(option)
        }

    @Suppress("DEPRECATION")
    private fun conversationInfoFromArguments(): CoreConversationInfo {
        fun Bundle.prepareToolbarData(
            recipientsUuids: List<UUID>,
            isChat: Boolean,
            threadInfo: ThreadInfo?
        ): ToolbarData {
            val toolbarTitle = this.getString(CONVERSATION_ACTIVITY_TOOLBAR_TITLE)
            val dialogToolbarTitle = this.getString(CONVERSATION_ACTIVITY_TOOLBAR_DIALOG_TITLE)
            val photoId = this.getString(CONVERSATION_ACTIVITY_TOOLBAR_PHOTO_ID)
            val viewData = this.getParcelableArrayListUniversally<PersonData>(CONVERSATION_ACTIVITY_TOOLBAR_VIEW_DATA)
            return ToolbarData(
                title = toolbarTitle ?: "",
                conversationName = dialogToolbarTitle,
                photoDataList = viewData.takeIf { data -> data.isNotEmpty() }
                    ?: photoId?.let { id ->
                        listOf(
                            PersonData(
                                uuid = recipientsUuids.getOrNull(0),
                                photoUrl =
                                if (id.contains(PREVIEWER_KEY)) id
                                else UrlUtils.getPhotoUrlById(
                                    id,
                                    AVATAR_PHOTO_SIZE
                                )!!
                                    .replace(
                                        "$AVATAR_PHOTO_SIZE",
                                        UNSPECIFIED_PHOTO_SIZE
                                    )
                            )
                        )
                    }
                    ?: emptyList(),
                isChat = isChat,
                subtitle = DOTS_FOR_UNLOAD_SUBTITLE
                    .takeIf { !toolbarTitle.isNullOrBlank() || !dialogToolbarTitle.isNullOrBlank() }
                    ?: "",
                editingState = if (threadInfo != null) {
                    ToolbarTitleEditingState.ENABLED
                } else {
                    ToolbarTitleEditingState.DISABLED
                }
            )
        }

        requireArguments().let {
            val conversationParams = it.getSerializable(CONVERSATION_ACTIVITY_CONVERSATION_ARG) as? ConversationParams

            var folderUuid: UUID? = null
            var docInfo: DocInfo? = null
            var conversationType: ConversationType = ConversationType.REGULAR
            var recipientsUuids: List<UUID> = arrayListOf()
            var isChat = false
            var dialogUuid: UUID? = null
            var messageUuid: UUID? = null
            var archivedConversation = false
            var isInitAsGroupDialog = false
            var highlightMessage = false

            var isFromShortcut = false
            val recipientUuid: UUID? = it.getSerializable(CONVERSATION_ACTIVITY_SENDER_UUID_KEY) as? UUID
                ?: it.getString(CONVERSATION_ACTIVITY_SENDER_UUID_KEY)?.toUuid()?.also {
                    isFromShortcut = true
                }

            if (conversationParams != null) {
                when (conversationParams) {
                    is DialogCreationParams -> {
                        folderUuid = conversationParams.folderUuid
                        docInfo = conversationParams.docInfo
                        conversationType = conversationParams.type
                    }
                    is DialogCreationWithParticipantsParams -> {
                        folderUuid = conversationParams.folderUuid
                        docInfo = conversationParams.docInfo
                        conversationType = conversationParams.type
                        recipientsUuids = conversationParams.participantsUuids
                    }
                    is ConversationOpenParams -> {
                        dialogUuid = conversationParams.conversationUuid
                        messageUuid = conversationParams.messageUuid
                        conversationType = conversationParams.type
                        isChat = conversationParams.isChat
                        docInfo = conversationParams.docInfo
                        highlightMessage = conversationParams.highlightMessage
                    }
                    is ConversationFromRegistryParams -> {
                        dialogUuid = conversationParams.conversationUuid
                        messageUuid = conversationParams.messageUuid
                        conversationType = conversationParams.type
                        isChat = conversationParams.isChat
                        docInfo = conversationParams.docInfo
                        archivedConversation = conversationParams.archivedConversation
                        isInitAsGroupDialog = conversationParams.isInitAsGroupDialog
                        highlightMessage = conversationParams.highlightMessage
                    }
                }
            } else {
                folderUuid = (it.getSerializable(CONVERSATION_ACTIVITY_FOLDER_UUID_KEY) as? UUID)?.let { uuid ->
                    if (!UUIDUtils.isNilUuid(uuid)) uuid else null
                }
                val document = it.getParcelable(EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY) as? Document
                docInfo = document?.let { DocInfo(UUIDUtils.fromString(document.uuid), document.type, document.title) }
                conversationType = it.getSerializable(EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY) as? ConversationType
                    ?: ConversationType.REGULAR
                recipientsUuids =
                    if (!isFromShortcut) {
                        UUIDUtils.fromParcelUuids(it.getParcelableArrayList(CONVERSATION_ACTIVITY_PARTICIPANTS_UUIDS_KEY))
                            ?.castTo<ArrayList<UUID>>()
                            ?.ifEmpty { null }
                            ?: singletonComponent.dependency
                                .getRecipientSelectionResultManager()
                                .selectionResult
                                .data
                                .allPersonsUuids
                                .asArrayList()
                    } else {
                        arrayListOf(recipientUuid!!)
                    }
                isChat = it.getBoolean(CONVERSATION_ACTIVITY_CHAT_KEY, false)
                dialogUuid = it.getSerializable(EXTRA_CONVERSATION_ACTIVITY_DIALOG_UUID_KEY) as? UUID
                    ?: it.getString(EXTRA_CONVERSATION_ACTIVITY_DIALOG_UUID_KEY)?.toUuid()
                messageUuid = it.getSerializable(CONVERSATION_ACTIVITY_MESSAGE_UUID_KEY) as? UUID
                archivedConversation = it.getBoolean(CONVERSATION_ACTIVITY_ARCHIVED_CONVERSATION, false)
                isInitAsGroupDialog = it.getBoolean(CONVERSATION_ACTIVITY_IS_GROUP_CONVERSATION, false)
                highlightMessage = it.getBoolean(CONVERSATION_ACTIVITY_HIGHLIGHT_MESSAGE)
            }

            val fromChatsRegistry = it.getBoolean(CONVERSATION_ACTIVITY_CHATS_REGISTRY_KEY, false)

            if (conversationType == ConversationType.VIDEO_CONVERSATION) {
                singletonComponent.dependency.loginInterface.getCurrentAccount()?.personId?.let { currentUserId ->
                    recipientsUuids.toMutableList().remove(UUIDUtils.fromString(currentUserId))
                }
            }

            val threadInfo = it.getSerializable(CONVERSATION_ACTIVITY_THREAD_INFO) as? ThreadInfo
            val fromParentThread = it.getBoolean(CONVERSATION_ACTIVITY_FROM_PARENT_THREAD)

            val toolbarInitialData = it.prepareToolbarData(recipientsUuids, isChat, threadInfo)
            val isPrivateChatCreation = isChat && dialogUuid == null && recipientsUuids.size == 1

            isNewConversation = dialogUuid == null

            return CoreConversationInfo(
                conversationUuid = dialogUuid,
                messageUuid = messageUuid,
                recipientUuid = recipientUuid,
                folderUuid = folderUuid,
                recipientsUuids = recipientsUuids,
                docInfo = docInfo,
                conversationType = conversationType,
                isChat = isChat,
                fromChatsRegistry = fromChatsRegistry,
                archivedConversation = archivedConversation,
                tablet = isTablet,
                toolbarInitialData = toolbarInitialData,
                isPrivateChatCreation = isPrivateChatCreation,
                isInitAsGroupDialog = isInitAsGroupDialog,
                creationThreadInfo = threadInfo,
                fromParentThread = fromParentThread,
                highlightMessage = highlightMessage,
                viewMode = viewMode
            )
        }
    }
}

private const val DIALOG_CODE_CONFIRM_DELETE_DIALOG = 1
private const val DIALOG_CODE_CONFIRM_QUIT_CHAT = 2
private const val DIALOG_CODE_CONFIRM_HIDE_CHAT = 3
private const val DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL = 4
private const val DIALOG_CODE_CONFIRM_DELETE_MESSAGE_FOR_ME = 6
private const val DIALOG_CODE_CONFIRM_CANCEL_RECORDING = 7
private const val DIALOG_CODE_RECORD_MEDIA_INFO_POPUP = 8
private const val DIALOG_CODE_DELETED_DIALOG = 101
private const val DIALOG_CODE_CHANGE_TOPIC = 33
private const val WAS_POPUP_MENU_VISIBLE = "WAS_POPUP_MENU_VISIBLE"
private const val MS_DELAY_FOR_CLOSING_THE_KEYBOARD = 100L
private const val AVATAR_PHOTO_SIZE = 124
private const val PREVIEWER_KEY = "previewer"
private const val UNSPECIFIED_PHOTO_SIZE = "%d"
private const val MESSAGE_PANEL_ALPHA_KEY = "message_panel_alpha"
private const val MAX_LENGTH = 256
private const val EDIT_TITLE_MAX_LINES = 5
private const val CONVERSATION_PREVIEW_RESULT = "CONVERSATION_PREVIEW_RESULT"

private val PROGRESS_DIALOG_FRAGMENT_TAG = ConversationFragment::class.simpleName + ".progress_dialog_fragment"
private val NOTIFY_DIALOG_REMOVED_TAG = PopupConfirmation::class.simpleName + ":notifyDialogRemoved"