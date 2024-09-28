package ru.tensor.sbis.widget_player.res.color

import android.content.Context
import ru.tensor.sbis.widget_player.res.ThemeResProvider

/**
 * @author am.boldinov
 */
internal abstract class CachedColorRes : ColorRes {

    private val provider = ThemeResProvider.cached {
        inflateValue(it)
    }

    final override fun getValue(context: Context) = provider(context)

    protected abstract fun inflateValue(context: Context): Int
}