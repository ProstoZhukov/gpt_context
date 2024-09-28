package ru.tensor.sbis.design.context_menu.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.context_menu.BaseItem
import ru.tensor.sbis.design.context_menu.MenuAdapter
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Класс содержащий ресурсы для ViewHolder-ов [MenuAdapter].
 *
 * @author da.zolotarev
 */
internal class SbisMenuStyleHolder {

    /** Размер тонкого разделителя. */
    @Dimension
    var dividerSlimSize = 0

    /** Размер толстого разделителя. */
    @Dimension
    var dividerBoldSize = 0

    /** Максимальный размер текста. */
    @Dimension
    var maxTextSize = 0

    /** Отступ при наличии чекбокса. */
    @Dimension
    var offsetWithCheckbox = 0

    /** Отступ при отсутствии чекбокса. */
    @Dimension
    var offsetWithoutCheckbox = 0

    /** Отступ картинки. */
    @Dimension
    var imageOffset = 0

    /** Размер иконки. */
    @Dimension
    var iconSize = 0

    /** Цвет иконки при [BaseItem.destructive] true. */
    @ColorInt
    var destructiveIconColor: Int = Color.MAGENTA

    /** Цвет текста при [BaseItem.destructive] true. */
    @ColorInt
    var destructiveTextColor: Int = Color.MAGENTA

    /** Цвет иконки. */
    @ColorInt
    var iconColor: Int = Color.MAGENTA

    /** Цвет текста. */
    @ColorInt
    var textColor: Int = Color.MAGENTA

    /** Цвет фона элемента. */
    @ColorInt
    var itemBackgroundColor: Int = Color.MAGENTA

    /** Цвет фона элемента при нажатии. */
    @ColorInt
    var itemBackgroundPressedColor: Int = Color.MAGENTA

    /** Вид иконки. */
    var stateOnIcon: CheckboxIcon = CheckboxIcon.CHECK

    /** Максимальная ширина элемента меню. */
    @Dimension
    var maxItemWidth = 0

    /** Минимальная ширина элемента меню. */
    @Dimension
    var minItemWidth = 0

    /** Прочитать значения из стилевых атрибутов и записать в соответствующие поля. */
    fun loadStyle(
        context: Context,
        stateOnIcon: CheckboxIcon,
        @DimenRes maxWidthRes: Int? = null
    ) = context.apply {
        this@SbisMenuStyleHolder.stateOnIcon = stateOnIcon
        dividerSlimSize = getDimenPx(R.attr.borderThickness_s)
        dividerBoldSize = getDimenPx(R.attr.borderThickness_3xl)

        maxItemWidth = (
            maxWidthRes?.let { resources.getDimensionPixelSize(it) }
                ?: getDimenPx(R.attr.popupMaxWidthMenuPopup)
            ).coerceAtMost(
            context.resources.displayMetrics.widthPixels - (Offset.M.getDimenPx(context) * 2)
        )

        if (maxWidthRes != null) minItemWidth = maxItemWidth

        maxTextSize = getDimenPx(R.attr.fontSize_2xl_scaleOn)
        offsetWithCheckbox = getDimenPx(R.attr.offset_m)
        offsetWithoutCheckbox = getDimenPx(R.attr.offset_l)
        imageOffset = getDimenPx(R.attr.offset_s)
        iconSize = getDimenPx(R.attr.iconSize_3xl_scaleOn)

        destructiveIconColor = getThemeColorInt(R.attr.dangerIconColor)
        destructiveTextColor = getThemeColorInt(R.attr.dangerTextColor)

        iconColor = getThemeColorInt(R.attr.iconColor)
        textColor = getThemeColorInt(R.attr.textColor)

        itemBackgroundColor = getThemeColorInt(R.attr.contrastBackgroundColor)
        itemBackgroundPressedColor = getThemeColorInt(R.attr.activeBackgroundColor)
    }

}