/**
 * @author us.bessonov
 */
package ru.tensor.sbis.design.toolbar.appbar.gradient

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

/**
 * Создаёт [Drawable] вертикального градиента с переходом от прозрачного цвета к заданному и с указанной высотой,
 * пространство под которым заполняется заданным цветом
 */
internal fun createExtendedGradientDrawable(@ColorInt color: Int, gradientHeight: Int): Drawable =
    ExtendedGradientDrawable(color, gradientHeight)

/**
 * Возвращает прозрачность градиента в графической шапке, в зависимости от степени разворота.
 * В свёрнутом состоянии градиент скрыт, а при развороте более чем наполовину, максимально непрозрачен
 */
@FloatRange(from = 0.0, to = 1.0)
internal fun getGradientAlpha(@FloatRange(from = 0.0, to = 1.0) normalizedOffset: Float): Float {
    return if (normalizedOffset > 0.5f) 1f else normalizedOffset / 0.5f
}