package ru.tensor.sbis.widget_player.widget.spoiler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.utils.extentions.getFullMeasuredHeight
import ru.tensor.sbis.design.utils.extentions.getFullMeasuredWidth
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout

/**
 * Виджет спойлера.
 *
 * @author am.boldinov
 *
 * @property isExpanded состояние спойлера, если [expanded] true - развернут, иначе свернут.
 * Значение по умолчанию false.
 *
 */
@SuppressLint("ViewConstructor")
internal class SpoilerView(
    context: Context,
    options: SpoilerOptions
) : ViewGroup(context) {

    private val contentMargin = options.contentMargin.getValuePx(context)

    private val arrowButton = ArrowButton(
        context = context,
        color = options.iconColor.getValue(context),
        size = options.iconSize.getValuePx(context)
    ).apply {
        layoutParams = generateDefaultLayoutParams()
        setPadding(options.iconPadding.getValuePx(context))
    }
    private val contentLayout = object : VerticalBlockLayout(context) {
        override fun onViewAdded(child: View) {
            super.onViewAdded(child)
            if (childCount > 1) {
                child.isVisible = isExpanded
            }
        }

        override fun onViewRemoved(child: View) {
            super.onViewRemoved(child)
            child.isVisible = true // возвращаем все виджеты к изначальному состоянию видимости
        }
    }.apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            marginStart = contentMargin
        }
    }

    private val rect = Rect()

    private var pendingScroll = false

    val childrenContainer: ViewGroup = contentLayout

    var isExpanded = false
        set(value) {
            if (field != value) {
                field = value
                setExpandedInternal(value)
            }
        }

    init {
        addView(arrowButton)
        addView(contentLayout)
        setExpandedInternal(isExpanded)
        arrowButton.setOnClickListener {
            isExpanded = !isExpanded
            if (isExpanded) {
                pendingScroll = true
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildWithMargins(arrowButton, widthMeasureSpec, 0, heightMeasureSpec, 0)
        var desiredWidth = arrowButton.getFullMeasuredWidth()
        measureChildWithMargins(contentLayout, widthMeasureSpec, desiredWidth, heightMeasureSpec, 0)
        desiredWidth += contentLayout.getFullMeasuredWidth() + paddingStart + paddingEnd
        desiredWidth = maxOf(desiredWidth, suggestedMinimumWidth)
        val desiredHeight = maxOf(
            maxOf(
                arrowButton.getFullMeasuredHeight(),
                contentLayout.getFullMeasuredHeight()
            ) + paddingTop + paddingBottom,
            suggestedMinimumHeight
        )
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childTop = paddingTop
        val arrowFullHeight = arrowButton.getFullMeasuredHeight()
        val contentTitleHeight = contentLayout.getFullMeasuredHeight(0)

        // arrow layout
        val arrowLp = arrowButton.layoutParams as LayoutParams
        val arrowLeft = paddingStart + arrowLp.marginStart
        var childRight = arrowLeft + arrowButton.measuredWidth
        val arrowTop = if (contentTitleHeight > arrowFullHeight) {
            childTop + ((contentTitleHeight - arrowFullHeight) / 2f).toInt() // centerY of title
        } else childTop + arrowLp.topMargin
        arrowButton.layout(arrowLeft, arrowTop, childRight, arrowTop + arrowButton.measuredHeight)
        childRight += arrowLp.marginEnd

        // content layout
        val contentLp = contentLayout.layoutParams as LayoutParams
        val contentLeft = childRight + contentLp.marginStart
        val contentTop = childTop + contentLp.topMargin
        contentLayout.layout(
            contentLeft,
            contentTop,
            contentLeft + contentLayout.measuredWidth,
            contentTop + contentLayout.measuredHeight
        )

        if (pendingScroll) {
            rect.set(0, -contentMargin, 0, b - t + contentMargin)
            post { // нужно дождаться layout pass всего RecyclerView для точного подскролла
                requestRectangleOnScreen(rect)
            }
            pendingScroll = false
        }
    }

    override fun checkLayoutParams(lp: ViewGroup.LayoutParams?): Boolean {
        return lp is LayoutParams
    }

    override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        if (lp is MarginLayoutParams) {
            return LayoutParams(lp)
        }
        return LayoutParams(lp)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setExpandedInternal(expanded: Boolean) {
        arrowButton.isExpanded = expanded
        contentLayout.children.forEachIndexed { index, view ->
            if (index > 0) {
                if (expanded) {
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.GONE
                }
            }
        }
    }

    class LayoutParams : MarginLayoutParams {

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: MarginLayoutParams?) : super(source)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
    }


    private class ArrowButton(
        context: Context,
        color: Int,
        size: Int
    ) : View(context) {

        private val drawable = IconicsDrawable(context, SbisMobileIcon.Icon.smi_FolderOpen).apply {
            color(color)
            sizePx(size)
        }

        var isExpanded = false
            set(value) {
                if (field != value) {
                    field = value
                    invalidate()
                }
            }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val size = maxOf(drawable.intrinsicWidth, drawable.intrinsicHeight)
            setMeasuredDimension(
                size + paddingStart + paddingEnd,
                size + paddingTop + paddingBottom
            )
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.save()
            if (!isExpanded) {
                canvas.rotate(-90f, width / 2f, height / 2f)
            }
            canvas.translate(paddingStart.toFloat(), paddingTop.toFloat())
            drawable.draw(canvas)
            canvas.restore()
        }
    }
}