package ru.tensor.sbis.design.navigation.view.view.tabmenu.item

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import kotlin.math.roundToInt

/**
 * Ячейка в горизонтальной ориентации ННП.
 *
 * @author ma.kolpakov
 */
internal class VerticalTabItemView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: TabItemViewController
) : View(
    ThemeContextBuilder(context, attrs, defStyleAttr).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    TabItemViewApi by controller {

    init {
        minimumWidth = controller.paints.width.roundToInt()
        minimumHeight = controller.paints.height.roundToInt()

        controller.attach(this)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.tabNavStyle,
        @StyleRes defStyleRes: Int = R.style.TabNavView,
        paints: TabMenuItemSharedPaints =
            TabMenuItemSharedPaints(context, attrs, defStyleAttr, defStyleRes, false)
    ) : this(context, attrs, defStyleAttr, defStyleRes, TabItemViewController(paints))

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        controller.layout()
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = controller.accessibilityText
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        if (controller.drawableStateChanged()) {
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) = with(controller) {
        paints.textPaint.color = textColor
        paints.iconPaint.color = iconColor

        drawIconWithText(canvas)
        drawCounter(canvas)
    }

    private fun TabItemViewController.drawIconWithText(canvas: Canvas) {
        if (paints.isUsedNavigationIcons && calendarDrawable.dayNumber != null) {
            calendarDrawable.setBounds(0, 0, calendarDrawable.intrinsicWidth, calendarDrawable.intrinsicHeight)
            canvas.withTranslation(
                (width - calendarDrawable.intrinsicWidth.toFloat()) / 2,
                paints.iconTopPadding,
                calendarDrawable::draw
            )
        } else {
            canvas.drawText(
                icon,
                (width - paints.iconPaint.measureText(icon)) / 2,
                paints.iconTopPadding + paints.iconPaint.textSize,
                paints.iconPaint
            )
        }
        canvas.drawText(textToDraw, (width - textWidth) / 2F, height - paints.textBottomMargin, paints.textPaint)
    }

    private fun TabItemViewController.drawCounter(
        canvas: Canvas,
    ) {
        canvas.withTranslation(
            paints.getCounterLeftPadding(counterDrawable.intrinsicWidth),
            paints.counterTopMargin,
            counterDrawable::draw
        )
    }
}