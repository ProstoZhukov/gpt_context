package ru.tensor.sbis.calendar.date.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.data.BOTTOM
import ru.tensor.sbis.calendar.date.data.LEFT
import ru.tensor.sbis.calendar.date.data.PIXEL_BR
import ru.tensor.sbis.calendar.date.data.PIXEL_TL
import ru.tensor.sbis.calendar.date.data.RIGHT
import ru.tensor.sbis.calendar.date.data.SelectedType
import ru.tensor.sbis.calendar.date.data.TOP
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Интерфейс для отрисовки выбранного дня
 * В календаре при выборе периода выделяем дни.
 * Данный интерфейс предоставляет метод для определения способа такой отрисовки
 */
interface SelectedDayDrawable {

    /**
     * Учитывать в отрисовке флаг isHoliday
     */
    var useHolidayVacationFlag: Boolean

    /**
     * Отрисовка выбранного дня
     */
    fun onDraw(
        selectedType: SelectedType,
        canvas: Canvas,
        colorsProvider: ColorsProvider
    )
}

/**
 * Пустой отрисовщик
 */
class EmptySelectedDayDrawable : SelectedDayDrawable {
    override fun onDraw(selectedType: SelectedType, canvas: Canvas, colorsProvider: ColorsProvider) = Unit
    override var useHolidayVacationFlag: Boolean = false
}

/**
 * Отрисовка границ вокруг выбранных дней
 */
class BorderSelectedDayDrawable(
    val context: Context,
    val selectedColor: Int,
    val selectedBackgroundColor: Int
) : SelectedDayDrawable {

    override var useHolidayVacationFlag: Boolean = false

    private val hatchDrawable = HatchDrawable(context.resources).apply {
        color = context.getThemeColorInt(RDesign.attr.borderCrossVacation)
    }
    private val colorBlockPaint = Paint().apply {
        color = context.getThemeColorInt(RDesign.attr.backgroundColorHoliday)
    }

    private val margin = context.resources.getDimension(RDesign.dimen.small_delimiter_height)

    private val paint = Paint()
    private var accentLineWidth: Float =
        context.resources.getDimensionPixelSize(R.dimen.calendar_day_select_line_width).toFloat()

    override fun onDraw(
        selectedType: SelectedType,
        canvas: Canvas,
        colorsProvider: ColorsProvider
    ) {
        if (selectedType.isHoliday && useHolidayVacationFlag) {
            drawVacationOnHoliday(canvas)
        }

        if (selectedType !is SelectedType.NONE) {
            paint.color = selectedBackgroundColor
            if (!selectedType.isHoliday || !useHolidayVacationFlag) {
                canvas.drawRect(
                    margin / 2,
                    margin / 2,
                    canvas.width.toFloat() - margin / 2,
                    canvas.height.toFloat() - margin / 2,
                    paint
                )
            }

            paint.color = selectedColor
            if (selectedType.flags.and(TOP) != 0) drawTop(canvas)
            if (selectedType.flags.and(BOTTOM) != 0) drawBottom(canvas)
            if (selectedType.flags.and(LEFT) != 0) drawLeft(canvas)
            if (selectedType.flags.and(RIGHT) != 0) drawRight(canvas)

            if (selectedType.flags.and(PIXEL_TL) != 0) drawPixelTopLeft(canvas)
            if (selectedType.flags.and(PIXEL_BR) != 0) drawPixelBottomRight(canvas)
        }
    }

    private fun drawLeft(canvas: Canvas) {
        canvas.drawRect(0f, 0f, accentLineWidth, canvas.height.toFloat(), paint)
    }

    private fun drawRight(canvas: Canvas) {
        canvas.drawRect(
            canvas.width.toFloat() - accentLineWidth,
            0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            paint
        )
    }

    private fun drawTop(canvas: Canvas) {
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), accentLineWidth, paint)
    }

    private fun drawBottom(canvas: Canvas) {
        canvas.drawRect(0f, canvas.height - accentLineWidth, canvas.width.toFloat(), canvas.height.toFloat(), paint)
    }

    private fun drawPixelBottomRight(canvas: Canvas) {
        canvas.drawRect(
            canvas.width - accentLineWidth,
            canvas.height - accentLineWidth,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            paint
        )
    }

    private fun drawPixelTopLeft(canvas: Canvas) {
        canvas.drawRect(0f, 0f, accentLineWidth, accentLineWidth, paint)
    }

    private fun drawVacationOnHoliday(canvas: Canvas) {
        val border = accentLineWidth / 4
        canvas.save()
        canvas.clipRect(
            0f + border,
            0f + border,
            canvas.width - border,
            canvas.height - border
        )
        canvas.drawRect(
            0f,
            0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            colorBlockPaint
        )
        hatchDrawable.setBounds(
            0,
            0,
            canvas.width,
            canvas.height
        )
        hatchDrawable.draw(canvas)
        canvas.restore()
    }
}

