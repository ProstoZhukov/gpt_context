package ru.tensor.sbis.design.checkbox.models

import android.content.Context
import androidx.annotation.Dimension
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.models.IconSizeModel

/**
 * Описание размеров чекбокса.
 *
 * @author mb.kruglova
 */
enum class SbisCheckboxSize(override val globalVar: IconSize) : IconSizeModel {
    SMALL(IconSize.X2L),
    LARGE(IconSize.X7L);

    @Dimension
    fun getCheckboxSizeDimen(context: Context) = globalVar.getDimenPx(context)
}