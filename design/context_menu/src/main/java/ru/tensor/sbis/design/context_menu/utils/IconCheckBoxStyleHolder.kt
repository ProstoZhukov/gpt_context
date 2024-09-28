package ru.tensor.sbis.design.context_menu.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import ru.tensor.sbis.design.context_menu.view.IconCheckBox
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.MarkerColor
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Класс содержащий ресурсы для [IconCheckBox].
 *
 * @author da.zolotarev
 */
class IconCheckBoxStyleHolder {

    /** Размер иконки. */
    @Dimension
    var iconChecboxSize: Int = 0

    /** Цвет иконки галочки. */
    @ColorInt
    var iconChecboxColor: Int = Color.MAGENTA

    /** Цвет иконки маркера. */
    @ColorInt
    var iconChecboxMarkerColor: Int = Color.MAGENTA

    /** Отступ для иконки маркера. */
    @Dimension
    var iconChecboxMarkerPadding: Int = 0

    /**
     * Прочитать значения стилевых атрибутов и записать в соответствующие поля.
     */
    fun loadStyle(
        context: Context,
    ) = context.apply {
        iconChecboxMarkerPadding = Offset.X3S.getDimenPx(context)
        iconChecboxSize = IconSize.S.getScaleOnDimenPx(context)
        iconChecboxColor = IconColor.DEFAULT.getValue(context)
        iconChecboxMarkerColor = MarkerColor.DEFAULT.getValue(context)
    }
}