/**
 * Заливка выбранных дней
 */
class FillSelectedDayDrawable(
    context: Context,
    @Deprecated("Заменить на sbisSelectedColor")
    val selectedColor: Int = BackgroundColor.DEFAULT.getValue(context),
    sbisSelectedColor: SbisColor = SbisColor.Int(selectedColor),
) : SelectedDayDrawable {

    override var useHolidayVacationFlag: Boolean = false

    @ColorInt
    private val selectedColorInt = sbisSelectedColor.getColor(context)

    private val hatchDrawable = HatchDrawable(context.resources).apply {
        color = context.getThemeColorInt(RDesign.attr.borderCrossVacation)
    }
    private val colorBlockPaint = Paint().apply {
        color = context.getThemeColorInt(RDesign.attr.backgroundColorHoliday)
    }
    private val paint = Paint()
    private var accentLineWidth: Float =
        context.resources.getDimensionPixelSize(R.dimen.calendar_day_view_lines_width).toFloat()

    override fun onDraw(
        selectedType: SelectedType,
        canvas: Canvas,
        colorsProvider: ColorsProvider
    ) {
        if (selectedType.isHoliday && useHolidayVacationFlag) {
            drawVacationOnHoliday(canvas)
        }
        val border = accentLineWidth / 2
        fun drawFirstLine() {
            canvas.drawRect(
                0f,
                0f + border / 2,
                accentLineWidth,
                canvas.height.toFloat() - border / 2,
                paint
            )
        }

        fun drawLastLine() {
            canvas.drawRect(
                canvas.width.toFloat() - accentLineWidth,
                0f + border / 2,
                canvas.width.toFloat(),
                canvas.height.toFloat() - border / 2,
                paint
            )
        }
        if (selectedType !is SelectedType.NONE) {
            paint.color = selectedColorInt
            if (!selectedType.isHoliday || !useHolidayVacationFlag) {
                canvas.drawRect(
                    0F + border / 2,
                    0F + border / 2,
                    canvas.width.toFloat() - border / 2,
                    canvas.height.toFloat() - border / 2,
                    paint
                )
            }
            paint.color = colorsProvider.colorCurrentDayBorder
            when (selectedType) {
                is SelectedType.SINGLE -> {
                    drawFirstLine()
                    drawLastLine()
                }

                is SelectedType.FIRST -> drawFirstLine()
                is SelectedType.LAST -> drawLastLine()
                else -> Unit
            }
        }
    }

    private fun drawVacationOnHoliday(canvas: Canvas) {
        val border = accentLineWidth / 4
        canvas.save()
        canvas.clipRect(
            0f + border,
            0f + border,
            canvas.width - border,
            canvas.height - border
        )
        canvas.drawRect(
            0f,
            0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            colorBlockPaint
        )
        hatchDrawable.setBounds(
            0,
            0,
            canvas.width,
            canvas.height
        )
        hatchDrawable.draw(canvas)
        canvas.restore()
    }
}

