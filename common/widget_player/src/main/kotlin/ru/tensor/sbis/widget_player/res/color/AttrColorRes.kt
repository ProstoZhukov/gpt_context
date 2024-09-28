package ru.tensor.sbis.widget_player.res.color

import android.content.Context
import androidx.annotation.AttrRes
import ru.tensor.sbis.design.theme.ThemeTokensProvider

/**
 * @author am.boldinov
 */
internal data class AttrColorRes(
    @AttrRes
    private val colorAttr: Int
) : ColorRes {

    override fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttr)
}

fun ColorRes.Companion.attr(@AttrRes colorAttr: Int): ColorRes {
    return AttrColorRes(colorAttr)
}