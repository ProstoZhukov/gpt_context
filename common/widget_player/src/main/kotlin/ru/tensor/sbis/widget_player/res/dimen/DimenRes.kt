package ru.tensor.sbis.widget_player.res.dimen

import android.content.Context

/**
 * @author am.boldinov
 */
interface DimenRes {

    fun getValuePx(context: Context): Int

    fun getValue(context: Context): Float

    companion object
}