package ru.tensor.sbis.design.profile.person

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.person.controller.PersonViewApi
import ru.tensor.sbis.design.profile.person.controller.PersonViewController
import ru.tensor.sbis.design.profile.person.controller.PersonViewControllerImpl
import ru.tensor.sbis.design.profile.person.data.DisplayMode
import ru.tensor.sbis.design.profile.person.feature.PersonViewPlugin
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.preventViewFromDoubleClickWithDelay
import ru.tensor.sbis.design.R as RDesign

/** @SelfDocumented */
internal val PERSON_VIEW_DEFAULT_CORNER_RADIUS = BorderRadius.X2S
private const val DOUBLE_CLICK_DELAY = 1000L

/**
 * View компонента "Фото персоны".
 * Предназначен для отображения одиночного фото с опциональным статусом активности. Поддерживает отображение инициалов
 * при отсутствии фото.
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=изображения_3&g=1)
 *
 * @see [PersonViewApi]
 *
 * @author us.bessonov
 */
class PersonView internal constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.personViewTheme,
    defStyleRes: Int = R.style.DesignProfilePersonViewDefaultStyle,
    private val activityStatusDrawable: ActivityStatusDrawable,
    private val controller: PersonViewController = PersonViewControllerImpl(
        PhotoSize.M.photoImageSize!!.getDimenPx(context),
        context
            .resources
            .getDimensionPixelSize(R.dimen.design_profile_person_view_photo_end_and_bottom_margin_in_toolbar),
        initialsColor = TextColor.CONTRAST.getValue(context),
        // по стандарту фон не темизируется
        backgroundColor = ContextCompat.getColor(context, RDesign.color.palette_color_white1),
        PersonViewPlugin.personActivityStatusNotifier?.get()
    )
) : View(context, attrs, defStyleAttr), PersonViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.personViewTheme,
        defStyleRes: Int = R.style.DesignProfilePersonViewDefaultStyle
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        ActivityStatusDrawable(context, attrs, ActivityStatusStyleHolder.create(context))
    )

    init {
        setWillNotDraw(false)
        controller.init(this, activityStatusDrawable)
        getContext().withStyledAttributes(
            attrs,
            R.styleable.PersonView,
            defStyleAttr,
            defStyleRes
        ) {
            val displayModeOrdinal =
                getInt(R.styleable.PersonView_PersonView_displayMode, DisplayMode.REGISTRY.ordinal)
            val sizeOrdinal = getInt(R.styleable.PersonView_PersonView_size, PhotoSize.UNSPECIFIED.ordinal)
            val clickable = getBoolean(R.styleable.PersonView_android_clickable, true)
            val withActivityStatus = getBoolean(R.styleable.PersonView_PersonView_withActivityStatus, false)
            val shapeOrdinal = getInt(R.styleable.PersonView_PersonView_shape, Shape.SUPER_ELLIPSE.ordinal)
            val cornerRadius = getDimension(R.styleable.PersonView_PersonView_cornerRadius, 0f)
                .takeIf { it > 0 }

            setSize(PhotoSize.values()[sizeOrdinal])
            controller.setDisplayMode(DisplayMode.values()[displayModeOrdinal])
            controller.setShape(Shape.values()[shapeOrdinal])
            cornerRadius?.let(::setCornerRadius)
            controller.setInitialsColor(
                getColor(
                    R.styleable.PersonView_PersonView_initialsColor,
                    TextColor.CONTRAST.getValue(context)
                )
            )
            isClickable = clickable
            setHasActivityStatus(withActivityStatus)
        }
    }

    private lateinit var previewImage: Drawable

    init {
        if (isInEditMode) {
            previewImage = ContextCompat.getDrawable(context, R.drawable.design_profile_person_placeholder)!!
            setSize(PhotoSize.M)
            controller.setPreviewActivityStatus()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        val listener = l?.let {
            preventViewFromDoubleClickWithDelay(DOUBLE_CLICK_DELAY, it::onClick)
        }
        super.setOnClickListener(listener)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo?) {
        super.onInitializeAccessibilityNodeInfo(info)
        info?.text = if (controller.personFullName.isEmpty()) {
            getActivityStatusInfo()
        } else {
            controller.nodeInfoText
        }
    }

    /**
     * @see [PersonViewController.setDisplayMode]
     */
    internal fun setDisplayMode(mode: DisplayMode) = controller.setDisplayMode(mode)

    /** @SelfDocumented */
    internal fun setCornerRadius(@Px radius: Float) {
        controller.setCornerRadius(radius)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (photoSize != PhotoSize.UNSPECIFIED) {
            super.onMeasure(
                makeMeasureSpec(paddingStart + paddingEnd + controller.photoSizePx, MeasureSpec.EXACTLY),
                makeMeasureSpec(paddingTop + paddingBottom + controller.photoSizePx, MeasureSpec.EXACTLY)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            controller.onSizeChanged(measuredWidth)
        }
        controller.onMeasured()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        controller.onSizeChanged(w - paddingStart - paddingEnd)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val statusEnd = measuredWidth - paddingEnd
        val statusBottom = measuredHeight - paddingBottom
        activityStatusDrawable.setBounds(
            statusEnd - activityStatusDrawable.intrinsicWidth,
            statusBottom - activityStatusDrawable.intrinsicHeight,
            statusEnd,
            statusBottom
        )
        controller.performLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        controller.performDraw(canvas)
        if (isInEditMode) drawPreviewCircle(canvas)
        activityStatusDrawable.draw(canvas)
    }

    override fun invalidate() {
        super.invalidate()
        controller.performInvalidate()
    }

    override fun hasOverlappingRendering(): Boolean = false

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        controller.onVisibilityAggregated(isVisible)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        controller.onViewDetachedFromWindow()
    }

    /**
     * Зарисовка скругления фотографии для preview в студии.
     */
    private fun drawPreviewCircle(canvas: Canvas) {
        previewImage.run {
            setBounds(paddingStart, paddingTop, width - paddingEnd, height - paddingBottom)
            draw(canvas)
        }
        val centerCircle = Path().apply {
            val widthWithoutPadding = width - paddingStart - paddingEnd
            val heightWithoutPadding = height - paddingTop - paddingBottom
            val imageCenterLeft = paddingStart + widthWithoutPadding / 2f
            val imageCenterTop = paddingTop + heightWithoutPadding / 2f
            val radius = minOf(widthWithoutPadding, heightWithoutPadding) / 2f
            addCircle(imageCenterLeft, imageCenterTop, radius, Path.Direction.CW)
        }
        val viewPath = Path().apply {
            addRect(
                paddingStart.toFloat(),
                paddingTop.toFloat(),
                width - paddingEnd.toFloat(),
                height - paddingBottom.toFloat(),
                Path.Direction.CW
            )
            op(centerCircle, Path.Op.DIFFERENCE)
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        canvas.drawPath(viewPath, paint)
    }

    private fun getActivityStatusInfo() = "\"Status\" : \"${controller.activityStatus.name.lowercase()}\""
}