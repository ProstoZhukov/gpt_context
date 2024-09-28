package ru.tensor.sbis.design.retail_views.tooltip

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlin.math.max
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.Position
import ru.tensor.sbis.design.theme.Position.*
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.getThemeColor

private const val ANIMATION_DURATION: Long = 150
private const val MARGIN_SCREEN_BORDER_TOOLTIP = 30
internal const val TOOLTIP_VIEW_TAG = "TOOLTIP_VIEW_TAG"

@SuppressLint("ViewConstructor")
class TooltipView internal constructor(
    context: Context,
    private val helperClickableView: View
) : FrameLayout(context.applyStyle(R.attr.retail_views_tooltip_view_theme, R.style.RetailViewsTooltipStyle_Light)) {

    private var shadowPadding = resources.getDimensionPixelSize(R.dimen.retail_views_popup_shadow_padding)
    private val arrowHeight = resources.getDimensionPixelSize(R.dimen.retail_views_popup_arrow_size)
    private val arrowWidth = resources.getDimensionPixelSize(R.dimen.retail_views_popup_arrow_size)
    private val borderStrokeWidth = 1.5f
    private var pointerPosition = Tooltip.PointerPosition.CENTER
    private var state = Tooltip.State.DEFAULT
    private var position = BOTTOM
    private var extraHorizontalOffset = 0
    private var extraVerticalOffset = 0

    @SuppressLint("InflateParams")
    private var childView: View = LayoutInflater.from(getContext()).inflate(R.layout.retail_tooltip_layout, null)
    private var closeButton: View?
    private var textView: SbisTextView?
    private var imageRight: ImageView?

    private var localViewRect: Rect? = null // координаты якоря, localViewRect.left - расстояние относительно родителя
    private var viewRect: Rect? = null // координаты якоря, viewRect.left - расстояние относительно левого края экрана
    private val distanceWithView = Offset.X2S.getDimenPx(context)
    private var shapePath: Path? = null
    private var highlightedPath: Path = Path()

    private var onDismissListener: OnDismissListener? = null

    private val shapePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = getContext().getColorFrom(R.attr.retail_views_tooltip_background_color)
            it.style = Paint.Style.FILL
            it.setShadowLayer(
                resources.getDimensionPixelSize(R.dimen.retail_views_popup_shadow_width).toFloat(),
                0f,
                0f,
                getContext().getColorFrom(R.attr.retail_views_tooltip_shadow_color)
            )
        }
    }

    private var borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = getContext().getColorFrom(R.attr.retail_views_tooltip_icon_color)
        it.style = Paint.Style.STROKE
        it.strokeWidth = borderStrokeWidth
    }

    private var highlightedBorderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = getContext().getColorFrom(R.attr.retail_views_main_green)
        it.style = Paint.Style.STROKE
        it.strokeWidth = borderStrokeWidth
    }

    init {
        tag = TOOLTIP_VIEW_TAG
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_SOFTWARE, shapePaint)

        addView(childView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        textView = childView.findViewById(R.id.retail_views_popup_text_view)
        imageRight = childView.findViewById(R.id.retail_views_popup_image_right)
        closeButton = childView.findViewById(R.id.retail_views_popup_close_button)
        closeButton?.setOnClickListener { close() }
    }

    internal fun setPosition(position: Position) {
        this.position = position
        when (position) {
            TOP -> setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + arrowHeight)
            BOTTOM -> setPadding(paddingLeft, paddingTop + arrowHeight, paddingRight, paddingBottom)
            LEFT -> setPadding(paddingLeft, paddingTop, paddingRight + arrowHeight, paddingBottom)
            RIGHT -> setPadding(paddingLeft + arrowHeight, paddingTop, paddingRight, paddingBottom)
        }
        postInvalidate()
    }

    internal fun setCustomView(view: View, @IdRes closeButtonId: Int) {
        this.removeAllViews()

        childView = view
        addView(childView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // пытаемся найти элемент для кнопки закрытия тултипа
        closeButton = childView.findViewById(closeButtonId)
        closeButton?.setOnClickListener { close() }
        postInvalidate()
    }

    internal fun setText(text: String) {
        textView?.text = text
        postInvalidate()
    }

    internal fun setRightIconRes(@DrawableRes drawableRes: Int) {
        imageRight?.setImageDrawable(ContextCompat.getDrawable(context, drawableRes))
        imageRight?.isVisible = true
        postInvalidate()
    }

    internal fun setPointerPosition(pointerPosition: Tooltip.PointerPosition) {
        this.pointerPosition = pointerPosition
        postInvalidate()
    }

    internal fun setAccentBorderColor(@AttrRes colorAttr: Int) {
        highlightedBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = context.getColorFrom(colorAttr)
            it.style = Paint.Style.STROKE
            it.strokeWidth = borderStrokeWidth
        }
        postInvalidate()
    }

    internal fun setState(state: Tooltip.State) {
        this.state = state
        val color = context.getColorFrom(
            when (state) {
                Tooltip.State.DEFAULT, Tooltip.State.HIGHLIGHTED -> R.attr.retail_views_tooltip_icon_color
                Tooltip.State.ERROR -> R.attr.retail_views_main_red
            }
        )

        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = color
            it.style = Paint.Style.STROKE
            it.strokeWidth = borderStrokeWidth
        }

        postInvalidate()
    }

    internal fun setOnTooltipDismissListener(onDismissListener: OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

    internal fun setExtraHorizontalOffset(extraOffset: Int) {
        this.extraHorizontalOffset = extraOffset

        postInvalidate()
    }

    internal fun setExtraVerticalOffset(extraOffset: Int) {
        this.extraVerticalOffset = extraOffset

        postInvalidate()
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)

        shapePath = drawPath(
            RectF(
                shadowPadding.toFloat(),
                shadowPadding.toFloat(),
                (width - shadowPadding * 2).toFloat(),
                (height - shadowPadding * 2).toFloat()
            )
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(shapePath!!, shapePaint)
        canvas.drawPath(shapePath!!, borderPaint)

        if (state == Tooltip.State.HIGHLIGHTED) {
            canvas.drawPath(highlightedPath, highlightedBorderPaint)
        }
    }

    private fun setupPosition(rect: Rect) {
        val x: Int
        val y: Int

        if (position == LEFT || position == RIGHT) {
            x = if (position == LEFT) {
                rect.left - width - distanceWithView
            } else {
                rect.right + distanceWithView
            }
            y = when (pointerPosition) {
                Tooltip.PointerPosition.START -> rect.top - 2 * arrowWidth
                Tooltip.PointerPosition.CENTER -> rect.top + (rect.height() - height) / 2
                Tooltip.PointerPosition.END -> rect.top - height + 3 * arrowWidth
            }
        } else {
            y = if (position == BOTTOM) {
                rect.bottom + distanceWithView
            } else {
                rect.top - height - distanceWithView
            }

            x = when (pointerPosition) {
                Tooltip.PointerPosition.START -> getTranslationXForStartPointerPosition()
                Tooltip.PointerPosition.CENTER -> rect.left + (rect.width() - width) / 2
                Tooltip.PointerPosition.END -> getTranslationXForEndPointerPosition()
            }
        }

        translationX = max(x.toFloat() + extraHorizontalOffset, 0F)
        translationY = y.toFloat() + extraVerticalOffset
    }

    private fun getTranslationXForStartPointerPosition(): Int {
        return parent?.let {
            val availableWidth = (parent as ViewGroup).run {
                width - paddingStart - paddingEnd
            }
            // если для Tooltip хватает места, то выравниваем его относительно левого края якоря
            return if (availableWidth - width > localViewRect!!.left) {
                localViewRect!!.left
            } // иначе смещаем влево настолько, насколько это необходимо
            else {
                availableWidth - width
            }
        } ?: 0
    }

    private fun getTranslationXForEndPointerPosition(): Int = (parent as ViewGroup).width - width

    private fun drawPath(myRect: RectF): Path {
        val path = Path()

        if (viewRect == null) {
            return path
        }

        val spacingLeft = (if (this.position == RIGHT) this.arrowHeight else 0).toFloat()
        val spacingTop = (if (this.position == BOTTOM) this.arrowHeight else 0).toFloat()
        val spacingRight = (if (this.position == LEFT) this.arrowHeight else 0).toFloat()
        val spacingBottom = (if (this.position == TOP) this.arrowHeight else 0).toFloat()

        val left = spacingLeft + myRect.left
        val top = spacingTop + myRect.top
        val right = myRect.right - spacingRight
        val bottom = myRect.bottom - spacingBottom
        val centerX = viewRect!!.centerX() - x

        var arrowSourceX = centerX
        var arrowSourceY = bottom / 2f

        when (pointerPosition) {
            Tooltip.PointerPosition.START -> {
                when (position) {
                    LEFT, RIGHT -> {
                        arrowSourceY = top + 2 * arrowWidth
                    }
                    TOP, BOTTOM -> {
                        arrowSourceX = getArrowSourceXForStartPointerPosition(left)
                    }
                }
            }

            Tooltip.PointerPosition.END -> {
                when (position) {
                    LEFT, RIGHT -> {
                        arrowSourceY = bottom - 2 * arrowWidth
                    }
                    TOP, BOTTOM -> {
                        arrowSourceX = right - 2 * arrowWidth
                    }
                }
            }
            Tooltip.PointerPosition.CENTER -> {}
        }

        highlightedPath.let {
            when (position) {
                LEFT -> {
                    it.moveTo(right, top)
                    it.lineTo(right, arrowSourceY - this.arrowWidth)
                    it.lineTo(myRect.right, arrowSourceY)
                    it.lineTo(right, arrowSourceY + this.arrowWidth)
                    it.lineTo(right, bottom)
                    it.moveTo(right, top)
                }
                RIGHT -> {
                    it.moveTo(left, top)
                    it.lineTo(left, arrowSourceY - this.arrowWidth)
                    it.lineTo(myRect.left, arrowSourceY)
                    it.lineTo(left, arrowSourceY + this.arrowWidth)
                    it.lineTo(left, bottom)
                    it.moveTo(left, top)
                }
                TOP -> {
                    it.moveTo(left, bottom)
                    it.lineTo(arrowSourceX - this.arrowWidth, bottom)
                    it.lineTo(arrowSourceX, myRect.bottom)
                    it.lineTo(arrowSourceX + this.arrowWidth, bottom)
                    it.lineTo(right, bottom)
                    it.moveTo(left, bottom)
                }
                BOTTOM -> {
                    it.moveTo(left, top)
                    it.lineTo(arrowSourceX - this.arrowWidth, top)
                    it.lineTo(arrowSourceX, myRect.top)
                    it.lineTo(arrowSourceX + this.arrowWidth, top)
                    it.lineTo(right, top)
                    it.moveTo(left, top)
                }
            }
        }

        path.moveTo(left, top)
        // LEFT, TOP

        if (position == BOTTOM) {
            path.lineTo(arrowSourceX - this.arrowWidth, top)
            path.lineTo(arrowSourceX, myRect.top)
            path.lineTo(arrowSourceX + this.arrowWidth, top)
        }
        path.lineTo(right, top)
        path.quadTo(right, top, right, top)
        // RIGHT, TOP

        if (position == LEFT) {
            path.lineTo(right, arrowSourceY - this.arrowWidth)
            path.lineTo(myRect.right, arrowSourceY)
            path.lineTo(right, arrowSourceY + this.arrowWidth)
        }
        path.lineTo(right, bottom)
        path.quadTo(right, bottom, right, bottom)
        // RIGHT, BOTTOM

        if (position == TOP) {
            path.lineTo(arrowSourceX + this.arrowWidth, bottom)
            path.lineTo(arrowSourceX, myRect.bottom)
            path.lineTo(arrowSourceX - this.arrowWidth, bottom)
        }
        path.lineTo(left, bottom)
        path.quadTo(left, bottom, left, bottom)
        // LEFT, BOTTOM

        if (position == RIGHT) {
            path.lineTo(left, arrowSourceY + this.arrowWidth)
            path.lineTo(myRect.left, arrowSourceY)
            path.lineTo(left, arrowSourceY - this.arrowWidth)
        }
        path.lineTo(left, top)
        path.quadTo(left, top, left, top)

        highlightedPath.close()
        path.close()

        return path
    }

    private fun getArrowSourceXForStartPointerPosition(left: Float): Float {
        // если Tooltip смещен влево относительно якоря, располагаем стрелку по левому краю якоря
        var arrowSourceX = if (translationX < localViewRect!!.left) {
            localViewRect!!.left.toFloat() - translationX
        } // иначе располагаем стрелку по левому краю Tooltip
        else {
            left + 2 * arrowWidth
        }
        // стрелка не должна выходить за границы самого Tooltip
        arrowSourceX = max(left + 2 * arrowWidth, arrowSourceX)
        return arrowSourceX
    }

    private fun adjustSize(rect: Rect, screenWidth: Int): Boolean {
        val r = Rect()
        getGlobalVisibleRect(r)

        var changed = false
        val layoutParams = layoutParams
        if (position == LEFT && width > rect.left) {
            layoutParams.width = rect.left - MARGIN_SCREEN_BORDER_TOOLTIP - distanceWithView
            changed = true
        } else if (position == RIGHT && rect.right + width > screenWidth) {
            layoutParams.width = screenWidth - rect.right - MARGIN_SCREEN_BORDER_TOOLTIP - distanceWithView
            changed = true
        } else if (position == TOP || position == BOTTOM) {
            var adjustedLeft = rect.left
            var adjustedRight = rect.right

            if (rect.centerX() + width / 2f > screenWidth) {
                val diff = rect.centerX() + width / 2f - screenWidth
                adjustedLeft -= diff.toInt()
                adjustedRight -= diff.toInt()
                changed = true

            } else if (rect.centerX() - width / 2f < 0) {
                val diff = -(rect.centerX() - width / 2f)
                adjustedLeft += diff.toInt()
                adjustedRight += diff.toInt()
                changed = true
            }

            if (adjustedLeft < 0) {
                adjustedLeft = 0
            }

            if (adjustedRight > screenWidth) {
                adjustedRight = screenWidth
            }

            rect.left = adjustedLeft
            rect.right = adjustedRight
        }

        setLayoutParams(layoutParams)
        postInvalidate()
        return changed
    }

    private fun onSetup(myRect: Rect) {
        setupPosition(myRect)
        shapePath = drawPath(
            RectF(
                shadowPadding.toFloat(),
                shadowPadding.toFloat(),
                width - shadowPadding * 2f,
                height - shadowPadding * 2f
            )
        )

        alpha = 0f
        animate()
            .alpha(1f)
            .setDuration(ANIMATION_DURATION)
            .setListener(null)
    }

    internal fun setup(localViewRect: Rect, viewRect: Rect, screenWidth: Int) {
        this.localViewRect = Rect(localViewRect)
        this.viewRect = Rect(viewRect)
        val myRect = Rect(viewRect)

        val changed = adjustSize(myRect, screenWidth)
        if (!changed) {
            onSetup(myRect)
        } else {
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    onSetup(myRect)
                    viewTreeObserver.removeOnPreDrawListener(this)
                    return false
                }
            })
        }
    }

    private fun close() {
        animate()
            .alpha(0f)
            .setDuration(ANIMATION_DURATION)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    forceClose()
                }
            })
    }

    internal fun forceClose() {
        if (parent != null) {
            val parent = parent as ViewGroup
            parent.removeView(helperClickableView)
            parent.removeView(this@TooltipView)
            onDismissListener?.onDismiss()
        }
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    @ColorInt
    private fun Context.getColorFrom(@AttrRes colorAttr: Int): Int {
        val resId = getThemeColor(colorAttr)
        return ContextCompat.getColor(this, resId)
    }
}