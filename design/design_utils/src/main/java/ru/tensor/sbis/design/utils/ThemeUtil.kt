@file:JvmName("ThemeUtil")

package ru.tensor.sbis.design.utils

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.content.res.TypedArray
import android.os.Build
import android.os.LocaleList
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat.ID_NULL
import java.lang.ref.WeakReference
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor

import java.util.*

/**
 * Get color from current theme
 *
 * @param attrColor Color attribute from custom attributes
 * @return Id 'color' res for current theme
 */
@ColorRes
fun Context.getThemeColor(@AttrRes attrColor: Int) = getResIdForCurrentTheme(attrColor)

/**
 * Get dimension from current theme
 *
 * @param attrDimension Dimension attribute from custom attributes
 * @return Id 'dimension' res for current theme
 */
@DimenRes
fun Context.getThemeDimension(@AttrRes attrDimension: Int) = getResIdForCurrentTheme(attrDimension)

/**
 * Get drawable from current theme
 *
 * @param attrDrawable Drawable attribute from custom attributes
 * @return Id 'drawable' res for current theme
 */
@DrawableRes
fun Context.getThemeDrawable(@AttrRes attrDrawable: Int) = getResIdForCurrentTheme(attrDrawable)

/**
 * Get integer from current theme
 *
 * @param attrInt Integer attribute from custom attributes
 * @return Id 'integer' res for current theme
 */
@Suppress("unused")
@IntegerRes
fun Context.getThemeInteger(@AttrRes attrInt: Int) = getResDataForCurrentTheme(attrInt)

fun Context.getThemeBoolean(attrBoolean: Int) = getResBooleanForCurrentTheme(attrBoolean)

/**
 * Получить цвет по аттрибута из текущей темы
 */
@ColorInt
fun Context.getThemeColorInt(@AttrRes attrColor: Int) = getResDataForCurrentTheme(attrColor)

/**
 * Получить идентификатор шрифта из аттрибута из текущей темы
 */
@FontRes
fun Context.getFontFromTheme(@AttrRes attrFont: Int) = getResIdForCurrentTheme(attrFont)

/**
 * Get custom attr from current theme, if him not initialized
 * returned null value
 *
 * @param attr Custom attribute for check
 * @return Id res for current theme or null
 */
@JvmOverloads
fun Context.getDataFromAttrOrNull(@AttrRes attr: Int, resolveRefs: Boolean = true): Int? {
    return TypedValue().takeIf { theme.resolveAttribute(attr, it, resolveRefs) }?.data
}

/**
 * Возвращает размер из темы по идентификатору атрибута [attr]
 *
 * @throws NotFoundException если [attr] не найден в теме
 */
