package ru.tensor.sbis.design.counters.sbiscounter

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StyleableRes
import androidx.core.content.withStyledAttributes
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.counters.R
import ru.tensor.sbis.design.counters.utils.Formatter
import ru.tensor.sbis.design.counters.utils.delegateNotNull
import ru.tensor.sbis.design.theme.res.SbisDimen
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.delegateNotEqual
import kotlin.math.roundToInt

/**
 * [Drawable] реализация счётчика для использования в "плоских" view. Если счётчик нужен как
 * самостоятельная view, нужно использовать [SbisCounter].
 *
 * [Ссылка на стандарт](http://axure.tensor.ru/MobileStandart8/#p=%D1%81%D1%87%D0%B5%D1%82%D1%87%D0%B8%D0%BA%D0%B8&g=1)
 *
 * @author ma.kolpakov
 */
class SbisCounterDrawable(
    private val context: Context,
    attrs: AttributeSet? = null
) : Drawable() {

    private val textPaint = TextPaint(ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    private val disabledTextPaint = TextPaint(ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    @TestOnly
    internal var customCornerRadiusActual: Float? = null

    private var textToDraw = ""

    @Dimension
    private var textWidth = 0F

    @Dimension
    private var horizontalPadding = 0F

    private val backgroundPaint = Paint(ANTI_ALIAS_FLAG)
    private val disabledBackgroundPaint = Paint(ANTI_ALIAS_FLAG)
    private val backgroundRect = RectF()

    /**
     * Состояние счетчика (активен/не активен).
     */
    var isEnabled by delegateNotEqual(true) { _ ->
        invalidateSelf()
    }

    /** Стилизация счетчика в зависимости от места его применения. */
    var useCase: SbisCounterUseCase by delegateNotEqual(SbisCounterUseCase.REGULAR) { value ->
        onStyleUpdated()
    }

    /** Использовать основной или второстепенный цвет фона счетчика. */
    var style: SbisCounterStyle by delegateNotEqual(PrimarySbisCounterStyle) { value ->
        onStyleUpdated()
    }

    /**
     * Минимальное значение, при котором счётчик становится видимым.
     * По умолчанию 1.
     */
    var minCount: Int by delegateNotEqual(1) { value ->
        updateText(count, value, counterFormatter)
    }

    /**
     * Значение в счётчике. Если значение меньше [minCount], счётчик будет скрыт. Если
     * прежнее значение счётчика меньше [minCount], а новое больше [minCount], счётчик будет показан.
     *
     * По умолчанию счётчик скрыт, значение [minCount] - 1
     *
     * @see formatter
     */
    var count: Int by delegateNotEqual(minCount - 1) { value ->
        updateText(value, minCount, counterFormatter)
    }

    /**
     * Форматированное значение счётчика
     */
    val countText: String
        get() = textToDraw

    @Deprecated(message = "Используйте counterFormatter")
    var formatter: SbisCounterFormatter? = null
        set(value) {
            field = value
            field?.let { counterFormatter = Formatter.CustomFormatter(it) }
            updateText(count, minCount, counterFormatter)
        }

    /**
     * Функция форматирования счётчика
     *
     * @see Formatter
     */
    var counterFormatter: Formatter by delegateNotNull { formatter ->
        updateText(count, minCount, formatter)
    }

    var customBorderRadius: SbisDimen? by delegateNotEqual(null) { value ->
        customCornerRadiusActual = value?.getDimen(context)
        invalidateSelf()
    }

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.SbisCounter,
            style.getAttrRes(useCase),
            style.getStyleRes(useCase)
        ) {
            loadStyle(this)
        }
    }

    /**
     * Устанавливает счётчик и оповещает, если нужно пересчитать размер
     */
    fun setCount(count: Int): Boolean {
        val oldWidth = backgroundRect.width()
        this.count = count
        return oldWidth != backgroundRect.width()
    }

    override fun draw(canvas: Canvas) {
        if (textToDraw.isEmpty()) {
            return
        }
        if (isEnabled) {
            drawCounter(canvas, backgroundPaint, textPaint)
        } else {
            drawCounter(canvas, disabledBackgroundPaint, disabledTextPaint)
        }
    }

    override fun getIntrinsicWidth(): Int =
        backgroundRect.width().roundToInt()

    override fun getIntrinsicHeight(): Int =
        backgroundRect.height().roundToInt()

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    private fun drawCounter(canvas: Canvas, curBackgroundPaint: Paint, curTextPaint: TextPaint) {
        val r = customCornerRadiusActual ?: (backgroundRect.height() / 2F)
        canvas.drawRoundRect(backgroundRect, r, r, curBackgroundPaint)
        val textBounds = Rect()
        textPaint.getTextBounds(textToDraw, 0, textToDraw.length, textBounds)
        canvas.drawText(
            textToDraw,
            (backgroundRect.width() - textWidth) / 2F,
            (backgroundRect.bottom / 2) - ((textPaint.descent() + textPaint.ascent()) / 2),
            curTextPaint
        )
    }

    private fun updateText(count: Int, minCount: Int, formatter: Formatter) {
        val oldText = textToDraw
        textToDraw = if (count >= minCount) formatter.format(count) else ""
        textWidth = textPaint.measureText(textToDraw)
        if (textToDraw.isEmpty()) {
            backgroundRect.right = 0F
        } else if (oldText != textToDraw) {
            backgroundRect.right = (textWidth + horizontalPadding * 2F)
                .coerceAtLeast(backgroundRect.bottom)
        }
        invalidateSelf()
    }

    private fun onStyleUpdated() {
        if (isEnabled) {
            val style = ThemeContextBuilder(
                context,
                style.getAttrRes(useCase),
                style.getStyleRes(useCase)
            ).buildThemeRes()
            context.withStyledAttributes(style, R.styleable.SbisCounter) { loadStyle(this) }
            invalidateSelf()
        }
    }

    private fun loadStyle(arr: TypedArray) {
        horizontalPadding = arr.getDimension(R.styleable.SbisCounter_SbisCounter_paddingHorizontal, horizontalPadding)
        isEnabled = arr.getBoolean(R.styleable.SbisCounter_SbisCounter_isEnabled, true)
        val textSize = arr.getDimension(
            R.styleable.SbisCounter_SbisCounter_textSize,
            0F
        )

        backgroundRect.bottom = textSize + 2 * arr.getDimension(
            R.styleable.SbisCounter_SbisCounter_paddingVertical,
            0F
        )

        arr.getColorIfIdentifyAttrType(R.styleable.SbisCounter_SbisCounter_backgroundColor, BLACK) {
            backgroundPaint.color = it
        }
        textPaint.apply {
            arr.getColorIfIdentifyAttrType(R.styleable.SbisCounter_SbisCounter_textColor, WHITE) { color = it }
            this.textSize = textSize
        }
        arr.getColorIfIdentifyAttrType(R.styleable.SbisCounter_SbisCounter_backgroundDisabledColor, BLACK) {
            disabledBackgroundPaint.color = it
        }
        disabledTextPaint.apply {
            arr.getColorIfIdentifyAttrType(R.styleable.SbisCounter_SbisCounter_textDisabledColor, WHITE) { color = it }
            this.textSize = textSize
        }
        // установку параметров важно выполнять после загрузки стилей
        counterFormatter =
            when (
                val formatterCode =
                    arr.getInt(R.styleable.SbisCounter_SbisCounter_formatter, 0)
            ) {
                0 -> Formatter.InternationalFormatter(context)
                1 -> Formatter.HundredFormatter
                2 -> Formatter.InternationalFormatter(context)
                3 -> Formatter.InternationalFormatter(context, false)
                else -> error("Unsupported formatter code $formatterCode")
            }
        minCount = arr.getInteger(R.styleable.SbisCounter_SbisCounter_minCount, minCount)
    }

    /**
     * Возвращает цвет, если смог определить тип атрибута, иначе вернет [defValue]
     * Ошибка - https://dev.sbis.ru/opendoc.html?guid=9f1621e0-9306-49b9-ab26-b8a7bba03534&client=3
     */
    private fun TypedArray.getColorIfIdentifyAttrType(
        @StyleableRes attr: Int,
        @ColorInt defValue: Int,
        lambda: (Int) -> Unit
    ) {
        if (getType(attr) != TypedValue.TYPE_ATTRIBUTE) {
            lambda(
                getColor(attr, defValue)
            )
        } else {
            lambda(defValue)
        }
    }
}