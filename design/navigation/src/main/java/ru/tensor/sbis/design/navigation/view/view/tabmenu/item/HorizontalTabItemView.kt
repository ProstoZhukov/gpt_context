package ru.tensor.sbis.design.navigation.view.view.tabmenu.item

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import kotlin.math.roundToInt

/**
 * Ячейка в горизонтальной ориентации ННП.
 *
 * @author ma.kolpakov
 */
internal class HorizontalTabItemView private constructor(
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
            TabMenuItemSharedPaints(context, attrs, defStyleAttr, defStyleRes, true)
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpecUtils.makeAtMostSpec(
                resources.getDimensionPixelSize(
                    ru.tensor.sbis.design.R.dimen.tab_navigation_menu_horizontal_height
                )
            )
        )
    }

    override fun onDraw(canvas: Canvas) = with(controller) {
        paints.textPaint.color = textColor
        paints.iconPaint.color = iconColor

        // смещение иконки и счётчика в центральный квадрат при растяжении элемента
        val centralRectLeft = (width - paints.width) / 2F

        drawIconWithText(canvas)
        drawCounter(canvas, centralRectLeft)
    }

    private fun TabItemViewController.drawCounter(
        canvas: Canvas,
        dX: Float
    ) {
        canvas.withTranslation(
            dX + paints.getCounterLeftPadding(counterDrawable.intrinsicWidth),
            paints.counterTopMargin,
            counterDrawable::draw
        )
    }

    private fun TabItemViewController.drawIconWithText(
        canvas: Canvas
    ) {
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
        canvas.drawText(textToDraw, (width - textWidth) / 2F, bottom - paints.textBottomMargin, paints.textPaint)
    }
}