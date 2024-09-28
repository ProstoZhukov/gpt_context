package ru.tensor.sbis.widget_player.res.color

import android.content.Context
import androidx.annotation.ColorInt

/**
 * @author am.boldinov
 */
interface ColorRes {

    @ColorInt
    fun getValue(context: Context): Int

    companion object
}

