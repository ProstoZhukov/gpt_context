package ru.tensor.sbis.design.radio_group.control.layout

import android.view.ViewGroup
import ru.tensor.sbis.design.radio_group.control.Size

/**
 * Стратегия измерения и размещения радиокнопок внутри группы.
 *
 * @author ps.smirnyh
 */
internal interface RadioGroupLayoutStrategy {

    /** Измерить элементы в соответствии с переданными размерами. */
    fun measure(viewGroup: ViewGroup, widthMeasureSpec: Int, heightMeasureSpec: Int): Size

    /** Расположить элементы внутри [viewGroup]. */
    fun layout(viewGroup: ViewGroup, left: Int, top: Int)
}