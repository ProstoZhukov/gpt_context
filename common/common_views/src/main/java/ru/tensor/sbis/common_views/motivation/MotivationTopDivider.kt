package ru.tensor.sbis.common_views.motivation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import ru.tensor.sbis.common_views.R

/**
 * Разделитель-линия для View элемента с мотивацией
 *
 * @author am.boldinov
 */
class MotivationTopDivider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        attrs?.apply {
            val attrArray =
                context.theme.obtainStyledAttributes(this, R.styleable.CommonViewsMotivationTopDivider, 0, 0)
            if (attrArray.indexCount > 0) {
                val positive = attrArray.getBoolean(R.styleable.CommonViewsMotivationTopDivider_positive, true)
                setPositive(positive)
            }
            attrArray.recycle()
        }
    }

    /**
     * Устаналивает цвет разделителя
     */
    fun setPositive(positive: Boolean) {
        if (positive) {
            setBackgroundResource(R.color.common_views_motivation_top_divider_color_positive)
        } else {
            setBackgroundResource(R.color.common_views_motivation_top_divider_color_negative)
        }
    }
}