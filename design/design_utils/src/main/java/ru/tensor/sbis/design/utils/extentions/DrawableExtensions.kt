package ru.tensor.sbis.design.utils.extentions

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * Расширение для получения [ColorDrawable] из ресурсов
 */
fun View.getColorDrawableFrom(@ColorRes colorResId: Int) = ColorDrawable(getColorFrom(colorResId))

/**
 * Расширение для получения [ColorDrawable] из ресурсов
 */
fun Context.getColorDrawableFrom(@ColorRes colorResId: Int) = ColorDrawable(getColorFrom(colorResId))

/**
 * Расширение для получения [ColorDrawable] из атрибута цвета
 */
fun View.getColorDrawableFromAttr(@AttrRes attrResId: Int) = ColorDrawable(getColorFromAttr(attrResId))

/**
 * Расширение для получения [ColorDrawable] из атрибута цвета
 */
fun Context.getColorDrawableFromAttr(@AttrRes attrResId: Int) = ColorDrawable(getColorFromAttr(attrResId))

/**
 * Расширение для получения Drawable из ресурсов с помощью ContextCompat
 */
fun View.getDrawableFromAttr(@AttrRes drawableAttrResId: Int): Drawable? =
    context.getDrawableFromAttr(drawableAttrResId)

/**
 * Расширение для получения Drawable из ресурсов с помощью ContextCompat
 */
fun Context.getDrawableFromAttr(@AttrRes drawableAttrResId: Int): Drawable? {
    val typedValue = TypedValue()
    theme.resolveAttribute(drawableAttrResId, typedValue, true)
    return getDrawableFrom(typedValue.resourceId)
}

/**
 * Расширение для получения Drawable из ресурсов с помощью ContextCompat
 */
fun Context.getDrawableFrom(@DrawableRes drawableResId: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableResId)

/**
 * Расширение для получения Drawable из ресурсов с помощью ContextCompat
 */
fun View.getDrawableFrom(@DrawableRes drawableResId: Int): Drawable? =
    context.getDrawableFrom(drawableResId)