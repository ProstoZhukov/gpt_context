package ru.tensor.sbis.design.message_panel.common.view.recipients

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isVisible
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams.StyleKey
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView
import ru.tensor.sbis.design.message_panel.common.R
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.PersonName
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Реализация панели получателей.
 * @see RecipientsView
 * @see RecipientsView.RecipientsViewData
 *
 * @author vv.chekurda
 */
class MessagePanelRecipientsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.MessagePanel_recipientsViewStyle,
    @StyleRes defStyleRes: Int = R.style.RecipientsViewDefaultStyle
) : RecipientsView(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build()) {

    /**
     * Разметка кнопки добавления новых получателей.
     */
    private val addButtonLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.MessagePanelRecipientsView_addButtonStyle, styleRes = R.style.RecipientsButton_AddButton)
    ).apply {
        id = R.id.design_message_panel_recipients_add_button
        setOnClickListener { _, _ -> performClick() }
    }

    /**
     * Размтека подсказки.
     */
    private val hintLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.MessagePanelRecipientsView_hintStyle, styleRes = R.style.RecipientsText_Hint)
    ).apply { id = R.id.design_message_panel_recipients_hint }

    /**
     * Разметка получателей.
     */
    private val recipientsLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.MessagePanelRecipientsView_recipientsStyle, styleRes = R.style.RecipientsText_Recipients)
    ).apply { id = R.id.design_message_panel_recipients_names }

    /**
     * Разметка счетчика скрытых получателей, которые не поместились в [recipientsLayout].
     */
    private val moreCounterLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.MessagePanelRecipientsView_moreCounterStyle, styleRes = R.style.RecipientsText_MoreCounter)
    ) { isVisibleWhenBlank = false }
        .apply { id = R.id.design_message_panel_recipients_more_counter }


    /**
     * Кнопка очистки получателей.
     * Необходима в формате view для функционирования автотестов панели сообщений.
     */
    private val clearButton = object : View(context) {

        private val clickableRect = Rect()

        private val layout = TextLayout.createTextLayoutByStyle(
            this.context,
            StyleKey(styleAttr = R.attr.MessagePanelRecipientsView_clearButtonStyle, styleRes = R.style.RecipientsButton_ClearButton)
        )

        private var layoutTopOffset: Int = 0

        val layoutHeight: Int
            get() = layout.height

        init {
            id = R.id.design_message_panel_recipients_clear_button
            minimumHeight = context.getDimenPx(RDesign.attr.inlineHeight_4xs)
            accessibilityDelegate = TextLayoutAutoTestsHelper(this, layout)
            isVisible = false
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(
                measureDirection(widthMeasureSpec) { minimumWidth },
                measureDirection(heightMeasureSpec) { minimumHeight }
            )
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            layout.layout(paddingStart, paddingTop + layoutTopOffset)
            configureClickableArea()
        }

        override fun onDraw(canvas: Canvas) {
            layout.draw(canvas)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean =
            super.onTouchEvent(event).also { if (it) layout.onTouch(this, event) }

        fun setLayoutTopOffset(topOffset: Int) {
            layoutTopOffset = topOffset
            layout.layout(paddingStart, paddingTop + layoutTopOffset)
        }

        override fun getMinimumWidth(): Int =
            maxOf(
                super.getMinimumWidth(),
                paddingStart + layout.width + paddingEnd
            )

        override fun getMinimumHeight(): Int =
            maxOf(
                super.getMinimumHeight(),
                paddingTop + layout.height + paddingBottom
            )

        private fun configureClickableArea() {
            clickableRect.set(
                paddingStart,
                paddingTop,
                paddingStart + layout.width,
                paddingTop + layout.height
            )
            layout.setStaticTouchRect(clickableRect)
        }
    }

    private val layouts = listOf(addButtonLayout, hintLayout, recipientsLayout, moreCounterLayout)
    private val clickableAreaRect = Rect()

    private val formatter = RecipientsFormatter(resources)
    private val touchManager = TextLayoutTouchManager(this, addButtonLayout)

    override var data: RecipientsViewData = RecipientsViewData()
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                clearButton.isVisible = data.hasRecipients
                safeRequestLayout()
            }
        }

    override var allChosenText: String = EMPTY
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) safeRequestLayout()
        }

    override var hintText: String = EMPTY
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) safeRequestLayout()
        }

    override var recipientsClearListener: (() -> Unit)? = null

    init {
        setWillNotDraw(false)
        val attrList = intArrayOf(
            android.R.attr.id,
            R.attr.MessagePanelRecipientsView_allChosenText,
            R.attr.MessagePanelRecipientsView_hintText
        )
        this.context.withStyledAttributes(attrs, attrList) {
            id = getResourceId(attrList.indexOf(android.R.attr.id), NO_ID)
            allChosenText = getString(attrList.indexOf(R.attr.MessagePanelRecipientsView_allChosenText))
                ?: resources.getString(R.string.design_message_panel_recipients_for_all_members)
            hintText = getString(attrList.indexOf(R.attr.MessagePanelRecipientsView_hintText))
                ?: resources.getString(R.string.design_message_panel_recipients_add_new_for_conversations)
        }

        addView(clearButton)
        clearButton.setOnClickListener {
            data = data.copy(recipients = emptyList())
            recipientsClearListener?.invoke()
        }

        accessibilityDelegate = TextLayoutAutoTestsHelper(
            this,
            addButtonLayout,
            hintLayout,
            recipientsLayout,
            moreCounterLayout
        )

        if (isInEditMode) showPreview()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        clearButton.measure(
            makeUnspecifiedSpec(),
            makeExactlySpec(height - paddingTop - paddingBottom)
        )
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            height
        )
    }

    override fun getSuggestedMinimumWidth(): Int {
        val contentWidth = when {
            data.hasRecipients -> {
                recipientsLayout.getDesiredWidth(formatter.getFormattedRecipientsNames(data))
                    .plus(clearButton.minimumWidth)
            }
            data.isHintEnabled -> hintLayout.getDesiredWidth(hintText)
            else -> recipientsLayout.getDesiredWidth(allChosenText)
        } + addButtonLayout.width

        val wrappedWidth = paddingStart + contentWidth + paddingEnd
        return maxOf(super.getSuggestedMinimumWidth(), wrappedWidth)
    }

    override fun getSuggestedMinimumHeight(): Int {
        val layoutsMaxHeight = maxOf(
            addButtonLayout.getDesiredHeight(),
            hintLayout.getDesiredHeight(),
            recipientsLayout.getDesiredHeight(),
            moreCounterLayout.getDesiredHeight(),
            clearButton.minimumHeight
        )
        val wrappedHeight = paddingTop + layoutsMaxHeight + paddingBottom
        return maxOf(super.getSuggestedMinimumHeight(), wrappedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        configureLayouts()

        addButtonLayout.layout(paddingStart, paddingTop)
        hintLayout.layout(addButtonLayout.right, paddingTop)
        recipientsLayout.layout(addButtonLayout.right, paddingTop)
        moreCounterLayout.layout(
            recipientsLayout.right,
            recipientsLayout.baseline - moreCounterLayout.baseline
        )
        val recipientsTextHeight = recipientsLayout.run { height - paddingTop - paddingBottom }.takeIf { it > 0 }
        clearButton.apply {
            layout(moreCounterLayout.right, paddingTop)
            setLayoutTopOffset(
                if (recipientsTextHeight == null) {
                    val addButtonTextTop = addButtonLayout.top + addButtonLayout.paddingTop
                    val addButtonTextHeight = addButtonLayout.run { height - paddingTop - paddingBottom }
                    (addButtonTextTop + (addButtonTextHeight - layoutHeight) / 2f).roundToInt()
                } else {
                    val recipientsTextTop = recipientsLayout.top + recipientsLayout.paddingTop
                    (recipientsTextTop + (recipientsTextHeight - layoutHeight) / 2f).roundToInt()
                }
            )
        }

        configureAddButtonClickableArea()
    }

    private fun configureLayouts() {
        if (data.hasRecipients) {
            configureShowingState()
        } else {
            configureEmptyState()
        }
    }

    private fun configureShowingState() {
        val availableViewWidth = measuredWidth - paddingStart - paddingEnd
        val availableRecipientsWidth = availableViewWidth - addButtonLayout.width - clearButton.measuredWidth
        val (recipientsString, moreCount) =
            formatter.getFormattedNamesAndCount(
                data = data,
                availableWidth = availableRecipientsWidth,
                namesLayout = recipientsLayout,
                counterLayout = moreCounterLayout
            )
        moreCounterLayout.configure { text = formatter.getFormattedCount(moreCount) }
        recipientsLayout.configure {
            text = recipientsString
            isVisible = true
        }
        hintLayout.configure { isVisible = false }
    }

    private fun configureEmptyState() {
        moreCounterLayout.configure { text = EMPTY }
        hintLayout.configure {
            text = if (data.isHintEnabled) hintText else allChosenText
            isVisible = true
        }
        recipientsLayout.configure {
            text = EMPTY
            isVisible = false
        }
    }

    override fun onDraw(canvas: Canvas) {
        layouts.forEach { it.draw(canvas) }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        touchManager.onTouch(this, event) || super.onTouchEvent(event)

    override fun setClickable(clickable: Boolean) {
        super.setClickable(clickable)
        children.forEach { it.isClickable = clickable }
    }

    private fun configureAddButtonClickableArea() {
        addButtonLayout.apply {
            clickableAreaRect.set(left, paddingTop, right, measuredHeight - paddingBottom)
            setStaticTouchRect(clickableAreaRect)
        }
    }

    private fun showPreview() {
        val recipients = listOf(
            ContactVM().apply { name = PersonName("Иван", "Иванов", EMPTY) },
            ContactVM().apply { name = PersonName("Петр", "Петров", EMPTY) },
            ContactVM().apply { name = PersonName("Егор", "Егоров", EMPTY) },
        )
        data = RecipientsViewData(recipients.map(::RecipientPersonItem))
    }
}