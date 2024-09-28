package ru.tensor.sbis.communicator.crm.conversation.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import androidx.core.view.size
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConversationLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.UUIDUtils.NIL_UUID
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communication_decl.crm.CRMChatListHistoryParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCreationParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationOpenParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.BaseConversationFragment
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationFeatureFacade.CRM_CONVERSATION_CHAT_PARAMS_KEY
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.messagesPushManagerProvider
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationContract.*
import ru.tensor.sbis.communicator.crm.conversation.di.DaggerCRMConversationComponent
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.presentation.adapter.CRMConversationAdapter
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons.ActionButtonType.NEXT
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons.ActionButtonType.REOPEN
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons.ActionButtonType.TAKE
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons.CRMActionButtons
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.conversation_option.CRMConversationOption
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.QuickReplyFragment
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplyPeekHeights
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplySearchResult
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.viewmodel.CRMConversationViewModel
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentFragment
import ru.tensor.sbis.communicator.declaration.crm.model.QuickReplyParams
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.counters.utils.Formatter
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.list_header.DateViewMode
import ru.tensor.sbis.design.list_header.HeaderDateView
import ru.tensor.sbis.design.message_view.content.crm_views.greetings_view.GreetingsView
import ru.tensor.sbis.design.message_view.model.MessageType
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.theme.HorizontalAlignment.LEFT
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.extentions.setBottomPadding
import ru.tensor.sbis.design.utils.extentions.setLeftMargin
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeDimension
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegate
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateImpl
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateParams
import ru.tensor.sbis.design.utils.insets.IndentType
import ru.tensor.sbis.design.utils.insets.Position
import ru.tensor.sbis.design.utils.insets.ViewToAddInset
import ru.tensor.sbis.design.view_ext.round_corner.setRoundedSideOutlineProvider
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_dialogs.movablepanel.isEqual
import ru.tensor.sbis.message_panel.view.MessagePanel
import java.util.UUID
import kotlin.math.abs
import ru.tensor.sbis.communicator.design.R as RDesignCommunicator
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.design_dialogs.R as RDesignDialogs
import ru.tensor.sbis.design.profile.R as RDesignProfile
import ru.tensor.sbis.design.toolbar.R as RToolbar

