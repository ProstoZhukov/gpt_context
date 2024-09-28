package ru.tensor.sbis.design.chips.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import ru.tensor.sbis.design.chips.models.SbisChipsBackgroundStyle
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * Класс хэлпер для цветов текста.
 *
 * @author ps.smirnyh
 */
internal class SbisChipsItemFontColorHelper(private val styleHolder: SbisChipsItemStyleHolder) {

    private var accentedTitleColorStateList = ColorStateList.valueOf(Color.MAGENTA)
    private var unaccentedTitleColorStateList = ColorStateList.valueOf(Color.MAGENTA)

    private var accentedIconColorStateList = ColorStateList.valueOf(Color.MAGENTA)
    private var unaccentedIconColorStateList = ColorStateList.valueOf(Color.MAGENTA)

    /**
     * Получить цвета заголовка.
     */
    fun getTitleColorStateList(style: SbisChipsBackgroundStyle): ColorStateList =
        if (style is SbisChipsBackgroundStyle.Accented) accentedTitleColorStateList else unaccentedTitleColorStateList

    /**
     * Получить цвета иконки.
     */
    fun getIconColorStateList(style: SbisChipsBackgroundStyle): ColorStateList =
        if (style is SbisChipsBackgroundStyle.Accented) accentedIconColorStateList else unaccentedIconColorStateList

    /**
     * Изменить цвета иконки.
     */
    fun iconChanged(context: Context, iconColor: Int?) {
        accentedIconColorStateList = context.createColorStateList(
            selectedColor = IconColor.CONTRAST.getValue(context),
            defaultColor = iconColor ?: styleHolder.iconColor
        )

        unaccentedIconColorStateList = context.createColorStateList(
            selectedColor = IconColor.DEFAULT.getValue(context),
            defaultColor = iconColor ?: styleHolder.iconColor
        )
    }

    /**
     * Изменить цвета заголовка.
     */
    fun titleChanged(context: Context, titleColor: Int?) {
        accentedTitleColorStateList = context.createColorStateList(
            selectedColor = TextColor.CONTRAST.getValue(context),
            defaultColor = titleColor ?: TextColor.DEFAULT.getValue(context)
        )

        unaccentedTitleColorStateList = context.createColorStateList(
            selectedColor = TextColor.DEFAULT.getValue(context),
            defaultColor = titleColor ?: TextColor.LABEL.getValue(context)
        )
    }

    private fun Context.createColorStateList(selectedColor: Int, defaultColor: Int) =
        ColorStateList(
            TEXT_COLOR_STATES,
            intArrayOf(
                IconColor.CONTRAST.getValue(this),
                IconColor.READ_ONLY.getValue(this),
                selectedColor,
                defaultColor
            )
        )

    private companion object {
        val TEXT_COLOR_STATES = arrayOf(
            intArrayOf(-android.R.attr.state_enabled, android.R.attr.state_selected),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_selected),
            intArrayOf()
        )
    }

}