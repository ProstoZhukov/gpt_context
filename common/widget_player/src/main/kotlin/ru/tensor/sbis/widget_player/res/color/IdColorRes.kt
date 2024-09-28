package ru.tensor.sbis.widget_player.res.color

import android.content.Context
import androidx.core.content.ContextCompat

/**
 * @author am.boldinov
 */
internal data class IdColorRes(
    @androidx.annotation.ColorRes
    private val colorResId: Int
) : CachedColorRes() {

    override fun inflateValue(context: Context) = ContextCompat.getColor(context, colorResId)
}

fun ColorRes.Companion.id(@androidx.annotation.ColorRes colorResId: Int): ColorRes {
    return IdColorRes(colorResId)
}