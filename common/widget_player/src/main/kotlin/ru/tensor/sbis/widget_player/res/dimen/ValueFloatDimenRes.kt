package ru.tensor.sbis.widget_player.res.dimen

import android.content.Context

/**
 * @author am.boldinov
 */
@JvmInline
internal value class ValueFloatDimenRes(
    private val value: Float
) : DimenRes {

    override fun getValuePx(context: Context): Int = value.toInt()

    override fun getValue(context: Context): Float = value
}

fun DimenRes.Companion.valueFloat(value: Float): DimenRes {
    return ValueFloatDimenRes(value)
}