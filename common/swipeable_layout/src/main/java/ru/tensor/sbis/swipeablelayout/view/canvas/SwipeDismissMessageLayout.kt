package ru.tensor.sbis.swipeablelayout.view.canvas

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.text.Layout.Alignment.ALIGN_CENTER
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withSave
import androidx.core.view.GestureDetectorCompat
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.design.TypefaceManager.getRobotoRegularFont
import ru.tensor.sbis.design.TypefaceManager.getSbisMobileIconTypeface
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.view.canvas.SwipeCanvasLayout.CanvasClickListener
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Разметка контейнера для отображения сообщения об удалении элемента смахиванием свайп-меню.
 * Является облегченным canvas аналогом [ViewGroup] для размещения отценрированного текстов иконки и сообщения.
 *
 * @author vv.chekurda
 */
internal class SwipeDismissMessageLayout(
    context: Context, attrs: AttributeSet? = null
) : SwipeCanvasLayout {

    private val resources = context.resources
    private var parentView: ViewGroup? = null

    private var onClickListener: CanvasClickListener? = null
    private val gestureDetector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(context, gestureListener)
    }
    private val gestureListener: GestureDetector.OnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean = rect.contains(event.x.roundToInt(), event.y.roundToInt())

            override fun onSingleTapUp(event: MotionEvent): Boolean =
                rect.contains(event.x.roundToInt(), event.y.roundToInt()).also { inRect ->
                    if (inRect) onClickListener?.onClick()
                }
        }

    /**
     * Разметка текста иконки.
     */
    private var iconLayout = TextLayout {
        paint.apply {
            typeface = getSbisMobileIconTypeface(context)
            textSize = resources.getDimensionPixelSize(RDesign.dimen.context_menu_icon_text_size).toFloat()
            color = Color.TRANSPARENT
        }
        isVisibleWhenBlank = false
        alignment = ALIGN_CENTER
    }

    /**
     * Разметка текста сообщения.
     */
    private var messageLayout = TextLayout {
        paint.apply {
            typeface = getRobotoRegularFont(context)
            textSize = resources.getDimensionPixelSize(RDesign.dimen.size_caption1_scaleOff).toFloat()
            color = Color.TRANSPARENT
        }
        alignment = ALIGN_CENTER
    }

    /**
     * Получить текст сообщения.
     * Необходим для автотестов.
     */
    val messageText: CharSequence
        get() = messageLayout.text

    /**
     * Значение прозрачности текста иконки и сообщения.
     * Диапазон прозрачности [TextPaint] от 0 до 255.
     */
    @IntRange(from = 0, to = TEXT_PAINT_MAX_ALPHA.toLong())
    private var textAlpha: Int = 0
        set(value) {
            field = value
            iconLayout.textPaint.alpha = value
            messageLayout.textPaint.alpha = value
        }

    /**
     * Fade аниматор появления разметки иконки и сообщения.
     */
    private val textFadeInAnimator = ValueAnimator.ofInt(0, TEXT_PAINT_MAX_ALPHA).apply {
            interpolator = LinearInterpolator()
            addUpdateListener {
                textAlpha = it.animatedValue as Int
                invalidate()
            }
        }

    /**
     * Признак необходимости рисовать текст иконки и сообщения.
     * Используется для блокировки отрисовки до вызова команды показа при удалении.
     */
    private var needDrawingText: Boolean = false

    /**
     * Признак видимости текста иконки и сообщения.
     *
     * @property isTextVisible true, если текст должен отображаться.
     */
    private var isTextVisible: Boolean = false
        set(value) {
            field = value
            needDrawingText = value
            textAlpha = if (field) TEXT_PAINT_MAX_ALPHA else 0
        }

    private var iconMarginBottom =
        resources.getDimensionPixelSize(R.dimen.swipeable_layout_dismiss_message_icon_margin_vertical)
    private var minTotalVerticalPadding =
        resources.getDimensionPixelSize(R.dimen.swipeable_layout_dismiss_message_min_total_padding_vertical)

    private val backgroundPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.TRANSPARENT
    }
    private val rect = Rect()

    private var measuredWidth: Int = 0
    private var measuredHeight: Int = 0

    override val left: Int
        get() = rect.left
    override val top: Int
        get() = rect.top
    override val right: Int
        get() = rect.right
    override val bottom: Int
        get() = rect.bottom

    override val width: Int
        get() = measuredWidth
    override val height: Int
        get() = measuredHeight

    /** @SelfDocumented */
    var hasCustomText = false
        private set

    /** @SelfDocumented */
    var hasIcon: Boolean = false
        set(value) {
            field = value
            configureLayout()
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.SwipeableLayout) {
            iconLayout.configure { text = getString(R.styleable.SwipeableLayout_SwipeableLayout_deleteIcon) ?: EMPTY }
            messageLayout.configure {
                text = getString(R.styleable.SwipeableLayout_dismissMessage)
                    ?.also { hasCustomText = true }
                    ?: resources.getString(R.string.swipeable_layout_default_post_dismiss_message)
            }
        }
    }

    /**
     * Присоединить разметку к родительской [ViewGroup].
     */
    fun attachToParent(parent: ViewGroup) {
        parentView = parent
    }

    /**
     * Показать сообщение об удалении.
     *
     * @param fadeDurationMs период времени анимации появления текста.
     */
    fun showDismissMessage(fadeDurationMs: Long) {
        needDrawingText = true
        textFadeInAnimator.run {
            duration = fadeDurationMs
            start()
        }
    }

    /**
     * Установить текст, отображаемый после смахивания элемента.
     */
    fun setDismissMessage(dismissMessage: String) {
        val isChanged = messageLayout.configure { text = dismissMessage }
        if (isChanged) {
            configureLayout()
            internalLayout()
            invalidate()
        }
    }

    /**
     * Установить цвет текста иконки и сообщения.
     *
     * @param color цвет текста.
     */
    fun setDismissMessageTextColor(@ColorInt color: Int) {
        iconLayout.textPaint.color = color
        messageLayout.textPaint.color = color
        invalidate()
    }

    /**
     * Изменить видимость текста иконки и сообщения.
     *
     * @param isVisible true, если текст должен отображаться.
     */
    fun changeTextVisibility(isVisible: Boolean) {
        textFadeInAnimator.end()
        isTextVisible = isVisible
        internalLayout()
        invalidate()
    }

    /**
     * Построить разметку.
     *
     * @return true, если разметка изменилась.
     */
    private fun configureLayout(): Boolean {
        val isIconChanged = iconLayout.configure {
            isVisible = hasIcon
        }
        val isMessageChanged = messageLayout.configure {
            val iconHeightWithMargins = if (iconLayout.isVisible) iconLayout.height + iconMarginBottom else 0
            val availableLayoutHeight = measuredHeight - minTotalVerticalPadding
            val availableMessageHeight =
                // Если текст сообщения не помещается вместе с иконкой - не учитываем высоту иконки и скрываем ее
                if (iconHeightWithMargins + messageLayout.textPaint.textHeight <= availableLayoutHeight) {
                    iconLayout.updatePadding(bottom = if (iconLayout.isVisible) iconMarginBottom else 0)
                    availableLayoutHeight - iconHeightWithMargins
                } else {
                    iconLayout.configure { isVisible = false }
                    availableLayoutHeight
                }
            layoutWidth = measuredWidth
            maxHeight = availableMessageHeight
        }
        return isIconChanged || isMessageChanged
    }

    override fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val isChanged = width != measuredWidth || height != measuredHeight
        setMeasuredDimension(width, height)
        if (isChanged) configureLayout()
    }

    private fun setMeasuredDimension(width: Int, height: Int) {
        measuredWidth = width
        measuredHeight = height
    }

    override fun layout(left: Int, top: Int) {
        rect.set(left, top, left + measuredWidth, top + measuredHeight)
        internalLayout()
    }

    /**
     * Расположить внутреннюю разметку.
     */
    private fun internalLayout() {
        val sumHeight = iconLayout.height + messageLayout.height
        iconLayout.layout(
            left + (measuredWidth - iconLayout.width) / 2, top + (measuredHeight - sumHeight) / 2
        )
        messageLayout.layout(left, iconLayout.bottom)
    }

    override fun draw(canvas: Canvas) {
        drawBackground(canvas)
        if (needDrawingText) drawIconWithMessage(canvas)
    }

    /**
     * Нарисовать фон.
     */
    private fun drawBackground(canvas: Canvas) {
        if (backgroundPaint.color != Color.TRANSPARENT) {
            canvas.withSave { drawRect(rect, backgroundPaint) }
        }
    }

    /**
     * Нарисовать иконку и сообщение.
     */
    private fun drawIconWithMessage(canvas: Canvas) {
        iconLayout.draw(canvas)
        messageLayout.draw(canvas)
    }

    override fun setBackgroundColor(@ColorInt colorInt: Int) {
        backgroundPaint.color = colorInt
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean = gestureDetector.onTouchEvent(event)

    override fun setOnClickListener(listener: CanvasClickListener?) {
        onClickListener = listener
    }

    private fun invalidate() {
        parentView?.invalidate()
    }
}

/** Максимальное значение прозрачности для [TextPaint] */
private const val TEXT_PAINT_MAX_ALPHA = 255