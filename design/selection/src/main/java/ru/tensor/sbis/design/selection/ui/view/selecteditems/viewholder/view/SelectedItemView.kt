package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Px
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Контейнер для отображения выбранного элемента с заданным ограничением по ширине
 *
 * @author us.bessonov
 */
internal class SelectedItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    /**
     * Ограничивает ширину [View] заданным значением.
     * В отличие от [setMaxWidth], не приводит к некорректному отображению содержимого
     */
    @Px
    var widthLimit: Int = Int.MAX_VALUE

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // TODO: 29.09.22 https://online.sbis.ru/opendoc.html?guid=7f53db52-85b5-412c-ade4-2314e116033e&client=3
        if (measuredWidth > widthLimit) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(widthLimit, MeasureSpec.EXACTLY), heightMeasureSpec)
        }
    }
}