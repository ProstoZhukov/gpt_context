package ru.tensor.sbis.design_selection.contract.customization.selected

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design_selection.R

/**
 * Контейнер для отображения выбранного элемента с заданным ограничением по ширине.
 *
 * @author vv.chekurda
 */
class SelectedItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var limitedWidth: Int = EMPTY_VALUE

    init {
        getContext().withStyledAttributes(attrs, R.styleable.SelectedItemView) {
            limitedWidth = getDimensionPixelSize(R.styleable.SelectedItemView_SelectedItemView_maxWidth, limitedWidth)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec.takeIf { it == EMPTY_VALUE }
                ?: MeasureSpecUtils.makeAtMostSpec(limitedWidth),
            heightMeasureSpec
        )
    }
}

private const val EMPTY_VALUE = -1