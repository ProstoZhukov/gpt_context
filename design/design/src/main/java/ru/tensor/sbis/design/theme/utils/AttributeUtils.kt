package ru.tensor.sbis.design.theme.utils

/**
 * Методы для получения данных из атрибутов.
 *
 * Методы скопированы из design_utils/ThemeUtil файла.
 * Только для внутреннего использования.
 */

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.IntegerRes

/**
 * Получить цвет по атрибуту из текущей темы.
 */
@ColorInt
internal fun Context.getThemeColorInt(@AttrRes attrColor: Int) = getResDataForCurrentTheme(attrColor)

/**
 * Получить идентификатор шрифта из атрибута из текущей темы.
 */
@FontRes
internal fun Context.getFontFromTheme(@AttrRes attrFont: Int) = getResIdForCurrentTheme(attrFont)

/**
 * Получить целочисленное значение по атрибуту из текущей темы.
 */
@IntegerRes
internal fun Context.getThemeInteger(@AttrRes attrInt: Int) = getResDataForCurrentTheme(attrInt)

/**
 * Получить размер из темы по идентификатору атрибута [attr].
 *
 * @throws NotFoundException если [attr] не найден в теме.
 */
@JvmOverloads
internal fun Context.getDimen(
    @AttrRes attr: Int,
    out: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Float = if (theme.resolveAttribute(attr, out, resolveRefs))
    out.getDimension(resources.displayMetrics)
else
    throw NotFoundException("Unable to get dimen for attr ${resources.getResourceEntryName(attr)}")

/**
 * Получить размер из темы по идентификатору атрибута [attr] в пикселях.
 *
 * @throws NotFoundException если [attr] не найден в теме.
 */
@JvmOverloads
internal fun Context.getDimenPx(
    @AttrRes attr: Int,
    out: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int = if (theme.resolveAttribute(attr, out, resolveRefs))
    TypedValue.complexToDimensionPixelSize(out.data, resources.displayMetrics)
else
    throw NotFoundException("Unable to get dimen for attr ${resources.getResourceEntryName(attr)}")

/**
 * Получить Drawable Id из темы по идентификатору атрибута [attrDrawable].
 */
@DrawableRes
internal fun Context.getThemeDrawable(@AttrRes attrDrawable: Int) = getResIdForCurrentTheme(attrDrawable)

private fun Context.getResDataForCurrentTheme(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

private fun Context.getResIdForCurrentTheme(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}