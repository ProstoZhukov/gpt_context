package ru.tensor.sbis.common.util

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.annotation.Px
import ru.tensor.sbis.design.R

/**
 * Срока с описанием стиля текста.
 *
 * @property string строка
 * @property isBold является ли текст полужирным
 * @property textSizeRes id ресурса размера текста
 * @property textSizeAttr id атрибута размера текста
 * @property textSize значение размера текста в пикселях
 */
class StyledString(
    val string: String,
    val isBold: Boolean = false,
    /** TODO https://online.sbis.ru/opendoc.html?guid=d1ec9abd-039a-4fa7-9b16-638fcc97ae91&client=3 удалить после вскрытия редизайна */
    @DimenRes
    val textSizeRes: Int = R.dimen.size_title2_scaleOff,
    @Px
    val textSize: Int = DEFAULT_TEXT_SIZE,
    @AttrRes
    val textSizeAttr: Int = R.attr.fontSize_xl_scaleOff
) {

    companion object {
        const val DEFAULT_TEXT_SIZE = 30
    }
}

/**
 * Определяет строку, имеющую наибольшую ширину при отображении.
 *
 * @param texts список строк
 * @return наиболее широкая строка
 */
fun findWidestString(texts: List<String>): String {
    return findWidestString(texts.map { StyledString(it) }).string
}

/**
 * Определяет строку, имеющую наибольшую ширину при отображении, принимая во внимание размер и
 * полужирность текста
 *
 * @param texts список строк с указанием размера и полужирности текста
 * @return значение [StyledString] с наиболее широкой строкой
 */
fun findWidestString(texts: List<StyledString>): StyledString {
    return texts
        .maxByOrNull { it.getApproximateWidth() }
        ?: StyledString("")
}

/**
 * @return предполагаемая ширина строки, с учётом размера и стиля шрифта
 */
fun StyledString.getApproximateWidth(): Int {
    val paint = Paint().apply {
        typeface = Typeface.create(
            Typeface.DEFAULT,
            if (isBold) Typeface.BOLD else Typeface.NORMAL
        )
        textSize = this@getApproximateWidth.textSize.toFloat()
    }
    return Rect().apply {
        paint.getTextBounds(string, 0, string.length, this)
    }.width()
}