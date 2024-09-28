package ru.tensor.sbis.widget_player.res.dimen

import android.content.Context
import ru.tensor.sbis.widget_player.res.ThemeResProvider

/**
 * @author am.boldinov
 */
internal abstract class CachedDimenRes : DimenRes {

    private val providerPx = ThemeResProvider.cached {
        inflateValuePx(it)
    }
    private val provider = ThemeResProvider.cached {
        inflateValue(it)
    }

    final override fun getValuePx(context: Context) = providerPx(context)

    final override fun getValue(context: Context) = provider(context)

    protected abstract fun inflateValuePx(context: Context): Int

    protected abstract fun inflateValue(context: Context): Float

}