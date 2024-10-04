package ru.tensor.sbis.design.counters.textcounter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.counters.R
import ru.tensor.sbis.design.counters.textcounter.utils.SbisTextCounterFormatter
import ru.tensor.sbis.design.counters.utils.Formatter
import ru.tensor.sbis.design.counters.utils.delegateNotNull
import ru.tensor.sbis.design.custom_view_tools.utils.StaticLayoutConfigurator
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth
import ru.tensor.sbis.design.theme.global_variables.BorderThickness
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Текстовый счетчик.
 *
 * [Ссылка на стандарт](http://axure.tensor.ru/MobileStandart8/#p=%D1%81%D1%87%D0%B5%D1%82%D1%87%D0%B8%D0%BA%D0%B8&g=1)
 *
 * @author da.zolotarev
 */
class SbisTextCounterDrawable(
    private val context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.SbisTextCounterStyle,
    @StyleRes defStyleRes: Int = R.style.SbisTextCounterStyle
) : Drawable() {

    private var intrinsicWidth: Int = 0
    private var intrinsicHeight: Int = 0

    private val accentedEventsTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    private val unaccentedEventsTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    @Dimension
    private var dividerHorizontalPadding = Offset.X2S.getDimen(context)

    @Dimension
    private var dividerThick = BorderThickness.S.getDimen(context)

    @Px
    private var accentedEventsTextWidth = 0

    @Px
    private var unaccentedEventsTextWidth = 0

    private var accentedEventsText = ""
    private var unaccentedEventsText = ""

    private var accentedEventsTextLayout =
        StaticLayoutConfigurator.createStaticLayout(accentedEventsText, accentedEventsTextPaint) {
            includeFontPad = false
        }
    private var unaccentedEventsTextLayout =
        StaticLayoutConfigurator.createStaticLayout(unaccentedEventsText, unaccentedEventsTextPaint) {
            includeFontPad = false
        }

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    /**
     * Акцентное число счетчика
     */
    var accentedCounter: Int = 0
        set(value) {
            if (field == value) {
                return
            }
            field = maxOf(0, value)
            updateTextLayouts()
            invalidateSelf()
        }

    /**
     * Неакцентное число счетчика
     */
    var unaccentedCounter: Int = 0
        set(value) {
            if (field == value) {
                return
            }
            field = maxOf(0, value)
            updateTextLayouts()
            invalidateSelf()
        }

    @Deprecated(message = "Используйте counterFormatter")
    var formatter: SbisTextCounterFormatter? = null
        set(value) {
            field = value
            field?.let { counterFormatter = Formatter.CustomFormatter(it) }
            invalidateSelf()
        }

    /**
     * Функция форматирования счётчика
     *
     * @see Formatter
     */
    var counterFormatter: Formatter by delegateNotNull {
        updateTextLayouts()
        invalidateSelf()
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.SbisTextCounter, defStyleAttr, defStyleRes) {
            accentedEventsTextPaint.apply {
                textSize = getDimension(
                    R.styleable.SbisTextCounter_SbisTextCounter_textSize,
                    0F
                )
                color = getColor(
                    R.styleable.SbisTextCounter_SbisTextCounter_accentedTextColor,
                    Color.MAGENTA
                )
            }

            unaccentedEventsTextPaint.apply {
                textSize = getDimension(
                    R.styleable.SbisTextCounter_SbisTextCounter_textSize,
                    0F
                )
                color = getColor(
                    R.styleable.SbisTextCounter_SbisTextCounter_unaccentedTextColor,
                    Color.MAGENTA
                )
            }

            dividerPaint.apply {
                color = getColor(
                    R.styleable.SbisTextCounter_SbisTextCounter_dividerColor,
                    Color.MAGENTA
                )
            }

            counterFormatter =
                when (val formatterCode = getInt(R.styleable.SbisTextCounter_SbisTextCounter_formatter, 0)) {
                    0 -> Formatter.InternationalFormatter(context)
                    1 -> Formatter.HundredFormatter
                    2 -> Formatter.InternationalFormatter(context)
                    3 -> Formatter.InternationalFormatter(context, false)
                    else -> error("Unsupported formatter code $formatterCode")
                }
        }
    }

    override fun draw(canvas: Canvas) {
        when {
            accentedCounter <= 0 && unaccentedCounter <= 0 -> return
            accentedCounter <= 0 -> unaccentedEventsTextLayout.draw(canvas)
            unaccentedCounter <= 0 -> accentedEventsTextLayout.draw(canvas)
            else -> drawCounterWithDivider(canvas)
        }
    }

    /**
     * Установить размер текста счетчика.
     */
    fun setCounterSize(dimen: Float) {
        accentedEventsTextPaint.apply {
            textSize = dimen
        }
        unaccentedEventsTextPaint.apply {
            textSize = dimen
        }
        updateTextLayouts()
        invalidateSelf()
    }

    /**
     * Возвращает координату базовой линии относительно счетчика
     */
    fun getBaseline() = maxOf(
        accentedEventsTextLayout.getLineBaseline(0),
        unaccentedEventsTextLayout.getLineBaseline(0)
    )

    private fun drawCounterWithDivider(canvas: Canvas) {
        accentedEventsTextLayout.draw(canvas)
        canvas.drawRect(
            accentedEventsTextWidth + dividerHorizontalPadding,
            accentedEventsTextLayout.getLineBaseline(0).toFloat() - getTextHeight(accentedEventsTextLayout),
            accentedEventsTextWidth + dividerHorizontalPadding + dividerThick,
            accentedEventsTextLayout.getLineBaseline(0).toFloat(),
            dividerPaint
        )
        val dx = accentedEventsTextWidth + dividerHorizontalPadding * 2 + dividerThick
        canvas.withTranslation(dx) {
            unaccentedEventsTextLayout.draw(canvas)
        }
    }

    private fun updateTextLayouts() {
        accentedEventsText = counterFormatter.format(accentedCounter)
        unaccentedEventsText = counterFormatter.format(unaccentedCounter)
        accentedEventsTextLayout =
            StaticLayoutConfigurator.createStaticLayout(accentedEventsText, accentedEventsTextPaint) {
                includeFontPad = false
            }
        unaccentedEventsTextLayout =
            StaticLayoutConfigurator.createStaticLayout(unaccentedEventsText, unaccentedEventsTextPaint) {
                includeFontPad = false
            }
        measure()
    }

    private fun measure() {
        accentedEventsTextWidth = accentedEventsTextPaint.getTextWidth(accentedEventsText)
        unaccentedEventsTextWidth = unaccentedEventsTextPaint.getTextWidth(unaccentedEventsText)
        intrinsicWidth = when {
            accentedCounter <= 0 && unaccentedCounter <= 0 -> 0
            accentedCounter <= 0 -> unaccentedEventsTextWidth
            unaccentedCounter <= 0 -> accentedEventsTextWidth
            else -> (accentedEventsTextWidth + unaccentedEventsTextWidth + dividerHorizontalPadding * 2 + dividerThick)
                .toInt()
        }
        intrinsicHeight = maxOf(accentedEventsTextLayout.height, unaccentedEventsTextLayout.height)
    }

    private fun getTextHeight(textLayout: StaticLayout) = Rect().also {
        textLayout.paint.getTextBounds(textLayout.text.toString(), 0, textLayout.text.length, it)
    }.height()

    override fun getIntrinsicWidth(): Int = intrinsicWidth
    override fun getIntrinsicHeight(): Int = intrinsicHeight

    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}