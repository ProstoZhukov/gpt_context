package ru.tensor.sbis.viper.helper.recyclerviewitemdecoration

import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.ColorInt

/**
 * Создание кастомного декоратора
 */
fun createDecorationDrawable(size: Int, @ColorInt color: Int): Drawable {
    return if (size > 0) ShapeDrawable(RectShape()).apply {
        intrinsicHeight = size
        intrinsicWidth = size
        paint.color = color
    } else ShapeDrawable()
}