/**
 * Фрагмент консультации CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationFragment :
    BaseConversationFragment<
            CRMConversationMessage,
            CRMConversationAdapter,
            CRMConversationViewContract,
            CRMConversationPresenterContract>(),
    CRMConversationViewContract,
    AdjustResizeHelper.KeyboardEventListener,
    DefaultViewInsetDelegate by DefaultViewInsetDelegateImpl() {

    override val withAudioMessages = false
    val messagesPushManager = messagesPushManagerProvider?.get()?.messagesPushManager

    @Suppress("DEPRECATION")
    private val viewModel by lazy { ViewModelProviders.of(this)[CRMConversationViewModel::class.java] }

    private var actionButtons: CRMActionButtons? = null
    private var greetingsView: GreetingsView? = null

    private var historyButton: SbisRoundButton? = null
    private var fab: SbisRoundButton? = null

    private var historyView: MovablePanel? = null
    private var quickReplyViewOnButton: MovablePanel? = null
    private var quickReplyViewOnText: MovablePanel? = null
    private val fitContentPeekHeight: MovablePanelPeekHeight by lazy {
        val difference = resources.getDimensionPixelSize(R.dimen.communicator_crm_conversation_movable_panel_fit_content_difference)
        val toolbarHeight = resources.getDimensionPixelSize(RToolbar.dimen.toolbar_size) * 2
        val absoluteHeight =
            resources.configuration.screenHeightDp * resources.displayMetrics.density - toolbarHeight - difference
        MovablePanelPeekHeight.Absolute(absoluteHeight.toInt())
    }
    private val hiddenPeekHeight: MovablePanelPeekHeight by lazy {
        MovablePanelPeekHeight.Percent(0F)
    }
    private val initPeekHeight: MovablePanelPeekHeight by lazy {
        MovablePanelPeekHeight.Dimen(R.dimen.communicator_crm_conversation_movable_panel_init_height)
    }

    private val onListLayoutListener = OnGlobalLayoutListener {
        if (needShowGreetingsView() && !isGreetingsViewInMessageList &&
            isMessagesOnScreen && greetingsView?.childCount != 0
            ) {
            val sbisListViewHeight = sbisListView?.height ?: 0
            if (getSumOfMessagesHeight() + getSumHeightsViewsAtBottom() > sbisListViewHeight) {
                presenter.insertGreetingsButtonsInMessageList(withNotify = true)
                greetingsView?.isVisible = false
            } else {
                changeGreetingsViewPosition()
                greetingsView?.isVisible = true
            }
        } else {
            greetingsView?.isVisible = false
        }
    }

    private var disposer = CompositeDisposable()

    @Suppress("DEPRECATION")
    private val initArguments: CRMConsultationParams
        get() = requireArguments().getSerializable(CRM_CONVERSATION_CHAT_PARAMS_KEY) as CRMConsultationParams

    // Есть сценарий когда при выбранном канале открывается существующая консультация, но в ней
    // необходимо отобразить заглушку(а для брендов еще и показать в шапке "ЧАТ").
    private val isNewConsultationMode
        get() = initArguments is CRMConsultationCreationParams
                || (initArguments is CRMConsultationOpenParams && initArguments.needOpenKeyboard)

    private val isSwipeBackEnabled
        get() = initArguments.isSwipeBackEnabled

    private val isClientChat
        get() = initArguments.crmConsultationCase is CRMConsultationCase.Client

    private val isSalePoints
        get() = initArguments.crmConsultationCase is CRMConsultationCase.SalePoint

    private val isBrand
        get() = initArguments.crmConsultationCase.castTo<CRMConsultationCase.SalePoint>()?.isBrand == true

    private val isMessagePanelVisible
       get() = if (initArguments is CRMConsultationOpenParams) (initArguments as CRMConsultationOpenParams).isMessagePanelVisible else true

    private val isOperatorConsultation
        get() = initArguments.crmConsultationCase is CRMConsultationCase.Operator

    private val isCompletedChat
       get() = if (initArguments is CRMConsultationOpenParams) (initArguments as CRMConsultationOpenParams).isCompleted else false

    private val isHistoryMode
       get() = if (initArguments is CRMConsultationOpenParams) (initArguments as CRMConsultationOpenParams).isHistoryMode else false

    private val needOpenKeyboard
        get() = initArguments.needOpenKeyboard

    private val isGreetingsViewInMessageList: Boolean
        get() = adapter?.content?.firstOrNull()?.viewData?.type == MessageType.GREETINGS_BUTTONS

    private val isMessagesOnScreen: Boolean
        get() = (sbisListView?.recyclerView?.size ?: 0) > 0

    private val messagePanelText: String
        get() = messagePanel?.currentText ?: StringUtils.EMPTY

    private val takeButtonVisible: Boolean
        get() = actionButtons?.isTakeButtonVisible == true

    private val component by lazy {
        DaggerCRMConversationComponent.builder()
            .crmConversationSingletonComponent(singletonComponent)
            .viewModelStoreOwner(this)
            .conversationData(getConversationInfoFromArguments())
            .viewModel(viewModel)
            .build()
    }

    private fun getConversationInfoFromArguments(): CRMCoreConversationInfo =
        when (val consultationParams = initArguments) {
            is CRMConsultationOpenParams -> {
                CRMCoreConversationInfo(
                    conversationUuid = consultationParams.crmConsultationCase.originUuid,
                    messageUuid = consultationParams.relevantMessageUuid,
                    isCompleted = consultationParams.isCompleted,
                    isMessagePanelVisible = consultationParams.isMessagePanelVisible,
                    crmConsultationCase = consultationParams.crmConsultationCase,
                    isNewConsultationMode = consultationParams.needOpenKeyboard
                )
            }
            is CRMConsultationCreationParams -> {
                CRMCoreConversationInfo(
                    chatName = consultationParams.consultationName,
                    photoUrl = consultationParams.photoUrl,
                    sourceId = consultationParams.crmConsultationCase.originUuid,
                    crmConsultationCase = consultationParams.crmConsultationCase,
                    isNewConsultationMode = true
                )
            }
            else -> CRMCoreConversationInfo()
        }

    override fun getLayoutRes(): Int = R.layout.communicator_fragment_crm_conversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMediaPlayerSessionHelper()
        childFragmentManager.setFragmentResultListener(
            CrmReassignCommentFragment.REQUEST,
            this@CRMConversationFragment
        ) { _, bundle ->
            onKeyboardCloseMeasure(0)
            presenter.onReassignCommentResult(bundle)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        if (isSwipeBackEnabled && !isTablet) {
            addToSwipeBackLayout(super.onCreateView(inflater, container, savedInstanceState)!!)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)!!
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Подписку необходимо выполнить до onViewCreated, в котором вызовется attachView на презентере.
        subscribeOnViewModel()
        viewModel.setQuickReplyScrollListener = true
        if (isOperatorConsultation) {
            subscribeOnListLayout()
            lifecycleScope.launch {
                presenter.getGreetings()
            }
        }
        super.onViewCreated(view, savedInstanceState)
        viewModel.requireKeyboardOnFirstLaunch.onNext(needOpenKeyboard)
        viewModel.showNNP.onNext {
            singletonComponent.scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_DOWN_FAKE)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (needOpenKeyboard && !hidden) {
            viewModel.requireKeyboardOnFirstLaunch.onNext(true)
        } else {
            viewModel.requireKeyboardOnFirstLaunch.onNext(false)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            messagePanel?.currentText?.let {
                setQuickReplyViewHeightOnTextChanged(it)
            }
        }
    }

    private fun subscribeOnViewModel() {
        if (disposer.isDisposed) disposer = CompositeDisposable()
        if (isClientChat || isSalePoints) {
            disposer += viewModel.createConsultationIcon.subscribe {
                topNavigation?.rightBtnContainer?.isVisible = it
            }
        } else {
            disposer += viewModel.menuConsultationMenuIcon.subscribe {
                topNavigation?.rightBtnContainer?.isVisible = it
            }
        }
        disposer += viewModel.isNeedShowNNp.subscribe {
            it.invoke()
        }
        disposer += viewModel.showKeyboardOnFirstLaunch.subscribe { if (it) showKeyboard() }
        disposer += viewModel.msgUnreadCount
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { refreshCounter(it) }
        disposer += viewModel.messagePanel.subscribe {
            messagePanel?.isVisible = it
            changeSbisButtonContainerPosition()
        }
        disposer += viewModel.createConsultationFab.subscribe {
            fab?.isVisible = it
            changeSbisButtonContainerPosition()
        }
        disposer += viewModel.isNeedShowNextButton.subscribe { uuid ->
            val needShowNextButton = uuid != NIL_UUID
            actionButtons?.apply {
                val viewID = initArguments.crmConsultationCase.castTo<CRMConsultationCase.Operator>()?.viewId ?: NIL_UUID
                setNextButtonClickListener(uuid, viewID)
                if (needShowNextButton) showActionButton(NEXT) else hideActionButton(NEXT)
            }
            handleActionButtonsVisibilityChanges()
        }
        disposer += viewModel.isNeedShowTakeButton.subscribe {
            actionButtons?.apply {
                if (it) showActionButton(TAKE) else hideActionButton(TAKE)
            }
            handleActionButtonsVisibilityChanges()
        }
        disposer += viewModel.isNeedShowReopenButton.subscribe {
            actionButtons?.apply {
                if (it) showActionButton(REOPEN) else hideActionButton(REOPEN)
            }
            handleActionButtonsVisibilityChanges()
        }
        disposer += viewModel.isNeedShowHistoryButton.subscribe {
            historyButton?.isVisible = it
        }

        disposer += viewModel.isNeedPreparedHistoryView
            .subscribe {
                val needPrepared = it.first
                val userId = it.second
                val excludeId = it.third
                if (needPrepared) {
                    prepareHistoryView(userId, excludeId)
                }
            }

        disposer += viewModel.isNeedShowHistoryView.subscribe {
            historyView?.isVisible = it
            val height = if (it) initPeekHeight else hiddenPeekHeight
            changeMessagesListTopPadding(height)
        }

        disposer += viewModel.needPrepareQuickReplyViews.subscribe {
            val quickReplyTag = QuickReplyFragment::class.java.simpleName
            val quickReplyAlreadyPrepared = childFragmentManager.findFragmentByTag(quickReplyTag) != null
            if (it && !quickReplyAlreadyPrepared) {
                prepareQuickReplyView()
                prepareQuickReplyView(true)
            }
            messagePanel?.setQuickReplyButtonOnClickListener {
                presenter.showQuickReplyView()
            }
        }

        disposer += viewModel.isNeedShowQuickReplyOnButton.subscribe {
            if (it) {
                openQuickReplyViewOnButton()
                messagePanel?.run {
                    clearFocus()
                    hideKeyboard()
                }
                quickReplyViewOnText?.closeQuickReplyView()
            } else {
                quickReplyViewOnButton?.closeQuickReplyView()
            }
        }

        disposer += viewModel.isNeedRequestQuickRepliesOnTextInput.subscribe { text ->
            if (messagePanel?.hasFocus() == true) {
                childFragmentManager.findFragmentById(R.id.communicator_crm_conversation_quick_reply_on_search_panel_container_id)
                    ?.castTo<QuickReplyFragment>()?.setSearchQuery(text)
                setQuickReplyViewHeightOnTextChanged(text)
            }
        }

        disposer += viewModel.greetings.subscribe {
            greetingsView?.setTitles(it)
        }
        disposer += viewModel.currentStubContent.subscribe { showStubView(it) }
        disposer += viewModel.stubIsVisible.subscribe { isVisible ->
            if (isVisible) {
                viewModel.currentStub.value?.let { showStubView(it) }
            } else {
                hideStubView()
            }
        }
    }

    private fun prepareHistoryView(userId: UUID, excludeId: UUID) {
        crmConversationDependency?.crmChatListFragmentFactory?.let { fragmentFactory ->
            childFragmentManager.apply {
                val panelId = R.id.communicator_crm_conversation_panel_container_id
                val fragment = findFragmentById(panelId) ?: fragmentFactory.createCRMChatListFragment(
                    CRMChatListHistoryParams(
                        userId = userId,
                        excludeId = excludeId
                    )
                )
                if (fragment.parentFragment != null) return
                beginTransaction()
                    .add(panelId, fragment, fragment::class.java.simpleName)
                    .commitNow()
            }
        }
    }

    private fun prepareQuickReplyView(viewAppearedOnTextInput: Boolean = false) {
        childFragmentManager.apply {
            val panelId = if (viewAppearedOnTextInput) {
                R.id.communicator_crm_conversation_quick_reply_on_search_panel_container_id
            } else {
                R.id.communicator_crm_conversation_quick_reply_on_button_panel_container_id
            }
            val params = if (viewAppearedOnTextInput) {
                QuickReplyParams(
                    channelUUID = viewModel.channelUuid,
                    needSearchInput = false,
                    needFolderView = false,
                    resultKey = QUICK_REPLY_ON_TEXT_RESULT_KEY,
                )
            } else {
                QuickReplyParams(
                    channelUUID = viewModel.channelUuid,
                    resultKey = QUICK_REPLY_ON_BUTTON_RESULT_KEY,
                )
            }
            setFragmentResultListener(params.resultKey, this@CRMConversationFragment) { _, bundle ->
                quickReplyViewOnText?.handleQuickReplySearchResult(bundle)
                handleQuickReplySelectionResult(bundle, viewAppearedOnTextInput)
            }
            val fragment = findFragmentById(panelId) ?: QuickReplyFragment.newInstance(params)
            beginTransaction()
                .add(panelId, fragment, QuickReplyFragment::class.java.simpleName)
                .commitNow()
        }
    }

    private fun MovablePanel.handleQuickReplySearchResult(bundle: Bundle) {
        val searchResult = bundle.getString(SEARCH_RESULT_QUICK_REPLY_KEY) ?: return
        if (searchResult == QuickReplySearchResult.LIST_IS_EMPTY.toString()) {
            closeQuickReplyView()
        } else {
            isVisible = true
            setQuickReplyViewHeightOnTextChanged(messagePanelText)
        }
    }

    private fun handleQuickReplySelectionResult(bundle: Bundle, viewAppearedOnTextInput: Boolean) {
        val result = bundle.getString(SELECTED_QUICK_REPLY_KEY) ?: StringUtils.EMPTY
        if (result.isNotEmpty()) {
            if (viewAppearedOnTextInput) {
                presenter.replaceTextInMessagePanel(result)
                quickReplyViewOnText?.peekHeight = QuickReplyPeekHeights.HIDDEN.value
            } else {
                presenter.pasteTextInMessagePanel(result)
                quickReplyViewOnButton?.peekHeight = QuickReplyPeekHeights.HIDDEN.value
            }
        }
    }

    private fun openQuickReplyViewOnButton() {
        quickReplyViewOnButton?.apply {
            isVisible = true
            peekHeight = if (needToShowKeyboard) {
                QuickReplyPeekHeights.MAX.value
            } else {
                QuickReplyPeekHeights.INIT_ON_BUTTON.value
            }
        }
    }

    private fun MovablePanel.closeQuickReplyView() {
        peekHeight = QuickReplyPeekHeights.HIDDEN.value
        isVisible = false
    }

    private fun setQuickReplyViewHeightOnTextChanged(text: String) {
        quickReplyViewOnText?.apply {
            val isHidden = peekHeight == QuickReplyPeekHeights.HIDDEN.value
            when {
                isHidden && text.length > 2 -> peekHeight = QuickReplyPeekHeights.INIT_ON_TEXT.value
                !isHidden && text.length <= 2 -> peekHeight = QuickReplyPeekHeights.HIDDEN.value
                else -> Unit
            }
            isVisible = peekHeight != QuickReplyPeekHeights.HIDDEN.value
        }
    }

    private fun handleActionButtonsVisibilityChanges() {
        changeSbisButtonContainerPosition()
        changeSbisListRecyclerViewBottomPadding()
    }

    private fun changeSbisButtonContainerPosition() {
        sbisButtonContainer?.updateLayoutParams<RelativeLayout.LayoutParams> {
            removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            when {
                actionButtons?.isVisible == true -> addRule(RelativeLayout.ABOVE, actionButtons!!.id)
                fab?.isVisible == true -> addRule(RelativeLayout.ABOVE, fab!!.id)
                messagePanel?.isVisible == true -> addRule(RelativeLayout.ABOVE, messagePanel!!.id)
                else -> addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
        }
    }

    private fun changeSbisListRecyclerViewBottomPadding() {
        sbisListView?.recyclerViewBottomPadding = if (actionButtons?.isVisible == true) {
            val actionButtonsHeight = if (actionButtons!!.height == 0) {
                requireContext().getDimenPx(RDesign.attr.inlineHeight_m)
            } else {
                actionButtons!!.height
            }
            actionButtonsHeight + actionButtons!!.marginTop + actionButtons!!.marginBottom
        } else {
           messagePanel?.height ?: defaultRecyclerBottomPadding
        }
    }

    private fun subscribeOnListLayout() {
        sbisListView?.recyclerView?.viewTreeObserver?.addOnGlobalLayoutListener(
            onListLayoutListener,
        )
    }

    private fun unsubscribeOnListLayout() {
        sbisListView?.recyclerView?.viewTreeObserver?.removeOnGlobalLayoutListener(
            onListLayoutListener,
        )
    }

    private fun needShowGreetingsView(): Boolean = !presenter.isConsultationCompleted &&
            (takeButtonVisible || !listContainsMessageFromMe())

    private fun listContainsMessageFromMe(): Boolean =
        adapter?.content?.indexOfFirst {
            it.viewData.type == MessageType.OUTCOME_MESSAGE || it.viewData.type == MessageType.OUTCOME_RATE_MESSAGE ||
                    it.viewData.type == MessageType.OUTCOME_VIDEO_MESSAGE
        } != -1

    private fun changeGreetingsViewPosition() = greetingsView?.updateLayoutParams<RelativeLayout.LayoutParams> {
        removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        when {
            actionButtons?.isVisible == true -> addRule(RelativeLayout.ABOVE, actionButtons!!.id)
            messagePanel?.isVisible == true -> addRule(RelativeLayout.ABOVE, messagePanel!!.id)
            else -> addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }
    }

    private fun getOptionMenuItem(option: CRMConversationOption) =
        MenuItem(
            title = requireContext().getString(option.textRes),
            image = option.iconRes,
            destructive = option.destructive
        ) {
            presenter.onConversationOptionSelected(option)
        }

    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        super.initViews(mainView, savedInstanceState)
        mainLayout = mainView.findViewById(R.id.communicator_layout_main)
        messagePanel = mainView.findViewById<MessagePanel>(R.id.communicator_crm_conversation_message_panel).apply {
            isVisible = isMessagePanelVisible
            addOnLayoutChangeListener { _, _, top, _, _, _, oldTop, _, _ ->
                if (top != oldTop) {
                    setQuickReplyPositionOnKeyboardMeasure(keyboardHeight)
                }
            }
        }
        sbisListView = mainView.findViewById(R.id.communicator_crm_conversation_sbis_list_view)
        if (isBrand && isNewConsultationMode) {
            sbisListView?.setRoundedSideOutlineProvider(resources.dp(16), top = true)
        }
        fab = mainView.findViewById<SbisRoundButton?>(R.id.communicator_crm_navigation_fab).apply {
            setOnClickListener {
                presenter.openNewConsultation()
            }
        }
        actionButtons = mainView.findViewById<CRMActionButtons?>(R.id.communicator_crm_action_buttons).apply {
            listener = presenter
        }
        historyButton = mainView.findViewById<SbisRoundButton>(R.id.communicator_crm_history_button).apply {
            setOnClickListener {
                presenter.openHistoryView()
            }
        }
        historyView = mainView.findViewById<MovablePanel>(R.id.communicator_crm_conversation_movable_panel).apply {
            disposer += getPanelSlideSubject().subscribe {
                contentContainer?.setPadding(
                    paddingLeft,
                    abs(getPanelY()),
                    paddingRight,
                    resources.getDimensionPixelSize(RDesignDialogs.dimen.movable_panel_default_header_height)
                )
            }
            disposer += subscribeToPanelClose(hiddenPeekHeight) {
                presenter.onHistoryViewClosed()
                changeMessagesListTopPadding(hiddenPeekHeight)
            }
            disposer += subscribeToPanelOpen(initPeekHeight) {
                changeMessagesListTopPadding(initPeekHeight)
            }
            setPeekHeightList(listOf(fitContentPeekHeight, initPeekHeight, hiddenPeekHeight), initPeekHeight)
        }
        initQuickReplyViews(mainView)
        greetingsView = mainView.findViewById<GreetingsView>(R.id.communicator_crm_greetings_view).apply {
            setOnGreetingClick { title ->
                presenter.onGreetingClicked(title)
            }
        }
        initListView(mainView)
        initTopNavigation(mainView)
        setBackSwipeAvailability(true)
        initScrollButton(mainView)
    }

    private fun initQuickReplyViews(mainView: View) {
        initQuickReplyViewOnButton(mainView)
        initQuickReplyViewOnText(mainView)
    }

    private fun initQuickReplyViewOnButton(mainView: View) {
        quickReplyViewOnButton =
            mainView.findViewById<MovablePanel>(R.id.communicator_crm_conversation_quick_reply_on_button).apply {
                setPeekHeightList(
                    listOf(
                        QuickReplyPeekHeights.MAX.value,
                        QuickReplyPeekHeights.INIT_ON_BUTTON.value,
                        QuickReplyPeekHeights.HIDDEN.value,
                    ),
                    QuickReplyPeekHeights.INIT_ON_BUTTON.value,
                )
                disposer += subscribeToPanelClose(hiddenPeekHeight) {
                    presenter.hideQuickReplyView()
                }
                disposer += subscribeToPanelHeightChanged {
                    childFragmentManager
                        .findFragmentById(R.id.communicator_crm_conversation_quick_reply_on_button_panel_container_id)
                        ?.castTo<QuickReplyFragment>()?.apply {
                            handleHeightChanges(peekHeight == QuickReplyPeekHeights.HIDDEN.value)
                            if (viewModel.setQuickReplyScrollListener) {
                                setScrollListener()
                                viewModel.setQuickReplyScrollListener = false
                            }
                        }
                }
                setOnShadowClickListener { presenter.hideQuickReplyView() }
            }
    }

    private fun QuickReplyFragment.setScrollListener() {
        setScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        val maxHeight = QuickReplyPeekHeights.MAX.value
                        if (quickReplyViewOnButton?.peekHeight != maxHeight) {
                            quickReplyViewOnButton?.peekHeight = maxHeight
                        }
                    }
                }
            }
        )
    }

    private fun initQuickReplyViewOnText(mainView: View) {
        quickReplyViewOnText =
            mainView.findViewById<MovablePanel>(R.id.communicator_crm_conversation_quick_reply_on_text).apply {
                setPeekHeightList(
                    listOf(
                        QuickReplyPeekHeights.MAX.value,
                        QuickReplyPeekHeights.MIDDLE_ON_TEXT.value,
                        QuickReplyPeekHeights.INIT_ON_TEXT.value,
                        QuickReplyPeekHeights.HIDDEN.value,
                    ),
                    QuickReplyPeekHeights.HIDDEN.value,
                )
                setBottomPadding(messagePanel?.height ?: 0)
                disposer += subscribeToPanelClose(hiddenPeekHeight) {
                    isVisible = false
                }
            }
    }

    private fun initListView(mainView: View) {
        val headerDateView = mainView.findViewById<HeaderDateView>(R.id.communicator_conversation_fragment_date_header_view)
            .apply { dateViewMode = DateViewMode.DATE_ONLY }

        layoutManager = ConversationLayoutManager(
            requireContext(),
            laidOutItemsListener
        ).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        defaultRecyclerBottomPadding = resources.getDimensionPixelOffset(
            RDesignCommunicator.dimen.communicator_messages_list_bottom_padding
        )
        sbisListView?.apply {
            recyclerViewBottomPadding = defaultRecyclerBottomPadding
            setLayoutManager(this@CRMConversationFragment.layoutManager!!)
            setRecyclerViewVerticalScrollbarEnabled(true)
            recyclerView.also {
                it.id = R.id.communicator_crm_conversation_recycler_view_id
                it.itemAnimator = null
                it.fitsSystemWindows = false
                it.stopScroll()
                component.listDateViewUpdater.bind(it, headerDateView)
            }
        }

        sbisListView?.setAdapter(adapter)
    }

    private fun initTopNavigation(rootView: View) {
        val needBackButton = initArguments.needBackButton && !initArguments.hasAccordion
        topNavigation = rootView.findViewById<SbisTopNavigationView?>(R.id.communicator_crm_conversation_toolbar).apply {
            if (isBrand && isNewConsultationMode) {
                content = SbisTopNavigationContent.LargeTitle(
                    PlatformSbisString.Res(R.string.communicator_crm_brand_title)
                )
                titlePosition = LEFT
                if (initArguments.hasAccordion) setLeftMargin(dp(45))
            } else {
                content = SbisTopNavigationContent.SmallTitle(
                    PlatformSbisString.Value(StringUtils.EMPTY)
                )
                titlePosition = LEFT
                subtitleView?.maxLines = 1
            }
        }

        val rightButton = SbisTextView(
            context = rootView.context,
            styleRes = ru.tensor.sbis.design.R.style.MobileFontStyle
        ).apply {
            id = ViewGroup.generateViewId()
            textSize = rootView.context.getDimen(RDesign.attr.iconSize_3xl)
            text = if (isClientChat || isSalePoints) {
                SbisMobileIcon.Icon.smi_AddButtonNew.character.toString()
            } else {
                SbisMobileIcon.Icon.smi_navBarMore.character.toString()
            }
            setPadding(context.getDimen(RDesign.attr.offset_s).toInt())
            setTextColor(context.getColorFromAttr(RDesign.attr.iconColor))
            setOnClickListener {
                if (isClientChat || isSalePoints) {
                    presenter.openNewConsultation()
                } else {
                    presenter.openCRMConversationMenu()
                }
            }
        }
        toolbarMoreButton = rightButton

        topNavigation!!.apply {
            rightBtnContainer!!.addView(toolbarMoreButton)
            rightBtnContainer!!.isVisible = true
            backBtn?.apply {
                setOnClickListener {
                    hideKeyboard()
                    requireActivity().onBackPressed()
                }
                isVisible = needBackButton
            }
            if (isSalePoints) {
                initInsetListener(
                    DefaultViewInsetDelegateParams(
                        listOf(
                            ViewToAddInset(
                                this,
                                listOf(IndentType.PADDING to Position.TOP)
                            )
                        )
                    )
                )
            }
            if (isSalePoints || isOperatorConsultation) {
                setOnClickListener {
                    presenter.onToolbarClick()
                }
            }
        }
        if (!isBrand) {
            topNavigation?.apply {
                personView?.isVisible = true
                leftIconView?.apply {
                    textSize = resources.getDimension(RDesignProfile.dimen.design_profile_sbis_title_view_collage_size)
                }
            }
        }
        if (!(isHistoryMode && isTablet && isBrand)) {
            topNavigation?.counterView?.apply {
                counterFormatter = Formatter.HundredFormatter
                isVisible = !initArguments.hasAccordion
            }
        }
    }

    private fun refreshCounter(count: Int) {
        topNavigation?.counter = count
    }

    private fun initMediaPlayerSessionHelper() {
        singletonComponent.dependency
            .mediaPlayerFeature
            ?.getMediaPlayerSessionHelper()
            ?.init(this)
    }

    override fun showComplainDialogFragment(complainUseCase: ComplainUseCase) {
        crmConversationDependency?.complainFragmentFeature?.showComplainDialogFragment(
            childFragmentManager,
            complainUseCase
        )
    }

    override fun showCantViewStub() {
        viewModel.currentStub.onNext(
            ResourceImageStubContent(
                message = requireContext().getString(R.string.communicator_crm_chat_cant_view_permisshion),
                details = null
            )
        )
    }

    override fun showStubView(stubContent: StubViewContent) {
        val currentStub = viewModel.currentStub
        if (currentStub.value != stubContent) {
            currentStub.onNext(stubContent)
        }
        sbisListView?.showInformationViewData(stubContent)
    }

    override fun onKeyboardMeasure() {
        if (keyboardHeight > 0) {
            onKeyboardOpenMeasure(keyboardHeight)
        } else {
            onKeyboardCloseMeasure(keyboardHeight)
        }
    }

    override fun showHistoryView() {
        historyView?.isVisible = true
        historyView?.peekHeight = initPeekHeight
    }

    override fun sendGreetingMessage(text: String) {
        presenter.sendGreetingMessage(text)
    }

    override fun copyLink(url: String) {
        ClipboardManager.copyToClipboard(requireContext(), url)
        showToast(RDesignCommunicator.string.communicator_link_copied, Toast.LENGTH_LONG)
    }

    override fun showCRMConversationMenu(options: List<CRMConversationOption>) {
        val anchor = toolbarMoreButton!!
        val sbisMenu = SbisMenu(
            children = options.map { getOptionMenuItem(it) }
        )
        sbisMenu.showMenuWithLocators(
            fragmentManager = childFragmentManager,
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.BOTTOM,
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

    override fun closeConversationFragment() {
        parentFragmentManager.popBackStack()
    }

    override fun showErrorPopup(messageRes: Int, icon: String?) = Unit

    override fun onBackPressed(): Boolean {
        childFragmentManager.run {
            quickReplyViewOnButton?.run {
                if (peekHeight != QuickReplyPeekHeights.HIDDEN.value) {
                    closeQuickReplyView()
                    return true
                }
            }
            if (backStackEntryCount == 0) return super.onBackPressed()
            if (fragments.last().castTo<FragmentBackPress>()?.onBackPressed() == false) {
                popBackStack()
                return true
            }
        }
        return false
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        setQuickReplyPositionOnKeyboardMeasure(keyboardHeight)
        setQuickReplyOnButtonHeightOnKeyboardMeasure(keyboardHeight)
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
                    messagePanelDelegate?.onKeyboardOpenMeasure(keyboardHeight)
                    translateMessagePanel(-keyboardHeight.toFloat())
                }
                animateKeyboardShowing = false
            } else {
                currentKeyboardOffset = keyboardHeight
                presenter.onKeyboardAppears(keyboardHeight)
                setListViewBottomPadding(keyboardHeight)
                scrollListView(keyboardHeight, false)
                translateMessagePanel(-keyboardHeight.toFloat())
                messagePanelDelegate?.onKeyboardOpenMeasure(keyboardHeight)
            }
            needToShowKeyboard = true
        }
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        setQuickReplyPositionOnKeyboardMeasure(0)
        if (this.keyboardHeight != 0) {
            this.keyboardHeight = 0
            keyboardAnimator?.cancel()
            /*
            Если клавиатура поднялась хотя бы на половину - анимируем опускание, иначе мгновенно все опускаем.
            Такое поведение предотвращает анимации на доли секунды, когда закрывается клавиатура при уходе с экрана
            уровнем выше, текущий фрагмент уже в состоянии Resumed и до него долетают колбэки onKeyboardOpen и CloseMeasure.
             */
            if (currentKeyboardOffset >= keyboardHeight / 2) {
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

    private fun setQuickReplyPositionOnKeyboardMeasure(keyboardHeight: Int) {
        if (messagePanel?.currentText?.isEmpty() == true) {
            setQuickReplyViewHeightOnTextChanged(StringUtils.EMPTY)
        }
        quickReplyViewOnText?.setBottomPadding(keyboardHeight + (messagePanel?.height ?: 0))
    }

    private fun setQuickReplyOnButtonHeightOnKeyboardMeasure(keyboardHeight: Int) {
        quickReplyViewOnButton?.let {
            if (it.isVisible) {
                it.peekHeight = if (keyboardHeight != 0) {
                    QuickReplyPeekHeights.MAX.value
                } else {
                    QuickReplyPeekHeights.INIT_ON_BUTTON.value
                }
            }
        }
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
        keyboardHeight = 0
        currentKeyboardOffset = 0
    }

    override fun createPresenter(): CRMConversationPresenterContract =
        component.crmConversationPresenter

    override fun getPresenterView(): CRMConversationViewContract = this

    override fun inject() {
        val messageViewPool =
            crmConversationDependency?.messageViewComponentsFactory?.createMessageViewPool(requireContext())
        adapter = CRMConversationAdapter(
            messageViewPool ?: component.messageViewPool,
            presenter,
            isOperatorConsultation,
            component.listDateViewUpdater,
        )
    }

    override fun swipeBackEnabled(): Boolean = initArguments.isSwipeBackEnabled

    override fun onDestroyView() {
        disposer.dispose()
        unsubscribeOnListLayout()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        singletonComponent.scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE)
    }

    private fun MovablePanel.subscribeToPanelClose(
        hiddenPeekHeight: MovablePanelPeekHeight,
        onClose: () -> Unit,
    ) = getPanelStateSubject()
        .filter { it.isEqual(hiddenPeekHeight) }
        .subscribe {
            onClose()
        }

    private fun MovablePanel.subscribeToPanelOpen(
        initPeekHeight: MovablePanelPeekHeight,
        onOpen: () -> Unit,
    ) = getPanelStateSubject()
        .filter { it.isEqual(initPeekHeight) }
        .subscribe {
            onOpen()
        }

    private fun MovablePanel.subscribeToPanelHeightChanged(
        onChange: () -> Unit
    ) = getPanelStateSubject()
        .subscribe {
            onChange()
        }

    private fun getSumOfMessagesHeight(): Int {
        var sumOfMessagesHeight = 0
        val layoutManager = sbisListView?.recyclerView?.layoutManager?.castTo<LinearLayoutManager>()
        val indexLast = layoutManager?.findLastVisibleItemPosition() ?: -1
        val indexFirst = layoutManager?.findFirstVisibleItemPosition() ?: -1
        if (indexFirst == -1 || indexLast == -1) return 0
        for (i in indexFirst..indexLast) {
            layoutManager?.getChildAt(i)?.height?.let {
                sumOfMessagesHeight += it
            }
        }
        return sumOfMessagesHeight
    }

    private fun getSumHeightsViewsAtBottom(): Int {
        val greetingsViewHeight = greetingsView?.getContentHeight() ?: 0
        val historyViewHeight = historyView?.height ?: 0
        val messagePanelHeight = messagePanel?.height ?: 0
        val actionButtonsHeight = actionButtons?.height ?: 0
        return greetingsViewHeight + historyViewHeight + messagePanelHeight + actionButtonsHeight
    }

    private fun changeMessagesListTopPadding(height: MovablePanelPeekHeight) {
        sbisListView?.let {
            val padding = height.toHistoryViewHeight(it.height)
            val offset = if (padding > 0) Offset.X3L.getDimenPx(requireContext()) else 0
            it.setRecyclerViewTopPadding(padding + offset)
        }
    }

    private fun MovablePanelPeekHeight.toHistoryViewHeight(listViewHeight: Int): Int = when (this) {
        is MovablePanelPeekHeight.Percent -> (listViewHeight * this.value).toInt()
        is MovablePanelPeekHeight.Dimen -> resources.getDimensionPixelSize(this.value)
        is MovablePanelPeekHeight.Absolute -> this.value
        else -> listViewHeight
    }

    companion object : CRMConversationFragmentFactory {

        /** @SelfDocumented */
        @JvmStatic
        fun newInstance(arguments: Bundle): Fragment {
            val fragment = CRMConversationFragment()
            fragment.arguments = Bundle(arguments)
            return fragment
        }

        @JvmStatic
        override fun createCRMConversationFragment(params: CRMConsultationParams) =
            CRMConversationFragment().withArgs {
                putSerializable(CRM_CONVERSATION_CHAT_PARAMS_KEY, params)
            }
    }
}

internal const val SELECTED_QUICK_REPLY_KEY = "SELECTED_QUICK_REPLY_KEY"
internal const val SEARCH_RESULT_QUICK_REPLY_KEY = "SEARCH_RESULT_QUICK_REPLY_KEY"
private const val QUICK_REPLY_ON_TEXT_RESULT_KEY = "QUICK_REPLY_ON_TEXT_RESULT_KEY"
private const val QUICK_REPLY_ON_BUTTON_RESULT_KEY = "QUICK_REPLY_ON_BUTTON_RESULT_KEY"