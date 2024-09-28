package ru.tensor.sbis.communicator.base.conversation.presentation.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ConversationLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.findFirstVisibleItemPosition
import ru.tensor.sbis.common.util.findLastVisibleItemPosition
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationFromRegistryParams
import ru.tensor.sbis.communicator.base.conversation.R
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.base.conversation.data.model.MessageAction
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.base.conversation.data.model.setToolbarData
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.BaseConversationAdapter
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListSizeSettings
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationPresenterContract
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationViewMode
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationViewContract
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView
import ru.tensor.sbis.communicator.base.conversation.utils.ConversationSubtitleExtension
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.DefaultButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonHandler
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialog
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialogStyle
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuForConversationRegistry
import ru.tensor.sbis.design.counters.sbiscounter.InfoSbisCounterStyle
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounter
import ru.tensor.sbis.design.counters.utils.Formatter
import ru.tensor.sbis.design.custom_view_tools.utils.animation.CubicBezierInterpolator
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.list_header.HeaderDateView
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.util.pxToDp
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.video_message_view.message.VideoMessageView
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.delegate.MessagePanelInitializerDelegate
import ru.tensor.sbis.message_panel.integration.CommunicatorMessagePanelController
import ru.tensor.sbis.message_panel.integration.CommunicatorMessagePanelInitializerDelegate
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.view.MessagePanel
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import timber.log.Timber
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Базовая реализация фрагмента переписки.
 *
 * @author vv.chekurda
 */
abstract class BaseConversationFragment<
    MESSAGE : BaseConversationMessage,
    ADAPTER : BaseConversationAdapter<MESSAGE>,
    VIEW : BaseConversationViewContract<MESSAGE>,
    PRESENTER : BaseConversationPresenterContract<VIEW>
