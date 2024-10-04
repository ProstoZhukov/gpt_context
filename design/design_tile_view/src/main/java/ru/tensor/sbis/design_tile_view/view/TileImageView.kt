package ru.tensor.sbis.design_tile_view.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Dimension.Companion.PX
import androidx.annotation.Px
import androidx.core.graphics.withSave
import ru.tensor.sbis.design_tile_view.R
import ru.tensor.sbis.design_tile_view.Rectangle
import ru.tensor.sbis.design_tile_view.SbisTileViewImageShape
import ru.tensor.sbis.design_tile_view.controller.SbisTileViewController
import ru.tensor.sbis.design_tile_view.util.TileImageDrawableProvider
import ru.tensor.sbis.fresco_view.shapeddrawer.ShapedImageView
import ru.tensor.sbis.fresco_view.shapeddrawer.SwapBufferShapedImageDrawer
import ru.tensor.sbis.design.utils.image_loading.DrawableImageView

/**
 * View изображения, отображаемого в Плитке
 *
 * @author us.bessonov
 */
internal class TileImageView(
    private val context: Context,
    controller: SbisTileViewController
) : ShapedImageView, DrawableImageView {

    private val drawer = SwapBufferShapedImageDrawer(this)

    private val drawableProvider = TileImageDrawableProvider(context, controller)

    @Px
    var measuredWidth = 0

    @Px
    var measuredHeight = 0

    @Px
    var imagePadding = context.resources.getDimensionPixelSize(R.dimen.design_tile_view_image_padding)

    @Px
    var padding: Int = 0
        private set

    var left = 0

    var top = 0

    val right: Int
        get() = left + measuredWidth

    val bottom: Int
        get() = top + measuredHeight

    /**
     * @see [TileImageDrawableProvider.getPreparedBitmap]
     */
    fun getPreparedBitmap() = drawableProvider.getPreparedBitmap()

    /** @SelfDocumented */
    fun setShape(shape: SbisTileViewImageShape) {
        setPadding(if (shape !is Rectangle) imagePadding else 0)
        drawer.setShape(shape.getDrawable(context))
    }

    /** @SelfDocumented */
    fun setPlaceholder(fontIcon: CharSequence, @ColorInt background: Int? = null) =
        drawableProvider.setPlaceholder(fontIcon, background)

    /** @SelfDocumented */
    fun setPlaceholder(image: Drawable) {
        drawableProvider.setPlaceholderImage(image)
    }

    /** @SelfDocumented */
    fun setPlaceholderSize(@Dimension(unit = PX) size: Int) = drawableProvider.setPlaceholderSize(size)

    /** @SelfDocumented */
    fun draw(canvas: Canvas) {
        canvas.withSave {
            translate(left.toFloat(), top.toFloat())
            if (getDrawable() != null) {
                drawer.onDraw(canvas)
            }
        }
    }

    /** @SelfDocumented */
    fun invalidate() {
        drawer.invalidate()
    }

    /** @SelfDocumented */
    fun layout(left: Int, top: Int) {
        this.left = left
        this.top = top
    }

    /** @SelfDocumented */
    fun setSize(@Px width: Int, @Px height: Int) {
        if (width == measuredWidth && height == measuredHeight) return
        val oldW = measuredWidth
        val oldH = measuredHeight
        measuredWidth = width
        measuredHeight = height
        drawableProvider.setSize(width, height)
        drawer.onSizeChanged(width, height, oldW, oldH)
    }

    /** @SelfDocumented */
    fun configureTint(isImageTintEnabled: Boolean, isPlaceholderTintEnabled: Boolean, isTintUnderContent: Boolean) =
        drawableProvider.configureTint(isImageTintEnabled, isPlaceholderTintEnabled, isTintUnderContent)

    /** @SelfDocumented */
    fun getImageTintMode() = drawableProvider.getImageTintMode()

    /**
     * @see [TileImageDrawableProvider.setTintFillHeight]
     */
    fun setTintFillHeight(@Px height: Int) = drawableProvider.setTintFillHeight(height)

    /** @SelfDocumented */
    override fun isBitmapRecycled(): Boolean = drawableProvider.isImageBitmapRecycled()

    override fun hasValidBitmap(): Boolean = drawableProvider.hasValidBitmap()

    /**
     * @see [TileImageDrawableProvider.getPlaceholderBitmap]
     */
    override fun getPlaceholderBitmap(@Px width: Int, @Px height: Int, withTint: Boolean) =
        drawableProvider.getPlaceholderBitmap(width, height, withTint)

    /** @SelfDocumented */
    override fun setBitmap(bitmap: Bitmap?) = drawableProvider.setBitmap(bitmap)

    /**
     * @see [TileImageDrawableProvider.setPreparedBitmap]
     */
    override fun setPreparedBitmap(bitmap: Bitmap) = drawableProvider.setPreparedBitmap(bitmap)

    override fun getName() = View.NO_ID.toString()

    override fun getDrawable(): Drawable? = drawableProvider.drawable

    override fun getWidth() = measuredWidth

    override fun getHeight() = measuredHeight

    override fun getPaddingLeft() = padding

    override fun getPaddingRight() = padding

    override fun getPaddingTop() = padding

    override fun getPaddingBottom() = padding

    override fun getImageMatrix(): Matrix? = null

    private fun setPadding(@Px padding: Int) {
        this.padding = padding
        drawableProvider.setPadding(padding)
    }

}