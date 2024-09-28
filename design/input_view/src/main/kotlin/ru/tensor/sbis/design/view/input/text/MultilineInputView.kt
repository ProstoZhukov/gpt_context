package ru.tensor.sbis.design.view.input.text

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.text.api.multi_line.MultiLineInputViewApi
import ru.tensor.sbis.design.view.input.text.api.multi_line.MultiLineInputViewController

/**
 * Многострочное поле ввода.
 *
 * @author ps.smirnyh
 */
class MultilineInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    private val controller: MultiLineInputViewController
) : BaseInputView(context, attrs, defStyleAttr, defStyleRes, controller),
    MultiLineInputViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.multilineInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.MultilineInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, MultiLineInputViewController())

    init {
        inputView.isSingleLine = false
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
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
        val remainWidth = (availableWidth - width).coerceAtLeast(0)

        val childWidthMeasureSpec = getDefaultWidthChildMeasureSpec(remainWidth, widthMeasureMode)
        val childHeightMeasureSpec = getDefaultHeightChildMeasureSpec(availableHeight - height, heightMeasureMode)
        inputView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        height += inputView.measuredHeight
        width += inputView.measuredWidth
        configureUnderlineBounds(width, height - validationStatusView.height)
        val resultWidth = getResultWidth(width)

        setMeasuredDimension(resultWidth.coerceAtLeast(suggestedMinimumWidth), height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) = with(controller) {
        layoutMainParts()

        val minLinesCount = if (inputView.minLines > inputView.lineCount) {
            inputView.minLines - inputView.lineCount
        } else {
            0
        }
        val maxLinesCount = if (inputView.lineCount < maxLines) {
            inputView.lineCount
        } else {
            maxLines
        }
        iconView.layout(
            inputView.right + clearView.width,
            inputView.lineHeight * minLinesCount +
                inputView.getLineBounds(maxLinesCount - 1, null) +
                inputView.top - (iconBounds.height() + (iconView.height - iconBounds.height()) / 2)
        )
    }

    override fun onDraw(canvas: Canvas) {
        drawMainParts(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        controller.updateEllipsisCallback.onChange()
    }
}