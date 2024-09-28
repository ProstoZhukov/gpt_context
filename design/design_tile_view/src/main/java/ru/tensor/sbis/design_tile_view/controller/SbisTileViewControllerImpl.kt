package ru.tensor.sbis.design_tile_view.controller

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.graphics.withSave
import androidx.core.view.isVisible
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.Direction
import ru.tensor.sbis.design.theme.VerticalAlignment
import ru.tensor.sbis.design.theme.VerticalAlignment.BOTTOM
import ru.tensor.sbis.design.theme.VerticalAlignment.CENTER
import ru.tensor.sbis.design.theme.VerticalAlignment.TOP
import ru.tensor.sbis.design.utils.GradientShaderFactory
import ru.tensor.sbis.design.utils.image_loading.ImageUrl
import ru.tensor.sbis.design.utils.image_loading.RawBitmap
import ru.tensor.sbis.design.utils.image_loading.ViewImageLoader
import ru.tensor.sbis.design_tile_view.ImagePlaceholder
import ru.tensor.sbis.design_tile_view.Rectangle
import ru.tensor.sbis.design_tile_view.SbisTileViewImageAlignment
import ru.tensor.sbis.design_tile_view.SbisTileViewImageModel
import ru.tensor.sbis.design_tile_view.SbisTileViewImageRatio
import ru.tensor.sbis.design_tile_view.SbisTileViewImageShape
import ru.tensor.sbis.design_tile_view.SbisTileViewPlaceholder
import ru.tensor.sbis.design_tile_view.TextIconPlaceholder
import ru.tensor.sbis.design_tile_view.TileViewBitmap
import ru.tensor.sbis.design_tile_view.TileViewImage
import ru.tensor.sbis.design_tile_view.TileViewImageUrl
import ru.tensor.sbis.design_tile_view.util.ImageBorderDrawer
import ru.tensor.sbis.design_tile_view.util.TileImageCollageBuilder
import ru.tensor.sbis.design_tile_view.util.TileRecentBitmapsCache
import ru.tensor.sbis.design_tile_view.view.SbisTileView
import ru.tensor.sbis.design_tile_view.view.TileImageView
import kotlin.math.roundToInt
import ru.tensor.sbis.design_tile_view.R as TileViewR

/**
 * Контроллер компонента Плитка
 *
 * @author us.bessonov
 */
