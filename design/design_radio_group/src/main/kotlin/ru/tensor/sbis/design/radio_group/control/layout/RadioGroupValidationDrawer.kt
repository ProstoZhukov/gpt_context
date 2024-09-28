package ru.tensor.sbis.design.radio_group.control.layout

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import ru.tensor.sbis.design.radio_group.control.RadioGroupStyleHolder
import ru.tensor.sbis.design.radio_group.control.api.SbisRadioGroupViewApi
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupValidationStatus

/**
 * Вспомогательный класс для отрисовки рамки валидации.
 *
 * @author ps.smirnyh
 */
internal class RadioGroupValidationDrawer(
    private val styleHolder: RadioGroupStyleHolder
) {

    private val validationRect = RectF()
    private val validationPaint: Paint by lazy {
        Paint().apply {
            color = styleHolder.validationBorderColor
            strokeWidth = styleHolder.validationBorderWidth.toFloat()
            style = Paint.Style.STROKE
        }
    }

    /** @see SbisRadioGroupViewApi.validationStatus */
    var validationStatus = SbisRadioGroupValidationStatus.VALID

    /** Отрисовать валидацию при [validationStatus] равному [SbisRadioGroupValidationStatus.INVALID]. */
    fun draw(canvas: Canvas) {
        if (validationStatus == SbisRadioGroupValidationStatus.VALID) {
            return
        }
        canvas.drawRoundRect(
            validationRect,
            styleHolder.validationBorderRadius.toFloat(),
            styleHolder.validationBorderRadius.toFloat(),
            validationPaint
        )
    }

    /** Обновить размеры рамки валидации по переданным размерам. */
    fun updateValidationRectSize(width: Float, height: Float) = with(validationRect) {
        val validationWidth = styleHolder.validationBorderWidth.toFloat()
        right = width - validationWidth
        bottom = height - validationWidth
        top = validationWidth
        left = validationWidth
    }

    /**
     * Получить текущий размер отступа валидации, который нужно учесть в размерах view.
     * При [validationStatus] равному [SbisRadioGroupValidationStatus.VALID] будет возвращено нулевое значение.
     */
    fun getValidationOffset() =
        if (validationStatus == SbisRadioGroupValidationStatus.VALID) {
            0
        } else {
            styleHolder.validationPadding
        }
}