@JvmOverloads
fun Context.getDimen(
    @AttrRes attr: Int,
    out: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Float = if (theme.resolveAttribute(attr, out, resolveRefs))
    out.getDimension(resources.displayMetrics)
else
    throw NotFoundException("Unable to get dimen for attr ${resources.getResourceEntryName(attr)}")

/**
 * Возвращает размер из темы по идентификатору атрибута [attr]
 *
 * @throws NotFoundException если [attr] не найден в теме
 */
@JvmOverloads
fun Context.getDimenPx(
    @AttrRes attr: Int,
    out: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int = if (theme.resolveAttribute(attr, out, resolveRefs))
    TypedValue.complexToDimensionPixelSize(out.data, resources.displayMetrics)
else
    throw NotFoundException("Unable to get dimen for attr ${resources.getResourceEntryName(attr)}")
/**
 * Применяет аттрибуты к текущей теме из другой темы, полученной из переданного аттрибута.
 * [force] по-умолчанию не переопределяет аттрибуты из переданной темы, если они объявлены в текущей
 * Если получить тему из аттрибута не удалось - применяет аттрибуты из темы, переданной по умолчанию
 */
fun Context.mergeAttrsWithCurrentTheme(attr: Int, defaultTheme: Int, force: Boolean = false) {
    val merging = getDataFromAttrOrNull(attr) ?: defaultTheme
    theme.applyStyle(merging, force)
}

/**
 * Ищет дочернюю тему с именем аттрибута [themeNameAttr] внутри текущей темы и внутри темы приложения.
 * В случае нахождения аттрибута с указанным именем - применяет содержимое найденного аттрибута к текущей теме,
 * иначе - применяет к текущей теме резервный стиль [reserveTheme], если он задан.
 * ВАЖНО - переписывает ранее определенные аттрибуты
 */
fun Context.obtainThemeAttrsAndMerge(themeNameAttr: Int, @StyleRes reserveTheme: Int = ID_NULL) {
    val themeToApply = getDataFromAttrOrNull(themeNameAttr)
        ?: run {
            // Ищем аттрибут внутри темы приложения. Если находим - мержим аттрибуты в текущую тему и возвращаем
            // идентификатор ресурса целевой дочерней темы для применения ее аттрибутов к текущей
            val id = getResIdForThemeByThemeId(themeNameAttr, applicationInfo.theme)
            when {
                id != ID_NULL -> {
                    theme.applyStyle(applicationInfo.theme, true)
                    id
                }
                reserveTheme != ID_NULL -> reserveTheme
                else -> return
            }
        }
    theme.applyStyle(themeToApply, true)
}

/**
 * Получить ссылку на цвет, содержащийся в другой теме.
 * Необходимо для случаев, когда в текущей теме нет необходимых атрибутов, и известен идентификатор темы, в которой эти атрибуты есть.
 *
 * @param attrColor Атрибут цвета.
 * @param themeRes Идентификатор темы, в которой содержится атрибут [attrColor]
 */
fun Context.getThemeColorByThemeId(@AttrRes attrColor: Int, @IntegerRes themeRes: Int): Int =
    getResIdForThemeByThemeId(attrColor, themeRes)

/**
 * Получить цвет, содержащийся в другой теме.
 * Необходимо для случаев, когда в текущей теме нет необходимых атрибутов, и известен идентификатор
 * темы, в которой эти атрибуты есть.
 *
 * @param attrColor Атрибут цвета.
 * @param themeRes Идентификатор темы, в которой содержится атрибут [attrColor]
 */
fun Context.getThemeColorIntByThemeId(@AttrRes attrColor: Int, @IntegerRes themeRes: Int): Int {
    val themeForResolve = resources.newTheme()
    themeForResolve.applyStyle(themeRes, true)

    val typedValue = TypedValue()
    themeForResolve.resolveAttribute(attrColor, typedValue, true)
    return typedValue.data
}

/**
 * Получить корректный цвет [BackgroundColor.HEADER] из обрамленного соответствующим стилем контекста.
 */
fun Context.getHeaderColor(isOldToolbarDesign: Boolean = true): Int = if (isOldToolbarDesign) {
    BackgroundColor.HEADER.getValue(ThemeContextBuilder(this, R.attr.globalToolbarStyle).build())
} else StyleColor.UNACCENTED.getAdaptiveBackgroundColor(this)

private fun Context.getResIdForCurrentTheme(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}

private fun Context.getResDataForCurrentTheme(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

private fun Context.getResBooleanForCurrentTheme(attr: Int): Boolean {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data != 0
}

private fun Context.getResIdForThemeByThemeId(@AttrRes attr: Int, @IntegerRes themeRes: Int): Int {
    val themeForResolve = resources.newTheme()
    themeForResolve.applyStyle(themeRes, true)

    val typedValue = TypedValue()
    themeForResolve.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}

/**
 * Получение значения из enum атрибута
 */
fun <ENUM_ATTR : Enum<*>?> TypedArray.loadEnum(
    @StyleableRes valueAttr: Int,
    default: ENUM_ATTR,
    vararg values: ENUM_ATTR
): ENUM_ATTR {
    val valueCode = getInteger(valueAttr, ID_NULL)
    val ordinal = valueCode - 1
    return when {
        valueCode == ID_NULL -> default
        ordinal in values.indices -> values[ordinal]
        else -> error("Unexpected value code $valueCode")
    }
}

/**
 * Утилитные функции для работы с локалью.
 */
object LocaleUtils {

    private var context: WeakReference<Context>? = null

    /** @SelfDocumented */
    fun getDefaultLocale(context: Context?): Locale {
        this.context?.get() ?: context?.let {
            this.context = WeakReference(context.applicationContext)
        }
        return getDefaultLocale
    }

    /** @SelfDocumented */
    val getDefaultLocale: Locale
        get() = context?.get()?.resources?.let {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.configuration.locales[0]
            else it.configuration.locale
        } ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) LocaleList.getDefault()[0]
        else Locale.getDefault()
}
