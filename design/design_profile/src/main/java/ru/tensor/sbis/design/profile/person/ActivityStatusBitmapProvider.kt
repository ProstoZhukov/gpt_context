package ru.tensor.sbis.design.profile.person

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toBitmap
import ru.tensor.sbis.design.profile.R

/**
 * Вспомогательный класс для кэширования и переиспользования [Bitmap] с изображениями статусов активности.
 * Позволяет предотвратить лишний обтейнинг темизированного контекста
 * у самых часто используемых белых статусов [R.style.DesignProfileActivityStatusStyleWhite].
 * Механика кэширования ускоряет время создания статусов, используемых в ячейках:
 * - [ActivityStatusView] ~ в 4 раза (На быстром девайсе 75мкс против 280мкс)
 * - [ActivityStatusDrawable] ~ в 10 раз (25мкс против 250мкс)
 */
internal object ActivityStatusBitmapProvider {

    private var cachedWhiteBitmaps: ActivityStatusBitmaps? = null

    /**
     * Получить модель картинок статусов [ActivityStatusBitmaps] с учетом темизации.
     */
    fun getActivityStatusBitmaps(
        context: Context,
        attrs: AttributeSet?,
        size: Int,
        styleHolder: ActivityStatusStyleHolder
    ): ActivityStatusBitmaps {
        val resultStyleRes: Int = getContextAndStyleRes(context, attrs)
        return if (
            resultStyleRes == R.style.DesignProfileActivityStatusStyleWhite && styleHolder.backgroundColor == -1
        ) {
            getWhiteBitmaps(size, styleHolder)
        } else {
            obtainActivityStatusBitmaps(size, styleHolder)
        }
    }

    private fun getWhiteBitmaps(
        size: Int,
        styleHolder: ActivityStatusStyleHolder
    ) = cachedWhiteBitmaps ?: obtainActivityStatusBitmaps(size, styleHolder).also { cachedWhiteBitmaps = it }

    private fun obtainActivityStatusBitmaps(
        size: Int,
        styleHolder: ActivityStatusStyleHolder
    ): ActivityStatusBitmaps {
        val onlineWorkBitmap = getActivityStatusBitmap(size, styleHolder, isOnline = true, isHome = false)
        val offlineWorkBitmap = getActivityStatusBitmap(size, styleHolder, isOnline = false, isHome = false)
        val onlineHomeBitmap = getActivityStatusBitmap(size, styleHolder, isOnline = true, isHome = true)
        val offlineHomeBitmap = getActivityStatusBitmap(size, styleHolder, isOnline = false, isHome = true)

        return ActivityStatusBitmaps(onlineWorkBitmap, offlineWorkBitmap, onlineHomeBitmap, offlineHomeBitmap)
    }

    /**
     * Создаёт [ActivityStatusView], применяя тему [R.attr.activityStatusViewTheme] из атрибутов при её наличии.
     */
    private fun getContextAndStyleRes(context: Context, attrs: AttributeSet?): Int {
        val contextWithTheme = applyActivityStatusThemeFromAttributes(context, attrs)
        val defaultStyle = if (contextWithTheme == null) {
            R.style.DesignProfileActivityStatusStyleWhite
        } else {
            ResourcesCompat.ID_NULL
        }
        return defaultStyle
    }

    private fun applyActivityStatusThemeFromAttributes(context: Context, attrs: AttributeSet?): Context? {
        context.withStyledAttributes(attrs, intArrayOf(R.attr.activityStatusViewTheme)) {
            val theme = getResourceId(0, ResourcesCompat.ID_NULL)
            if (theme != ResourcesCompat.ID_NULL) {
                return ContextThemeWrapper(context, theme)
            }
        }
        return null
    }

    private fun getActivityStatusBitmap(
        size: Int,
        styleHolder: ActivityStatusStyleHolder,
        isOnline: Boolean,
        isHome: Boolean
    ): Bitmap {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.setColor(if (isOnline) styleHolder.primaryColor else styleHolder.unaccentedColor)
        shape.setStroke(styleHolder.strokeWidth, styleHolder.backgroundColor)
        shape.setSize(size, size)

        return if (isHome) {
            // внутренний белый круг
            val smallShape = GradientDrawable()
            smallShape.shape = GradientDrawable.OVAL
            smallShape.setColor(styleHolder.backgroundColor)
            smallShape.setSize(
                size - 4 * styleHolder.strokeWidth,
                size - 4 * styleHolder.strokeWidth
            )
            createSingleBitmapFromTwoDrawable(shape, smallShape, shape.intrinsicHeight - smallShape.intrinsicHeight)
        } else {
            shape.toBitmap()
        }
    }

    private fun createSingleBitmapFromTwoDrawable(bigShape: Drawable, smallShape: Drawable, size: Int): Bitmap {
        val bigImage = bigShape.toBitmap()
        val smallImage = smallShape.toBitmap()

        val result = Bitmap.createBitmap(bigImage.width, bigImage.height, bigImage.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(bigImage, 0f, 0f, null)
        canvas.drawBitmap(smallImage, size / 2.0f, size / 2.0f, null)
        return result
    }

    /**
     * Модель картинок статусов активности [ActivityStatus].
     */
    internal data class ActivityStatusBitmaps(
        val onlineWorkBitmap: Bitmap,
        val offlineWorkBitmap: Bitmap,
        val onlineHomeBitmap: Bitmap,
        val offlineHomeBitmap: Bitmap
    )
}