package ru.tensor.sbis.widget_player.res.dimen

import android.content.Context

/**
 * @author am.boldinov
 */
internal data class IdDimenRes(
    @androidx.annotation.DimenRes
    private val dimenResId: Int
) : CachedDimenRes() {

    override fun inflateValuePx(context: Context) = context.resources.getDimensionPixelSize(dimenResId)

    override fun inflateValue(context: Context) = context.resources.getDimension(dimenResId)
}

fun DimenRes.Companion.id(@androidx.annotation.DimenRes dimenResId: Int): DimenRes {
    return IdDimenRes(dimenResId)
}