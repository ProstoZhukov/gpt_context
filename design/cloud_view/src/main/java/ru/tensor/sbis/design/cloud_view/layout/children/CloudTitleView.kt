package ru.tensor.sbis.design.cloud_view.layout.children

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import org.apache.commons.lang3.StringUtils.EMPTY
import org.json.JSONObject
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.CloudViewStylesProvider.paddingStyleProvider
import ru.tensor.sbis.design.cloud_view.CloudViewStylesProvider.textStyleProvider
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.model.PersonModel
import ru.tensor.sbis.design.cloud_view.model.ReceiverInfo
import ru.tensor.sbis.design.cloud_view.utils.showPreview
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.Companion.createTextLayoutByStyle
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.common.util.getCompatColor
import ru.tensor.sbis.design.R as RDesign

/**
 * Заголовок ячейки-облака [CloudView].
 * Содержит разметки для отображения имени автора и получателей сообщения.
 *
 * @author vv.chekurda
 */
class CloudTitleView(
    context: Context,
    @StyleRes styleRes: Int = R.style.IncomeCloudViewTitleStyle
) : View(context) {

    /**
     * Конструктор для отображения preview в студии
     */
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : this(ContextThemeWrapper(context, R.style.DefaultCloudViewTheme_Income), R.style.IncomeCloudViewTitleStyle)

    /**
     * Список дочерних разметок, которые необходимо разместить и отобразить.
     */
    private val children: MutableList<TextLayout> = mutableListOf()

    /**
     * Разметка имени автора сообщения.
     */
    private val authorLayout: TextLayout

    /**
     * Разметка префикса получателей "Для".
     */
    private val receiverPrefixLayout: TextLayout

    /**
     * Разметка имени первого получателя сообщения.
     */
    private val receiverNameLayout: TextLayout

    /**
     * Разметка количества получателей "(+15)".
     */
    private val receiverCounterLayout: TextLayout

    /**
     * Отступ между разметками.
     */
    private var layoutsSpacing: Int = 0

    /**
     * Метка исходящего сообщения.
     */
    private var outcome: Boolean = false

    /**
     * Метка об изменении данных разметки.
     */
    private var isChanged: Boolean = false

    /**
     * Проверка на наличие данных для построения разметки.
     */
    private val hasData: Boolean
        get() = !data.author?.name.isNullOrBlank() || !data.receiverInfo?.receiver?.name.isNullOrBlank()

    /**
     * Стандартный текст для отображения имени автора исходящего сообщения.
     */
    private val iAmAuthorText = resources.getString(RDesign.string.design_cloud_view_me)
    @ColorInt
    private val originAuthorTextColor: Int
    @ColorInt
    private val blockedAuthorTextColor: Int = context.getCompatColor(RDesign.color.palette_color_gray25)

    private var leftPos = 0
    private var topPos = 0
    private var rightPos = 0
    private var bottomPos = 0
    private var textBaseline = 0

    /**
     * Установить данные для отображения заголовка сообщения [CloudTitleView].
     */
    var data: CloudTitleData = CloudTitleData()
        set(value) {
            if (value != field) {
                isChanged = true
                safeRequestLayout()
            }
            field = value
            isVisible = hasData
        }

    /**
     * Признак заблокированности автора сообщения.
     */
    var isAuthorBlocked: Boolean = false
        set(value) {
            if (field != value) {
                updateAuthorTextColor(value)
            }
            field = value
        }

    init {
        setWillNotDraw(false)
        isVisible = false

        @StyleRes var authorStyle = ResourcesCompat.ID_NULL
        @StyleRes var receiverStyle = ResourcesCompat.ID_NULL
        context.withStyledAttributes(attrs = R.styleable.CloudTitleView, defStyleRes = styleRes) {
            outcome = getBoolean(R.styleable.CloudTitleView_CloudTitleView_outcome, outcome)
            authorStyle = getResourceId(
                R.styleable.CloudTitleView_CloudTitleView_authorStyle,
                R.style.IncomeCloudViewAuthorStyle
            )
            receiverStyle = getResourceId(
                R.styleable.CloudTitleView_CloudTitleView_receiverStyle,
                R.style.CloudViewReceiverStyle
            )
        }
        authorLayout = createTextLayoutByStyle(context, authorStyle, textStyleProvider, false)
        originAuthorTextColor = authorLayout.textPaint.color
        receiverPrefixLayout = createTextLayoutByStyle(context, receiverStyle, textStyleProvider, false)
        receiverNameLayout = createTextLayoutByStyle(context, receiverStyle, textStyleProvider, false)
        receiverCounterLayout = createTextLayoutByStyle(context, receiverStyle, textStyleProvider, false) {
            text = EMPTY
        }
        paddingStyleProvider.getStyleParams(context, receiverStyle).run {
            layoutsSpacing = paddingStart
        }
        paddingStyleProvider.getStyleParams(context, styleRes).run {
            updatePadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
        }
        supportAutoTests()
        if (isInEditMode) showPreview()
    }

    /**
     * Обновить цвет имени автора сообщения, исходя из состояния заблокированности.
     */
    private fun updateAuthorTextColor(isBlocked: Boolean) {
        authorLayout.textPaint.color = if (isBlocked) {
            blockedAuthorTextColor
        } else {
            originAuthorTextColor
        }
        invalidate()
    }

    /**
     * Настроить дочерние разметки по заданной ширине [width].
     */
    private fun configureLayout(width: Int) {
        children.clear()
        if (hasData && width > 0) {
            val author = data.author?.name.takeIf { !(outcome && it == null) } ?: iAmAuthorText
            val receiverName = data.receiverInfo?.receiver?.name.orEmpty()
            val receiverCount = formatMoreReceiverCount(data.receiverInfo?.count)
            val hasAuthor = author.isNotBlank()
            val hasReceiver = receiverName.isNotBlank()
            val hasCounter = hasReceiver && receiverCount.isNotBlank()
            val authorSpacing = if (hasReceiver) layoutsSpacing else 0
            val receiverNameSpacing = if (hasCounter) layoutsSpacing else 0

            val (desiredAuthorWidth, desiredAuthorWithSpacing) =
                if (hasAuthor) {
                    val desiredWidth = authorLayout.getDesiredWidth(author)
                    desiredWidth to desiredWidth + authorSpacing
                } else 0 to 0
            val desiredPrefixWidthWithSpacing =
                if (hasReceiver) receiverPrefixLayout.width + layoutsSpacing
                else 0
            val desiredCounterWidth =
                if (hasCounter) receiverCounterLayout.getDesiredWidth(receiverCount)
                else 0
            val (desiredReceiverNameWidth, desiredReceiverNameWidthWithSpacing) =
                if (hasReceiver) {
                    val desiredWidth = receiverNameLayout.getDesiredWidth(receiverName)
                    desiredWidth to desiredWidth + receiverNameSpacing
                } else 0 to 0
            val desiredWidth = (
                desiredAuthorWithSpacing + desiredPrefixWidthWithSpacing +
                    desiredReceiverNameWidthWithSpacing + desiredCounterWidth
                )
            val availableWidth = width - desiredPrefixWidthWithSpacing - desiredCounterWidth
            val halfWidth = availableWidth / 2

            val (authorLayoutWidth, receiverNameLayoutWidth) = when {
                // Если суммарная ширина меньше или ровна допустимой
                desiredWidth <= width -> {
                    desiredAuthorWidth to desiredReceiverNameWidth
                }
                desiredAuthorWithSpacing <= halfWidth -> {
                    desiredAuthorWidth to availableWidth - desiredAuthorWithSpacing - receiverNameSpacing
                }
                desiredReceiverNameWidthWithSpacing <= halfWidth -> {
                    availableWidth - desiredReceiverNameWidthWithSpacing - authorSpacing to desiredReceiverNameWidth
                }
                else -> {
                    halfWidth - authorSpacing to halfWidth - receiverNameSpacing
                }
            }

            if (authorLayoutWidth > 0) {
                addChildWithEllipsize(authorLayout, author, authorLayoutWidth)
            }
            if (receiverNameLayoutWidth > 0) {
                children.add(receiverPrefixLayout)
                addChildWithEllipsize(receiverNameLayout, receiverName, receiverNameLayoutWidth)
                if (hasCounter) {
                    addChildWithEllipsize(receiverCounterLayout, receiverCount, desiredCounterWidth)
                }
            }

            textBaseline =
                if (hasAuthor) authorLayout.baseline
                else receiverNameLayout.baseline
        }
        isChanged = false
    }

    /**
     * Добавить дочерние разметки с модом сокращения текста.
     */
    private fun addChildWithEllipsize(layout: TextLayout, text: CharSequence, width: Int) {
        layout.let {
            it.buildLayout {
                this.text = text
                layoutWidth = width
                ellipsize = TextUtils.TruncateAt.END
            }
            children.add(layout)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = authorLayout.textPaint.textHeight + paddingTop + paddingBottom
        when {
            !hasData -> {
                setMeasuredDimension(0, 0)
            }
            !isChanged && width > 0 && width == measuredWidth && height == measuredHeight -> {
                setMeasuredDimension(measuredWidth, measuredHeight)
            }
            widthMode == MeasureSpec.EXACTLY -> {
                configureLayout(width)
                setMeasuredDimension(width, height)
            }
            widthMode == MeasureSpec.AT_MOST -> {
                configureLayout(width)
                setMeasuredDimension(measureWidth(), height)
            }
            widthMode == MeasureSpec.UNSPECIFIED -> {
                configureLayout(Integer.MAX_VALUE)
                setMeasuredDimension(measureWidth(), height)
            }
        }
    }

    private fun measureWidth(): Int {
        var width = children.sumBy { it.width }
        if (children.isNotEmpty()) {
            width += (children.size - 1) * layoutsSpacing + paddingLeft + paddingRight
        }
        return width
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (measuredWidth == 0 && measuredHeight == 0) return
        leftPos = paddingLeft
        topPos = paddingTop
        rightPos = measuredWidth - paddingRight
        bottomPos = measuredHeight - paddingBottom
        layoutAlignLeft()
    }

    /**
     * Разместить дочернюю разметку с выравниванием по левому краю.
     */
    private fun layoutAlignLeft() {
        var nextChildLeft = leftPos
        children.forEach {
            it.layout(
                nextChildLeft,
                baseline - it.baseline
            )
            nextChildLeft = it.right + layoutsSpacing
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isVisible) return
        children.forEach { it.draw(canvas) }
    }

    private fun formatMoreReceiverCount(totalCount: Int?) =
        totalCount?.takeIf { it > 1 }?.let { "(+${it - 1})" }.orEmpty()

    override fun getBaseline(): Int = paddingTop + textBaseline

    override fun hasOverlappingRendering(): Boolean = false

    /**
     * Модель данных для отображения заголовка в ячейке-облаке [CloudView].
     *
     * @property author автор сообщения.
     * @property receiverInfo информация о получателе.
     */
    data class CloudTitleData(val author: PersonModel? = null, val receiverInfo: ReceiverInfo? = null)

    private fun supportAutoTests() {
        accessibilityDelegate = AutoTestsAccessHelper()
    }

    private inner class AutoTestsAccessHelper : AccessibilityDelegate() {

        private val layouts by lazy {
            mapOf(
                "author" to { authorLayout.text },
                "receiver" to { receiverNameLayout.text },
                "additionalReceiversCount" to { receiverCounterLayout.text }
            )
        }

        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            val json = JSONObject()
            layouts.forEach { (name, getter) ->
                val text = getter()
                if (text.isNotBlank()) {
                    json.put(name, text.toString())
                }
            }
            info?.text = json.toString()
        }
    }
}