@file:Suppress("unused")

package ru.tensor.sbis.hallscheme.v2.business

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import ru.tensor.sbis.hallscheme.R

/**
 * Текстура столов.
 */
internal enum class TableTexture(
    val typeValue: Int,
    @DrawableRes
    val pressedImageResId: Int,
    @DrawableRes
    val unpressedImageResId: Int,
    @ColorRes
    val occupiedColor: Int
) {
    /**@SelfDocumented*/
    TABLE_BRIGHT_WOOD(
        0,
        R.drawable.hall_scheme_pattern_table_bright_wood_pressed,
        R.drawable.hall_scheme_pattern_table_bright_wood,
        R.color.hall_scheme_bright_wood_occupied_color
    ),
    /**@SelfDocumented*/
    TABLE_OAK_WOOD(
        1,
        R.drawable.hall_scheme_pattern_table_oak_wood_pressed,
        R.drawable.hall_scheme_pattern_table_oak_wood,
        R.color.hall_scheme_oak_wood_occupied_color
    ),
    /**@SelfDocumented*/
    TABLE_WHITE_WOOD(
        2,
        R.drawable.hall_scheme_pattern_table_white_wood_pressed,
        R.drawable.hall_scheme_pattern_table_white_wood,
        R.color.hall_scheme_white_wood_occupied_color
    ),
    /**@SelfDocumented*/
    TABLE_FRAXINUS_WOOD(
        3,
        R.drawable.hall_scheme_pattern_table_fraxinus_wood_pressed,
        R.drawable.hall_scheme_pattern_table_fraxinus_wood,
        R.color.hall_scheme_fraxinus_wood_occupied_color
    ),
    /**@SelfDocumented*/
    TABLE_WOOD(
        4,
        R.drawable.hall_scheme_pattern_table_wood_pressed,
        R.drawable.hall_scheme_pattern_table_wood,
        R.color.hall_scheme_wood_occupied_color
    ),
    /**@SelfDocumented*/
    TABLE_DARK_WOOD(
        5,
        R.drawable.hall_scheme_pattern_table_dark_wood_pressed,
        R.drawable.hall_scheme_pattern_table_dark_wood,
        R.color.hall_scheme_dark_wood_occupied_color
    ),
    /**@SelfDocumented*/
    TABLE_FAGUS_WOOD(
        6,
        R.drawable.hall_scheme_pattern_table_fagus_wood_pressed,
        R.drawable.hall_scheme_pattern_table_fagus_wood,
        R.color.hall_scheme_fagus_wood_occupied_color
    ),
    /**@SelfDocumented*/
    TABLE_NUT_WOOD(
        7,
        R.drawable.hall_scheme_pattern_table_nut_wood_pressed,
        R.drawable.hall_scheme_pattern_table_nut_wood,
        R.color.hall_scheme_nut_wood_occupied_color
    ),
    /**@SelfDocumented*/
    TABLE_VENGE_WOOD(
        8,
        R.drawable.hall_scheme_pattern_table_venge_wood_pressed,
        R.drawable.hall_scheme_pattern_table_venge_wood,
        R.color.hall_scheme_venge_wood_occupied_color
    );


    /**
     * Возвращает цвет текста для данной текстуры.
     */
    @ColorRes
    fun getTextColorResId() =
        when (this) {
            in TABLE_BRIGHT_WOOD..TABLE_WOOD -> R.color.hall_scheme_black
            else -> R.color.hall_scheme_white
        }

    companion object {
        /**@SelfDocumented*/
        fun getByType(type: Int): TableTexture =
            values().find { it.typeValue == type } ?: TABLE_WOOD
    }
}