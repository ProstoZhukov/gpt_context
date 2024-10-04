package ru.tensor.sbis.design.theme.res

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.theme.utils.getThemeDrawable

/**
 * Модель представления drawable.
 *
 * Используется для передачи [Drawable] различными способами, через один метод/переменную в API.
 * @author da.zolotarev
 */
sealed interface SbisDrawable {

    /** Получить drawable, если найден, иначе null. */
    fun getOrNull(context: Context): Drawable?

    /** Получить drawable, если найден, иначе [defValue]. */
    fun getOrDefault(context: Context, defValue: Drawable): Drawable

    /** Drawable, заданный значением. */
    class Value(
        val drawable: Drawable?
    ) : SbisDrawable {
        override fun getOrNull(context: Context) = drawable
        override fun getOrDefault(context: Context, defValue: Drawable) = drawable ?: defValue
    }

    /** Drawable, заданный ресурсом. */
    class Res(
        @DrawableRes
        val drawableRes: Int,
    ) : SbisDrawable {
        override fun getOrNull(context: Context) = if (drawableRes != ResourcesCompat.ID_NULL)
            ResourcesCompat.getDrawable(context.resources, drawableRes, context.theme) else null

        override fun getOrDefault(context: Context, defValue: Drawable) = if (drawableRes != ResourcesCompat.ID_NULL)
            ResourcesCompat.getDrawable(context.resources, drawableRes, context.theme) ?: defValue else defValue
    }

    /** Drawable, заданный атрибутом. */
    class Attr(
        @AttrRes
        val drawableAttr: Int
    ) : SbisDrawable {

        override fun getOrNull(context: Context) =
            ResourcesCompat.getDrawable(context.resources, context.getThemeDrawable(drawableAttr), context.theme)

        override fun getOrDefault(context: Context, defValue: Drawable) =
            ResourcesCompat.getDrawable(context.resources, context.getThemeDrawable(drawableAttr), context.theme)
                ?: defValue
    }

    /** Drawable, заданный [android.graphics.Bitmap]. */
    class Bitmap(
        val bitmap: android.graphics.Bitmap
    ) : SbisDrawable {

        override fun getOrNull(context: Context) = BitmapDrawable(context.resources, bitmap)

        override fun getOrDefault(context: Context, defValue: Drawable) = BitmapDrawable(context.resources, bitmap)
    }

    /** Drawable отсутствует. */
    object NotSpecified : SbisDrawable {
        override fun getOrNull(context: Context) = null
        override fun getOrDefault(context: Context, defValue: Drawable) = defValue
    }
}
