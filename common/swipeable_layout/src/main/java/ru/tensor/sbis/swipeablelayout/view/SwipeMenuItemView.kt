package ru.tensor.sbis.swipeablelayout.view

import android.content.Context
import android.graphics.Canvas
import android.text.Layout.Alignment.ALIGN_CENTER
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeable_layout.R.styleable.SwipeMenuItemView_SwipeMenuItemView_iconTextColor
import ru.tensor.sbis.swipeable_layout.R.styleable.SwipeMenuItemView_SwipeMenuItemView_iconTextSize
import ru.tensor.sbis.swipeable_layout.R.styleable.SwipeMenuItemView_SwipeMenuItemView_labelHorizontalMargin
import ru.tensor.sbis.swipeable_layout.R.styleable.SwipeMenuItemView_SwipeMenuItemView_labelTextColor
import ru.tensor.sbis.swipeable_layout.R.styleable.SwipeMenuItemView_SwipeMenuItemView_labelTextSize
import ru.tensor.sbis.swipeable_layout.R.styleable.SwipeMenuItemView_SwipeMenuItemView_labelTopMargin
import ru.tensor.sbis.swipeablelayout.view.edit_mode.showPreview
import ru.tensor.sbis.design.R as RDesign

private const val TITLE_MAX_LINES = 2

/**
 * View элемента свайп-меню для стандартного отображения.
 * Содержит иконку и опциональное название пункта меню.
 *
 * Стандарт: <a href="http://axure.tensor.ru/MobileStandart8/#p=%D1%81%D0%B2%D0%B0%D0%B9%D0%BF_%D0%B8_%D1%81%D0%BC%D0%B0%D1%85%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_2_&g=1">Свайп и смахивание]</a>
 *
 * @author vv.chekurda
 */
class SwipeMenuItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    @Px
    private val expectedTopLayoutHeight =
        resources.getDimensionPixelSize(R.dimen.swipeable_layout_expected_top_layout_height)

    /**
     * Разметка для отображения иконки.
     */
    private val iconLayout = TextLayout {
        isVisibleWhenBlank = false
        alignment = ALIGN_CENTER
    }

    /**
     * Разметка для отображения названия пунка меню.
     */
    private val labelLayout = TextLayout {
        isVisibleWhenBlank = false
        alignment = ALIGN_CENTER
    }

    private val symbolLayout = TextLayout {
        isVisibleWhenBlank = false
        alignment = ALIGN_CENTER
    }

    /**
     * Возвращает фактически используемый [TextLayout], расположенный над подписью - символ, либо иконку.
     */
    private val topLayout: TextLayout
        get() = if (symbolLayout.isVisible) symbolLayout else iconLayout

    private var iconMarginBottom = resources.getDimensionPixelSize(RDesign.dimen.context_menu_icon_margin_bottom)

    /**
     * Установить текст иконки.
     */
    var iconText: CharSequence?
        get() = iconLayout.text
        set(value) {
            val isChanged = iconLayout.configure { text = value ?: EMPTY }
            if (isChanged) safeRequestLayout()
        }

    /**
     * Символ текста, отображаемый в качестве альтернативы иконке.
     */
    var symbol: CharSequence?
        get() = symbolLayout.text
        set(value) {
            val isChanged = symbolLayout.configure { text = value ?: EMPTY }
            if (isChanged) safeRequestLayout()
        }

    /**
     * Установить цвет текста иконки.
     */
    @get:ColorInt
    var iconTextColor: Int
        get() = iconLayout.textPaint.color
        set(value) {
            val isChanged = iconLayout.configure { paint.color = value }
            if (isChanged) invalidate()
        }

    /**
     * Установить текст названия.
     */
    var labelText: CharSequence?
        get() = labelLayout.text
        set(value) {
            val isChanged = labelLayout.configure { text = value ?: EMPTY }
            if (isChanged) safeRequestLayout()
        }

    /**
     * Установить цвет текста названия.
     */
    @get:ColorInt
    var labelTextColor: Int
        get() = labelLayout.textPaint.color
        set(value) {
            val isChanged = labelLayout.configure { paint.color = value }
            if (isChanged) invalidate()
        }

    /**
     * Установить однострочное отображение текста названия.
     * @property isLabelSingleLine true, если текст должен быть однострочным.
     */
    var isLabelSingleLine: Boolean
        get() = labelLayout.maxLines == 1
        set(value) {
            val isChanged = labelLayout.configure { maxLines = if (value) 1 else TITLE_MAX_LINES }
            if (isChanged) safeRequestLayout()
        }

    /**
     * Установить видимость названия.
     * @property isLabelVisible true, если название должно отображаться.
     */
    var isLabelVisible: Boolean = true
        set(value) {
            val isChanged = value != field
            field = value
            if (isChanged) safeRequestLayout()
        }

    /** @SelfDocumented */
    var autotestsText: String? = null

    init {
        setWillNotDraw(false)
        context.withStyledAttributes(attrs, R.styleable.SwipeMenuItemView) {
            val defaultTextColor = ContextCompat.getColor(context, RDesign.color.palette_color_white1)
            iconLayout.configure {
                paint.apply {
                    typeface = TypefaceManager.getSbisMobileIconTypeface(context)
                    textSize = getDimensionPixelSize(
                        SwipeMenuItemView_SwipeMenuItemView_iconTextSize,
                        resources.getDimensionPixelSize(RDesign.dimen.context_menu_icon_text_size)
                    ).toFloat()
                    color = getColor(
                        SwipeMenuItemView_SwipeMenuItemView_iconTextColor, defaultTextColor
                    )
                }
            }

            labelLayout.configure {
                paint.apply {
                    typeface = TypefaceManager.getRobotoRegularFont(context)
                    textSize = getDimensionPixelSize(
                        SwipeMenuItemView_SwipeMenuItemView_labelTextSize,
                        resources.getDimensionPixelSize(R.dimen.item_menu_font_size)
                    ).toFloat()
                    color = getColor(
                        SwipeMenuItemView_SwipeMenuItemView_labelTextColor, defaultTextColor
                    )
                }
                val horizontalPadding = getDimensionPixelSize(
                    SwipeMenuItemView_SwipeMenuItemView_labelHorizontalMargin,
                    resources.getDimensionPixelSize(RDesign.dimen.context_menu_label_horizontal_padding)
                )
                padding = TextLayout.TextLayoutPadding(start = horizontalPadding, end = horizontalPadding)
                maxLines = TITLE_MAX_LINES
            }

            symbolLayout.configure {
                paint.apply {
                    typeface = TypefaceManager.getRobotoRegularFont(context)
                    textSize = resources.getDimension(R.dimen.swipeable_layout_symbol_text_size)
                    color = labelLayout.textPaint.color
                }
            }

            iconMarginBottom = getDimensionPixelSize(
                SwipeMenuItemView_SwipeMenuItemView_labelTopMargin, iconMarginBottom
            )
        }

        if (isInEditMode) showPreview()

        // Для автоматического тестирования определяем id и accessibility text для кастомной вью
        id = R.id.swipeable_layout_swipe_menu_item_view
        accessibilityDelegate = object : AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.text = autotestsText ?: labelLayout.text.toString()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        labelLayout.configure {
            val topLayoutHeightWithMargins = when {
                !topLayout.isVisible -> 0
                topLayout.paddingBottom != 0 || !labelLayout.isVisible -> topLayout.height
                else -> topLayout.height + getTopLayoutPaddingBottom()
            }
            val availableLabelHeight = measuredHeight - topLayoutHeightWithMargins
            val hasPlaceForLabel = availableLabelHeight > labelLayout.textPaint.textHeight
            isVisible = isLabelVisible && hasPlaceForLabel
            layoutWidth = measuredWidth
        }

        topLayout.updatePadding(bottom = if (labelLayout.isVisible) getTopLayoutPaddingBottom() else 0)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val sumHeight = getTopLayoutHeightForAlignment() + labelLayout.height
        val labelLayoutTop = (measuredHeight - sumHeight) / 2 + getTopLayoutHeightForAlignment()
        val topLayoutTop = labelLayoutTop - topLayout.height

        labelLayout.layout(0, labelLayoutTop)
        topLayout.apply {
            layout((measuredWidth - width) / 2, topLayoutTop)
        }
    }

    override fun onDraw(canvas: Canvas) {
        topLayout.draw(canvas)
        labelLayout.draw(canvas)
    }

    override fun hasOverlappingRendering(): Boolean = false

    @Px
    private fun getTopLayoutPaddingBottom() = if (iconLayout.isVisible) iconMarginBottom else 0

    @Px
    private fun getTopLayoutHeightForAlignment() = when {
        labelLayout.isVisible -> expectedTopLayoutHeight + getTopLayoutPaddingBottom()
        symbolLayout.isVisible -> symbolLayout.height
        iconLayout.isVisible -> iconLayout.height
        else -> 0
    }
}