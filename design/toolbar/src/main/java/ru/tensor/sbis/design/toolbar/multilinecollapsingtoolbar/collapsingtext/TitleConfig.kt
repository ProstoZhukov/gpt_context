package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.collapsingtext

import android.graphics.Paint
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint

/**
 * Параметры заголовка, отображаемого в графической шапке.
 *
 * @author us.bessonov
 */
internal class TitleConfig(
    var text: CharSequence? = null,
    var textToDraw: CharSequence? = null,
    @JvmField
    var layout: StaticLayout? = null,
    var typeface: Typeface? = null,
    var paint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)
)
