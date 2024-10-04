package ru.tensor.sbis.design.utils.extentions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.utils.getThemeColorInt
import timber.log.Timber

/**
 * Класс для безопасного парсинга цвета. В случае ошибки вернет черный цвет
 */
object ColorHelper {

    /**@SelfDocumented*/
    @ColorInt
    fun parse(colorString: String): Int =
        try {
            Color.parseColor(colorString)
        } catch (exception: IllegalArgumentException) {
            Timber.w(exception, "Failed parse color $colorString")
            BLACK
        }
}

/**
 * Расширение для получения списка цветов из ресурсов с помощью ContextCompat
 */
@ColorInt
fun Context.getColorFrom(@ColorRes colorResId: Int) = ContextCompat.getColor(this, colorResId)

/**
 * Расширение для получения цвета из атрибута
 */
@ColorInt
fun Context.getColorFromAttr(@AttrRes attrResId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrResId, typedValue, true)
    return typedValue.data
}

/**
 * Расширение для получения цвета из ресурсов с помощью ContextCompat
 */
@ColorInt
fun View.getColorFromAttr(@AttrRes attrResId: Int) = context.getColorFromAttr(attrResId)

/**
 * Расширение для получения ресурса цвета из атрибута
 */
@ColorRes
fun Context.getColorResFromAttr(@AttrRes attrResId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrResId, typedValue, true)
    return typedValue.resourceId
}

/**
 * Расширение для получения цвета по названию глобальной переменной. Если название не найдено возвращает null
 */
@SuppressLint("DiscouragedApi")
@ColorInt
fun Context.getColorByName(colorName: String): Int? {
    val chars = colorName.toCharArray()
    chars.forEachIndexed { index, char ->
        if ((char == '-' || char == '_') && index + 1 < chars.size)
            chars[index + 1] = chars[index + 1].uppercaseChar()
    }
    val defaultColorName = chars.concatToString().filter { it != '-' && it != '_' }
    val attrId = resources.getIdentifier(defaultColorName, "attr", packageName)
    if (attrId == ResourcesCompat.ID_NULL) return null
    return getThemeColorInt(attrId)
}

/**
 * Расширение для получения ресурса цвета из атрибута
 */
@ColorRes
fun View.getColorResFromAttr(@AttrRes attrResId: Int) = context.getColorResFromAttr(attrResId)

/**
 * Расширение для получения списка цветов из ресурсов с помощью ContextCompat
 */
@Suppress("unused")
fun Context.getColorStateListFrom(@ColorRes colorResId: Int) = ContextCompat.getColorStateList(this, colorResId)

/**
 * Расширение для получения цвета из ресурсов с помощью ContextCompat
 */
@ColorInt
fun View.getColorFrom(@ColorRes colorResId: Int) = context.getColorFrom(colorResId)

/**
 * Расширение для получения цвета из ресурсов с помощью ContextCompat
 */
fun View.getColorStateListFrom(@ColorRes colorResId: Int) = context.getColorStateListFrom(colorResId)

/**
 * Расширение для получения цвета из атриюутов
 */
fun View.getColorStateListFromAttr(@AttrRes attrResId: Int): ColorStateList? {
    val typedArray = context.obtainStyledAttributes(null, intArrayOf(attrResId))
    try {
        return typedArray.getColorStateList(0)
    } finally {
        typedArray.recycle()
    }
}

@ColorInt
fun String?.parseColor(): Int? =
    if (this == null) null
    else try {
        when {
            contains("rgb", true) ->
                substring(indexOf("(") + 1, indexOf(")"))
                    .split(",")
                    .map { it.trim().toInt() }.run { Color.argb(255, get(0), get(1), get(2)) }

            contains("#") && length == 9 -> {
                val colorWithoutHash = substring(1)
                val alpha = colorWithoutHash.substring(6, 8)
                val rgb = colorWithoutHash.substring(0, 6)
                Color.parseColor("#$alpha$rgb")
            }

            contains("#") -> Color.parseColor(this)
            else -> null
        }
    } catch (exception: Exception) {
        Timber.w(exception, "Failed parse color $this")
        null
    }

/**
 * Расширение для получения Drawable из цвета или градиента
 */
fun String?.toDrawable(): Drawable? {
    if (this.isNullOrEmpty()) return null

    return try {
        when {
            contains("linear-gradient", true) -> parseLinearGradient()
            contains("radial-gradient", true) -> parseRadialGradient()
            startsWith("#") -> {
                val color = this.parseColor() ?: Color.TRANSPARENT
                ColorDrawable(color)
            }

            else -> null
        }
    } catch (exception: Exception) {
        Timber.w(exception, "Failed to parse color or gradient: $this")
        null
    }
}

/**
 * Обработка линейного градиента
 */
private fun String.parseLinearGradient(): GradientDrawable? {
    val gradientInfo = substring(indexOf("(") + 1, indexOf(")"))
        .split(",").map { it.trim() }

    val angle = gradientInfo[0].substringBefore("deg").toFloat()
    val colors = gradientInfo.drop(1).map {
        it.split(" ")[0].parseColor() ?: Color.TRANSPARENT
    }.toIntArray()

    // Преобразуем угол из веба в Android
    val androidAngle = (360f - angle + 90f) % 360f

    val orientation = when (androidAngle) {
        in 0f..45f -> GradientDrawable.Orientation.LEFT_RIGHT
        in 46f..90f -> GradientDrawable.Orientation.BOTTOM_TOP
        in 91f..135f -> GradientDrawable.Orientation.BL_TR
        in 136f..180f -> GradientDrawable.Orientation.BR_TL
        in 181f..225f -> GradientDrawable.Orientation.TR_BL
        in 226f..270f -> GradientDrawable.Orientation.RIGHT_LEFT
        in 271f..315f -> GradientDrawable.Orientation.TOP_BOTTOM
        in 316f..360f -> GradientDrawable.Orientation.TL_BR
        else -> GradientDrawable.Orientation.LEFT_RIGHT
    }

    return GradientDrawable(orientation, colors)
}

/**
 * Обработка радиального градиента
 */
private fun String.parseRadialGradient(): GradientDrawable? {
    val gradientInfo = substring(indexOf("(") + 1, indexOf(")"))
        .split(",").map { it.trim() }

    val positionPart = gradientInfo[0].substringAfter("circle at", "").trim().ifEmpty { "50% 50%" }
    val (centerX, centerY) = positionPart.split(" ").map { it.replace("%", "").toFloat() / 100 }

    val colors = gradientInfo.drop(1).map {
        val parts = it.split(" ")
        parts[0].parseColor() ?: Color.TRANSPARENT
    }.toIntArray()

    //Значение радиуса градиента подобрано эмпирически, чтобы на карточке визитки в Android градиент был виден.
    val radiusPercentage = 0.6f

    val gradientDrawable = GradientDrawable().apply {
        gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientRadius = radiusPercentage * 1000
        setColors(colors)
        setGradientCenter(centerX, centerY)
    }
    return gradientDrawable
}