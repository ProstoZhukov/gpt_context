package ru.tensor.sbis.design.profile.imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.withSave
import ru.tensor.sbis.design.profile.imageview.drawer.DefaultShapedDrawer
import ru.tensor.sbis.design.profile.imageview.drawer.ShapedDrawer
import ru.tensor.sbis.design.profile.person.UserInitialsDrawable
import ru.tensor.sbis.design.profile.personcollagelist.contentviews.CounterDrawer
import ru.tensor.sbis.design.profile.personcollagelist.util.DefaultInitialsDrawableFactory
import ru.tensor.sbis.design.profile.personcollagelist.util.PersonViewDrawableProvider
import ru.tensor.sbis.design.profile.util.getPlaceholder
import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.profile_decl.person.ImageData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.utils.image_loading.DrawableImageView
import ru.tensor.sbis.design.utils.image_loading.ImageLoaderDiagnostics
import ru.tensor.sbis.fresco_view.shapeddrawer.ShapedImageView
import ru.tensor.sbis.design.R as RDesign

/**
 * Предназначен для отображения фото сотрудника на [Canvas].
 *
 * @author us.bessonov
 */
internal class PersonImageView(
    private val context: Context,
    private val diagnosticsId: Int? = null
) : ShapedImageView, DrawableImageView {

    @ColorInt
    private val defaultBackgroundColor = ContextCompat.getColor(context, RDesign.color.palette_color_white1)

    private val defaultBackgroundFilter = PorterDuffColorFilter(defaultBackgroundColor, PorterDuff.Mode.SRC_IN)
    private val companyBackgroundFilter = PorterDuffColorFilter(
        StyleColor.UNACCENTED.getColor(context),
        PorterDuff.Mode.SRC_IN
    )

    private val bounds = Rect()
    private val drawableBounds = Rect()

    private var tempBitmap: Bitmap? = null

    private var isLaidOut = false

    private val counterDrawer = CounterDrawer(context)

    private var drawable: Drawable? = null

    private var placeholderBitmap: Bitmap? = null

    private var background: Drawable? = null

    private var nominalPadding = 0

    private val padding: Int
        get() = if (counterDrawer.hasCounter()) 0 else nominalPadding

    private var photoData: PhotoData? = null

    /** @SelfDocumented */
    @Px
    private var photoSize = 0

    @Px
    private var cornerRadius = 0f

    private var isSquare = false

    private var isNeedShowPlaceholder: Boolean = true

    /**
     * Позволяет переопределить способ применения специфичной формы для изображения
     */
    var drawer: ShapedDrawer = DefaultShapedDrawer(this, counterDrawer)

    /** @SelfDocumented */
    @Px
    var initialsTextSize: Float? = null

    /** @SelfDocumented */
    @ColorInt
    var initialsColor = 0

    /** @SelfDocumented */
    var viewIdName: String = View.NO_ID.toString()

    /** @SelfDocumented */
    fun setData(data: PhotoData) {
        if (photoData == null || data.photoUrl.isNullOrEmpty()) {
            setPlaceholder(data)
            invalidate()
        }
        photoData = data
        when (data) {
            is CompanyData -> {
                isNeedShowPlaceholder = true
                background?.colorFilter = companyBackgroundFilter
                setShapeBackgroundColor(defaultBackgroundColor)
            }

            is ImageData -> {
                isNeedShowPlaceholder = data.placeholder != null
                setShapeBackgroundColor(Color.TRANSPARENT)
            }

            else -> {
                isNeedShowPlaceholder = true
                background?.colorFilter = defaultBackgroundFilter
                setShapeBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    /** @SelfDocumented */
    fun setShape(shape: Shape) {
        drawer.setShape(
            when (shape) {
                Shape.SUPER_ELLIPSE -> PersonViewDrawableProvider.getSuperEllipseShape(context)
                Shape.CIRCLE -> PersonViewDrawableProvider.getCircleShape(context)
                Shape.SQUARE -> getSquareShape()
                    .also { isSquare = true }
            }
        )
    }

    /** @SelfDocumented */
    fun setCornerRadius(@Px radius: Float) {
        cornerRadius = radius
        if (isSquare) {
            drawer.setShape(getSquareShape())
        }
    }

    private fun getSquareShape(): Drawable = if (cornerRadius > 0) {
        PersonViewDrawableProvider.getSquareShape(cornerRadius)
    } else {
        PersonViewDrawableProvider.getSquareShape(context)
    }

    /** @SelfDocumented */
    fun setPadding(padding: Int) {
        nominalPadding = padding
    }

    /** @SelfDocumented */
    fun setBackground(drawable: Drawable) {
        background = drawable
    }

    /** @SelfDocumented */
    fun resetCounter(count: Int = 0) {
        val shouldInvalidate = counterDrawer.currentCount != count
        counterDrawer.setCount(count)
        (drawable as? UserInitialsDrawable)?.let {
            it.initialsEnabled = areInitialsEnabled()
        }
        if (shouldInvalidate) invalidate()
    }

    /** @SelfDocumented */
    fun setPhotoSize(@Px size: Int) {
        photoSize = size
        counterDrawer.setItemSize(size)
    }

    /** @SelfDocumented */
    fun draw(canvas: Canvas) {
        background?.bounds = bounds
        background?.draw(canvas)

        drawable?.let {
            it.bounds = drawableBounds
            canvas.withSave {
                translate(bounds.left.toFloat(), bounds.top.toFloat())
                drawer.onDraw(it, canvas)
            }
        }
    }

    /** @SelfDocumented */
    fun layout(left: Int, top: Int, right: Int, bottom: Int) {
        log("layout view in bounds $left $top $right $bottom")
        val oldW = bounds.width()
        val oldH = bounds.height()
        bounds.set(left, top, right, bottom)
        drawableBounds.set(0, 0, bounds.width() - padding * 2, bounds.height() - padding * 2)
        isLaidOut = true
        setTempBitmapToDrawable()
        drawer.onSizeChanged(bounds.width(), bounds.height(), oldW, oldH)
    }

    /** @SelfDocumented */
    fun setShapeBackgroundColor(@ColorInt color: Int) {
        drawer.setBackgroundColor(if (!isNeedShowPlaceholder) Color.TRANSPARENT else color)
    }

    /** @SelfDocumented */
    fun invalidate() {
        drawer.invalidate()
    }

    /** @SelfDocumented */
    fun configurePlaceholderBitmap(bitmap: Bitmap?) {
        placeholderBitmap = bitmap
    }

    /**
     * Задаёт для отображения заданный [bitmap], либо заглушку при его отсутствии
     */
    override fun setBitmap(bitmap: Bitmap?): Boolean {
        if (bitmap?.isRecycled == true) {
            ImageLoaderDiagnostics.error(diagnosticsId, "Cannot use recycled bitmap")
        }
        if (bitmap != null && drawable != null && (drawable as? BitmapDrawable?)?.bitmap == bitmap) {
            log("set same $bitmap as before")
            return false
        }
        bitmap
            ?.takeUnless { it.isRecycled || it.width <= 0 || it.height <= 0 }
            ?.let {
                tempBitmap = it
                setTempBitmapToDrawable()
            }
            ?: setPlaceholder()
        return true
    }

    /** @SelfDocumented */
    override fun isBitmapRecycled(): Boolean = getDrawableBitmap()?.isRecycled ?: false

    override fun hasValidBitmap(): Boolean = getDrawableBitmap()?.isRecycled == false

    override fun getPlaceholderBitmap(width: Int, height: Int, withTint: Boolean): Bitmap? = placeholderBitmap

    override fun setPreparedBitmap(bitmap: Bitmap) = Unit

    // region ShapedImageView
    override fun getName() = viewIdName

    override fun getDrawable(): Drawable? = drawable

    override fun getWidth() = bounds.width()

    override fun getHeight() = bounds.height()

    override fun getPaddingLeft() = padding

    override fun getPaddingRight() = padding

    override fun getPaddingTop() = padding

    override fun getPaddingBottom() = padding

    override fun getImageMatrix(): Matrix? = null
    // endregion

    private fun setTempBitmapToDrawable() {
        if (!isLaidOut) {
            return
        }
        val originBitmap = tempBitmap?.takeUnless { it.isRecycled || it.width <= 0 || it.height <= 0 } ?: return
        val modifiedBitmap = if (originBitmap.width == originBitmap.height || getWidth() <= 0 || getHeight() <= 0) {
            originBitmap
        } else {
            ThumbnailUtils.extractThumbnail(originBitmap, getWidth(), getHeight())
        }
        drawable = modifiedBitmap.toDrawable(context.resources)
        tempBitmap = null
        log("drawable after setting bitmap: $drawable")
    }

    private fun setPlaceholder(data: PhotoData? = photoData) {
        data ?: return
        drawable = getPlaceholder(
            data,
            PersonViewDrawableProvider,
            initialsTextSize,
            initialsColor,
            context,
            DefaultInitialsDrawableFactory,
            areInitialsEnabled()
        )
        log("drawable after setting bitmap: $drawable")
    }

    private fun areInitialsEnabled() = !counterDrawer.hasCounter()

    private fun log(msg: String) = diagnosticsId?.let { ImageLoaderDiagnostics.log(it, msg) }

    private fun getDrawableBitmap() = (drawable as? BitmapDrawable?)?.bitmap
}
