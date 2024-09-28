package ru.tensor.sbis.design.cloud_view.layout.children

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Layout
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.CloudViewStylesProvider.paddingStyleProvider
import ru.tensor.sbis.design.cloud_view.CloudViewStylesProvider.textStyleProvider
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.Companion.createTextLayoutByStyle
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams.StyleKey
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.view_ext.drawable.ClockDrawable
import java.lang.StringBuilder
import kotlin.math.max

/**
 * Статус сообщения ячейки-облака [CloudView].
 * Содержит разметки иконок для отображения статусов [SendingState].
 *
 * @author vv.chekurda
 */
class CloudStatusView(
    context: Context,
    @StyleRes styleRes: Int = R.style.OutcomeCloudViewStatusStyle
) : View(context) {

    /**
     * Конструктор для отображения preview в студии
     */
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : this(ContextThemeWrapper(context, R.style.DefaultCloudViewTheme_Income), R.style.OutcomeCloudViewStatusStyle)

    /**
     * Шрифт иконок.
     */
    private var iconsTypeface: Typeface = TypefaceManager.getSbisMobileIconTypeface(context)

    /**
     * Отступы между иконками.
     */
    private var iconsSpacing: Int = 0

    /**
     * Список дочерних разметок, которые необходимо разместить и отобразить.
     */
    private val children: MutableList<TextLayout> = mutableListOf()

    /**
     * Разметка текста статуса сообщения "Не доставлено".
     */
    private val statusTextLayout: TextLayout

    /**
     * Разметка иконки сообщения в статусе отправки.
     */
    private var sendingLayout: TextLayout? = null

    /**
     * Картинка часов с анимацией стрелок.
     */
    private var clockDrawable: ClockDrawable? = null

    /**
     * Суммарный горизонтальный отступ от картинки часов [clockDrawable].
     */
    private val clockHorizontalPadding = dp(CLOCK_DRAWABLE_HORIZONTAL_PADDING_DP)

    /**
     * Размер картинки часов [clockDrawable].
     */
    private val clockDrawableSize = dp(CLOCK_DRAWABLE_SIZE_DP)

    /**
     * Разметка иконки сообщения в статусе отредактировано.
     */
    private val editedLayout: TextLayout

    /**
     * Разметка иконки сообщения в статусе не доставлено.
     */
    private val notDeliveredLayout: TextLayout

    /**
     * Разметка иконки сообщения в статусе непрочитано.
     */
    private val notReadLayout: TextLayout

    /**
     * Метка об изменении данных разметки.
     */
    private var isChanged: Boolean = false

    /**
     * Признак исходящего сообщения.
     */
    private var outcome: Boolean = false

    private var leftPos = 0
    private var topPos = 0
    private var rightPos = 0
    private var bottomPos = 0
    private var iconBaseline = 0

    /**
     * Установить данные для отображения статуса сообщения [CloudStatusView].
     */
    var data: CloudStatusData = CloudStatusData()
        set(value) {
            if (field != value) {
                isChanged = true
                if (field.sendingState != value.sendingState) {
                    sendingErrorStateListener?.invoke(value.sendingState == SendingState.NEEDS_MANUAL_SEND)
                }
                safeRequestLayout()
            }
            field = value
        }

    /**
     * Слушатель состояния ошибки отправки сообщения.
     */
    var sendingErrorStateListener: SendingErrorStateListener? = null

    init {
        setWillNotDraw(false)
        @StyleRes var sendingIconStyle = ID_NULL
        @StyleRes var editedIconStyle = ID_NULL
        @StyleRes var notReadIconStyle = ID_NULL
        @StyleRes var undeliveredIconStyle = ID_NULL
        @StyleRes var messageStateStyle = ID_NULL
        context.withStyledAttributes(attrs = R.styleable.CloudStatusView, defStyleRes = styleRes) {
            iconsSpacing = getDimensionPixelSize(R.styleable.CloudStatusView_CloudStatusView_iconsHorizontalPadding, 0)
            outcome = getBoolean(R.styleable.CloudStatusView_CloudStatusView_outcome, outcome)
            sendingIconStyle = getResourceId(
                R.styleable.CloudStatusView_CloudStatusView_sendingIconStyle,
                R.style.OutcomeCloudViewSendingIconStyle
            )
            editedIconStyle = getResourceId(
                R.styleable.CloudStatusView_CloudStatusView_editedIconStyle,
                R.style.CloudViewEditedIconStyle
            )
            notReadIconStyle = getResourceId(
                R.styleable.CloudStatusView_CloudStatusView_notReadIconStyle,
                R.style.CloudViewNotReadIconStyle
            )
            undeliveredIconStyle = getResourceId(
                R.styleable.CloudStatusView_CloudStatusView_undeliveredIconStyle,
                R.style.OutcomeCloudViewUndeliveredStyle
            )
            messageStateStyle = getResourceId(
                R.styleable.CloudStatusView_CloudStatusView_messageStateStyle,
                R.style.CloudViewMessageState
            )
        }
        initSendingIcon(sendingIconStyle)
        editedLayout = createIconLayout(editedIconStyle)
        notDeliveredLayout = createIconLayout(undeliveredIconStyle)
        notReadLayout = createIconLayout(notReadIconStyle)
        statusTextLayout = createTextLayoutByStyle(
            context = context,
            styleKey = StyleKey(messageStateStyle, tag = outcome.toString()),
            styleProvider = textStyleProvider,
            obtainPadding = false
        )
        paddingStyleProvider.getStyleParams(context, styleRes).run {
            updatePadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
        }
        if (outcome) {
            minimumWidth = resources.getDimensionPixelSize(R.dimen.cloud_view_outcome_status_min_width)
            minimumHeight = resources.getDimensionPixelSize(R.dimen.cloud_view_outcome_status_min_height)
        }

        if (isInEditMode) {
            data = CloudStatusData(
                sendingState = SendingState.SENT,
                isEdited = true
            )
        }

        accessibilityDelegate = AutoTestsAccessHelper()
    }

    /**
     * Создать резметку иконки по стилю [styleRes].
     */
    private fun createIconLayout(@StyleRes styleRes: Int): TextLayout =
        createTextLayoutByStyle(
            context = context,
            styleKey = StyleKey(styleRes, tag = outcome.toString()),
            styleProvider = textStyleProvider,
            obtainPadding = false
        ) {
            paint.typeface = iconsTypeface
            alignment = Layout.Alignment.ALIGN_CENTER
        }

    /**
     * Проинициализировать иконку статуса отправки сообщения по стилю [styleRes].
     */
    private fun initSendingIcon(@StyleRes styleRes: Int) {
        val params = textStyleProvider.getStyleParams(
            context = context,
            styleKey = StyleKey(styleRes, tag = outcome.toString())
        )
        if (params.text == SbisMobileIcon.Icon.smi_clock.character.toString()) {
            clockDrawable = ClockDrawable(context).apply {
                callback = this@CloudStatusView
                setVisible(false, false)
                size = clockDrawableSize
                params.textColor?.let { color = it }
            }
        } else {
            sendingLayout = createIconLayout(styleRes)
        }
    }

    /**
     * Настроить дочерние разметки.
     */
    private fun configureLayout() {
        children.clear()
        clockDrawable?.setVisible(false, false)
        when (data.sendingState) {
            SendingState.SENDING -> {
                val sendingLayout = sendingLayout
                iconBaseline = if (sendingLayout != null) {
                    children.add(sendingLayout)
                    sendingLayout.baseline
                } else {
                    clockDrawable?.let {
                        it.setVisible(true, true)
                        it.baseline
                    } ?: 0
                }
            }
            SendingState.SENT -> {
                iconBaseline = notReadLayout.baseline
                children.add(notReadLayout)
            }
            SendingState.NEEDS_MANUAL_SEND -> {
                iconBaseline = maxOf(statusTextLayout.baseline, notDeliveredLayout.baseline)
                children.addAll(listOf(notDeliveredLayout, statusTextLayout))
            }
            else -> iconBaseline = minimumHeight
        }

        if (data.isEdited) {
            iconBaseline = max(editedLayout.baseline, iconBaseline)
            children.add(editedLayout)
        }

        isChanged = false
    }

    private fun getClockDrawable() =
        clockDrawable?.takeIf { it.isVisible }

    private fun getClockDrawableAccessibilityText(): String =
        StringBuilder(getClockDrawable()?.let { SbisMobileIcon.Icon.smi_clock.character.toString() }.orEmpty())
            .apply { if (isNotEmpty() && children.isNotEmpty()) append(", ") }
            .toString()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isChanged) configureLayout()
        setMeasuredDimension(measureWidth(), measureHeight())
    }

    private fun measureWidth(): Int {
        var width = children.sumOf { it.width }
        if (children.isNotEmpty()) {
            width += (children.size - 1) * iconsSpacing + paddingLeft + paddingRight
        }
        width += getClockDrawable()?.let {
            it.intrinsicWidth + clockHorizontalPadding + if (children.isNotEmpty()) iconsSpacing else 0
        } ?: 0
        return maxOf(width, minimumWidth)
    }

    private fun measureHeight(): Int =
        maxOf(
            children.maxOfOrNull { it.height } ?: 0,
            getClockDrawable()?.intrinsicHeight ?: 0,
            minimumHeight
        )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (isChanged) {
            configureLayout()
            setMeasuredDimension(measureWidth(), measureHeight())
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (measuredWidth == 0 || measuredHeight == 0) return
        leftPos = paddingLeft
        topPos = paddingTop
        rightPos = measuredWidth - paddingRight
        bottomPos = measuredHeight - paddingBottom
        layoutAlignRight()
    }

    /**
     * Разместить дочернюю разметку с выравниванием по правому краю.
     */
    private fun layoutAlignRight() {
        var nextIconRight = rightPos

        getClockDrawable()?.let {
            val clockTop = baseline - it.baseline
            it.setBounds(
                rightPos - it.intrinsicWidth - clockHorizontalPadding,
                clockTop,
                rightPos,
                clockTop + it.intrinsicHeight
            )
            nextIconRight = it.bounds.left - iconsSpacing
        }

        if (children.isNotEmpty()) {
            children.forEach {
                it.layout(
                    nextIconRight - it.width,
                    baseline - it.baseline
                )
                nextIconRight = it.left - iconsSpacing
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (children.isNotEmpty()) {
            children.forEach { it.draw(canvas) }
        }
        getClockDrawable()?.draw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean =
        super.verifyDrawable(who) || who == clockDrawable

    override fun getBaseline(): Int = paddingTop + iconBaseline

    override fun onSetAlpha(alpha: Int): Boolean =
        true.also {
            children.forEach { it.textPaint.alpha = alpha }
        }

    override fun hasOverlappingRendering(): Boolean = false

    /**
     * Модель данных для отображения статуса сообщения в компоненте ячейка-облачко.
     *
     * @property sendingState статус отправки сообщения.
     * @property isEdited true, если сообщение отредактировано.
     */
    data class CloudStatusData(val sendingState: SendingState? = null, val isEdited: Boolean = false)

    private inner class AutoTestsAccessHelper : AccessibilityDelegate() {

        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info?.text = getClockDrawableAccessibilityText() + children.joinToString { it.text }
        }
    }
}

private const val CLOCK_DRAWABLE_HORIZONTAL_PADDING_DP = 7
private const val CLOCK_DRAWABLE_SIZE_DP = 10

/**
 * Слушатель состояния ошибки отправки сообщения [SendingState.NEEDS_MANUAL_SEND].
 * Передавать true, если произошла ошибка.
 */
internal typealias SendingErrorStateListener = (Boolean) -> Unit