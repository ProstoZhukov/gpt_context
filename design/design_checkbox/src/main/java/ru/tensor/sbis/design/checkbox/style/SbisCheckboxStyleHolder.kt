package ru.tensor.sbis.design.checkbox.style

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import ru.tensor.sbis.design.checkbox.R
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.utils.getDimenPx

/**
 * Ресурсы для [SbisCheckboxView].
 *
 * @author mb.kruglova
 */
internal data class SbisCheckboxStyleHolder(

    /** Отступ между чекбоксом и меткой. */
    @Dimension
    var innerSpacing: Int = 0,

    /** Вертикальные отступы. */
    @Dimension
    var verticalOffset: Int = 0,

    /** Размер метки-иконки. */
    @Dimension
    var iconSize: Int = 0,

    /** Размер текста ошибки валидации. */
    @Dimension
    var textValidationSize: Float = 0f,

    /** Цвет текста ошибки валидации. */
    @ColorInt
    var textValidationColor: Int = 0,

    /** Вертикальный отступ между чекбоксом и текстом ошибки валидации. */
    @Dimension
    var textValidationTopMargin: Int = 0,
) {
    companion object {
        /**
         * Создать SbisCheckboxStyleHolder с заполненными полями.
         */
        fun create(context: Context): SbisCheckboxStyleHolder {
            return SbisCheckboxStyleHolder().apply {
                verticalOffset = Offset.X2S.getDimenPx(context)
                iconSize = IconSize.L.getDimenPx(context)
                textValidationSize = FontSize.XS.getScaleOffDimen(context)
                textValidationColor = StyleColor.DANGER.getTextColor(context)

                val style =
                    context.getDataFromAttrOrNull(R.attr.sbisCheckboxDefaultsTheme) ?: R.style.SbisCheckboxDefaultsTheme
                ContextThemeWrapper(context, style).apply {
                    textValidationTopMargin = getDimenPx(R.attr.sbisCheckboxValidationTopMargin)
                    innerSpacing = getDimenPx(R.attr.sbisCheckboxContentStartMargin)
                }
            }
        }
    }
}