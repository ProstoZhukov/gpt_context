package ru.tensor.sbis.design.list_header

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.view_ext.R as RViewExt
import ru.tensor.sbis.design.R as RDesign

/**
 * Визуальный компонент для отображения даты/времени в элементах списка.
 * Обеспечивает форматирование дат в ячейках
 *
 * @author ra.petrov
 */
class ItemDateView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val delegate: DateViewDelegate
) : SbisTextView(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr),
    BaseDateView by delegate {

    @ColorInt
    private val defaultTextColor: Int = textColor
    @ColorInt
    private val highlightTextColor: Int

    /**
     * Установка подсветки даты
     */
    var highlighted: Boolean
        get() = textColor == highlightTextColor
        set(isHighlighted) { setTextColor(if (isHighlighted) highlightTextColor else defaultTextColor) }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.listItemDateTheme,
        @StyleRes defStyleRes: Int = RViewExt.style.ItemDateViewDateViewDefaultTheme
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        DateViewDelegate(R.styleable.ItemDateView[R.styleable.ItemDateView_ItemDateView_dateViewMode])
    ) {
        delegate.init(this, attrs, defStyleAttr, defStyleRes)
    }

    init {
        @ColorInt var highlightColor: Int = ContextCompat.getColor(getContext(), RDesign.color.text_color_accent_3)
        getContext().withStyledAttributes(
            attrs,
            R.styleable.ItemDateView,
            R.attr.listItemDateTheme,
            RViewExt.style.ItemDateViewDateViewDefaultTheme
        ) {
            highlightColor = getColor(R.styleable.ItemDateView_ItemDateView_highlightTextColor, highlightColor)
        }
        highlightTextColor = highlightColor
    }
}