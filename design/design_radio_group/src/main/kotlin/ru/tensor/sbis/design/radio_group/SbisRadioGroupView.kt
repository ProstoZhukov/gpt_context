package ru.tensor.sbis.design.radio_group

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.radio_group.control.api.SbisRadioGroupController
import ru.tensor.sbis.design.radio_group.control.api.SbisRadioGroupViewApi
import ru.tensor.sbis.design.radio_group.control.layout.RadioGroupLayoutView
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Компонент радиокнопки.
 * Предназначен для выбора одного из нескольких взаимоисключающих значений.
 *
 * [Стандарт](https://www.figma.com/proto/huCYY2f1EDFdrQkGU9wyfU/%E2%9C%94%EF%B8%8F-%D0%A0%D0%B0%D0%B4%D0%B8%D0%BE%D0%BA%D0%BD%D0%BE%D0%BF%D0%BA%D0%B8?page-id=1%3A3&type=design&node-id=49-15623&t=PdVuRIltDcUPrZaW-0&scaling=min-zoom&starting-point-node-id=49%3A15623&hide-ui=1)
 *
 * @author ps.smirnyh
 */
class SbisRadioGroupView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: SbisRadioGroupController
) : HorizontalScrollView(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(), attrs),
    SbisRadioGroupViewApi by controller {

    @Suppress("UNUSED")
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.sbisRadioGroupTheme,
        @StyleRes defStyleRes: Int = R.style.SbisRadioGroupDefaultTheme
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        SbisRadioGroupController()
    )

    /** Layout для размещения элементов с поддержкой multiline либо singleline размещения
     * и горизонтальной и вертикальной ориентации.
     */
    internal val radioGroupLayoutView = RadioGroupLayoutView(context, controller.styleHolder).apply {
        id = R.id.radio_group_layout_view_id
    }

    init {
        isHorizontalScrollBarEnabled = false
        addView(radioGroupLayoutView)
        controller.attach(this.context, attrs, defStyleAttr, defStyleRes, this)
    }
}