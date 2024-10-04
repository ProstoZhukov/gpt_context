package ru.tensor.sbis.design.navigation.view.view.navmenu

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.children
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.model.content.NavigationItemContent
import ru.tensor.sbis.design.navigation.view.view.navmenu.view.CounterLayout
import ru.tensor.sbis.design.navigation.view.view.navmenu.view.IconButtonLayout
import ru.tensor.sbis.design.navigation.view.view.navmenu.view.ItemBaseLayout
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Представление элемента аккордеона.
 *
 * @author ma.kolpakov
 */
internal class NavMenuItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.navItemTheme,
    @StyleRes defStyleRes: Int = R.style.NavItem,
    private val controller: NavMenuItemViewController = NavMenuItemViewController(ItemTitleRightAlignmentHolder()),
    private val style: NavViewSharedStyle = NavViewSharedStyle(context)
) : ViewGroup(
    ThemeContextBuilder(context, attrs, defStyleAttr).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    NavViewItemViewApi by controller {

    internal val counterLayout = CounterLayout(getContext(), style)
    internal val itemBaseLayout = ItemBaseLayout(getContext(), style).apply {
        calendarDrawable.callback = this@NavMenuItemView
    }
    internal val iconButtonLayout = IconButtonLayout(getContext(), style)
    private val touchManager =
        TextLayoutTouchManager(
            this,
            itemBaseLayout.expandContentIcon,
            iconButtonLayout.iconView,
            itemBaseLayout.titleView
        )

    private val selectorRect = RectF(
        0f,
        style.verticalMarkerMargin,
        style.selectionWidth.toFloat(),
        style.itemHeight.toFloat() - style.verticalMarkerMargin
    )

    init {
        controller.attach(this)
        accessibilityDelegate = TextLayoutAutoTestsHelper(
            this,
            iconButtonLayout.iconView,
            itemBaseLayout.titleView,
            counterLayout.counterLeftView,
            counterLayout.counterRightView
        )
        background = style.createBackground()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        measureChildren(
            MeasureSpecUtils.makeAtMostSpec(width - getTitleOffset()),
            MeasureSpecUtils.makeUnspecifiedSpec()
        )
        var height = style.itemHeight
        if (childCount > 0 && controller.contentExpanded) {
            children.forEach {
                height += it.measuredHeight
            }
            height += style.backgroundMargins
        }
        setMeasuredDimension(
            MeasureSpecUtils.makeExactlySpec(width),
            MeasureSpecUtils.makeExactlySpec(height)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val dX = style.selectionWidth
        val iconButtonWidth = iconButtonLayout.getWidth()
        if (iconButtonWidth > 0) counterLayout.inEdge = false
        iconButtonLayout.layout(0, r - style.backgroundMargins, style.itemHeight)
        counterLayout.layout(
            0,
            measuredWidth - iconButtonWidth - style.backgroundMargins,
            style.itemHeight
        )

        itemBaseLayout
            .layout(
                dX,
                0,
                measuredWidth - iconButtonWidth - counterLayout.getWidth() - style.backgroundMargins,
                style.itemHeight
            )
        if (childCount > 0 && controller.contentExpanded) {
            val contentView = getChildAt(0)
            contentView.layout(
                getTitleOffset(),
                style.itemHeight,
                measuredWidth,
                style.itemHeight + contentView.measuredHeight
            )
        }
        controller.publishRightAlignment(itemBaseLayout.titleView.right)
    }

    override fun onDraw(canvas: Canvas) {
        itemBaseLayout.draw(canvas)
        iconButtonLayout.draw(canvas)
        counterLayout.draw(canvas)
        if (isSelected) {
            canvas.drawRoundRect(
                selectorRect,
                style.selectionWidth / 2f,
                style.selectionWidth / 2f,
                style.selectorPaint
            )
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        itemBaseLayout.iconView.isSelected = selected
        itemBaseLayout.titleView.isSelected = selected
        itemBaseLayout.expandContentIcon.isSelected = selected
        itemBaseLayout.calendarDrawable.setIsSelected(selected)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) = touchManager.onTouch(this, event) || super.onTouchEvent(event)

    /**
     * Показать дополнительный контент.
     */
    fun showContent(content: NavigationItemContent) {
        if (childCount == 0) {
            addView(content.createContentView(LayoutInflater.from(context), this))
        }
        getChildAt(0).visibility = VISIBLE
    }

    override fun verifyDrawable(who: Drawable) = who == itemBaseLayout.calendarDrawable || super.verifyDrawable(who)

    /**
     * Скрыть дополнительный контент.
     */
    fun hideContent() {
        if (childCount > 0) {
            removeAllViews()
        }
    }

    private fun getTitleOffset() = style.iconMarginStart + style.iconSize + style.iconMarginEnd
}
