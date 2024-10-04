package ru.tensor.sbis.marks.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.ParcelableSpan
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxSize
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.utils.hasFlag
import ru.tensor.sbis.marks.model.BOLD
import ru.tensor.sbis.marks.model.ITALIC
import ru.tensor.sbis.marks.model.STRIKETHROUGH
import ru.tensor.sbis.marks.item.api.SbisMarksElementViewApi
import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus
import ru.tensor.sbis.marks.model.UNDERLINE
import ru.tensor.sbis.marks.model.item.SbisMarksColorElement
import ru.tensor.sbis.marks.model.item.SbisMarksElement
import ru.tensor.sbis.marks.model.item.SbisMarksIconElement
import ru.tensor.sbis.marks.style.SbisMarksStyleHolder
import ru.tensor.sbis.marks.utils.createPlus

/**
 * View-компонент одной пометки в списке пометок.
 *
 * @author ra.geraskin
 */

@SuppressLint("ViewConstructor")
internal class SbisMarksElementView(
    context: Context,
    override val item: SbisMarksElement = createPlus(SbisMarksCheckboxStatus.UNCHECKED),
    override val isDefaultTitleStyle: Boolean,
    override val isCheckboxVisible: Boolean,
    override var selectionChangeListener: ((SbisMarksElement) -> Unit)? = null,
    override var elementClickListener: ((SbisMarksElement) -> Unit)? = null,
    private val styleHolder: SbisMarksStyleHolder = SbisMarksStyleHolder.create(context, isCheckboxVisible)
) : ViewGroup(context), SbisMarksElementViewApi {

    private val singleSelectMarkPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = styleHolder.markerColor }

    private val circleIconPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            if (item is SbisMarksColorElement) color = item.color.getColor(context)
        }

    private val checkBox = SbisCheckboxView(context).apply {
        value = item.checkboxValue.value
        isVisible = isCheckboxVisible
        size = SbisCheckboxSize.SMALL
        setOnClickListener {
            item.checkboxValue = SbisMarksCheckboxStatus.getByValue(value)
            selectionChangeListener?.invoke(item)
        }
    }

    private val titleLayout: TextLayout = TextLayout {
        paint.typeface = TypefaceManager.getRobotoRegularFont(context)
        paint.color = getTextColor()
        paint.textSize = styleHolder.titleFontSize.toFloat()
        text = getStyledMarkTitle()
    }

    private val iconLayout: TextLayout = TextLayout {
        if (item is SbisMarksIconElement) {
            paint.textSize = styleHolder.iconFontSize.toFloat()
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            paint.color = styleHolder.iconFontColor
            text = item.icon.character.toString()
        }
    }

    private val clickableView = View(context).apply {
        isVisible = true
        background = ColorDrawable(Color.TRANSPARENT)
        setOnClickListener { checkBox.performClick() }
    }

    private val checkboxSpaceWidth by lazy { checkBox.measuredWidth + styleHolder.checkboxSpaceHorizontalPadding * 2 }

    private val selectionMarkerShape: RectF by lazy {
        RectF(
            0f,
            ((measuredHeight - styleHolder.markerHeight) / 2).toFloat(),
            (styleHolder.markerWidth).toFloat(),
            ((measuredHeight - styleHolder.markerHeight) / 2 + styleHolder.markerHeight).toFloat()
        )
    }

    init {
        setWillNotDraw(false)
        addView(clickableView)
        addView(checkBox)
        setOnClickListener { elementClickListener?.invoke(item) }
    }

    /**
     * Очистка чекбокса от выделения
     */
    override fun clearSelection() {
        checkBox.value = SbisCheckboxValue.UNCHECKED
        item.checkboxValue = SbisMarksCheckboxStatus.UNCHECKED
    }

    /**
     * Получение значения чекбокса
     */
    override fun getSelectionStatus(): SbisMarksCheckboxStatus = SbisMarksCheckboxStatus.getByValue(checkBox.value)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(checkBox, widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val width = when (widthMode) {
            EXACTLY -> {
                var availableWidth = parentWidth
                availableWidth -= if (isCheckboxVisible) {
                    checkboxSpaceWidth
                } else {
                    styleHolder.iconSpaceStartPadding
                }
                availableWidth -= styleHolder.iconSpaceWidth
                availableWidth -= styleHolder.titleSpaceStartPadding
                availableWidth -= styleHolder.titleSpaceEndPadding
                titleLayout.configure { maxWidth = availableWidth }
                parentWidth
            }

            else -> {
                var currentWidth = if (isCheckboxVisible) {
                    checkboxSpaceWidth
                } else {
                    styleHolder.iconSpaceStartPadding
                }
                currentWidth += styleHolder.iconSpaceWidth
                currentWidth += styleHolder.titleSpaceStartPadding
                currentWidth += styleHolder.titleSpaceEndPadding
                titleLayout.configure { maxWidth = parentWidth }
                currentWidth
            }
        }
        minimumHeight = styleHolder.elementHeight
        setMeasuredDimension(width, minimumHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isCheckboxVisible) {
            clickableView.layout(0, 0, checkboxSpaceWidth, styleHolder.elementHeight)
            checkBox.layout(
                (checkboxSpaceWidth - checkBox.measuredWidth) / 2,
                styleHolder.checkboxSpaceVerticalPadding +
                    (styleHolder.checkboxSpaceHeight - checkBox.measuredHeight) / 2
            )
            if (item is SbisMarksIconElement) {
                iconLayout.layout(
                    checkboxSpaceWidth + (styleHolder.iconSpaceWidth - iconLayout.width) / 2,
                    styleHolder.elementVerticalPadding + (styleHolder.iconSpaceHeight - iconLayout.height) / 2
                )
            }
            titleLayout.layout(
                checkboxSpaceWidth + styleHolder.iconSpaceWidth + styleHolder.titleSpaceStartPadding,
                styleHolder.elementVerticalPadding + (styleHolder.titleSpaceHeight - titleLayout.height) / 2
            )
        } else {
            titleLayout.layout(
                styleHolder.iconSpaceStartPadding + styleHolder.iconSpaceWidth + styleHolder.titleSpaceStartPadding,
                styleHolder.elementVerticalPadding + (styleHolder.titleSpaceHeight - titleLayout.height) / 2
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        titleLayout.draw(canvas)

        if (item is SbisMarksColorElement) {
            drawCircleIcon(canvas)
        } else {
            iconLayout.draw(canvas)
        }

        if (!isCheckboxVisible && item.checkboxValue == SbisMarksCheckboxStatus.CHECKED) {
            drawSingleSelectionMarker(canvas)
        }
    }

    private fun drawCircleIcon(canvas: Canvas) {
        val circleCenterY = styleHolder.elementVerticalPadding + styleHolder.iconSpaceHeight / 2
        val circleCenterX = if (isCheckboxVisible) {
            checkboxSpaceWidth + styleHolder.iconSpaceWidth / 2
        } else {
            styleHolder.iconSpaceStartPadding + styleHolder.iconSpaceWidth / 2
        }
        canvas.drawCircle(
            circleCenterX.toFloat(),
            circleCenterY.toFloat(),
            styleHolder.colorCircleSize.toFloat() / 2,
            circleIconPaint
        )
    }

    private fun drawSingleSelectionMarker(canvas: Canvas) = canvas.drawRoundRect(
        selectionMarkerShape,
        (styleHolder.markerBorderRadius).toFloat(),
        (styleHolder.markerBorderRadius).toFloat(),
        singleSelectMarkPaint
    )

    private fun getTextColor(): Int =
        if (isDefaultTitleStyle) {
            styleHolder.titleFontColor
        } else {
            (item as SbisMarksColorElement).color.getColor(context)
        }

    /**
     * Получение стилизованного текста пометки
     */
    private fun getStyledMarkTitle(): CharSequence {
        if (isDefaultTitleStyle) return item.title.getString(context)
        (item as SbisMarksColorElement)
        return SpannableString(item.title.getString(context)).apply {
            if (item.textStyle hasFlag BOLD) setSpan(StyleSpan(Typeface.BOLD))
            if (item.textStyle hasFlag ITALIC) setSpan(StyleSpan(Typeface.ITALIC))
            if (item.textStyle hasFlag UNDERLINE) setSpan(UnderlineSpan())
            if (item.textStyle hasFlag STRIKETHROUGH) setSpan(StrikethroughSpan())
        }
    }

    private fun SpannableString.setSpan(span: ParcelableSpan) {
        this.setSpan(span, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}
