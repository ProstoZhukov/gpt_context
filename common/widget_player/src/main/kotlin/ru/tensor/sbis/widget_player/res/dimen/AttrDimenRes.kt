package ru.tensor.sbis.widget_player.res.dimen

import android.content.Context
import androidx.annotation.AttrRes
import ru.tensor.sbis.design.theme.ThemeTokensProvider

/**
 * @author am.boldinov
 */
internal data class AttrDimenRes(
    @AttrRes
    private val dimenAttr: Int
) : DimenRes {

    override fun getValuePx(context: Context) = ThemeTokensProvider.getDimenPx(context, dimenAttr)

    override fun getValue(context: Context) = ThemeTokensProvider.getDimen(context, dimenAttr)
}

fun DimenRes.Companion.attr(@AttrRes dimenAttr: Int): DimenRes {
    return AttrDimenRes(dimenAttr)
}