> : BasePresenterFragment<VIEW, PRESENTER>(),
    BaseConversationViewContract<MESSAGE>,
    AdjustResizeHelper.KeyboardEventListener,
    PopupConfirmation.DialogItemClickListener,
    ConfirmationButtonHandler {

    protected var mainLayout: RelativeLayout? = null
    protected var topNavigation: SbisTopNavigationView? = null
    protected var toolbarMoreButton: View? = null
    protected var messagePanel: MessagePanel? = null
    protected var headerDateView: HeaderDateView? = null
    protected var messagePanelKeyboardOffsetView: View? = null
    protected var messagePanelMovablePanelContainer: View? = null
    protected var animateKeyboardShowing = false
    private var keyboardInterpolator = CubicBezierInterpolator.superSmoothDecelerateInterpolator
    protected var sbisListView: AbstractListView<StubView, StubViewContent>? = null
    protected var adapter: ADAPTER? = null

    private var sbisButton: SbisRoundButton? = null
    private var sbisButtonCounter: SbisCounter? = null
    /** Контейнер для кнопки прокрутки вниз*/
    protected var sbisButtonContainer: RelativeLayout? = null
    protected var viewPropertyAnimator: ViewPropertyAnimator? = null
    private var sbisButtonStateTranslation = 0f
    private var isFastScrollUpAnimationRunning = false
    private var isFastScrollDownAnimationRunning = false

    /**@SelfDocumented*/
    protected var layoutManager: ConversationLayoutManager? = null

    /**@SelfDocumented*/
    protected var messagePanelDelegate: CommunicatorMessagePanelInitializerDelegate? = null
    private var onMessagePanelChangeListener: View.OnLayoutChangeListener? = null
    /**@SelfDocumented*/
    protected open val withAudioMessages: Boolean = true

    /**@SelfDocumented*/
    protected var isFirstActivityLaunch: Boolean = true

    /**@SelfDocumented*/
    protected var keyboardAnimator: ValueAnimator? = null
    /**@SelfDocumented*/
    protected var isKeyboardAnimationRunning: Boolean = false
    /**@SelfDocumented*/
    protected var keyboardHeight: Int = 0
    /**@SelfDocumented*/
    protected var currentKeyboardOffset = 0
    /**@SelfDocumented*/
    protected var additionalOffset = 0

    /**@SelfDocumented*/
    @Px
    protected var defaultRecyclerBottomPadding: Int = 0

    /**@SelfDocumented*/
    protected var needToShowKeyboard: Boolean = false
    private var lastToast: Toast? = null

    @Suppress("DEPRECATION")
    private val listHandler = Handler()
    private var lastToolbarData: ToolbarData? = null

    /**@SelfDocumented*/
    protected var deferredFastScrollVisibility: Boolean = false
    private val canChangeFastScrollDownVisibility: Boolean
        get() = !(presenter.actionsMenuShown || isKeyboardAnimationRunning)

    /**
     * Базовое условие восстановления состояния клавиатуры.
     */
    protected open val keyboardShowingRule: Boolean
        get() = needToShowKeyboard

    protected val laidOutItemsListener: ConversationLayoutManager.LaidOutItemsListener by lazy { presenter }

    private val viewMode: ConversationViewMode by lazy {
        arguments?.getSerializable(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_CONVERSATION_ARG)?.let {
            (it as? ConversationFromRegistryParams)?.conversationViewMode
        } ?: ConversationViewMode.FULL
    }

    protected val isFullViewMode by lazy { viewMode == ConversationViewMode.FULL }

    /** @SelfDocumented */
    override fun inject() = Unit

    override fun onAttach(context: Context) {
        ConversationListSizeSettings.init(context.applicationContext)
        super.onAttach(context)
    }

    /** @SelfDocumented */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState == null) {
            needToShowKeyboard = arguments?.getBoolean(IntentAction.Extra.NEED_TO_SHOW_KEYBOARD, false) ?: false
        } else {
            restoreStateFromBundle(savedInstanceState)
        }
        val mainView: View = inflater.inflate(getLayoutRes(), container, false)
        initViews(mainView, savedInstanceState)
        initViewListeners()
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListViewBottomPadding(0)
        updateMovablePanelContainerPosition()
        setScrollVelocityRestriction()
        swipeBackLayout?.layoutParams?.width = MATCH_PARENT
        swipeBackLayout?.layoutParams?.height = MATCH_PARENT
    }

    private fun setScrollVelocityRestriction() {
        sbisListView?.recyclerView?.onFlingListener =
            object : RecyclerView.OnFlingListener() {
                override fun onFling(velocityX: Int, velocityY: Int): Boolean =
                    when {
                        velocityY > MAX_SCROLL_VELOCITY -> {
                            sbisListView?.recyclerView?.fling(velocityX, MAX_SCROLL_VELOCITY)
                            true
                        }
                        velocityY < -MAX_SCROLL_VELOCITY -> {
                            sbisListView?.recyclerView?.fling(velocityX, -MAX_SCROLL_VELOCITY)
                            true
                        }
                        else -> {
                            false
                        }
                    }
            }
    }

    @LayoutRes
    protected abstract fun getLayoutRes(): Int

    /** @SelfDocumented */
    open fun initViews(mainView: View, savedInstanceState: Bundle?) {
        messagePanelDelegate = MessagePanelInitializerDelegate.createCommunicatorMessagePanelInitializer(
            requireContext(),
            this,
            withAudioMessages,
            presenter.signDelegate
        )
    }

    /**@SelfDocumented*/
    protected open fun initScrollButton(mainView: View) {
        val clickListener: ((button: View) -> Unit) = { presenter.onFastScrollDownPressed() }
        mainView.findViewById<RelativeLayout>(R.id.communicator_fast_forward_down_container)?.let {
            sbisButtonContainer = it.also {
                it.visibility = View.GONE
                it.updateLayoutParams<RelativeLayout.LayoutParams> {
                    if (messagePanel?.isVisible == true) {
                        removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                        addRule(RelativeLayout.ABOVE, messagePanel!!.id)
                    } else {
                        removeRule(RelativeLayout.ABOVE)
                        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    }
                }
            }
            sbisButton = mainView.findViewById<SbisRoundButton>(R.id.communicator_fast_forward_down).apply {
                setOnClickListener(clickListener)
            }
            sbisButtonCounter = mainView.findViewById(R.id.communicator_fast_forward_down_counter)
            initSbisButtonContainerTranslation()
            return
        }

        sbisButtonContainer = RelativeLayout(requireContext()).apply {
            id = R.id.communicator_fast_forward_down_container
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                context.getDimenPx(ru.tensor.sbis.design.R.attr.inlineHeight_xl)
            ).apply {
                val marginEnd = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_m)
                val marginBottom = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_m)
                setMargins(0, 0, marginEnd, marginBottom)
                addRule(RelativeLayout.ALIGN_PARENT_END)
                if (isFullViewMode) {
                    addRule(RelativeLayout.ABOVE, messagePanel!!.id)
                } else {
                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }
            }
            visibility = View.GONE
        }
        sbisButton = SbisRoundButton(requireContext()).apply {
            id = R.id.communicator_fast_forward_down
            style = DefaultButtonStyle
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            size = SbisRoundButtonSize.M
            icon = SbisButtonTextIcon(
                SbisMobileIcon.Icon.smi_arrowBotDown,
                SbisButtonIconSize.X2L,
                SbisButtonIconStyle(
                    ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_enabled)),
                        intArrayOf(context.getThemeColorInt(ru.tensor.sbis.design.R.attr.labelIconColor))
                    )
                )
            )
            setOnClickListener(clickListener)
            background = ContextCompat.getDrawable(
                context,
                ru.tensor.sbis.communicator.design.R.drawable.communicator_scroll_down_ripple
            )
        }

        val result = TypedValue()
        val counterContext =
            if (requireContext().theme.resolveAttribute(ru.tensor.sbis.communicator.common.R.attr.communicatorFastScrollDownTheme, result, true)) {
                ContextThemeWrapper(requireContext(), result.data)
            } else {
                requireContext()
            }
        sbisButtonCounter = SbisCounter(counterContext, null).apply {
            id = R.id.communicator_fast_forward_down_counter
            style = InfoSbisCounterStyle
            counterFormatter = Formatter.HundredFormatter
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_TOP)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            }
            visibility = View.GONE
            outlineProvider = null
            translationZ = sbisButton!!.dp(4).toFloat()
        }
        sbisButtonContainer!!.addView(sbisButton)
        sbisButtonContainer!!.addView(sbisButtonCounter)
        val messagePanelIndex =
            messagePanelMovablePanelContainer?.let {
                mainLayout!!.indexOfChild(it)
            } ?: mainLayout!!.indexOfChild(messagePanel)
        mainLayout!!.addView(sbisButtonContainer, messagePanelIndex)
        initSbisButtonContainerTranslation()
    }

    private fun initSbisButtonContainerTranslation() {
        sbisButtonStateTranslation = sbisButton!!.size.globalVar.getDimen(requireContext()) +
            requireContext().getDimenPx(ru.tensor.sbis.design.R.attr.offset_m) +
            // Выпирающий бэйджик
            requireContext().pxToDp(4)
        sbisButtonContainer?.translationY = sbisButtonStateTranslation
    }

    /** @SelfDocumented */
    override fun initMessagePanelController(
        coreConversationInfo: CoreConversationInfo?,
        viewerSliderArgsFactory: ViewerSliderArgsFactory
    ): CommunicatorMessagePanelController =
        messagePanelDelegate!!.initMessagePanel(
            messagePanel = messagePanel!!,
            coreConversationInfo = coreConversationInfo,
            attachmentViewerArgsFactory = viewerSliderArgsFactory,
            movablePanelContainerId = messagePanelMovablePanelContainer?.id
                ?.takeIf { isFullViewMode }
                ?: ResourcesCompat.ID_NULL
        )

    /** @SelfDocumented */
    protected fun initViewListeners() {
        sbisListView?.apply {
            setOnRefreshListener { presenter.onRefresh() }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    presenter.onScroll(
                        dy,
                        sbisListView!!.recyclerView.findFirstVisibleItemPosition(),
                        sbisListView!!.recyclerView.findLastVisibleItemPosition()
                    )
                }
            })
        }
        if (isFullViewMode) {
            onMessagePanelChangeListener = View.OnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
                val oldHeight = oldBottom - oldTop
                val newHeight = bottom - top
                val difference = newHeight - oldHeight
                if (difference != 0) presenter.onMessagePanelHeightChanged(difference, oldHeight == 0)
            }
            messagePanel!!.addOnLayoutChangeListener(onMessagePanelChangeListener)
        }
    }

    /**@SelfDocumented*/
    override fun changeListViewBottomPadding(difference: Int, withScroll: Boolean, addWithKeyboard: Boolean) {
        if (addWithKeyboard) {
            additionalOffset += difference
        } else {
            sbisListView?.run {
                recyclerViewBottomPadding += difference
                setProgressBarPaddingBottom(recyclerViewBottomPadding)
                setInformationViewVerticalPadding(0, recyclerViewBottomPadding)
                if (withScroll) { scrollRecyclerBy(0, difference) }
            }
            updateMovablePanelContainerPosition()
        }
    }

    /** @SelfDocumented */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapter?.onSavedInstanceState(outState)
        messagePanelDelegate?.onSaveInstanceState(outState)
        outState.putBoolean(IS_FIRST_ACTIVITY_LAUNCH, isFirstActivityLaunch)
        outState.putBoolean(NEED_TO_SHOW_KEYBOARD, needToShowKeyboard)
    }

    /**@SelfDocumented*/
    protected fun restoreStateFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            adapter?.onRestoreInstanceState(it)
            isFirstActivityLaunch = it.getBoolean(IS_FIRST_ACTIVITY_LAUNCH)
            needToShowKeyboard = it.getBoolean(NEED_TO_SHOW_KEYBOARD)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.viewIsStarted()
    }

    override fun onResume() {
        super.onResume()
        presenter.viewIsResumed()
        if (keyboardShowingRule) showKeyboardSafeStackChanges()
        isFirstActivityLaunch = false
    }

    override fun onPause() {
        messagePanel?.hideKeyboard()
        super.onPause()
        presenter.viewIsPaused()
    }

    override fun onStop() {
        super.onStop()
        presenter.viewIsStopped()
    }

    override fun onDestroyView() {
        lastToolbarData = null
        messagePanel!!.removeOnLayoutChangeListener(onMessagePanelChangeListener)
        messagePanel = null
        messagePanelKeyboardOffsetView = null
        messagePanelMovablePanelContainer = null
        messagePanelDelegate = null
        sbisButton = null
        sbisButtonCounter = null
        sbisButtonContainer = null
        viewPropertyAnimator = null
        headerDateView = null
        clearTopNavigation()
        lastToast?.cancel()
        lastToast = null
        adapter?.clear()
        animateKeyboardShowing = false
        sbisListView?.setAdapter(null)
        sbisListView?.setOnRefreshListener(null)
        sbisListView?.clearOnScrollListener()
        sbisListView = null
        layoutManager = null
        super.onDestroyView()
    }

    private fun clearTopNavigation() {
        topNavigation?.run {
            isEditingEnabled = false
            setOnClickListener(null)
            backBtn?.setOnClickListener(null)
            personView?.apply {
                setDataList(emptyList())
                isVisible = true
                setOnClickListener(null)
            }
            titleView?.value = StringUtils.EMPTY
            titleView?.isVisible = true
            subtitleView?.text = StringUtils.EMPTY
            subtitleView?.isVisible = true
        }
        topNavigation = null
        toolbarMoreButton?.setOnClickListener(null)
        toolbarMoreButton = null
    }

    override fun onDestroy() {
        super.onDestroy()
        listHandler.removeCallbacksAndMessages(null)
    }

    /**@SelfDocumented*/
    override fun setToolbarData(toolbarData: ToolbarData) {
        topNavigation?.setToolbarData(toolbarData, lastToolbarData)
        lastToolbarData = toolbarData
    }

    override fun setHasActivityStatus(hasStatus: Boolean) {
        topNavigation?.personView?.setHasActivityStatus(hasStatus)
    }

    /**@SelfDocumented*/
    override fun setTypingUsers(data: UsersTypingView.UsersTypingData) {
        topNavigation?.subtitleView
            ?.getExtension<ConversationSubtitleExtension>()
            ?.typingData = data
    }

    /** @SelfDocumented */
    override fun setMessagesListStyle(newConversation: Boolean) {
        // Nothing
    }

    /**@SelfDocumented*/
    override fun setRelevantMessagePosition(position: Int) {
        adapter?.setRelevantMessagePosition(position)
    }

    /** @SelfDocumented */
    override fun updateDataList(dataList: List<MESSAGE>?, offset: Int) {
        adapter?.setData(dataList, offset)
        layoutManager?.updateLaidOutPositions()
    }

    /** @SelfDocumented */
    override fun notifyItemsChanged(position: Int, count: Int) {
        notifyItemsChanged(position, count, null)
    }

    /** @SelfDocumented */
    override fun notifyItemsChanged(position: Int, count: Int, payLoad: Any?) {
        if (count > 1) {
            adapter?.notifyItemRangeChanged(position, count)
        } else {
            if (payLoad != null) {
                adapter?.notifyItemChanged(position, payLoad)
            } else {
                adapter?.notifyItemChanged(position)
            }
        }
        layoutManager?.updateLaidOutPositions()
    }

    @SuppressLint("NotifyDataSetChanged")
    protected open fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    override fun updateDataListWithoutNotification(dataList: List<MESSAGE>?, offset: Int) {
        adapter?.setDataWithoutNotify(dataList, offset)
    }

    override fun showOlderLoadingProgress(show: Boolean) {
        adapter?.showOlderLoadingProgress(show)
    }

    override fun showNewerLoadingProgress(show: Boolean) {
        adapter?.showNewerLoadingProgress(show)
    }

    protected open fun notifyItemsInserted(position: Int, count: Int) {
        if (count > 1) {
            adapter?.notifyItemRangeInserted(position, count)
        } else {
            adapter?.notifyItemInserted(position)
        }
    }

    /** @SelfDocumented */
    protected open fun notifyItemsRemoved(position: Int, count: Int) {
        if (count > 1) {
            adapter?.notifyItemRangeRemoved(position, count)
        } else {
            adapter?.notifyItemRemoved(position)
        }
        layoutManager?.updateLaidOutPositions()
    }

    /** @SelfDocumented */
    override fun showNewerLoadingError() {
        adapter?.showNewerLoadingError()
    }

    /** @SelfDocumented */
    override fun showOlderLoadingError() {
        adapter?.showOlderLoadingError()
    }

    /** @SelfDocumented */
    override fun setHighlightedMessageUuid(messageUuid: UUID?) {
        adapter?.setHighlightedMessageUuid(messageUuid)
    }

    /** @SelfDocumented */
    override fun showToast(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") @StringRes toastTextId: Int) {
        lastToast?.cancel()
        lastToast = SbisPopupNotification.pushToast(requireContext(), toastTextId)
    }

    /** @SelfDocumented */
    override fun showToast(message: String) {
        lastToast?.cancel()
        lastToast = SbisPopupNotification.pushToast(requireContext(), message)
    }

    /** @SelfDocumented */
    override fun showSnackbarError(@StringRes errorTextId: Int) = Unit

    /** @SelfDocumented */
    override fun showError(errorMessage: String) {
        Timber.e(Exception(errorMessage))
    }

    override fun showConfirmationDialog(
        text: CharSequence?,
        buttons: List<ButtonModel<ConfirmationButtonId>>,
        tag: String,
        style: ConfirmationDialogStyle
    ) {
        val isKeyboardClosed = safeOnClosedKeyboard { showConfirmationDialog(text, buttons, tag, style) }
        if (!isKeyboardClosed) return

        val resultText = if (text.isNullOrEmpty()) {
            resources.getString(ru.tensor.sbis.communicator.design.R.string.communicator_confirmation_dialog_error_stub_text)
        } else {
            text
        }
        val dialog = ConfirmationDialog.YesNoDialogCustom(
            message = resultText,
            buttons = buttons,
            tag = tag,
            style = style
        )
        dialog.show(childFragmentManager)
    }

    override fun showOkCancelDialog(
        message: String?,
        comment: String?,
        tag: String,
        style: ConfirmationDialogStyle,
    ) {
        ConfirmationDialog.OkCancelDialog(
            message = message,
            comment = comment,
            tag = tag,
            style = style
        ).show(childFragmentManager)
    }

    override fun onButtonClick(tag: String?, id: String, sbisContainer: SbisContainerImpl) {
        sbisContainer.dismiss()
        presenter.onConfirmationDialogButtonClicked(tag, id)
    }

    /** @SelfDocumented */
    override fun showUnattachedPhoneError(errorText: String?) = Unit

    /** @SelfDocumented */
    override fun showMessageActionsList(message: MESSAGE, actions: List<MessageAction>) {
        if (presenter.actionsMenuShown) return
        val isKeyboardClosed = safeOnClosedKeyboard { showMessageActionsList(message, actions) }
        if (!isKeyboardClosed) return

        val recyclerView = preparedRecyclerView() ?: return
        val messagePosition = adapter!!.getPositionForMessage(message)

        val (anchor, alignment) = getAnchorAndAlignment(messagePosition, recyclerView)

        if (anchor != null && alignment != null) {
            val sbisMenu = SbisMenu(children = actions.mapIndexed { index, action -> getActionMenuItem(action, index) })
            showMenu(recyclerView, sbisMenu, anchor, alignment, DimType.CUTOUT)
        }
    }

    protected fun preparedRecyclerView(): RecyclerView? =
        sbisListView?.recyclerView?.apply {
            id = R.id.communicator_conversation_messages_recycler_view_id
        }

    protected fun getAnchorAndAlignment(messagePosition: Int, recyclerView: RecyclerView): Pair<View?, HorizontalAlignment?> {
        val clickedItem = recyclerView.findViewHolderForAdapterPosition(messagePosition)?.itemView

        val videoMessageView = clickedItem?.findViewById<View>(ru.tensor.sbis.design.cloud_view.R.id.cloud_view_content) as? VideoMessageView
        val income = clickedItem?.findViewById<View>(ru.tensor.sbis.design.cloud_view.R.id.cloud_view_background_income)
        val outcome = clickedItem?.findViewById<View>(ru.tensor.sbis.design.cloud_view.R.id.cloud_view_background_outcome)

        val anchor = videoMessageView ?: income ?: outcome
        val alignment = income?.let { HorizontalAlignment.LEFT } ?: outcome?.let { HorizontalAlignment.RIGHT }
        return anchor to alignment
    }

    protected fun showMenu(
        recyclerView: RecyclerView,
        sbisMenu: SbisMenu,
        anchor: View,
        alignment: HorizontalAlignment,
        dimType: DimType
    ) {
        doWithShowedMenu(sbisMenu)

        sbisMenu.showMenuForConversationRegistry(
            fragmentManager = childFragmentManager,
            anchor = anchor,
            priorityHorizontalAlignment = alignment,
            dimType = dimType,
            cutoutBounds = getCutoutBounds(recyclerView),
            boundsViewId = if (isTablet) recyclerView.id else View.NO_ID
        ) {
            sbisListView?.recyclerView?.apply {
                id = View.NO_ID
            }
        }
    }

    protected fun safeOnClosedKeyboard(action: () -> Unit): Boolean {
        val rootView = view ?: return false

        return if (currentKeyboardOffset != 0) {
            if (needToShowKeyboard) hideKeyboard()
            rootView.postDelayed({ action.invoke() }, 70)
            false
        } else {
            true
        }
    }

    protected fun doWithShowedMenu(sbisMenu: SbisMenu) {
        presenter.actionsMenuShown = true
        clearSwipeTouchEvents()
        val isFastScrollVisible = sbisButtonContainer?.isVisible == true
        hideFastScrollDownButton()
        headerDateView?.hide()

        sbisMenu.addCloseListener {
            presenter.actionsMenuShown = false
            setFastScrollDownVisibility(isFastScrollVisible)
            headerDateView?.let {
                it.text = StringUtils.EMPTY
                it.show()
            }
        }
    }

    protected fun getCutoutBounds(recyclerView: RecyclerView): Rect? =
        messagePanel?.let {
            recyclerView.getRectDescendantParent(recyclerView.rootView as ViewGroup).apply {
                val messagePanelRect = it.getRectDescendantParent(it.rootView as ViewGroup)
                bottom = messagePanelRect.top
            }
        }

    /**
     * Сбросить состояние тача по ячейке-облачку, чтобы предотвратить свайп для цитирования.
     *
     * Небольшой костыль, который предотвращает свайп во время показа меню,
     * не работает на 100%, если очень захотеть, то может получиться.
     * Правильное предотвращение свайпа ячейки требует больших доработок, а сам кейс немного дурной.
     */
    private fun clearSwipeTouchEvents() {
        view?.dispatchTouchEvent(
            MotionEvent.obtain(
                0,
                0,
                MotionEvent.ACTION_CANCEL,
                Float.MAX_VALUE,
                Float.MAX_VALUE,
                0
            )
        )
    }

    /** @SelfDocumented */
    override fun showKeyboard() {
        messagePanel?.showKeyboard()
    }

    /** @SelfDocumented */
    override fun hideKeyboard() {
        needToShowKeyboard = false
        messagePanel?.hideKeyboard()
    }

    /** @SelfDocumented */
    override fun forceHideKeyboard() {
        hideKeyboard()
        onKeyboardCloseMeasure(0)
    }

    /**
     * Костыль для открытия сообщений по пушу в сценарии,
     * когда экран переписки уже был открыт с поднятой клавиатурой.
     * В этом случае предыдущий экран пытается восстановить клавиатуру одновременно с добавлением нового фрагмента,
     * на котором тоже должен быть подъем клавиатуры.
     */
    private fun showKeyboardSafeStackChanges() {
        try {
            parentFragmentManager.executePendingTransactions()
        } catch (ex: IllegalStateException) {
            // ignore already executing
        }
        if (parentFragmentManager.fragments.last() === this) {
            showKeyboard()
        } else {
            hideKeyboard()
        }
    }

    /** @SelfDocumented */
    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        presenter.onKeyboardAppears(keyboardHeight)
        messagePanelDelegate?.onKeyboardOpenMeasure(keyboardHeight)
        needToShowKeyboard = true
        currentKeyboardOffset = keyboardHeight
        return true
    }

    /** @SelfDocumented */
    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        presenter.onKeyboardDisappears(keyboardHeight)
        messagePanelDelegate?.onKeyboardCloseMeasure(keyboardHeight)
        needToShowKeyboard = false
        currentKeyboardOffset = 0
        return true
    }

    protected fun getKeyboardAnimator(keyboardHeight: Int): ValueAnimator {
        val from = currentKeyboardOffset.toFloat()
        val to = keyboardHeight.toFloat()
        val additionalScroll = additionalOffset
        val updateMessagePanel = keyboardHeight < currentKeyboardOffset
        return ValueAnimator.ofFloat(from, to).apply {
            interpolator = keyboardInterpolator
            duration = KEYBOARD_ANIMATION_DURATION_MS
            var lastAdditionalScrollValue = 0
            addUpdateListener { animator ->
                val offset = animator.animatedValue.castTo<Float>()!!
                val fraction = animator.animatedFraction.castTo<Float>()!!
                val intOffset = offset.roundToInt()
                val offsetDiff = intOffset - currentKeyboardOffset
                currentKeyboardOffset = intOffset
                presenter.onKeyboardAppears(intOffset)
                setListViewBottomPadding(intOffset)

                val additionalScrollValue = (fraction * additionalScroll).roundToInt()
                val additionalScrollDy = additionalScrollValue - lastAdditionalScrollValue
                lastAdditionalScrollValue = additionalScrollValue
                val scrollDy = offsetDiff + additionalScrollDy
                scrollListView(scrollDy, false)

                if (!updateMessagePanel) return@addUpdateListener
                translateMessagePanel(-offset)
                messagePanelDelegate?.let {
                    if (intOffset > 0) {
                        it.onKeyboardOpenMeasure(intOffset)
                    } else {
                        it.onKeyboardCloseMeasure(intOffset)
                        presenter.onKeyboardDisappears(intOffset)
                    }
                }
            }
        }
    }

    /**@SelfDocumented*/
    protected open fun translateMessagePanel(y: Float) {
        messagePanel?.translationY = y
        val buttonDefaultTranslation = if (sbisButtonContainer?.isVisible == true) 0f else sbisButtonStateTranslation
        sbisButtonContainer?.translationY = y + buttonDefaultTranslation
        messagePanelKeyboardOffsetView?.updateLayoutParams { height = abs(y).roundToInt() }
        updateMovablePanelContainerPosition()
    }

    private fun updateMovablePanelContainerPosition() {
        val bottomPadding = (messagePanel?.height ?: 0) + currentKeyboardOffset
        messagePanelMovablePanelContainer?.updatePadding(bottom = bottomPadding)
    }

    /** @SelfDocumented */
    override fun setFastScrollDownUnreadCounterValue(unreadCounter: Int) {
        sbisButtonCounter?.counter = unreadCounter
        sbisButtonCounter?.contentDescription = unreadCounter.toString()
    }

    private fun setFastScrollDownVisibility(isVisible: Boolean) =
        if (isVisible) showFastScrollDownButton()
        else hideFastScrollDownButton()

    /** @SelfDocumented */
    override fun showFastScrollDownButton() {
        if (canChangeFastScrollDownVisibility) {
            val isVisible = sbisButtonContainer?.visibility == View.VISIBLE
            if (!isFastScrollDownAnimationRunning && (isFastScrollUpAnimationRunning || isVisible)) return
            isFastScrollDownAnimationRunning = false
            isFastScrollUpAnimationRunning = true

            changeShadowVisibility(true)
            viewPropertyAnimator?.cancel()
            getScrollButtonAnimator(
                translationYValue = { -keyboardHeight.toFloat() },
                startAction = { sbisButtonContainer?.visibility = View.VISIBLE },
                endAction = null
            )?.start()
        }
        deferredFastScrollVisibility = true
    }

    /** @SelfDocumented */
    override fun hideFastScrollDownButton(force: Boolean) {
        if (canChangeFastScrollDownVisibility) {
            val isVisible = sbisButtonContainer?.visibility == View.VISIBLE
            if (!isFastScrollUpAnimationRunning && (isFastScrollDownAnimationRunning || !isVisible)) return
            isFastScrollUpAnimationRunning = false
            isFastScrollDownAnimationRunning = true

            changeShadowVisibility(false)
            viewPropertyAnimator?.cancel()
            getScrollButtonAnimator(
                translationYValue = { sbisButton?.let { -keyboardHeight + sbisButtonStateTranslation } ?: 0F },
                startAction = null,
                endAction = { sbisButtonContainer?.visibility = View.GONE },
                delayed = !force
            )?.start()
        }
        deferredFastScrollVisibility = false
    }

    /**@SelfDocumented*/
    protected fun restoreFastScrollDownVisibility() {
        setFastScrollDownVisibility(deferredFastScrollVisibility)
    }

    private fun changeShadowVisibility(isVisible: Boolean) {
        sbisButton?.elevation = if (isVisible) 4F else 0F
    }

    private fun getScrollButtonAnimator(
        translationYValue: () -> Float,
        startAction: Runnable?,
        endAction: Runnable?,
        delayed: Boolean = true
    ): ViewPropertyAnimator? =
        sbisButtonContainer?.let {
            val originValue = translationYValue()
            val keyboardHeight = keyboardHeight
            viewPropertyAnimator = it.animate()
                .apply {
                    if (delayed) startDelay = 75
                }
                .withStartAction {
                    startAction?.run()
                }
                .withEndAction {
                    endAction?.run()
                    isFastScrollDownAnimationRunning = false
                    isFastScrollUpAnimationRunning = false
                }
                .setUpdateListener {
                    val keyboardDiff = keyboardHeight - this.keyboardHeight
                    if (keyboardDiff != 0) {
                        sbisButtonContainer?.let { container -> container.translationY += keyboardDiff }
                    }
                }
                .translationY(originValue)
                .setInterpolator(LinearInterpolator())
                .setDuration(150)
            return viewPropertyAnimator
        }

    /**
     * @param bottomPadding - new bottom padding that should be set to sbisListView
     */
    override fun setListViewBottomPadding(bottomPadding: Int) {
        messagePanel?.also {
            val sumPadding = defaultRecyclerBottomPadding + (it.height.takeIf { isFullViewMode } ?: 0) + bottomPadding
            sbisListView?.run {
                recyclerViewBottomPadding = sumPadding
                setProgressBarPaddingBottom(recyclerViewBottomPadding)
                setInformationViewVerticalPadding(0, recyclerViewBottomPadding)
            }
        }
    }

    /** @SelfDocumented */
    override fun scrollListView(scrollBy: Int, post: Boolean) {
        if (post) {
            listHandler.post {
                if (scrollBy == Integer.MAX_VALUE) layoutManager?.onScrollToBottom()
                sbisListView?.scrollRecyclerBy(0, scrollBy)
            }
        } else {
            if (scrollBy == Integer.MAX_VALUE) layoutManager?.onScrollToBottom()
            sbisListView?.scrollRecyclerBy(0, scrollBy)
        }
    }

    /** @SelfDocumented */
    override fun scrollToBottom(skipScrollToPosition: Boolean, withHide: Boolean) {
        if (withHide) {
            sbisListView?.setRecyclerViewVisibilityStatus(View.INVISIBLE)
        }
        if (skipScrollToPosition) {
            sbisListView?.recyclerView?.stopScroll()
        }

        sbisListView?.scrollToPosition(0)
        sbisListView?.recyclerView?.doOnPreDraw {
            val holder = sbisListView?.recyclerView?.findViewHolderForAdapterPosition(0)
            val holderHeight = holder?.itemView?.measuredHeight ?: 0
            scrollListView(holderHeight, false)
        }

        listHandler.post {
            if (withHide) {
                sbisListView?.setRecyclerViewVisibilityStatus(AbstractListView.UNSPECIFIED)
            }
        }
    }

    /** @SelfDocumented */
    override fun scrollToPosition(position: Int) {
        sbisListView?.scrollToPosition(position)
    }

    override fun showLoading() {
        sbisListView?.setInProgress(true)
    }

    override fun hideLoading() {
        sbisListView?.setInProgress(false)
    }

    override fun showControllerErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            sbisListView?.showInformationViewData(createStubViewContent(it))
        }
    }

    override fun ignoreProgress(ignore: Boolean) {
        sbisListView?.ignoreProgress(ignore)
    }

    override fun hideStubView() {
        sbisListView?.hideInformationView()
    }

    override fun showStubView(messageTextId: Int) {
        showStubView(createStubViewContent(messageTextId))
    }

    override fun showStubView(stubContent: StubViewContent) {
        sbisListView?.showInformationViewData(stubContent)
    }

    override fun resetPagingLoadingError() {
        adapter?.resetPagingLoadingError()
    }

    protected open fun createStubViewContent(@StringRes messageTextId: Int): StubViewContent {
        sbisListView?.hideInformationView()
        val imageType = if (messageTextId == ru.tensor.sbis.communicator.design.R.string.communicator_conversation_not_available) {
            StubViewImageType.ETC
        } else {
            StubViewImageType.NO_MESSAGES
        }
        return ImageStubContent(
            imageType = imageType,
            messageRes = messageTextId,
            detailsRes = ResourcesCompat.ID_NULL
        )
    }

    protected open fun createStubViewContent(messageTextId: String): StubViewContent {
        return ImageStubContent(
            imageType = StubViewImageType.NO_MESSAGES,
            message = messageTextId,
            detailsRes = ResourcesCompat.ID_NULL
        )
    }

    /**
     * Изменяет доступность жеста свайпа влево для закрытия фрагмента
     * @param availability - true, если жест доступен
     */
    fun setBackSwipeAvailability(availability: Boolean) {
        swipeBackLayout?.isEnabled = availability
    }

    /** @SelfDocumented */
    override fun onViewGoneBySwipe() {
        view?.visibility = View.GONE
        super.onViewGoneBySwipe()
    }

    /** @SelfDocumented */
    override fun updateSendingState(position: Int) {
        adapter?.updateSendingState(position)
    }

    override fun getAdapterPosition(message: MESSAGE): Int =
        adapter?.getPositionForMessage(message) ?: 0

    /** @SelfDocumented */
    override fun onItemClicked(requestCode: Int, position: Int) {
        if (requestCode == DIALOG_CODE_SELECTED_MESSAGE_ACTIONS) presenter.onMessageActionClick(position)
    }

    override fun onMessageQuoted() {
        animateKeyboardShowing = true
    }

    override fun showPopupDeleteMessageForAll() {
        showPopupWithManyButtons(
            DIALOG_CODE_CONFIRM_DELETE_OUTGOING_MESSAGE,
            null,
            ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_forever,
            arrayListOf(
                getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_for_all),
                getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_for_me),
                getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_negative)
            ),
            hashMapOf(Pair(getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_for_all), ru.tensor.sbis.modalwindows.R.style.ModalWindowsAlertDialogButtonRemoval)),
            buttonsId = mapOf(
                getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_for_all) to R.id.communicator_delete_message_or_dialog_for_all_id,
                getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_for_me) to R.id.communicator_delete_message_or_dialog_for_me_id,
                getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_negative) to R.id.communicator_delete_message_or_dialog_negative_id
            )
        )
    }

    protected fun showPopupWithManyButtons(
        requestCode: Int,
        message: Int?,
        title: Int,
        buttonsName: ArrayList<String>,
        buttonsStyle: HashMap<String, Int>? = null,
        buttonsId: Map<String, Int>? = null
    ) {
        PopupConfirmation.newButtonsListInstance(
            requestCode,
            buttonsName,
            message?.let { getString(it) },
            buttonsStyle,
            buttonsId
        )
            .requestTitle(getString(title))
            .setEventProcessingRequired(true)
            .show(childFragmentManager, PopupConfirmation::class.java.simpleName)
    }

    protected fun deleteMyDialogOrOutgoingMessage(requestCode: Int, itemValue: String?) {
        when (itemValue) {
            getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_negative) -> {
                super.onItemClicked(requestCode, itemValue)
            }
            getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_for_me) -> {
                if (requestCode == DIALOG_CODE_CONFIRM_DELETE_OUTGOING_MESSAGE) presenter.deleteMessageOnlyForMe() else presenter.onDialogDeletingClicked()
            }
            getString(ru.tensor.sbis.communicator.design.R.string.communicator_delete_message_or_dialog_for_all) -> {
                if (requestCode == DIALOG_CODE_CONFIRM_DELETE_OUTGOING_MESSAGE) presenter.deleteMessageForAll() else presenter.onDialogDeletingConfirmed()
            }
        }
    }

    override fun onYes(requestCode: Int) {
        when (requestCode) {
            DIALOG_CODE_CONFIRM_DELETE_OUTGOING_MESSAGE -> presenter.deleteMessageForAll()
        }
    }

    override fun onItemClicked(requestCode: Int, itemValue: String?) {
        when (requestCode) {
            DIALOG_CODE_CONFIRM_DELETE_OUTGOING_MESSAGE -> {
                deleteMyDialogOrOutgoingMessage(requestCode, itemValue)
            }
        }
    }

    private fun getActionMenuItem(action: MessageAction, actionIndex: Int): MenuItem {
        with(action) {
            return MenuItem(title = getString(textRes), image = iconRes, destructive = destructive) {
                presenter.onMessageActionClick(actionIndex)
            }
        }
    }

    private fun View.getRectDescendantParent(parent: ViewGroup): Rect {
        val anchorRect = Rect()
        getDrawingRect(anchorRect)
        parent.offsetDescendantRectToMyCoords(this, anchorRect)
        return anchorRect
    }
}

private const val DIALOG_CODE_SELECTED_MESSAGE_ACTIONS = 777
private const val IS_FIRST_ACTIVITY_LAUNCH = "is_first_activity_launch"
private const val NEED_TO_SHOW_KEYBOARD = "need_to_show_keyboard"
private const val KEYBOARD_ANIMATION_DURATION_MS = 250L
private const val DIALOG_CODE_CONFIRM_DELETE_OUTGOING_MESSAGE = 5
private const val MAX_SCROLL_VELOCITY = 20000
internal const val CONVERSATION_VIEW_MODE = "CONVERSATION_VIEW_MODE"