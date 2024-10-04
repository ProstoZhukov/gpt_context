package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.style_holder

import android.content.Context
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.BorderThickness
import ru.tensor.sbis.design.theme.global_variables.SelectedItemColor
import ru.tensor.sbis.design.theme.global_variables.SeparatorColor
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx

/**
 * Ресурсы для фонов.
 *
 * @author mb.kruglova
 */
internal data class BackgroundDrawableStyleHolder internal constructor(
    var cornerColor: Int = -1,
    var borderColor: Int = -1,
    var backgroundColor: Int = -1,
    var borderThickness: Int = 0,
    var cornerRadius: Float = 0F,
    var compactCornerRadius: Float = 0F,
    var borderRadius: Float = 0F
) {

    companion object {
        /**
         * Создать BackgroundDrawableStyleHolder с заполненными полями.
         */
        fun create(context: Context) = BackgroundDrawableStyleHolder().apply {
            cornerColor = SelectedItemColor.DEFAULT.getValue(context)
            borderColor = SeparatorColor.UNACCENTED.getValue(context)
            backgroundColor = BackgroundColor.STACK.getValue(context)
            borderThickness = BorderThickness.S.getDimenPx(context)
            cornerRadius = context.getDimen(R.attr.BackgroundDrawable_cornerRadius)
            compactCornerRadius = context.getDimen(R.attr.BackgroundDrawable_compactCornerRadius)
            borderRadius = context.getDimenPx(R.attr.BackgroundDrawable_borderRadius).toFloat()
        }
    }
}
