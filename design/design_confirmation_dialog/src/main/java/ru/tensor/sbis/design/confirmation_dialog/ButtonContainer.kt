package ru.tensor.sbis.design.confirmation_dialog

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.delegateNotEqual
import kotlin.math.max

/**
 * Контейнер для кнопок диалога подтверждения, позволяет выстраивать кнопки вертикально, горизонтально
 * и автоматически(горизонтально если не помещаются то вертикально)
 *
 * @author ma.kolpakov
 */
internal class ButtonContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(
    ThemeContextBuilder(context, attrs, defStyleAttr).build(),
    attrs,
    defStyleAttr
) {
    var orientation: ConfirmationButtonOrientation by delegateNotEqual(ConfirmationButtonOrientation.AUTO) { _ ->
        invalidate()
    }

    private val offset: Int = Offset.M.getDimenPx(context)
    private var isHorizontal = true
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        var maxWidth = 0
        var maxHeight = 0
        var fullHeight = 0
        var fullWidth = 0

        children.forEach {
            measureChild(
                it,
                MeasureSpecUtils.makeUnspecifiedSpec(),
                MeasureSpecUtils.makeUnspecifiedSpec()
            )
            fullWidth += it.measuredWidth
            fullHeight += it.measuredHeight
            maxHeight = max(maxHeight, it.measuredHeight)
            maxWidth = max(maxWidth, it.measuredWidth)
        }

        isHorizontal = when (orientation) {
            ConfirmationButtonOrientation.HORIZONTAL -> true
            ConfirmationButtonOrientation.VERTICAL -> false
            ConfirmationButtonOrientation.AUTO -> {
                fullWidth + (childCount - 1) * offset <= availableWidth - paddingEnd - paddingStart
            }
        }

        val width = if (isHorizontal) {
            fullWidth + (childCount - 1) * offset
        } else {
            maxWidth
        }
        val height = if (isHorizontal) {
            maxHeight
        } else {
            fullHeight + (childCount - 1) * offset
        }

        if (!isHorizontal) {
            children.forEach {
                it.minimumWidth = maxWidth
                measureChild(
                    it,
                    MeasureSpecUtils.makeUnspecifiedSpec(),
                    MeasureSpecUtils.makeUnspecifiedSpec()
                )
            }
        }
        super.onMeasure(
            MeasureSpecUtils.makeExactlySpec(width + paddingStart + paddingEnd),
            MeasureSpecUtils.makeExactlySpec(height + paddingTop + paddingBottom)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentVerticalOffset = paddingTop - offset
        var currentHorizontalOffset = paddingStart - offset
        children.forEach {
            if (isHorizontal) {
                it.layout(currentHorizontalOffset + offset, paddingTop)
                currentHorizontalOffset = it.right
            } else {
                it.layout(paddingStart, currentVerticalOffset + offset)
                currentVerticalOffset = it.bottom
            }
        }
    }
}
