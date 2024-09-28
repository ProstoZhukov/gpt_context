package ru.tensor.sbis.widget_player.res.color

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import ru.tensor.sbis.richtext.util.HtmlHelper

/**
 * @author am.boldinov
 */
@JvmInline
internal value class ValueIntColorRes(
    @ColorInt
    private val value: Int
) : ColorRes {

    override fun getValue(context: Context) = value

}

fun ColorRes.Companion.valueInt(@ColorInt value: Int): ColorRes {
    return ValueIntColorRes(value)
}

fun ColorRes.Companion.valueRaw(value: String): ColorRes {
    val color = HtmlHelper.parseColor(value) ?: Color.TRANSPARENT
    return ValueIntColorRes(color)
}