package ru.tensor.sbis.widget_player.res.array

import android.content.Context
import androidx.annotation.ArrayRes
import ru.tensor.sbis.widget_player.res.ThemeResProvider

/**
 * @author am.boldinov
 */
internal data class IdIntArrayRes(
    @ArrayRes
    private val arrayResId: Int
) : IntArrayRes {

    private val provider = ThemeResProvider.cached {
        it.resources.getIntArray(arrayResId)
    }

    override fun getValue(context: Context) = provider(context)
}

fun IntArrayRes.Companion.id(@ArrayRes arrayResId: Int): IntArrayRes {
    return IdIntArrayRes(arrayResId)
}