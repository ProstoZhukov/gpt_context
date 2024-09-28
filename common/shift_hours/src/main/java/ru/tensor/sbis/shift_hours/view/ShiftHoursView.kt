package ru.tensor.sbis.shift_hours.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.core.content.withStyledAttributes
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.BorderColor
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.utils.getThemeBoolean
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.shift_hours.R
import ru.tensor.sbis.shift_hours.data.EventTimeBlock
import ru.tensor.sbis.shift_hours.data.ShiftType
import ru.tensor.sbis.shift_hours.data.WorkingShift
import ru.tensor.sbis.shift_hours.databinding.ShiftHoursViewBinding
import ru.tensor.sbis.design.R as RDesign

/**
 * Вью для отображения смен с рабочим временем или с типом дня (отпуск, больничный, etc.)
 * При отображении рабочего времени может отрисовывать дневную смену (с текстом посередине),
 * утреннюю смену (с обрезанным текстом с левой стороны) и вечернюю смену (с обрезанным
 * текстом с правой стороны), а также позволяет комбинировать отображение всех трех смен.
 *
 * @author im.zheglov
 */
open class ShiftHoursView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    /**
     * Размер текста виджета
     */
    var textSize: Float = 0f
        set(value) {
            field = value
            binding.shiftLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            binding.shiftFirst.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            binding.shiftSecond.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }
    private val borderWidth = resources.getDimension(R.dimen.shift_hours_border_width)

    private val iconDrawable = IconicsDrawable(context).apply {
        sizeRes(RDesign.dimen.size_title1_scaleOff)
    }

    private val borderPaint = Paint().apply {
        strokeWidth = borderWidth
        color = if (context.getThemeBoolean(RDesign.attr.isDark)) {
            BorderColor.DEFAULT.getValue(context)
        } else {
            BackgroundColor.CONTRAST.getValue(context)
        }
        style = Paint.Style.STROKE
    }
    private val timeBlocksPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = borderWidth
        style = Paint.Style.FILL_AND_STROKE
    }
    private val defaultBackgroundColor = context.getThemeColorInt(RDesign.attr.backgroundColorWorkingDay)
    private val defaultTimeBlockColor = context.getThemeColorInt(RDesign.attr.calendarActivityWorkTimeBlock)
    private val defaultTextColor = context.getThemeColorInt(RDesign.attr.calendarScheduleEventTextColor)

    /**
     * Цвет для части текста смены если смена заканчивается (начинается) в другом дне
     */
    private val defaultTextColorForTimeInOtherDay = StyleColor.UNACCENTED.getTextColor(context)

    var workingShift: WorkingShift = WorkingShift()
        @MainThread
        set(value) {
            field = value
            resetLabelsVisibility()
            val shiftTextColor = value.shiftTextColor ?: defaultTextColor
            if (!value.shiftDayTypeIcon.isNullOrEmpty()) {
                iconDrawable.apply {
                    iconText(value.shiftDayTypeIcon)
                    color(value.shiftTextColor ?: defaultTextColor)
                    typeface(TypefaceManager.getSbisMobileIconTypeface(context))
                }
                binding.shiftLabel.setCompoundDrawables(iconDrawable, null, null, null)
            } else {
                binding.shiftLabel.setCompoundDrawables(null, null, null, null)
            }
            if (!workingShift.shiftDayTypeTitle.isNullOrEmpty())
                binding.shiftLabel.apply {
                    isVisible = true
                    text = " ${workingShift.shiftDayTypeTitle!!}"
                    setTextColor(shiftTextColor)
                }
            else {
                val blockNeedDrawTime = workingShift.eventTimeBlocks.filter { it.needDrawTime }
                val first = blockNeedDrawTime.getOrNull(0)
                val second = blockNeedDrawTime.getOrNull(1)

                first?.let {
                    setLabelData(
                        labelTimeView = binding.shiftFirst,
                        eventTimeBlock = it
                    )
                }
                second?.let {
                    setLabelData(
                        labelTimeView = binding.shiftSecond,
                        eventTimeBlock = it
                    )
                    with(binding) {
                        shiftSecond.doOnNextLayout { shift ->
                            if (getStartPositionOfText(shift, it) < (shiftFirst.x + shiftFirst.width))
                                shift.x = shiftFirst.x + shiftFirst.width + root.paddingStart / 2
                        }
                    }
                }
            }
        }

    private val binding: ShiftHoursViewBinding

    init {
        setWillNotDraw(false)
        binding = ShiftHoursViewBinding.inflate(LayoutInflater.from(context), this, true)
        resetLabelsVisibility()
        if (isInEditMode) {
            workingShift.apply {
                eventTimeBlocks = arrayListOf(
                    EventTimeBlock(
                        timeStart = "9:00",
                        timeEnd = "18:00",
                        needDrawTime = true,
                        columnStart = 10,
                        columnEnd = 90
                    ),
                    EventTimeBlock(
                        timeStart = "10:00",
                        timeEnd = "12:00",
                        needDrawTime = false,
                        columnStart = 20,
                        columnEnd = 40
                    )
                )
            }
        }
        getContext().withStyledAttributes(attrs, R.styleable.ShiftHoursView) {
            textSize = getDimension(
                R.styleable.ShiftHoursView_android_textSize,
                FontSize.M.getScaleOffDimen(context)
            )
        }
    }

    /**
     * Определить положение текста периода активности на вью
     * Текст периода активности выровнен по центру ячейки рабочего дня, который задается в EventTimeBlock
     * Для этого:
     * 1. Определяем центр блока смены через columnEnd и columnStart
     * 2. С учётом отступов определяем начало текста (на половину левее центра блока смены)
     * 3. Проверяем, что не вышли за границу экрана x ∈ [padding, width - textView.width- padding]
     *
     * Результат:
     *
     * |-----|  окраска смены |-------------------|
     * |-----|  8:00 - 17:00  |-------------------|
     */
    private fun getStartPositionOfText(textView: View, eventTimeBlock: EventTimeBlock): Float {
        val x = (((eventTimeBlock.columnEnd ?: 0) + (eventTimeBlock.columnStart
            ?: 0)) / 2F) / workingShift.totalColumns * width - (textView.width + binding.root.paddingStart * 2) / 2F + binding.root.paddingStart
        return minOf(
            maxOf(binding.root.paddingStart.toFloat(), x),
            width - textView.width - binding.root.paddingStart.toFloat()
        )
    }

    private fun resetLabelsVisibility() {
        binding.shiftFirst.isVisible = false
        binding.shiftSecond.isVisible = false
        binding.shiftLabel.isVisible = false
    }

    /**
     * Установить текст и цвет текста смены
     *
     * Если это ночная смена, то покрасим конец смены (или начало) из другого дня в цвет defaultTextColorForTimeInNextDay
     */
    private fun setLabelData(
        labelTimeView: TextView,
        eventTimeBlock: EventTimeBlock
    ) {
        val timeStart = eventTimeBlock.timeStart ?: ""
        val timeEnd = eventTimeBlock.timeEnd ?: ""
        val labelText = "$timeStart - $timeEnd"
        val textColor = eventTimeBlock.textColor ?: defaultTextColor

        if (eventTimeBlock.shiftType == ShiftType.NORMAL) {
            labelTimeView.apply {
                isVisible = true
                text = labelText
                setTextColor(textColor)
            }
        } else {
            val spannable = SpannableStringBuilder(labelText)
            spannable.setSpan(
                ForegroundColorSpan(if (eventTimeBlock.shiftType == ShiftType.NIGHT) textColor else defaultTextColorForTimeInOtherDay),
                labelText.indexOf(timeStart),
                labelText.indexOf(timeStart) + timeStart.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(if (eventTimeBlock.shiftType == ShiftType.MORNING) textColor else defaultTextColorForTimeInOtherDay),
                labelText.indexOf(timeEnd),
                labelText.indexOf(timeEnd) + timeEnd.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE
            )
            labelTimeView.apply {
                isVisible = true
                text = spannable
                setTextColor(textColor)
            }
        }
        labelTimeView.doOnNextLayout {  shift ->
            shift.x = getStartPositionOfText(shift, eventTimeBlock)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(workingShift.shiftBackgroundColor ?: defaultBackgroundColor)
        if (workingShift.needDrawTimeBlocks) {
            workingShift.eventTimeBlocks.forEach { timeBlock ->
                if (!timeBlock.columnStartEqualsEnd()) drawTimeBlock(canvas, timeBlock)
                timeBlock.backgroundDrawable?.run {
                    setBounds(
                        mapBlockBorder(
                            value = timeBlock.columnStart?.toFloat() ?: 0f,
                            oldRightBorder = workingShift.totalColumns.toFloat(),
                            newRightBorder = width.toFloat() - borderWidth
                        ).toInt(),
                        0,
                        mapBlockBorder(
                            value = (timeBlock.columnEnd ?: workingShift.totalColumns).toFloat(),
                            oldRightBorder = workingShift.totalColumns.toFloat(),
                            newRightBorder = width.toFloat() - borderWidth
                        ).toInt(),
                        height
                    )
                    this.draw(canvas)
                }
            }
            workingShift.eventTimeBlocks.forEach { timeBlock ->
                // Если время начала блока равно времени его окончания, то такой блок будем рисовать тонкой линией
                if (timeBlock.columnStartEqualsEnd()) drawWithThinLine(canvas, timeBlock)
            }
        }
        val halfBorderPaintWidth = borderPaint.strokeWidth / 2f
        if (workingShift.shiftBackgroundColor == defaultBackgroundColor) {
            canvas.drawRect(
                halfBorderPaintWidth,
                halfBorderPaintWidth,
                width - halfBorderPaintWidth,
                height - halfBorderPaintWidth,
                borderPaint
            )
        }
        super.onDraw(canvas)
    }

    private fun mapBlockBorder(value: Float, oldRightBorder: Float, newRightBorder: Float): Float {
        return borderWidth + value * newRightBorder / oldRightBorder
    }

    private fun drawWithThinLine(canvas: Canvas, timeBlock: EventTimeBlock) {
        timeBlocksPaint.strokeWidth = borderWidth
        timeBlocksPaint.style = Paint.Style.STROKE
        timeBlocksPaint.color = timeBlock.backgroundColor ?: defaultTimeBlockColor
        val xLineCoordinate = mapBlockBorder(
            timeBlock.columnStart?.toFloat() ?: 0f,
            workingShift.totalColumns.toFloat(),
            width.toFloat() - borderWidth
        ).coerceIn(borderWidth * 1.5f, width - borderWidth * 1.5f)
        canvas.drawLine(
            xLineCoordinate,
            0f,
            xLineCoordinate,
            height.toFloat(),
            timeBlocksPaint
        )
    }

    protected fun drawTimeBlock(
        canvas: Canvas,
        timeBlock: EventTimeBlock,
        isUnwantedVacationDay: Boolean = false
    ) {
        val paddingVertical = if (isUnwantedVacationDay) borderWidth else 0F
        timeBlocksPaint.strokeWidth = 0f
        timeBlocksPaint.style = Paint.Style.FILL_AND_STROKE
        timeBlocksPaint.color = timeBlock.backgroundColor ?: defaultTimeBlockColor
        val timeBlockStart =  mapBlockBorder(
            value = timeBlock.columnStart?.toFloat() ?: 0f,
            oldRightBorder = workingShift.totalColumns.toFloat(),
            newRightBorder = width.toFloat() - borderWidth
        )
        val timeBlockTop = 0f + paddingVertical
        val timeBlockEnd = mapBlockBorder(
            value = (timeBlock.columnEnd ?: workingShift.totalColumns).toFloat(),
            oldRightBorder = workingShift.totalColumns.toFloat(),
            newRightBorder = width.toFloat() - borderWidth
        )
        val timeBlockBottom = height.toFloat() - paddingVertical
        canvas.drawRect(
            timeBlockStart,
            timeBlockTop,
            timeBlockEnd,
            timeBlockBottom,
            timeBlocksPaint
        )
        // в темном режиме для блоков необходимо рисовать границы слева и справа
        if (context.getThemeBoolean(RDesign.attr.isDark)) {
            timeBlocksPaint.strokeWidth = borderWidth
            timeBlocksPaint.style = Paint.Style.STROKE
            timeBlocksPaint.color = BackgroundColor.CONTRAST.getValue(context)
            canvas.drawLine(
                timeBlockStart,
                0f,
                timeBlockStart,
                height.toFloat(),
                timeBlocksPaint
            )
            canvas.drawLine(
                timeBlockEnd,
                0f,
                timeBlockEnd,
                height.toFloat(),
                timeBlocksPaint
            )
        }
    }
}