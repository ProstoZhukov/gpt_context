/**
 * Вспомогательные проверки для тестирования TextLayout.
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.design.custom_view_tools.text_layout

import android.text.TextPaint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.TextLayoutParams

/**
 * Неполная проверка равенства двух [TextLayoutParams] из-за TextLayout() != TextLayout().
 */
fun assertParamsEquals(expected: TextLayoutParams, actual: TextLayoutParams) {
    actual.paint.also {
        actual.paint = expected.paint
        assertEquals(expected, actual)
        actual.paint = it

        assertTextPaintEquals(expected.paint, actual.paint)
    }
}

/**
 * Кастомная проверка равенства двух [TextPaint].
 */
fun assertTextPaintEquals(expected: TextPaint, actual: TextPaint) {
    assertEquals(expected.color, actual.color)
    assertEquals(expected.textSize, actual.textSize)
    assertEquals(expected.typeface, actual.typeface)
}

/**
 * Проверка неравенства двух [TextLayoutParams] без учета [TextLayoutParams.paint],
 * т.к. TextLayout() != TextLayout().
 */
fun assertNotEqualsExcludingPaint(unexpected: TextLayoutParams, actual: TextLayoutParams) {
    actual.paint.also {
        actual.paint = unexpected.paint
        assertNotEquals(unexpected, actual)
        actual.paint = it
    }
}

/**
 * Проверка на отсутствие кэша [TextLayout.drawingLayout] со статичной разметкой при выполненнии [invoke].
 */
fun assertLayoutNotCreated(layout: TextLayout, invoke: TextLayout.() -> Any) {
    assertNull(layout.apply { invoke() }.stateSnapshot.layout)
}

/**
 * Проверка на наличие кэша [TextLayout.drawingLayout] со статичной разметкой при выполненнии [invoke].
 */
fun assertLayoutCreated(layout: TextLayout, invoke: TextLayout.() -> Any) {
    assertNotNull(layout.apply { invoke() }.stateSnapshot.layout)
}