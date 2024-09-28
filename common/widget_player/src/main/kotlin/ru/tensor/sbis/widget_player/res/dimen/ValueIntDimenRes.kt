package ru.tensor.sbis.widget_player.res.dimen

import android.content.Context

/**
 * @author am.boldinov
 */
@JvmInline
internal value class ValueIntDimenRes(
    private val value: Int
) : DimenRes {

    override fun getValuePx(context: Context) = value

    override fun getValue(context: Context): Float = value.toFloat()
}

fun DimenRes.Companion.valueInt(value: Int): DimenRes {
    return ValueIntDimenRes(value)
}