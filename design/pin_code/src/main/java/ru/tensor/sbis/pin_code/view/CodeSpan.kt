package ru.tensor.sbis.pin_code.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px

/**
 * Специальный span для ввода кода с фиксированной шириной.
 *
 * @param bubbleColor цвет пузыря
 * @param width фиксированная ширина любого вводимого символа
 * @param bubbleRadius радиус пузырька
 * @param isPrivate происходит ли ввод кода в защищенном режиме
 *
 * @author mb.kruglova
 */
internal class CodeSpan(
    @ColorInt private val bubbleColor: Int,
    @Px private val width: Int,
    @Px private val bubbleRadius: Float,
    val isPrivate: Boolean
) : ReplacementSpan() {

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = bubbleColor
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val char = text.subSequence(start, end)[0].toString()
        // Высота цифр необходима для отрисовки кружков. Так как входные размеры метода draw() учитывают конкретный
        // символ и различаются между "баблом" и цифрой.
        val letterHeight = Rect().apply {
            paint.getTextBounds(DIGITS_STRING, 0, DIGITS_STRING.length, this)
        }.height()

        if (char == BUBBLE) {
            drawBubble(canvas, x, y.toFloat() - letterHeight / 2, bubblePaint)
        } else {
            if (isPrivate) {
                drawBubble(canvas, x, y.toFloat() - letterHeight / 2, paint)
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    // баг на 5,6-ом андроиде, может прийти некорректный [y]
                    val middle = (bottom - top) / 2f
                    canvas.drawText(text, start, end, x, middle + letterHeight / 2f, paint)
                } else {
                    canvas.drawText(text, start, end, x, y.toFloat(), paint)
                }
            }
        }
    }

    private fun drawBubble(canvas: Canvas, x: Float, y: Float, paint: Paint) =
        canvas.drawCircle(x + width / 2f, y, bubbleRadius, paint)

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ) = width

    // из за ошибки на некоторых версиях андроид размер области для отрисовки символа определяется не правильно и для
    // выравнивания кругов относительно центра цифр приходится вычислять размер цифры перед отри сковкой кружка.
    // Для этого используется константа со всеми цифрами
    private companion object {
        const val DIGITS_STRING = "1234567890"
    }
}