internal class SbisTileViewControllerImpl(
    private val imageLoader: ViewImageLoader = ViewImageLoader(TileImageCollageBuilder()),
    private val imageGradientDrawableFactory: () -> PaintDrawable = {
        PaintDrawable().apply { shape = RectShape() }
    },
    private val backgroundDrawableFactory: (Int) -> GradientDrawable = {
        GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(it, it))
    },
    private val createMeasureSpec: (size: Int, mode: Int) -> Int = { size, mode ->
        View.MeasureSpec.makeMeasureSpec(size, mode)
    }
) : SbisTileViewController {

    private lateinit var view: SbisTileView

    private lateinit var image: TileImageView

    private lateinit var imageBorderDrawer: ImageBorderDrawer

    @get:Px
    private val elevationSize by lazy {
        view.resources.getDimension(R.dimen.elevation_high)
    }

    @get:Px
    private val gradientHeight by lazy {
        view.resources.getDimensionPixelSize(TileViewR.dimen.design_tile_view_image_gradient_height)
    }

    private var imageGradient: PaintDrawable = imageGradientDrawableFactory.invoke()

    @Px
    private var contentWidth = 0

    @Px
    private var contentAreaHeight = 0

    private var contentView: View? = null

    private var topView: View? = null

    private var bottomView: View? = null

    private var model: SbisTileViewImageModel = SbisTileViewImageModel.DEFAULT

    private var contentAlignment = BOTTOM

    private var hasShadow = false

    @get:ColorInt
    private val defaultBackgroundColor by lazy {
        ContextCompat.getColor(view.context, R.color.palette_color_white1)
    }

    private val backgroundDrawable by lazy {
        backgroundDrawableFactory(defaultBackgroundColor)
    }

    private var isBorderEnabled = false

    private var isBorderUnderContent = false

    @ColorInt
    private var startBackgroundColor = 0

    @ColorInt
    private var endBackgroundColor = 0

    @Px
    private var imageSize = 0

    @get:Px
    override var width = 0

    @get:Px
    override var height = 0

    override val isLoading: Boolean
        get() = imageLoader.isLoading

    override var images = emptyList<TileViewImage>()
        private set

    override var cornerRadius = 0f
        private set

    // region SbisTileViewApi
    override fun setImageModel(model: SbisTileViewImageModel) {
        if (model == this.model && imageLoader.hasResult()) return
        val shouldUpdateGradient = model.alignment != this.model.alignment
        val shouldUpdateShape = model.shape != this.model.shape
        val shouldUpdatePlaceholder = model.placeholder != this.model.placeholder
        val shouldRequestLayout = model.alignment != this.model.alignment ||
            model.darkerImage != this.model.darkerImage ||
            model.darkerPlaceholder != this.model.darkerPlaceholder
        this.model = model
        images = model.images
        imageLoader.setImages(
            images.map {
                when (it) {
                    is TileViewBitmap -> RawBitmap(it.bitmap)
                    is TileViewImageUrl -> ImageUrl(it.imageUrl)
                }
            }
        )
        updateImageTint()
        if (!updateImageCornerRadiusIfNeeded() && shouldUpdateShape) {
            setShape(model.shape)
        }
        if (shouldUpdatePlaceholder) {
            model.placeholder?.let(::setPlaceholder)
        }
        if (shouldUpdateGradient) updateImageGradient()
        if (shouldRequestLayout) view.requestLayout() else view.invalidate()
    }

    override fun setPlaceholder(placeholder: SbisTileViewPlaceholder) = when (placeholder) {
        is ImagePlaceholder -> image.setPlaceholder(ContextCompat.getDrawable(view.context, placeholder.image)!!)
        is TextIconPlaceholder -> image.setPlaceholder(placeholder.iconString, placeholder.background)
    }

    override fun setContentAlignment(alignment: VerticalAlignment) {
        if (alignment != contentAlignment) {
            contentAlignment = alignment
            updateImageTint()
            view.invalidate()
            view.requestLayout()
        }
    }

    override fun setCornerRadius(radius: Float) {
        if (radius != cornerRadius) {
            cornerRadius = radius
            backgroundDrawable.cornerRadius = cornerRadius
            updateImageCornerRadiusIfNeeded()
            view.invalidate()
        }
    }

    override fun setNeedSetupShadow(needShadow: Boolean) {
        if (needShadow != hasShadow) {
            hasShadow = needShadow
            view.elevation = if (needShadow) elevationSize else 0f
            view.invalidate()
        }
    }

    override fun setStartBackgroundColor(color: Int) {
        if (color != startBackgroundColor) {
            startBackgroundColor = color
            updateBackground()
            updateImageGradient()
            view.invalidate()
        }
    }

    override fun setEndBackgroundColor(color: Int) {
        if (color != endBackgroundColor) {
            endBackgroundColor = color
            updateBackground()
            updateImageGradient()
            view.invalidate()
        }
    }

    override fun setImageBorderEnabled(isEnabled: Boolean) {
        if (isBorderEnabled != isEnabled) {
            isBorderEnabled = isEnabled
            view.invalidate()
        }
    }

    override fun setContentView(view: View) {
        contentView = view
        updateImageTint()
        this.view.invalidate()
        this.view.requestLayout()
    }

    override fun setTopView(view: View) {
        topView = view
        this.view.requestLayout()
    }

    override fun setBottomView(view: View) {
        bottomView = view
        this.view.requestLayout()
    }
    // endregion

    // region SbisTileViewController
    override fun init(
        view: SbisTileView,
        image: TileImageView,
        @Px imageSize: Int,
        @Px imagePadding: Int,
        isBorderEnabled: Boolean,
        isBorderUnderContent: Boolean,
        imageBorderDrawer: ImageBorderDrawer
    ) {
        this.view = view
        this.image = image
        this.imageSize = imageSize
        this.isBorderEnabled = isBorderEnabled
        this.isBorderUnderContent = isBorderUnderContent
        this.imageBorderDrawer = imageBorderDrawer
        imagePadding.takeIf { it > 0 }
            ?.let { image.imagePadding = it }
        startBackgroundColor = defaultBackgroundColor
        endBackgroundColor = defaultBackgroundColor
        view.background = backgroundDrawable
        updateImageGradient()
        imageLoader.init(
            view,
            image,
            ::putPreparedBitmapToCache,
            ::getPreparedBitmapFromCache,
            ::getCurrentImageWidthAndHeight
        )
    }

    override fun initOverlayViews(contentView: View?, topView: View?, bottomView: View?) {
        this.contentView = contentView
        this.topView = topView
        this.bottomView = bottomView
    }

    override fun performMeasure(availableViewWidth: Int, availableViewHeight: Int) {
        val availableWidth = availableViewWidth.coerceAtLeast(view.minimumWidth)
        val availableHeight = availableViewHeight.coerceAtLeast(view.minimumHeight)
        when {
            imageSize == 0 -> {
                width = availableWidth
                height = availableHeight
            }
            model.alignment == SbisTileViewImageAlignment.FILL -> {
                val fullImageSize = getFullImageSize()
                width = fullImageSize
                height = fullImageSize
            }
            model.alignment == SbisTileViewImageAlignment.LEFT ||
                model.alignment == SbisTileViewImageAlignment.RIGHT -> {
                width = availableWidth
                height = getFullImageSize()
            }
            else -> {
                width = getFullImageSize()
                height = availableHeight
            }
        }
        val (imageWidth, imageHeight) = getImageWidthAndHeight()
        image.setSize(imageWidth, imageHeight)
        updateBorderBounds()

        topView?.let { measureAccessoryView(it, imageWidth, imageHeight) }
        bottomView?.let { measureAccessoryView(it, imageWidth, imageHeight) }

        val (contentWidth, contentAreaHeight) = when (model.alignment) {
            SbisTileViewImageAlignment.LEFT, SbisTileViewImageAlignment.RIGHT -> {
                (width - imageWidth + image.padding) to height
            }
            SbisTileViewImageAlignment.TOP, SbisTileViewImageAlignment.BOTTOM -> {
                width to (height - imageHeight + image.padding)
            }
            SbisTileViewImageAlignment.FILL -> width to height
        }
        this.contentWidth = contentWidth
        this.contentAreaHeight = contentAreaHeight
        contentView?.let {
            it.measure(
                createMeasureSpec(contentWidth, View.MeasureSpec.EXACTLY),
                createMeasureSpec(contentAreaHeight, View.MeasureSpec.AT_MOST)
            )
            image.setTintFillHeight(it.measuredHeight)
        }

        imageLoader.onViewMeasured()
    }

    override fun performLayout() {
        when (model.alignment) {
            SbisTileViewImageAlignment.LEFT -> layoutInternal(contentLeft = image.measuredWidth - image.padding)
            SbisTileViewImageAlignment.RIGHT -> layoutInternal(imageLeft = contentWidth - image.padding)
            SbisTileViewImageAlignment.TOP -> {
                val top = image.measuredHeight - image.padding
                layoutInternal(
                    contentTopBound = top,
                    contentBottomBound = top + contentAreaHeight
                )
            }
            SbisTileViewImageAlignment.BOTTOM -> layoutInternal(
                imageTop = contentAreaHeight - image.padding,
                contentBottomBound = contentAreaHeight
            )
            SbisTileViewImageAlignment.FILL -> layoutInternal()
        }
    }

    override fun performDraw(canvas: Canvas) {
        if (hasImage()) {
            image.draw(canvas)
            if (isBorderEnabled && isBorderUnderContent) {
                imageBorderDrawer.draw(canvas)
            }
        }
        if (hasImageGradient()) imageGradient.draw(canvas)
        drawChild(contentView, canvas)
        drawChild(topView, canvas)
        drawChild(bottomView, canvas)
        if (hasImage() && isBorderEnabled && !isBorderUnderContent) {
            imageBorderDrawer.draw(canvas)
        }
    }

    override fun performInvalidate() {
        image.invalidate()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        imageLoader.onVisibilityAggregated(isVisible)
    }
    // endregion

    private fun drawChild(view: View?, canvas: Canvas) {
        view?.takeIf { it.visibility == ViewGroup.VISIBLE }
            ?.apply {
                canvas.withSave {
                    translate(left.toFloat(), top.toFloat())
                    draw(this)
                }
            }
    }

    private fun drawImageBorder(canvas: Canvas) {
        canvas.withSave {
            translate(image.left.toFloat(), image.top.toFloat())
            imageBorderDrawer.draw(this)
        }
    }

    private fun updateImageCornerRadiusIfNeeded() = when (val shape = model.shape) {
        is Rectangle -> {
            shape.radii = getCornerRadii()
            setShape(shape)
            true
        }
        else -> false
    }

    private fun setShape(shape: SbisTileViewImageShape) {
        image.setShape(shape)
        imageBorderDrawer.setShape(shape)
        updateBorderBounds()
    }

    private fun updateBackground() {
        backgroundDrawable.colors = intArrayOf(startBackgroundColor, endBackgroundColor)
    }

    private fun getPreparedBitmapFromCache(): Bitmap? {
        val cacheKey = getRecentBitmapsCacheKey()
        return cacheKey?.let { TileRecentBitmapsCache.get(it, image.getWidth(), image.getHeight()) }
    }

    private fun getRecentBitmapsCacheKey() = getImageUrls()?.let {
        TileRecentBitmapsCache.createKey(getImageUrls(), image.getImageTintMode())
    }

    private fun putPreparedBitmapToCache() {
        val preparedBitmap = image.getPreparedBitmap() ?: return
        getRecentBitmapsCacheKey()
            ?.let { TileRecentBitmapsCache.put(preparedBitmap, it) }
    }

    private fun layoutInternal(
        imageLeft: Int = 0,
        imageTop: Int = 0,
        contentLeft: Int = 0,
        contentTopBound: Int = 0,
        contentBottomBound: Int = image.measuredHeight.takeIf { it > 0 } ?: view.measuredHeight
    ) {
        image.layout(imageLeft, imageTop)
        layoutImageGradient()
        layoutAccessoryViews(imageLeft, imageTop)
        contentView?.let {
            val (top, bottom) = when (contentAlignment) {
                TOP -> contentTopBound to contentTopBound + it.measuredHeight
                BOTTOM -> contentBottomBound - it.measuredHeight to contentBottomBound
                CENTER -> {
                    val top = (contentTopBound + contentBottomBound - it.measuredHeight) / 2
                    top to top + it.measuredHeight
                }
            }
            it.layout(contentLeft, top, contentLeft + it.measuredWidth, bottom)
        }
    }

    private fun measureAccessoryView(view: View, @Px imageWidth: Int, @Px imageHeight: Int) {
        val width = (imageWidth - image.padding * 2).coerceAtLeast(0)
        val height = (imageHeight - image.padding * 2).coerceAtLeast(0)
        view.measure(
            createMeasureSpec(width, View.MeasureSpec.EXACTLY),
            createMeasureSpec(
                if (hasBothAccessoryViews()) height / 2 else height,
                View.MeasureSpec.EXACTLY
            )
        )
    }

    private fun getCurrentImageWidthAndHeight(): Pair<Int, Int> {
        if (image.getWidth() > 0) {
            return image.getWidth() to image.getHeight()
        }
        return getImageWidthAndHeight()
    }

    private fun getImageWidthAndHeight(): Pair<Int, Int> {
        val (imageWidth, imageHeight) = if (hasImage()) {
            val aspectRatio = getAspectRatio()
            if (aspectRatio == SbisTileViewImageRatio.SQUARE.fraction && imageSize > 0) {
                val fullImageSize = getFullImageSize()
                fullImageSize to fullImageSize
            } else when (model.alignment) {
                SbisTileViewImageAlignment.LEFT, SbisTileViewImageAlignment.RIGHT -> getWidth(
                    height,
                    aspectRatio
                ) to height
                SbisTileViewImageAlignment.TOP, SbisTileViewImageAlignment.BOTTOM -> width to getHeight(
                    width,
                    aspectRatio
                )
                SbisTileViewImageAlignment.FILL -> width to height
            }
        } else {
            0 to 0
        }
        return imageWidth to imageHeight
    }

    private fun getAspectRatio() = (model.shape as? Rectangle)?.ratio?.fraction
        ?: SbisTileViewImageRatio.DEFAULT_FRACTION

    @Px
    private fun getFullImageSize() = imageSize + 2 * image.padding

    private fun layoutAccessoryViews(imageLeft: Int, imageTop: Int) {
        val left = imageLeft + image.padding
        val top = imageTop + image.padding
        topView?.let {
            it.layout(left, top, left + it.measuredWidth, top + it.measuredHeight)
        }
        bottomView?.let {
            val bottomViewTop = if (topView?.isVisible == true) topView!!.bottom else top
            it.layout(left, bottomViewTop, left + it.measuredWidth, bottomViewTop + it.measuredHeight)
        }
    }

    private fun layoutImageGradient() {
        when (model.alignment) {
            SbisTileViewImageAlignment.LEFT -> layoutImageGradient(left = image.right - gradientHeight)
            SbisTileViewImageAlignment.RIGHT -> layoutImageGradient(right = image.left + gradientHeight)
            SbisTileViewImageAlignment.TOP, SbisTileViewImageAlignment.FILL -> {
                layoutImageGradient(top = image.bottom - gradientHeight)
            }
            SbisTileViewImageAlignment.BOTTOM -> layoutImageGradient(bottom = image.top + gradientHeight)
        }
    }

    private fun layoutImageGradient(
        left: Int = image.left,
        top: Int = image.top,
        right: Int = image.right,
        bottom: Int = image.bottom
    ) = imageGradient.setBounds(left, top, right, bottom)

    private fun updateBorderBounds() {
        val padding = image.padding.toFloat()
        imageBorderDrawer.setBounds(RectF(padding, padding, image.getWidth() - padding, image.getHeight() - padding))
    }

    private fun hasImage() = model.images.isNotEmpty()

    private fun getWidth(height: Int, aspectRatio: Float) =
        (aspectRatio * height).roundToInt()

    private fun getHeight(width: Int, aspectRatio: Float) =
        (width / aspectRatio).roundToInt()

    private fun getCornerRadii() = when (model.alignment) {
        SbisTileViewImageAlignment.LEFT -> getRadii(topLeft = cornerRadius, bottomLeft = cornerRadius)
        SbisTileViewImageAlignment.RIGHT -> getRadii(topRight = cornerRadius, bottomRight = cornerRadius)
        SbisTileViewImageAlignment.TOP -> getRadii(topLeft = cornerRadius, topRight = cornerRadius)
        SbisTileViewImageAlignment.BOTTOM -> getRadii(bottomLeft = cornerRadius, bottomRight = cornerRadius)
        SbisTileViewImageAlignment.FILL -> getRadii(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
    }

    private fun getRadii(topLeft: Float = 0f, topRight: Float = 0f, bottomRight: Float = 0f, bottomLeft: Float = 0f) =
        floatArrayOf(topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft)

    private fun hasImageGradient() = hasImage() && model.alignment != SbisTileViewImageAlignment.FILL &&
        (model.shape as? Rectangle)?.needGradient == true

    private fun updateImageGradient() {
        imageGradient = imageGradientDrawableFactory.invoke().apply {
            shaderFactory = GradientShaderFactory(
                endBackgroundColor,
                orientation = when (model.alignment) {
                    SbisTileViewImageAlignment.LEFT -> Direction.LEFT_TO_RIGHT
                    SbisTileViewImageAlignment.RIGHT -> Direction.RIGHT_TO_LEFT
                    SbisTileViewImageAlignment.TOP -> Direction.TOP_TO_BOTTOM
                    SbisTileViewImageAlignment.BOTTOM -> Direction.BOTTOM_TO_TOP
                    SbisTileViewImageAlignment.FILL -> Direction.TOP_TO_BOTTOM
                }
            )
        }
    }

    private fun updateImageTint() {
        image.configureTint(
            isImageTintEnabled = model.darkerImage || hasDefaultTint(),
            isPlaceholderTintEnabled = model.darkerPlaceholder,
            isTintUnderContent = contentAlignment == BOTTOM
        )
    }

    private fun hasDefaultTint() =
        contentView != null && model.alignment == SbisTileViewImageAlignment.FILL && model.shape is Rectangle

    private fun hasBothAccessoryViews() = topView?.isVisible == true && bottomView?.isVisible == true

    private fun getImageUrls() =
        model.images.filterIsInstance<TileViewImageUrl>().takeUnless { it.isEmpty() }?.map { it.imageUrl }

    private fun getDisplayedImages(images: List<TileViewImage>): List<TileViewImage> {
        val (validImages, emptyImages) = images.partition { it !is TileViewImageUrl || it.imageUrl.isNotBlank() }
        return validImages.plus(emptyImages.take(1)).take(4)
    }
}