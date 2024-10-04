package ru.tensor.sbis.design.radio_group.item

import android.graphics.Canvas
import android.graphics.Paint
import ru.tensor.sbis.design.radio_group.control.RadioGroupStyleHolder
import ru.tensor.sbis.design.utils.delegateNotEqual

/**
 * Вспомогательный класс для отрисовки маркера радиокнопки.
 *
 * @author ps.smirnyh
 */
internal class RadioGroupItemCircleDrawer(
    private val styleHolder: RadioGroupStyleHolder
) {

    private val selectPaint: Paint by lazy {
        Paint()
    }

    private val borderPaint: Paint

    /** @SelfDocumented */
    var isSelected: Boolean by delegateNotEqual(false) { _ ->
        onStateUpdated()
    }

    /** @SelfDocumented */
    var isEnable: Boolean by delegateNotEqual(true) { value ->
        onStateUpdated()
    }

    init {
        borderPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = styleHolder.borderWidth.toFloat()
            color = getPaintColor()
        }
    }

    /** Отрисовать маркер с позиции [positionX]. */
    fun draw(canvas: Canvas, positionX: Float) = with(styleHolder) {
        canvas.drawCircle(
            positionX + defaultMarkerPadding + borderCircleRadius,
            circleTopPadding.toFloat() + borderCircleRadius.toFloat(),
            borderCircleRadius.toFloat() - borderWidth / 2,
            borderPaint
        )
        if (!isSelected) {
            return@with
        }
        canvas.drawCircle(
            positionX + defaultMarkerPadding + borderCircleRadius,
            circleTopPadding.toFloat() + borderCircleRadius.toFloat(),
            selectedCircleRadius.toFloat(),
            selectPaint
        )
    }

    private fun onStateUpdated() {
        borderPaint.color = getPaintColor()
        selectPaint.color = getPaintColor()
    }

    private fun getPaintColor(): Int =
        when {
            isEnable && isSelected -> styleHolder.selectedCircleColor
            isEnable && !isSelected -> styleHolder.unselectedCircleColor
            !isEnable && isSelected -> styleHolder.readOnlySelectedCircleColor
            else -> styleHolder.readOnlyUnselectedCircleColor
        }
}