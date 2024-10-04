package ru.tensor.sbis.design.toolbar.appbar.gradient

import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.Px
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.toolbar.R
import ru.tensor.sbis.design.utils.createGradientDrawable
import kotlin.math.roundToInt

/**
 * Реализация [GradientHelper] для простого градиента, высота которого зависит от числа строк заголовка и от степени
 * раскрытия шапки
 *
 * @author us.bessonov
 */
internal class SimpleGradientHelper(private val gradientView: View) : GradientHelper {

    @Px
    private val oneLineExpandedGradientHeight = getDimensionPx(R.dimen.toolbar_gradient_expanded_height_one_line)

    @Px
    private val twoLinesExpandedGradientHeight = getDimensionPx(R.dimen.toolbar_gradient_expanded_height_two_lines)

    @Px
    private val threeLinesExpandedGradientHeight = getDimensionPx(R.dimen.toolbar_gradient_expanded_height_three_lines)

    @Px
    private val collapsedHeight = getDimensionPx(R.dimen.toolbar_gradient_collapsed_height)

    @Px
    private var expandedHeight = threeLinesExpandedGradientHeight

    private var normalizedOffset = 0f

    override fun updateGradient(color: Int?) {
        gradientView.background = color?.let(::createGradientDrawable)
    }

    override fun onExpandedTitleLineCountChanged(lineCount: Int) {
        val height = when (lineCount) {
            1 -> oneLineExpandedGradientHeight
            2 -> twoLinesExpandedGradientHeight
            else -> threeLinesExpandedGradientHeight
        }
        expandedHeight = height
        update(normalizedOffset)
    }

    override fun update(normalizedOffset: Float) {
        this.normalizedOffset = normalizedOffset

        val targetHeight = collapsedHeight + (normalizedOffset * (expandedHeight - collapsedHeight)).roundToInt()
        if (gradientView.layoutParams.height == targetHeight) return

        gradientView.updateLayoutParams {
            height = targetHeight
        }
        gradientView.alpha = getGradientAlpha(normalizedOffset)
    }

    override fun setFillHeight(fillHeight: Int) {
        // заливка отсутствует
    }

    private fun getDimensionPx(
        @DimenRes
        resId: Int
    ) = gradientView.resources.getDimensionPixelSize(resId)
}
