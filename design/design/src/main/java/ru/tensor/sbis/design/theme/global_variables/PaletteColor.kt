package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.ColorInt

/**
 * Линейка дополнительных цветов.
 *
 * @author da.zolotarev
 */
interface PaletteColor {

    /**
     * Получить цвет.
     */
    @ColorInt
    fun getValue(context: Context): Int
}