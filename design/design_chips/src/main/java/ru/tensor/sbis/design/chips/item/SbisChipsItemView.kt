package ru.tensor.sbis.design.chips.item

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.Px
import androidx.core.graphics.withTranslation
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.chips.SbisChipsView
import ru.tensor.sbis.design.chips.models.SbisChipsBackgroundStyle
import ru.tensor.sbis.design.chips.models.SbisChipsCaption
import ru.tensor.sbis.design.chips.models.SbisChipsIcon
import ru.tensor.sbis.design.chips.models.SbisChipsStyle
import ru.tensor.sbis.design.chips.models.SbisChipsViewMode
import ru.tensor.sbis.design.chips.utils.getDefaultIconSize
import ru.tensor.sbis.design.chips.utils.getDefaultTitleSize
import ru.tensor.sbis.design.counters.textcounter.SbisTextCounterDrawable
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.utils.extentions.setHorizontalPadding
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.design.utils.theme.InlineHeightCompatibleView

/**
 * Чипса.
 *
 * Кнопка в которую можно поместить текст, иконку и счетчик [SbisTextCounterDrawable].
 *
 * @author da.zolotarev
 */
class SbisChipsItemView internal constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private val styleHolder: SbisChipsItemStyleHolder
) : View(context, attrs), InlineHeightCompatibleView<InlineHeight> {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : this(context, attrs, SbisChipsItemStyleHolder.create(context, attrs))

    private val counter = SbisTextCounterDrawable(context, attrs)

    private val backgroundHolder: SbisChipsItemBackgroundHolder
    private val fontColorHelper = SbisChipsItemFontColorHelper(styleHolder)

    @Px
    private var occupiedWidth = 0F

    private val titleTextLayout: TextLayout
    private var titleWidth = 0F

    private var iconText = ""
    private val iconTextLayout: TextLayout
    private var iconWidth = 0F

    private var isNeedTextAlignCenterHorizontal = false

    private var counterDx = 0.0f
    private var counterDy = 0.0f

    private var isMeasureSpecUndefined = false

    private val iconSize: Float
        get() = icon?.customSize?.getDimenPx(context)?.toFloat()
            ?: size.getDefaultIconSize().getDimenPx(context).toFloat()

    private val titleSize: Float
        get() = title?.customSize?.getScaleOffDimenPx(context)?.toFloat()
            ?: size.getDefaultTitleSize().getScaleOffDimenPx(context).toFloat()

    private val isNeedElevation: Boolean
        get() = (
            style is SbisChipsBackgroundStyle.Accented && !isSelected ||
                style is SbisChipsBackgroundStyle.Unaccented && isSelected
            ) && isEnabled

    /** Сокращение ширины в пределах экрана. */
    internal var isNeedShrink = false

    /**
     * @see [android.R.attr.maxWidth].
     */
    var maximumWidth: Int = Int.MAX_VALUE
        set(value) {
            if (field == value) return
            field = value
            safeRequestLayout()
        }

    /**
     * Может ли вью обрабатывать нажатое состояние.
     */
    var isCanPressed: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            changeBackground()
        }

    /**
     * Может ли вью обрабатывать выбранное состояние.
     */
    var isCanSelected: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            changeBackground()
        }

    /**
     * Модель иконки.
     */
    var icon: SbisChipsIcon? = null
        set(value) {
            if (value != field) {
                field = value
                iconText = field?.icon?.getString(context) ?: run { "" }
                iconTextLayout.configure {
                    text = iconText
                    paint.textSize = iconSize
                }
                onIconChanged()
                safeRequestLayout()
            }
        }

    /**
     * Может заголовка.
     */
    var title: SbisChipsCaption? = null
        set(value) {
            if (value != field) {
                field = value
                titleTextLayout.configure {
                    text = field?.caption ?: ""
                    paint.textSize = titleSize
                }
                onTitleChanged()
                safeRequestLayout()
            }
        }

    /**
     * @see [SbisTextCounterDrawable.accentedCounter]
     */
    var accentedCounter: Int
        get() = counter.accentedCounter
        set(value) {
            counter.accentedCounter = value
            safeRequestLayout()
        }

    /**
     * @see [SbisTextCounterDrawable.unaccentedCounter]
     */
    var unaccentedCounter: Int
        get() = counter.unaccentedCounter
        set(value) {
            counter.unaccentedCounter = value
            safeRequestLayout()
        }

    /**
     * Высота вью.
     */
    var size: InlineHeight = InlineHeight.X3S
        set(value) {
            if (field == value) return
            field = value
            onSizeChanged()
            safeRequestLayout()
        }

    /**
     * Стиль фона.
     */
    var style: SbisChipsBackgroundStyle = SbisChipsBackgroundStyle.Accented(SbisChipsStyle.DEFAULT)
        set(value) {
            if (field == value) return
            field = value
            titleTextLayout.colorStateList = fontColorHelper.getTitleColorStateList(style = field)
            iconTextLayout.colorStateList = fontColorHelper.getIconColorStateList(style = field)
            backgroundHolder.changeSelectedStyle(field)
            updateElevation()
        }

    /**
     * Режим отображения фона (всегда или только в выбранном состоянии).
     */
    var viewMode: SbisChipsViewMode = SbisChipsViewMode.FILLED
        set(value) {
            if (field == value) return
            field = value
            onChangeViewMode()
            invalidate()
        }

    override val inlineHeight: InlineHeight
        get() = size

    override fun setInlineHeight(height: InlineHeight) {
        size = height
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        titleTextLayout.isEnabled = enabled
        iconTextLayout.isEnabled = enabled
    }

    override fun setSelected(selected: Boolean) {
        if (!isCanSelected) return
        super.setSelected(selected)
        titleTextLayout.isSelected = selected
        iconTextLayout.isSelected = selected
        updateElevation()
        backgroundHolder.changeSelected(isSelected = selected)
    }

    init {
        // Для нужд автоматического тестирования необходимо определить id и Accessibility text для специализированной вью
        accessibilityDelegate = object : AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.text = "${title?.caption ?: ""}|$accentedCounter|$unaccentedCounter"
            }
        }
        isClickable = true
        maximumWidth = styleHolder.maxWidth
        elevation = styleHolder.elevation
        setHorizontalPadding(styleHolder.horizontalPadding)
        backgroundHolder = SbisChipsItemBackgroundHolder(context, styleHolder, style)
        titleTextLayout = TextLayout {
            paint.textSize = titleSize
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
        }.apply {
            colorStateList = fontColorHelper.getTitleColorStateList(style)
        }

        iconTextLayout = TextLayout {
            paint.textSize = iconSize
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
        }.apply {
            colorStateList = fontColorHelper.getIconColorStateList(style)
        }

        changeBackground()
        onSizeChanged()
        onIconChanged()
        onTitleChanged()

        if (isInEditMode) {
            title = SbisChipsCaption("Preview")
            accentedCounter = 10
            unaccentedCounter = 10
        }

        accessibilityDelegate = TextLayoutAutoTestsHelper(this, titleTextLayout, iconTextLayout) {
            "$accentedCounter : $unaccentedCounter"
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setMaxWidthEqualsScreenWidthIfNeeded()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        isMeasureSpecUndefined = widthMeasureSpec == 0 && isNeedShrink
        val actualWidth = when {
            (widthMode == EXACTLY || widthMode == AT_MOST) -> measureContent(width.coerceAtMost(maximumWidth))
            else -> measureContent(maximumWidth)
        }
        isNeedTextAlignCenterHorizontal = actualWidth < minimumWidth
        val widthSpec = MeasureSpec.makeMeasureSpec(maxOf(actualWidth, minimumWidth), EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(minimumHeight, EXACTLY)
        super.onMeasure(widthSpec, heightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var dX = if (isNeedTextAlignCenterHorizontal) {
            var occupiedWidth = (titleWidth + styleHolder.iconStartMargin + iconWidth).toInt()
            if (isCounterVisible()) {
                occupiedWidth += styleHolder.counterStartMargin + counter.intrinsicWidth
            }
            (width - occupiedWidth) / 2
        } else {
            paddingStart
        }

        val titleTopPadding = (height - titleTextLayout.height) / 2
        if (title?.position == HorizontalPosition.LEFT) {
            titleTextLayout.layout(dX, titleTopPadding)
            dX += titleWidth.toInt()
            if (iconWidth != 0F) {
                if (title != null) dX += styleHolder.iconStartMargin
                iconTextLayout.layout(dX, (height - iconTextLayout.height) / 2)
                dX += iconWidth.toInt()
            }
        } else {
            if (iconWidth != 0F) {
                iconTextLayout.layout(dX, (height - iconTextLayout.height) / 2)
                if (title != null) dX += styleHolder.iconStartMargin
                dX += iconWidth.toInt()
            }
            titleTextLayout.layout(dX, titleTopPadding)
            dX += titleWidth.toInt()
        }

        val titleBaseline = titleTextLayout.baseline + titleTopPadding

        if (!isCounterVisible()) return
        // координата верха счетчика по y
        val counterY = (height - counter.intrinsicHeight) / 2.0f
        // абсолютная координата базовой линии счетчика по y
        val counterBaselineAbsoluteCoordinate = counterY + counter.getBaseline()
        // вычисляю разницу в базовых линиях, чтобы отрисовать счетчик со смещением
        val baselineDiffer = titleBaseline - counterBaselineAbsoluteCoordinate

        counterDx = (dX + styleHolder.counterStartMargin).toFloat()
        counterDy = counterY + baselineDiffer
    }

    override fun onDraw(canvas: Canvas) {
        titleTextLayout.draw(canvas)
        iconTextLayout.draw(canvas)
        if (isCounterVisible()) drawCounter(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isMeasureSpecUndefined) {
            maximumWidth = Int.MAX_VALUE
        }
        isMeasureSpecUndefined = false
    }

    private fun drawCounter(canvas: Canvas) {
        canvas.withTranslation(counterDx, counterDy) {
            counter.draw(this)
        }
    }

    private fun setMaxWidthEqualsScreenWidthIfNeeded() = with(context.resources) {
        if (!isMeasureSpecUndefined || maximumWidth != Int.MAX_VALUE) return@with
        findViewParent<SbisChipsView>(this@SbisChipsItemView)?.let { chipsView ->
            val horizontalPaddings = chipsView.paddingStart + chipsView.paddingEnd
            val horizontalMargins = chipsView.marginStart + chipsView.marginEnd
            maximumWidth = displayMetrics.widthPixels - horizontalPaddings - horizontalMargins
        }
    }

    private fun isCounterVisible() = accentedCounter != 0 || unaccentedCounter != 0

    /**
     * Возвращает измерения контента по правилам стандарта
     */
    private fun measureContent(parentWidth: Int): Int {
        // приоритет отдаётся счётчикам и иконке
        occupiedWidth = if (accentedCounter != 0 || unaccentedCounter != 0) {
            counter.intrinsicWidth.toFloat() + styleHolder.counterStartMargin
        } else {
            0F
        }
        iconWidth = iconTextLayout.width.toFloat()
        if (iconWidth != 0F) {
            occupiedWidth += (if (title != null) styleHolder.iconStartMargin else 0) + iconWidth
        }

        // остаток места рапределяем на текст
        titleWidth = titleTextLayout.textPaint.measureText(title?.caption?.toString() ?: "")
        val remainWidth = parentWidth - paddingStart - paddingEnd - occupiedWidth
        return if (titleWidth > remainWidth) {
            titleTextLayout.configure {
                text = TextUtils.ellipsize(
                    title?.caption ?: "",
                    titleTextLayout.textPaint,
                    remainWidth,
                    TextUtils.TruncateAt.END
                )
            }
            titleWidth = remainWidth
            occupiedWidth = (parentWidth - paddingStart - paddingEnd).toFloat()
            // содержимое не поместилось, занимаем максимально доступное пространство
            parentWidth
        } else {
            // место под текст + то, что заняли иконками/счётчиками
            occupiedWidth += titleWidth
            (paddingStart + occupiedWidth + paddingEnd).toInt()
        }
    }

    /**
     * Пересоздать фон.
     */
    private fun changeBackground() {
        background = backgroundHolder.changeBackground(
            isCanSelected = isCanSelected,
            isCanPressed = isCanPressed
        )
        updateElevation()
    }

    private fun updateElevation() {
        elevation = if (isNeedElevation) styleHolder.elevation else 0f
    }

    /**
     * Обработать изменение размера.
     */
    private fun onSizeChanged() {
        minimumHeight = size.getDimenPx(context)
        backgroundHolder.changeSize(minimumHeight)
        iconTextLayout.configure {
            paint.textSize = iconSize
        }
        titleTextLayout.configure {
            paint.textSize = titleSize
        }
    }

    /**
     * Обработать изменение режима отображения фона.
     */
    private fun onChangeViewMode() {
        backgroundHolder.changeViewMode(viewMode, style)
    }

    /**
     * Обработать изменение иконки.
     */
    private fun onIconChanged() {
        fontColorHelper.iconChanged(context, icon?.customColor?.getColor(context))
        iconTextLayout.colorStateList =
            fontColorHelper.getIconColorStateList(style)
    }

    /**
     * Обработать изменение заголовка.
     */
    private fun onTitleChanged() {
        fontColorHelper.titleChanged(context, title?.customColor?.getColor(context))
        titleTextLayout.colorStateList =
            fontColorHelper.getTitleColorStateList(style)
    }

}