package ru.tensor.sbis.widget_player.converter.style

import android.content.Context
import android.graphics.Typeface
import ru.tensor.sbis.design.TypefaceManager

/**
 * @author am.boldinov
 */
enum class FontWeight {
    NORMAL,
    BOLD;

    fun getTypeface(context: Context): Typeface? {
        return when (this) {
            NORMAL -> TypefaceManager.getRobotoRegularFont(context)
            BOLD   -> TypefaceManager.getRobotoBoldFont(context)
        }
    }
}