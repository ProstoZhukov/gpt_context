package ru.tensor.sbis.design.message_panel.view.layout

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewOutlineProvider
import android.widget.FrameLayout.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsView
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewMode
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.R
import ru.tensor.sbis.design.message_panel.common.view.quote.MessagePanelQuoteView
import ru.tensor.sbis.design.message_panel.common.view.recipients.MessagePanelRecipientsView
import ru.tensor.sbis.design.message_panel.decl.record.MessagePanelRecordButtonListener
import ru.tensor.sbis.design.message_panel.view.MessagePanel
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.extentions.setLeftPadding
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

/**
 * Разметка панели ввода сообщений.
 *
 * @property rootView корневая view панели сообщений.
 * @property isInEditMode true, если разметка находится в превью моде студии.
 *
 * @author vv.chekurda
 */
class MessagePanelLayout(
    val rootView: ViewGroup,
    private val isInEditMode: Boolean = false
) {

    init {
        applyPreviewAppTheme()
    }

    private val context: Context
        get() = rootView.context

    private val resources: Resources
        get() = rootView.resources

    private val messageContainerVerticalSpacing = context.getDimenPx(RDesign.attr.offset_xs)
    private val messageContainerStartSpacingWithoutAttach = context.getDimenPx(RDesign.attr.offset_m)
    private val sendButtonHorizontalSpacing = context.getDimenPx(RDesign.attr.offset_xs)
    private val iconsPaddingBottom = context.getDimenPx(RDesign.attr.offset_2xs)
    private val buttonsColorStateList = ContextCompat.getColorStateList(context, RMPCommon.color.design_message_panel_control_selectable_color)
    private val buttonsTextSize = context.getDimenPx(RDesign.attr.iconSize_3xl).toFloat()
    private val recordButtonWidth = resources.getDimensionPixelSize(R.dimen.design_message_panel_record_button_width)
    private val recordButtonHeight = resources.getDimensionPixelSize(RDesign.dimen.input_text_field_minimum_height)
    private val recordButtonEndSpacing = context.getDimenPx(RDesign.attr.offset_2xs)
    private val quickReplyButtonSize = context.getDimenPx(RDesign.attr.iconSize_2xl).toFloat()
    private val quickReplyButtonEndSpacing = context.getDimenPx(RDesign.attr.offset_m)

    private val messageContainerStartSpacing: Int
        get() = if (attachButton.safeMeasuredWidth == 0) {
            messageContainerStartSpacingWithoutAttach
        } else {
            0
        }

    private var topCustomView: View? = null
    private var bottomCustomView: View? = null
    private val topDivider = View(context).apply {
        id = R.id.design_message_panel_top_divider
        layoutParams = LayoutParams(
            MATCH_PARENT,
            resources.getDimensionPixelSize(RMPCommon.dimen.design_message_panel_common_top_divider_height)
        )
        setBackgroundColor(context.getColorFromAttr(RDesign.attr.readonlyBorderColor))
    }

    private var displayHeight: Int = 0

    private val isLandscape = DeviceConfigurationUtils.isLandscape(context)
    private val isTablet = DeviceConfigurationUtils.isTablet(context)

    @Px
    var topOffset: Int = 0
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) rootView.safeRequestLayout()
        }

    @Px
    var bottomOffset: Int = 0
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) rootView.safeRequestLayout()
        }

    /**
     * Поле ввода.
     */
    val inputView = MessagePanelEditText(
        ContextThemeWrapper(context, R.style.MessagePanelInputStyle)
    ).apply {
        id = R.id.design_message_panel_edit_text_view
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            val horizontalMargin = context.getDimenPx(RDesign.attr.offset_st)
            leftMargin = horizontalMargin
            rightMargin = horizontalMargin
        }
        isFocusable = true
        isFocusableInTouchMode = true
    }

    /**
     * Список превью прикрепленных вложений.
     */
    val attachmentsView = AttachmentsView(context, viewMode = AttachmentsViewMode.MESSAGE).apply {
        id = R.id.design_message_panel_attachments_view
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            val margin = context.getDimenPx(RDesign.attr.offset_xs)
            topMargin = margin
            bottomMargin = margin
        }
        setVisibility(AttachmentsViewVisibility.GONE)
    }

    /**
     * Контейнер контента сообщения.
     */
    val messageContainer = LinearLayout(context).apply {
        id = R.id.design_message_panel_message_container_view
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        orientation = LinearLayout.VERTICAL
        background = ContextCompat.getDrawable(context, RMPCommon.drawable.design_message_panel_common_input_field_drawable)
        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true
        addView(inputView)
        addView(attachmentsView)
    }

    /**
     * Панель получателей сообщения.
     */
    val recipientsView = MessagePanelRecipientsView(context).apply {
        id = R.id.design_message_panel_recipients_view
        layoutParams = LayoutParams(
            MATCH_PARENT,
            context.getDimenPx(RDesign.attr.inlineHeight_4xs)
        )
        isVisible = false
    }

    /**
     * Панель прикрепленной цитаты/редакции сообщения.
     */
    val quoteView = MessagePanelQuoteView(context).apply {
        id = R.id.design_message_panel_quote_view
        layoutParams = LayoutParams(
            MATCH_PARENT,
            context.getDimenPx(RDesign.attr.inlineHeight_s)
        )
        isVisible = false
    }

    /**
     * Кнопка отправки сообщения.
     */
    val sendButton = SbisRoundButton(context).apply {
        id = R.id.design_message_panel_send_button
        icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_BtArrow)
        style = PrimaryButtonStyle.copy(
            defaultRoundButtonStyle = R.style.MessagePanelSendButtonStyle
        )
        size = SbisRoundButtonSize.S
    }

    /**
     * Кнопка прикрепления вложений к сообщению.
     */
    val attachButton = TextView(context).apply {
        applyTextButtonStyle()
        id = R.id.design_message_panel_attach_button_view
        text = SbisMobileIcon.Icon.smi_attach.character.toString()
        setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonsTextSize)
        val horizontalPadding = context.getDimenPx(RDesign.attr.offset_s)
        updatePadding(
            left = horizontalPadding,
            right = horizontalPadding
        )
        layoutParams = LayoutParams(
            WRAP_CONTENT,
            context.getDimenPx(RDesign.attr.inlineHeight_2xs)
        )
    }

    /**
     * Кнопка подписания прикрепленных вложений.
     */
    val signButton = TextView(context).apply {
        applyTextButtonStyle()
        id = R.id.design_message_panel_sign_button
        text = SbisMobileIcon.Icon.smi_medal.character.toString()
        setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonsTextSize)
        updatePadding(
            left = context.getDimenPx(RDesign.attr.offset_m),
            top = context.getDimenPx(RDesign.attr.offset_m),
            right = context.getDimenPx(RDesign.attr.offset_st),
            bottom = iconsPaddingBottom
        )
        isVisible = false
    }

    /**
     * Кнопка записи аудиосообщения.
     */
    val audioRecordButton = RecordButton(context).apply {
        id = R.id.design_message_panel_audio_record_button
        isAudioRecord = true
        isVisible = false
    }

    /**
     * Кнопка записи видеосообщения.
     */
    val videoRecordButton = RecordButton(context).apply {
        id = R.id.design_message_panel_video_record_button
        isAudioRecord = false
        isVisible = false
    }

    /**
     * Кнопка панели быстрых ответов crm.
     */
    private val quickReplyButton = TextView(context).apply {
        id = R.id.design_message_panel_quick_reply_button
        applyTextButtonStyle()
        text = SbisMobileIcon.Icon.smi_menuMessages.character.toString()
        setTextSize(TypedValue.COMPLEX_UNIT_PX, quickReplyButtonSize)
        isVisible = false
        setLeftPadding(quickReplyButtonEndSpacing)
    }

    /**
     * Установить слушателя зажатия кнопки аудиозаписи.
     */
    fun setAudioRecordActionListener(listener: MessagePanelRecordButtonListener?) {
        audioRecordButton.setListener(listener)
    }

    /**
     * Установить слушателя зажатия кнопки видеозаписи.
     */
    fun setVideoRecordActionListener(listener: MessagePanelRecordButtonListener?) {
        videoRecordButton.setListener(listener)
    }

    /**
     * Установить слушателя нажатия кнопки открытия шторки быстрых ответов crm.
     */
    fun setQuickReplyButtonOnClickListener(listener: View.OnClickListener) {
        quickReplyButton.setOnClickListener(listener)
    }

    /**
     * Инициализировать разметку панели ввода.
     */
    fun init() {
        rootView.apply {
            addView(recipientsView)
            addView(quoteView)
            addView(messageContainer)
            addView(sendButton)
            addView(signButton)
            addView(attachButton)
            addView(audioRecordButton)
            addView(videoRecordButton)
            addView(topDivider)
            addView(quickReplyButton)
        }
        configurePreview()
    }

    /**
     * Посчитать размеры дочерних view элементов для размещения в контейнере.
     * @param widthMeasureSpec спека для подсчета ширины [rootView].
     */
    fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = width - with(rootView) { paddingStart + paddingEnd }
        val fullWidthSpec = makeExactlySpec(availableWidth)

        recipientsView.safeMeasure(fullWidthSpec, makeExactlySpec(recipientsView.layoutParams.height))
        quoteView.safeMeasure(fullWidthSpec, makeExactlySpec(quoteView.layoutParams.height))
        attachButton.safeMeasure(makeUnspecifiedSpec(), makeExactlySpec(attachButton.layoutParams.height))
        sendButton.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        signButton.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        audioRecordButton.safeMeasure(
            makeExactlySpec(recordButtonWidth),
            makeExactlySpec(recordButtonHeight)
        )
        videoRecordButton.safeMeasure(
            makeExactlySpec(recordButtonWidth),
            makeExactlySpec(recordButtonHeight)
        )
        quickReplyButton.safeMeasure(
            makeUnspecifiedSpec(),
            makeExactlySpec(messageContainer.safeMeasuredHeight),
        )

        val rightButtonsWidth = maxOf(
            sendButton.safeMeasuredWidth.let { if (it != 0) it + sendButtonHorizontalSpacing * 2 else 0 },
            signButton.safeMeasuredWidth
        )
        val containerAvailableWidth = availableWidth.minus(attachButton.safeMeasuredWidth)
            .minus(messageContainerStartSpacing)
            .minus(rightButtonsWidth)
        messageContainer.measure(
            makeExactlySpec(containerAvailableWidth),
            makeUnspecifiedSpec()
        )

        topDivider.safeMeasure(
            fullWidthSpec,
            ViewGroup.getChildMeasureSpec(
                makeUnspecifiedSpec(),
                0,
                topDivider.layoutParams.height
            )
        )
        topCustomView?.apply {
            safeMeasure(
                fullWidthSpec,
                ViewGroup.getChildMeasureSpec(
                    makeUnspecifiedSpec(),
                    0,
                    layoutParams.height
                )
            )
        }
        bottomCustomView?.apply {
            safeMeasure(
                fullWidthSpec,
                ViewGroup.getChildMeasureSpec(
                    makeUnspecifiedSpec(),
                    0,
                    layoutParams.height
                )
            )
        }
    }

    // TODO(Восстановить ограничение с доработкой новой View панели)
    private fun updateMaxHeight() {
        val availableHeightPercent = if (isLandscape && !isTablet) 1f else 0.7f
        val maxAvailableHeight = (displayHeight - topOffset - bottomOffset) * availableHeightPercent
        val availableInputHeight = getInputViewAvailableHeight(maxAvailableHeight.toInt())
        if (availableInputHeight >= inputView.lineHeight * inputView.minLines) {
            // Минимальный размер EditText вмещается на экран
            if (inputView.maxHeight != availableInputHeight) {
                inputView.maxHeight = availableInputHeight
            }
        } else {
            val heightDiff = (attachmentsView.safeMeasuredHeight - attachmentsView.partialHeight).takeIf { it >= 0 } ?: 0
            when {
                // Проверка на вместимость со сжатыми вложениями
                attachmentsView.safeMeasuredHeight > attachmentsView.partialHeight
                        && availableInputHeight + heightDiff >= inputView.lineHeight * inputView.minLines -> {
                    inputView.maxHeight = availableInputHeight + heightDiff
                    attachmentsView.setVisibility(AttachmentsViewVisibility.PARTIALLY)
                }
                availableInputHeight + attachmentsView.safeMeasuredHeight >= inputView.lineHeight * inputView.minLines -> {
                    inputView.maxHeight = availableInputHeight + attachmentsView.safeMeasuredHeight
                    attachmentsView.setVisibility(AttachmentsViewVisibility.GONE)
                }
                else -> {
                    inputView.maxHeight = availableInputHeight
                }
            }
        }
    }

    private fun getInputViewAvailableHeight(height: Int): Int =
        height - with(rootView) { paddingTop + paddingBottom }
            .plus(recipientsView.safeMeasuredHeight)
            .plus(quoteView.safeMeasuredHeight)
            .plus(attachmentsHeightWithMargins)
            .plus(inputView.marginTop + inputView.marginBottom)
            .plus(messageContainer.paddingTop + messageContainer.paddingBottom)
            .plus(messageContainerVerticalSpacing * 2)
            .plus(topCustomView?.safeMeasuredHeight ?: 0)
            .plus(bottomCustomView?.safeMeasuredHeight ?: 0)

    private val attachmentsHeightWithMargins: Int
        get() = attachmentsView.safeMeasuredHeight
            .takeIf { it != 0 }
            ?.plus(attachmentsView.marginTop + attachmentsView.marginBottom)
            ?: 0

    /**
     * Возвращает предлагаемую минимальную высоту разметки.
     */
    fun getSuggestedMinimumHeight(): Int =
        with(rootView) { paddingTop + paddingBottom }
            .plus(recipientsView.safeMeasuredHeight)
            .plus(quoteView.safeMeasuredHeight)
            .plus(messageContainer.safeMeasuredHeight)
            .plus(messageContainerVerticalSpacing * 2)
            .plus(topCustomView?.safeMeasuredHeight ?: 0)
            .plus(bottomCustomView?.safeMeasuredHeight ?: 0)

    /**
     * Разместить дочерние элементы в разметке.
     */
    fun onLayout() {
        rootView.apply {
            topDivider.safeLayout(paddingStart, paddingTop)
            topCustomView?.safeLayout(paddingStart, paddingTop)
            recipientsView.safeLayout(paddingStart, topCustomView?.bottom ?: paddingTop)
            quoteView.safeLayout(paddingStart, recipientsView.bottom)

            val messageContainerTop = quoteView.bottom + messageContainerVerticalSpacing
            val messageContainerBottom = messageContainerTop + messageContainer.measuredHeight
            sendButton.safeLayout(
                measuredWidth - paddingEnd - sendButtonHorizontalSpacing - sendButton.safeMeasuredWidth,
                messageContainerBottom - sendButton.measuredHeight
            )
            attachButton.safeLayout(
                paddingStart,
                messageContainerBottom - attachButton.safeMeasuredHeight
            )
            signButton.safeLayout(
                measuredWidth - paddingEnd - signButton.safeMeasuredWidth,
                quoteView.bottom
            )
            messageContainer.layout(
                attachButton.right + messageContainerStartSpacing,
                messageContainerTop
            )
            videoRecordButton.safeLayout(
                messageContainer.right - recordButtonEndSpacing - videoRecordButton.safeMeasuredWidth,
                messageContainer.bottom - videoRecordButton.safeMeasuredHeight
            )
            audioRecordButton.safeLayout(
                videoRecordButton.left - audioRecordButton.safeMeasuredWidth,
                messageContainer.bottom - audioRecordButton.safeMeasuredHeight
            )
            quickReplyButton.safeLayout(
                messageContainer.right - quickReplyButtonEndSpacing - quickReplyButton.safeMeasuredWidth,
                messageContainer.top,
            )
            bottomCustomView?.safeLayout(
                paddingStart,
                messageContainer.bottom + messageContainerVerticalSpacing
            )
        }
    }

    /**
     * Добавить view [child] в корневой контейнер панели сообщений на позицию [index] с параметрами [params].
     * Отвечает за механики добавления кастомных дочерних элементов снизу и сверху панели сообщений.
     */
    fun addView(child: View, index: Int, params: ViewGroup.LayoutParams): Int {
        if (params !is MessagePanel.LayoutParams) return index
        val customViewField = if (params.layoutGravity == MessagePanel.LayoutParams.Gravity.TOP) {
            ::topCustomView
        } else {
            ::bottomCustomView
        }
        check(customViewField.get() == null) {
            "MessagePanel поддерживает добавление только двух сторонних View: одна сверха и одна снизу. Для решения такой задачи используйте ViewGroup."
        }
        customViewField.set(child)
        return 0
    }

    private fun RecordButton.setListener(listener: MessagePanelRecordButtonListener?) {
        if (listener != null) {
            setActionListener { isLongPressed ->
                listener.onClick(isLongPressed = isLongPressed)
            }
        } else {
            setActionListener(null)
        }
    }

    private fun TextView.applyTextButtonStyle() {
        typeface = TypefaceManager.getSbisMobileIconTypeface(context)
        gravity = Gravity.CENTER
        isClickable = true
        isFocusable = true
        setTextColor(buttonsColorStateList)
    }

    private fun applyPreviewAppTheme() {
        if (isInEditMode) {
            context.theme.applyStyle(RDesign.style.AppGlobalTheme, true)
        }
    }

    private fun configurePreview() {
        if (isInEditMode) {
            val visibleViews = listOf(recipientsView, quoteView, signButton, attachmentsView)
            visibleViews.forEach { it.isVisible = true }
        }
    }

    fun onAttachedToWindow() {
        displayHeight = DisplayMetrics().apply(rootView.display::getMetrics).heightPixels
    }
}