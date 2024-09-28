package ru.tensor.sbis.message_panel.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsView
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.message_panel.view.layout.MessagePanelLayout
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView
import ru.tensor.sbis.design.message_panel.decl.record.MessagePanelRecordButtonListener
import ru.tensor.sbis.design.message_panel.view.layout.MessagePanelEditText
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import androidx.core.view.isVisible
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.message_panel.contract.MessagePanelBindingDependency
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.contract.MessagePanelControllerImpl
import ru.tensor.sbis.message_panel.helper.isNewMessageMode
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.hint.MessagePanelHintConfig
import ru.tensor.sbis.message_panel.viewModel.livedata.hint.MessagePanelHintConfig.Companion.DEFAULT_HINT
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.*
import kotlin.math.roundToInt

private const val MAX_HEIGHT_PERCENTS = 0.7

/**
 * @author Subbotenko Dmitry
 */
open class MessagePanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.messagePanelTheme,
    @StyleRes defStyleRes: Int = R.style.MessagePanelDefaultTheme
) : RelativeLayout(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr, defStyleRes),
    MessagePanelViewDependency,
    KeyboardEventListener {

    private val disposer = CompositeDisposable()
    @Px
    private var topOffset: Int = 0
    @Px
    private var insetsSum: Int = 0
    @Px
    private var displayHeight: Int = 0
    @Px
    private var maxHeight = Int.MAX_VALUE
    private var keyboardHeight: Int = 0

    protected val editTextView: EditText

    private val hintConfig: MessagePanelHintConfig

    private val attachmentsPanelDelegate: AttachmentsPanelDelegate
    protected open val bindingDelegate: MessagePanelBindingDelegate
    protected val landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    /**
     * TODO: 1/9/2020 https://online.sbis.ru/opendoc.html?guid=94a29537-e3f9-49cc-bda5-3e31d1f73f66
     */
    protected var keyboardEventMediator: KeyboardEventMediator? = null
    private val messagePanelFitContentDelegate: MessagePanelFitContentDelegate

    private var onKeyPreImeListener: OnKeyPreImeListener? = null

    private val isTablet = DeviceConfigurationUtils.isTablet(context)
    private val keyboardEventDelegates: MutableList<KeyboardEventListener> = mutableListOf()

    private val defaultLayout: MessagePanelLayout?

    val currentText: String
        get() = editTextView.text.toString()

    val isAudioRecordEnabled: Boolean
        get() = defaultLayout?.audioRecordButton?.isVisible == true

    val isVideoRecordEnabled: Boolean
        get() = defaultLayout?.videoRecordButton?.isVisible == true

    final override val shouldAdjustPositionOnKeyboardChanges: Boolean

    /**
     * Признак блокировки ввода/установки текста в поле ввода.
     */
    var isInputLocked: Boolean
        get() = (editTextView as? MessagePanelEditText)?.isInputLocked == true
        set(value) {
            (editTextView as? MessagePanelEditText)?.isInputLocked = value
        }

    /**
     * Признак доступности кнопки отправки.
     */
    var isSendButtonEnabled: Boolean
        get() = defaultLayout?.sendButton?.isEnabled == true
        set(value) {
            defaultLayout?.sendButton?.isEnabled = value
        }

    interface OnKeyPreImeListener {
        /**
         * Callback события нажатия кнопки перед его обработкой IME
         *
         * @param event описание события
         */
        fun onKeyPreImeEvent(event: KeyEvent)
    }

    init {
        val layoutRes: Int
        // получение атрибутов из разметки
        val attributes = getContext().theme.obtainStyledAttributes(attrs, R.styleable.MessagePanel, defStyleAttr, defStyleRes)

        val topOffset = attributes.getDimensionPixelSize(R.styleable.MessagePanel_android_topOffset, ID_NULL)
        layoutRes = attributes.getResourceId(R.styleable.MessagePanel_MessagePanel_customLayout, ID_NULL)

        val disabledHint = attributes.getResourceId(R.styleable.MessagePanel_MessagePanel_disabledHint, DEFAULT_HINT)
        val enabledHint = attributes.getResourceId(R.styleable.MessagePanel_MessagePanel_enabledHint, DEFAULT_HINT)
        hintConfig = MessagePanelHintConfig(disabledHint, enabledHint)

        val hideAllRecipients = attributes.getBoolean(R.styleable.MessagePanel_MessagePanel_hideAllRecipients, false)
        val recipientsHintText = attributes.getString(RMPCommon.styleable.MessagePanel_MessagePanel_recipientsHintText)
        val recipientsAllChosenText = attributes.getString(RMPCommon.styleable.MessagePanel_MessagePanel_recipientsAllChosenText)

        shouldAdjustPositionOnKeyboardChanges =
            attributes.getBoolean(R.styleable.MessagePanel_MessagePanel_adjustPositionOnKeyboardChanges, true)

        attributes.recycle()

        @Suppress("LeakingThis")
        if (layoutRes != ID_NULL) {
            defaultLayout = null
            inflate(getContext(), layoutRes, this)
            bindingDelegate = MessagePanelBindingDelegate(
                messagePanel = this,
                viewDependency = this,
                autoTranslateOnFocus = !shouldAdjustPositionOnKeyboardChanges
            )
        } else {
            defaultLayout = MessagePanelLayout(this, isInEditMode)
            bindingDelegate = MessagePanelBindingDelegate(
                layout = defaultLayout,
                viewDependency = this,
                autoTranslateOnFocus = !shouldAdjustPositionOnKeyboardChanges
            )
        }

        editTextView = findViewById(R.id.message_panel_edit_text_view)
        messagePanelFitContentDelegate = MessagePanelFitContentDelegate(context.resources, editTextView.lineHeight)

        val recipientsView = findViewById<RecipientsView>(R.id.message_panel_recipients_view)
        recipientsView?.also { view ->
            view.isVisible = !hideAllRecipients
            recipientsHintText?.also { view.hintText = it }
            recipientsAllChosenText?.also { view.allChosenText = it }
        }

        val attachmentsView: AttachmentsView? = findViewById(R.id.message_panel_attachments_view)
        attachmentsView?.disableFadeAnimation()
        attachmentsPanelDelegate = AttachmentsPanelDelegate(attachmentsView)

        setTopOffset(topOffset)

        @Suppress("LeakingThis")
        if (background == null) {
            setBackgroundColor(context.getColorFromAttr(RDesign.attr.contrastBackgroundColor))
        }
    }

    fun setTopOffset(topOffset: Int) {
        this.topOffset = topOffset
        defaultLayout?.topOffset = topOffset
    }

    fun setOnKeyPreImeListener(listener: OnKeyPreImeListener?) {
        onKeyPreImeListener = listener
    }

    /**
     * Установить слушателя зажатия кнопки аудиозаписи.
     */
    fun setOnAudioRecordPressedListener(listener: MessagePanelRecordButtonListener?) {
        defaultLayout?.setAudioRecordActionListener(listener)
    }

    /**
     * Установить слушателя зажатия кнопки видеозаписи.
     */
    fun setOnVideoRecordPressedListener(listener: MessagePanelRecordButtonListener?) {
        defaultLayout?.setVideoRecordActionListener(listener)
    }

    /**
     * Установить слушателя нажатия кнопки открытия шторки быстрых ответов crm.
     */
    fun setQuickReplyButtonOnClickListener(listener: OnClickListener) {
        defaultLayout?.setQuickReplyButtonOnClickListener(listener)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (bindingDelegate.isInitialize && !bindingDelegate.isBinded) {
            bindingDelegate.bind()
            val liveData = bindingDelegate.viewModel.liveData
            attachmentsPanelDelegate.bind(liveData)
            messagePanelFitContentDelegate.bind(liveData)
            keyboardEventMediator = liveData
        }
        updateDisplayHeight()
    }

    /**
     * Определение максимальной высоты компонента в зависимости от занятого пространства [keyboardHeight] и отступа
     * сверху [topOffset]
     *
     * @param keyboardHeight занятое чем-либо пространство. Может быть занято клавиатурой или зарезервировано под контент
     */
    fun updateMaxHeight(keyboardHeight: Int = this.keyboardHeight) {
        this.keyboardHeight = keyboardHeight
        defaultLayout?.bottomOffset = keyboardHeight
        val space = when {
            !isTablet && landscape -> getLandscapeReservedSpace(keyboardHeight)
            else                   -> getPortraitReservedSpace(keyboardHeight)
        }

        displayHeight.minus(space).minus(topOffset).let { newMaxHeight ->
            if (maxHeight != newMaxHeight) {
                maxHeight = newMaxHeight
                requestLayout()
            }

            messagePanelFitContentDelegate.setPanelMaxHeight(maxHeight)
        }
    }

    private fun updateDisplayHeight() {
        insetsSum = ViewCompat.getRootWindowInsets(this)?.let {
            it.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                .plus(it.getInsets(WindowInsetsCompat.Type.statusBars()).top)
        } ?: 0
        displayHeight = rootView.measuredHeight - insetsSum
        updateMaxHeight()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        keyboardEventMediator = null
        bindingDelegate.unbind()
        attachmentsPanelDelegate.unbind()
        messagePanelFitContentDelegate.unbind()
        disposer.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (defaultLayout != null) {
            defaultLayout.onMeasure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(
                measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
                measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
            )
        } else {
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)

            val panelHeightSpec = if (heightMode == MeasureSpec.EXACTLY) {
                val height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            } else {
                MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
            }
            super.onMeasure(widthMeasureSpec, panelHeightSpec)
        }
    }

    override fun getSuggestedMinimumHeight(): Int =
        defaultLayout?.getSuggestedMinimumHeight()
            ?: super.getSuggestedMinimumHeight()

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        defaultLayout?.onLayout()
            ?: super.onLayout(changed, l, t, r, b)
        updateDisplayHeight()
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        event?.let {
            onKeyPreImeListener?.onKeyPreImeEvent(it)
        }
        return super.dispatchKeyEventPreIme(event)
    }

    override fun showKeyboard() {
        keyboardEventMediator?.postKeyboardEvent(OpenedByRequest)
    }

    override fun hideKeyboard() {
        keyboardEventMediator?.postKeyboardEvent(ClosedByRequest)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        updateMaxHeight(keyboardHeight = keyboardHeight)
        keyboardEventMediator?.postKeyboardEvent(OpenedByAdjustHelper(keyboardHeight))
        keyboardEventDelegates.forEach { it.onKeyboardOpenMeasure(keyboardHeight) }
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        updateMaxHeight(keyboardHeight = 0)
        keyboardEventMediator?.postKeyboardEvent(ClosedByAdjustHelper(keyboardHeight))
        keyboardEventDelegates.forEach { it.onKeyboardCloseMeasure(0) }
        return true
    }

    /**
     * Добавить слушателя делегата панели сообщений для событий клавиатуры.
     */
    fun addKeyboardEventDelegate(listener: KeyboardEventListener) {
        keyboardEventDelegates.add(listener)
    }

    /**
     * Удалить слушателя делегата панели сообщений для событий клавиатуры.
     */
    fun removeKeyboardEventDelegate(listener: KeyboardEventListener) {
        keyboardEventDelegates.remove(listener)
    }

    /**
     * Вычисление зарезервированного места на экране для диалога над панелью ввода в альбомной ориентации
     */
    private fun getLandscapeReservedSpace(keyboardHeight: Int): Int {
        // Если телефон в альбомной ориентации, то оставляем размер как есть
        return keyboardHeight
    }

    /**
     * Вычисление зарезервированного места на экране для диалога над панелью ввода в портретной ориентации или планшета
     */
    private fun getPortraitReservedSpace(keyboardHeight: Int): Int {
        // "Режим ввода нового сообщения" - для заполнения доступно всё пространство
        if (editTextView.isNewMessageMode) {
            return keyboardHeight
        }

        //Если телефон в портретной ориентации или планшет, то максимальный размер урезаем до 70%
        return displayHeight.minus(keyboardHeight).minus(topOffset).times(1 - MAX_HEIGHT_PERCENTS).roundToInt()
            .plus(keyboardHeight)
    }

    protected open fun <MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> getViewModel(
        controller: MessagePanelController<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>
    ): MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> {
        check(controller is MessagePanelControllerImpl<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>) {
            "MessagePanelController must be provided from DI as MessagePanelControllerImpl"
        }
        return controller.viewModel
    }

    fun <MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> initViewModel(
        controller: MessagePanelController<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
        dependency: MessagePanelBindingDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT>,
        progressDialogDelegate: ProgressDialogDelegate,
        alertDialogDelegate: AlertDialogDelegate,
        @IdRes movablePanelContainerId: Int = ID_NULL
    ) {
        val viewModel = getViewModel(controller)

        viewModel.liveData.setIsLandscape(landscape)
        viewModel.liveData.hintConfig = hintConfig

        keyboardEventMediator = viewModel.liveData

        bindingDelegate.initDelegate(
            dependency,
            viewModel,
            progressDialogDelegate,
            alertDialogDelegate,
            movablePanelContainerId
        )
        bindingDelegate.bind()

        attachmentsPanelDelegate.bind(viewModel.liveData)

        messagePanelFitContentDelegate.bind(viewModel.liveData)
    }

    /**
     * Запрещаем обработку события родителям, для того что бы не вызывалось взаимодействие с теми элементами которые перекрывает панель сообщений
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        return true
    }
}