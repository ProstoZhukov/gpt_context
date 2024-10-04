package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.day

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.DayBackgroundDrawable
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumSelection
import ru.tensor.sbis.design.period_picker.view.utils.dayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.year
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.OtherColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import java.util.Calendar

/**
 * View для отображения дня в календаре.
 *
 * @author mb.kruglova
 */
internal class DayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.sbisPeriodPickerViewTheme,
    @StyleRes defStyleRes: Int = R.style.SbisPeriodPickerViewTheme
) : View(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
) {

    /** Layout счётчика. */
    private val counterLayout by lazy {
        TextLayout {
            paint.color = StyleColor.UNACCENTED.getTextColor(context)
            paint.textSize = context.getDimen(R.attr.DayView_counterTextSize)
            includeFontPad = false
        }
    }

    /** Layout числа дня. */
    private val numberLayout by lazy {
        TextLayout {
            paint.color = TextColor.DEFAULT.getValue(context)
            paint.textSize = context.getDimen(R.attr.DayView_textSize)
            includeFontPad = false
        }
    }

    /** Drawable пометки. */
    private val markerDrawable: Drawable? = getDrawable(R.drawable.period_picker_day_marker)

    /** Drawable выделения. */
    private var selectionDrawable: DayBackgroundDrawable? = null

    /** День месяца. */
    var dayOfMonth: Int? = null
        set(value) {
            field = value
            numberLayout.configure {
                text = field.toString()
            }
            invalidateView()
        }

    /** День недели. */
    var dayOfWeek: Int? = null
        set(value) {
            field = value
            numberLayout.configure {
                paint.color = getDayOfWeekColor(value)
            }
            invalidateView()
        }

    /** Значение счётчика. */
    var counter: String = ""
        set(value) {
            field = value
            counterLayout.configure {
                text = value
            }
            invalidateView()
        }

    /** Выделение дня. */
    var daySelection: QuantumSelection = QuantumSelection()
        set(value) {
            field = value
            selectionDrawable = DayBackgroundDrawable(context, customBackgroundColor).apply {
                quantumType = daySelection.quantumType
                drawableType = daySelection.drawableType
            }

            invalidate()
        }

    /** Тип пометки. */
    var markerType: MarkerType = MarkerType.NO_MARKER

    /** Является ли день текущим. */
    var isCurrentDay: Boolean = false
        set(value) {
            field = value
            numberLayout.configure {
                paint.color = getDayOfWeekColor(dayOfWeek)
            }
            invalidateView()
        }

    /** Попадает ли день в доступный для отображения период. */
    var isRangePart: Boolean = false

    /** Доступен ли день для взаимодействия. */
    var isAvailable: Boolean = true

    /** Цвет кастомного фона. */
    @ColorInt
    var customBackgroundColor: Int? = null

    @ColorInt
    var customDayOfWeekColor: Int? = null

    /** Полная дата дня. */
    var date: Calendar = Calendar.getInstance()

    init {
        setWillNotDraw(false)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        internalLayout()
    }

    override fun onDraw(canvas: Canvas) {
        selectionDrawable?.let {
            it.setBounds(0, 0, width, height)
            it.draw(canvas)
        }

        numberLayout.draw(canvas)

        if (counter.isEmpty()) return

        when (markerType) {
            MarkerType.DOT -> {
                val size = Offset.X2S.getDimenPx(context)
                markerDrawable?.setBounds(0, 0, size, size)
                markerDrawable?.let {
                    canvas.withTranslation(
                        (width - size.toFloat()) / 2,
                        height - ((height - numberLayout.baseline) / 4f) - size,
                        markerDrawable::draw
                    )
                }
            }

            MarkerType.COUNTER -> counterLayout.draw(canvas)
            else -> Unit
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        var month = (date.month + 1).toString()
        if (month.length == 1) {
            month = "0$month"
        }
        info.text = String.format(resources.getString(R.string.accessibility_text), date.dayOfMonth, month, date.year)
    }

    /** Вычисление позиций элементов. */
    private fun internalLayout() {
        if (markerType == MarkerType.COUNTER) {
            val counterHeight = counterLayout.textPaint.textHeight
            val offset = context.getDimenPx(R.attr.DayView_counterMargin)
            val height = (measuredHeight - offset - counterHeight - numberLayout.baseline) / 2
            numberLayout.layout(
                (measuredWidth - numberLayout.width) / 2,
                height
            )

            counterLayout.layout(
                (measuredWidth - counterLayout.width) / 2,
                measuredHeight - height - counterHeight
            )
        } else {
            numberLayout.layout(
                (measuredWidth - numberLayout.width) / 2,
                (measuredHeight - numberLayout.height) / 2
            )
        }
    }

    private fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return AppCompatResources.getDrawable(context, resId)
    }

    private fun invalidateView() {
        internalLayout()
        invalidate()
    }

    private fun getDayOfWeekColor(day: Int?) = when {
        isCurrentDay -> OtherColor.BRAND.getValue(context)
        customDayOfWeekColor != null -> customDayOfWeekColor ?: Color.MAGENTA
        !isRangePart || !isAvailable -> TextColor.READ_ONLY.getValue(context)
        day == null || day < 5 -> TextColor.DEFAULT.getValue(context)
        else -> StyleColor.UNACCENTED.getTextColor(context)
    }
}