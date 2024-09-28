package ru.tensor.sbis.design_tile_view.view

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.theme.VerticalAlignment
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.design_tile_view.R
import ru.tensor.sbis.design_tile_view.SbisTileViewImageModel
import ru.tensor.sbis.design_tile_view.SbisTileViewImageModel.Companion.DEFAULT_PLACEHOLDER
import ru.tensor.sbis.design_tile_view.controller.SbisTileViewController
import ru.tensor.sbis.design_tile_view.controller.SbisTileViewControllerImpl

/**
 * View компонента "Плитка".
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/настраиваемый_шаблон_плитки.html)
 *
 * @author us.bessonov
 */
class SbisTileView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: SbisTileViewController
) : ViewGroup(context, attrs, defStyleAttr), SbisTileViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.sbisTileViewTheme,
        @StyleRes defStyleRes: Int = 0,
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisTileViewControllerImpl())

    private val image = TileImageView(context, controller)

    @IdRes
    private var contentId = NO_ID

    @IdRes
    private var topViewId = NO_ID

    @IdRes
    private var bottomViewId = NO_ID

    init {
        setWillNotDraw(false)
        var imageSize = 0
        var imagePadding = 0
        var isBorderEnabled = false
        var isBorderUnderContent = false
        context.withStyledAttributes(attrs, R.styleable.SbisTileView, defStyleAttr, defStyleRes) {
            initPlaceholder()
            contentId = getResourceId(R.styleable.SbisTileView_SbisTileView_contentId, NO_ID)
            topViewId = getResourceId(R.styleable.SbisTileView_SbisTileView_topViewId, NO_ID)
            bottomViewId = getResourceId(R.styleable.SbisTileView_SbisTileView_bottomViewId, NO_ID)
            imageSize = getDimensionPixelSize(R.styleable.SbisTileView_SbisTileView_imageSize, 0)
            imagePadding = getDimensionPixelSize(R.styleable.SbisTileView_SbisTileView_imagePadding, 0)
            isBorderEnabled = getBoolean(R.styleable.SbisTileView_SbisTileView_enableImageBorder, isBorderEnabled)
            isBorderUnderContent =
                getBoolean(R.styleable.SbisTileView_SbisTileView_drawImageBorderUnderContent, isBorderUnderContent)
            getDimensionPixelSize(R.styleable.SbisTileView_SbisTileView_placeholderSize, 0)
                .takeIf { it > 0f }
                ?.let(image::setPlaceholderSize)
        }

        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, controller.cornerRadius)
            }
        }
        controller.init(this, image, imageSize, imagePadding, isBorderEnabled, isBorderUnderContent)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        var contentView: View? = null
        var topView: View? = null
        var bottomView: View? = null
        if (contentId != NO_ID) {
            contentView = checkNotNullSafe(findViewById(contentId)) {
                "Cannot find content view with specified id"
            }
        }
        if (topViewId != NO_ID) {
            topView = checkNotNullSafe(findViewById(topViewId)) {
                "Cannot find top view with specified id"
            }
        }
        if (bottomViewId != NO_ID) {
            bottomView = checkNotNullSafe(findViewById(bottomViewId)) {
                "Cannot find bottom view with specified id"
            }
        }
        controller.initOverlayViews(contentView, topView, bottomView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        controller.performMeasure(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(controller.width, controller.height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        controller.performLayout()
    }

    override fun invalidate() {
        controller.performInvalidate()
        super.invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) = Unit

    override fun onDraw(canvas: Canvas) {
        controller.performDraw(canvas)
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        controller.onVisibilityAggregated(isVisible)
    }

    override fun addOnAttachStateChangeListener(listener: OnAttachStateChangeListener) =
        super.addOnAttachStateChangeListener(listener)

    private fun TypedArray.initPlaceholder() {
        var placeholderSet = false
        try {
            getDrawable(R.styleable.SbisTileView_SbisTileView_placeholder)
                ?.let {
                    image.setPlaceholder(it)
                    placeholderSet = true
                }
        } catch (e: Resources.NotFoundException) {
            getString(R.styleable.SbisTileView_SbisTileView_placeholder)
                ?.let {
                    val placeholderBackground =
                        getColor(R.styleable.SbisTileView_SbisTileView_placeholderBackground, Color.TRANSPARENT)
                    image.setPlaceholder(it, placeholderBackground)
                    placeholderSet = true
                }
        }
        if (!placeholderSet) {
            image.setPlaceholder(DEFAULT_PLACEHOLDER.character.toString())
        }
    }

    companion object {

        /**
         * Создаёт стандартный [View] содержимого, отображаемого на плитке
         *
         * @param title заголовок
         * @param description текст
         * @param titleColor цвет заголовка
         * @param descriptionColor цвет текста
         * @param customContentStyle стиль, который требуется использовать вместо [R.style.SbisTileContentViewTheme]
         */
        fun createDefaultContentView(
            context: Context,
            title: String? = null,
            description: String? = null,
            @ColorInt titleColor: Int? = null,
            @ColorInt descriptionColor: Int? = null,
            @StyleRes customContentStyle: Int? = null
        ): View {
            val view = customContentStyle?.let {
                SbisTileContentView(context, defStyleAttr = ResourcesCompat.ID_NULL, defStyleRes = customContentStyle)
            } ?: SbisTileContentView(context)
            return view.apply {
                setTitle(title)
                setDescription(description)
                setTitleColor(titleColor)
                setDescriptionColor(descriptionColor)
            }
        }
    }
}

/**
 * Используется для создания и упрощённой конфигурации [View] плитки
 *
 * @param imageModel модель изображения
 * @param contentAlignment расположение содержимого
 * @param cornerRadius радиус скругления углов плитки
 * @param needSetupShadow наличие тени у плитки
 * @param startBackgroundColor начальный цвет фона у градиента, либо цвет однотонного фона
 * @param endBackgroundColor конечный цвет фона у градиента
 * @param topView контейнер с содержимым, отображаемым поверх изображения, в его верхней части
 * @param bottomView контейнер с содержимым, отображаемым поверх изображения, в его нижней части
 *
 * @see [SbisTileView.createDefaultContentView]
 */
fun SbisTileView.configureDefault(
    imageModel: SbisTileViewImageModel,
    title: String? = null,
    description: String? = null,
    @ColorInt titleColor: Int? = null,
    @ColorInt descriptionColor: Int? = null,
    @StyleRes customContentStyle: Int? = null,
    contentAlignment: VerticalAlignment = VerticalAlignment.BOTTOM,
    cornerRadius: Float = 0f,
    needSetupShadow: Boolean = false,
    @ColorInt startBackgroundColor: Int = Color.WHITE,
    @ColorInt endBackgroundColor: Int = startBackgroundColor,
    topView: View? = null,
    bottomView: View? = null
) {
    setImageModel(imageModel)
    setContentView(
        SbisTileView.createDefaultContentView(
            context,
            title,
            description,
            titleColor,
            descriptionColor,
            customContentStyle
        )
    )
    setContentAlignment(contentAlignment)
    setCornerRadius(cornerRadius)
    setNeedSetupShadow(needSetupShadow)
    setStartBackgroundColor(startBackgroundColor)
    setEndBackgroundColor(endBackgroundColor)
    topView?.let(::setTopView)
    bottomView?.let(::setBottomView)
}