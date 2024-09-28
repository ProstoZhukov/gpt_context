package ru.tensor.sbis.design.view.input.text

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewApi
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi
import kotlin.math.max

/**
 * Однострочное поле ввода, базовый класс.
 *
 * @author ps.smirnyh
 */
abstract class SingleLineInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes
    defStyleAttr: Int,
    @StyleRes
    defStyleRes: Int,
    internal open val controller: SingleLineInputViewControllerApi
) : BaseInputView(context, attrs, defStyleAttr, defStyleRes, controller),
    SingleLineInputViewApi by controller {

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes
        defStyleAttr: Int,
        @StyleRes
        defStyleRes: Int
    ) : this(context, attrs, defStyleAttr, defStyleRes, SingleLineInputViewController())

    init {
        inputView.isSingleLine = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = with(controller) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        var width = 0
        var height = 0
        configureMaxWidthTextLayouts(availableWidth)
        height += getHeightMainParts()
        width += getWidthMainParts()
        width += getLinkWidth()
        val remainWidth = (availableWidth - width).coerceAtLeast(0)

        val childWidthMeasureSpec = getDefaultWidthChildMeasureSpec(remainWidth, widthMeasureMode)
        val childHeightMeasureSpec = getDefaultHeightChildMeasureSpec(availableHeight, heightMeasureMode)
        inputView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        height += inputView.measuredHeight
        width += inputView.measuredWidth
        if (isProgressVisible) {
            height = max(height, progressView.bounds.height() + inputView.baseline)
        }
        configureUnderlineBounds(width, height - validationStatusView.height)
        val resultWidth = getResultWidth(width)

        setMeasuredDimension(resultWidth.coerceAtLeast(suggestedMinimumWidth), height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) = with(controller) {
        layoutMainParts()
        iconView.layout(
            inputView.right + clearView.width,
            inputView.bottom - (iconView.height + inputView.paddingBottom)
        )
        linkView.layout(
            measuredWidth - paddingRight - linkView.width,
            baseline - linkView.baseline
        )
    }

    override fun onDraw(canvas: Canvas) = with(controller) {
        drawMainParts(canvas)
        linkView.draw(canvas)
    }

    private fun getLinkWidth() = if (isLinkVisible && controller.linkView.isVisible) {
        controller.linkView.width
    } else {
        0
    }
}