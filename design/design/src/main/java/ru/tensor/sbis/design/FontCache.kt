package ru.tensor.sbis.design

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.FontRes
import ru.tensor.sbis.design.TypefaceManager.getFont

/**
 * Класс для ленивой инифицализации файлов-шрифтов.
 *
 * @author du.bykov
 * */
internal class FontCache(@FontRes var res: Int) {

    private var typeface: Typeface? = null

    /** Метод для получения шрифта [Typeface]. */
    fun getTypeface(context: Context): Typeface? {
        return if (typeface != null) {
            typeface
        } else {
            typeface = getFont(context, res)
            return typeface
        }
    }
}