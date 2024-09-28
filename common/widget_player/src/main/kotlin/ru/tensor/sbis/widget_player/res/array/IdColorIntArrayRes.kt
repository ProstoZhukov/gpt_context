package ru.tensor.sbis.widget_player.res.array

import android.content.Context
import androidx.annotation.ArrayRes
import ru.tensor.sbis.richtext.util.ColorIntArray
import ru.tensor.sbis.widget_player.res.ThemeResProvider

/**
 * @author am.boldinov
 */
internal data class IdColorIntArrayRes(
    @ArrayRes
    private val arrayResId: Int
) : IntArrayRes {

    private val provider = ThemeResProvider.cached {
        ColorIntArray(it, arrayResId).toIntArray()
    }

    override fun getValue(context: Context) = provider(context)
}

fun IntArrayRes.Companion.idColor(@ArrayRes arrayResId: Int): IntArrayRes {
    return IdColorIntArrayRes(arrayResId)
}