package ru.tensor.sbis.design.rating

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import ru.tensor.sbis.design.rating.api.SbisRatingViewApi
import ru.tensor.sbis.design.rating.api.SbisRatingViewController

/**
 * Компонент используется для выставления оценки (например, когда пользователь оставляет отзыв) и для отображения рейтинга.
 *
 * [Стандарт](https://www.figma.com/proto/7RwDqYnCpBvKWuKPlGcI78/%D0%A0%D0%B5%D0%B9%D1%82%D0%B8%D0%BD%D0%B3?page-id=0%3A1&node-id=2809-5620&viewport=386%2C48%2C0.64&scaling=min-zoom&starting-point-node-id=2809%3A5620&hide-ui=1&t=rOYlqNbIRU5SYDsP-8)
 *
 * @author ps.smirnyh
 */
class SbisRatingView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    private val controller: SbisRatingViewController
) : View(context, attrs, defStyleAttr, defStyleRes), SbisRatingViewApi by controller {

    @Suppress("UNUSED")
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.sbisRatingViewTheme,
        defStyleRes: Int = R.style.SbisRatingViewDefaultTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisRatingViewController())

    private var iconOffset = 0

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 0
        var height = 0
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec)
            height = controller.icons.firstOrNull()?.height ?: 0
            val sumWidthIcons = controller.icons.sumOf { it.width }
            iconOffset = (width - sumWidthIcons) / (controller.icons.size - 1)
        } else {
            controller.icons.forEach {
                width += it.width + controller.iconsOffset
                height = it.height
                iconOffset = controller.iconsOffset
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var x = 0
        controller.icons.forEach {
            it.layout(x, 0)
            x += it.width + iconOffset
        }
    }

    override fun onDraw(canvas: Canvas) {
        controller.icons.forEach {
            it.draw(canvas)
        }
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        controller.accessibilityDelegate.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return controller.touchManager?.onTouch(this, event) == true || super.onTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val delegateDispatchEventResult = controller.accessibilityDelegate.dispatchKeyEvent(event)
        val superClassDispatchEventResult = super.dispatchKeyEvent(event)
        return delegateDispatchEventResult || superClassDispatchEventResult
    }

    override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        return controller.accessibilityDelegate.dispatchHoverEvent(event) || super.dispatchHoverEvent(event)
    }
}