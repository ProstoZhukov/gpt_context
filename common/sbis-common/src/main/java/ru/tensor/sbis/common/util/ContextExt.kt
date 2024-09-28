package ru.tensor.sbis.common.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/**
 * Возвращает [Resources] для портретной ориентации
 */
@Suppress("deprecation")
fun Context.getPortraitOrientationResources(): Resources {
    val config = Configuration(resources.configuration).apply {
        orientation = Configuration.ORIENTATION_PORTRAIT
    }
    return createConfigurationContext(config).resources
}

/**
 * Предоставляет Drawable, используя [ContextCompat]
 */
fun Context.getCompatDrawable(@DrawableRes drRes: Int) = ContextCompat.getDrawable(this, drRes)

/**
 * Предоставляет Color, используя [ContextCompat]
 */
fun Context.getCompatColor(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

/**
 * Предоставляет Font, используя [ResourcesCompat]
 */
fun Context.getCompatFont(@FontRes fontRes: Int) = ResourcesCompat.getFont(this, fontRes)