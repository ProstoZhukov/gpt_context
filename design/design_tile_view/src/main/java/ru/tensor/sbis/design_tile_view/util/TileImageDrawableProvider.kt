package ru.tensor.sbis.design_tile_view.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.utils.asMutableBitmap
import ru.tensor.sbis.design.utils.image_loading.DrawableImageView
import ru.tensor.sbis.design_tile_view.R
import ru.tensor.sbis.design_tile_view.controller.SbisTileViewController
import timber.log.Timber
import kotlin.math.min
import kotlin.math.roundToInt

private const val PLACEHOLDER_SIZE_RATIO = 0.5

/**
 * Предоставляет [Drawable] для отрисовки в качестве изображения в плитке
 *
 * @author us.bessonov
 */
internal class TileImageDrawableProvider(
    private val context: Context,
    private val controller: SbisTileViewController
) {

    private val tintDrawer = TileImageTintDrawer(context)

    private val iconicsPlaceholder = IconicsDrawable(context).apply {
        color(ContextCompat.getColor(context, R.color.design_tile_view_image_placeholder_color))
        typeface(TypefaceManager.getSbisMobileIconTypeface(context))
    }

    @Px
    private var width = 0

    @Px
    private var height = 0

    @Px
    private var padding = 0

    @Px
    private var placeholderSize = 0

    private var originalBitmap: Bitmap? = null
    private var croppedBitmap: Bitmap? = null

    private var placeholderImage: Drawable? = null
    private var placeholderBitmap: Bitmap? = null
    @ColorInt
    private var placeholderBackground: Int? = null

    private var isImageTintEnabled = false

    private var isPlaceholderTintEnabled = false

    private var isTintUnderContent = false

    var drawable: Drawable? = null
        private set

    /** @SelfDocumented */
    fun setBitmap(bitmap: Bitmap?): Boolean {
        if (originalBitmap == bitmap) return false
        originalBitmap = bitmap
        croppedBitmap = null
        update()
        return true
    }

    /**
     * Задаёт обработанное изображение для непосредственного отображения
     */
    fun setPreparedBitmap(bitmap: Bitmap) {
        originalBitmap = bitmap
        croppedBitmap = bitmap
        update()
    }

    /**
     * Возвращает непосредственно отображаемое изображение
     */
    fun getPreparedBitmap() = croppedBitmap

    /** @SelfDocumented */
    fun setPlaceholderImage(image: Drawable?) {
        if (image == placeholderImage) return
        setPlaceholderDrawable(image)
    }

    /** @SelfDocumented */
    fun setPlaceholder(fontIcon: CharSequence, @ColorInt background: Int?) {
        iconicsPlaceholder.iconText(fontIcon.toString(), TypefaceManager.getSbisMobileIconTypeface(context))
        placeholderBackground = background
        setPlaceholderDrawable(iconicsPlaceholder)
    }

    /** @SelfDocumented */
    fun setSize(@Px w: Int, @Px h: Int) {
        if (w == width && h == height) return
        width = w
        height = h
        tintDrawer.apply {
            imageWidth = w
            imageHeight = h
        }
        iconicsPlaceholder.layout(w, h)
        update()
    }

    /** @SelfDocumented */
    fun setPadding(@Px padding: Int) {
        if (this.padding == padding) return
        this.padding = padding
        update()
    }

    /** @SelfDocumented */
    fun setPlaceholderSize(@Dimension(unit = Dimension.PX) size: Int) {
        placeholderSize = size
        iconicsPlaceholder.sizePx(size)
    }

    /** @SelfDocumented */
    fun isImageBitmapRecycled() = getDrawableBitmap()?.isRecycled
        ?: false

    /**
     * @see [DrawableImageView.hasValidBitmap]
     */
    fun hasValidBitmap() = getDrawableBitmap()?.isRecycled == false

    /** @SelfDocumented */
    fun configureTint(isImageTintEnabled: Boolean, isPlaceholderTintEnabled: Boolean, isTintUnderContent: Boolean) {
        if (!shouldUpdateTint(isImageTintEnabled, isPlaceholderTintEnabled, isTintUnderContent)) return
        this.isImageTintEnabled = isImageTintEnabled
        this.isPlaceholderTintEnabled = isPlaceholderTintEnabled
        this.isTintUnderContent = isTintUnderContent
        croppedBitmap = null
        update()
    }

    /** @SelfDocumented */
    fun getImageTintMode() = when {
        !isImageTintEnabled -> TintMode.NONE
        isTintUnderContent -> TintMode.FILL_AND_GRADIENT
        else -> TintMode.GRADIENT
    }

    /**
     * @see [TileImageTintDrawer.fillHeight]
     */
    fun setTintFillHeight(@Px height: Int) {
        tintDrawer.fillHeight = height
    }

    /** @SelfDocumented */
    fun getPlaceholderBitmap(
        @Px width: Int = this.width,
        @Px height: Int = this.height,
        withTint: Boolean = isPlaceholderTintEnabled
    ): Bitmap? {
        return if (withTint == isPlaceholderTintEnabled) {
            placeholderBitmap.takeIfSizeMatches()
                ?: createPlaceholderBitmap(withTint, width, height)
                    ?.also { placeholderBitmap = it }
        } else {
            createPlaceholderBitmap(withTint, width, height)
        }
    }

    private fun getDrawableBitmap() = (drawable as? BitmapDrawable?)?.bitmap

    private fun createPlaceholderBitmap(withTint: Boolean, @Px width: Int, @Px height: Int): Bitmap? {
        return placeholderImage?.let { drawable ->
            /*
            Общая механика toBitmap() используется только для неизвестных сценариев.
            Для остальных применяются более эффективные трансформации
             */
            when (drawable) {
                is IconicsDrawable -> drawable.toMutableBitmap(width, height, placeholderBackground)
                is BitmapDrawable -> drawable.bitmap.asMutableBitmap(width, height, false)
                else -> drawable.toBitmap().asMutableBitmap(width, height, true)
            }.applyTint(if (withTint) TintMode.GRADIENT else TintMode.NONE)
        }
    }

    private fun update() {
        if (!isMeasured()) return
        drawable = getBitmap()?.let {
            it.toDrawable(context.resources)
                .apply { setBounds(0, 0, width - padding * 2, height - padding * 2) }
        }
    }

    private fun getBitmap(): Bitmap? {
        return getImageBitmap()
            ?: getPlaceholderBitmap()
    }

    private fun getImageBitmap(): Bitmap? {
        return croppedBitmap?.takeIfSizeMatches()
            ?: getOriginalBitmap()?.let { original ->
                croppedBitmap = original.asMutableBitmap(width, height, false)
                    .ensureNotTheSame(original)
                    .applyTint(getImageTintMode())
                croppedBitmap
            }
    }

    private fun IconicsDrawable.toMutableBitmap(width: Int, height: Int, background: Int?): Bitmap {
        layout(width, height)
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .applyCanvas {
                drawColor(background ?: Color.TRANSPARENT)
                draw(this)
            }
    }

    private fun Bitmap.applyTint(tintMode: TintMode): Bitmap {
        tintDrawer.setTintMode(tintMode)
        if (tintMode == TintMode.NONE) return this
        return applyCanvas {
            tintDrawer.draw(this)
        }
    }

    private fun shouldUpdateTint(
        isImageTintEnabled: Boolean,
        isPlaceholderTintEnabled: Boolean,
        isTintUnderContent: Boolean
    ) = isImageTintEnabled != this.isImageTintEnabled ||
        isPlaceholderTintEnabled != this.isPlaceholderTintEnabled ||
        isTintUnderContent != this.isTintUnderContent

    @SuppressLint("BinaryOperationInTimber")
    private fun getOriginalBitmap() = originalBitmap?.let {
        if (it.isRecycled) {
            Timber.e(
                "Cannot create scaled bitmap because original is recycled!. " +
                    "Is loading in progress: ${controller.isLoading}, images = ${controller.images}"
            )
            return@let null
        } else {
            return@let it
        }
    }

    private fun setPlaceholderDrawable(drawable: Drawable?) {
        placeholderImage = drawable
        placeholderBitmap?.recycle()
        placeholderBitmap = null
        update()
    }

    private fun Bitmap?.takeIfSizeMatches() = this?.takeIf {
        it.width == this@TileImageDrawableProvider.width && it.height == this@TileImageDrawableProvider.height
    }

    private fun isMeasured() = width > 0 || height > 0

    private fun Bitmap.ensureNotTheSame(original: Bitmap): Bitmap {
        if (this == original) return copy(this.config, true)
        return this
    }

    private fun IconicsDrawable.layout(@Px width: Int, @Px height: Int) {
        val size = placeholderSize
            .takeIf { it > 0 }
            ?: (min(width, height) * PLACEHOLDER_SIZE_RATIO).roundToInt()
        val left = (width - size) / 2
        val top = (height - size) / 2
        setBounds(left, top, left + size, top + size)
    }
}