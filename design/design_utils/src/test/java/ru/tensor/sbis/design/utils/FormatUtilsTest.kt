package ru.tensor.sbis.design.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Тест проверки сценариев форматирования числа(счетчика) в строковое значение.
 */
@RunWith(JUnit4::class)
class FormatUtilsTest {

    @Test
    fun `Если 0 возвращает пустую строку`() {
        assertEquals("", formatCount(0))
    }

    @Test
    fun `Если меньше 0 возвращает пустую строку`() {
        assertEquals("", formatCount(-1))
    }

    @Test
    fun `Если больше 0, но меньше 1000, возвращает строковое представление числа`() {
        assertEquals("999", formatCount(999))
    }

    @Test
    fun `Если 1000 возвращает 1K`() {
        assertEquals("1K", formatCount(1000))
    }

    @Test
    fun `Если 2000, возвращает 2K`() {
        assertEquals("2K", formatCount(2000))
    }

    @Test
    fun `Если 1300, то возвращает 1точка3K`() {
        assertEquals("1.3K", formatCount(1303))
    }

    @Test
    fun `Если 9999, то возвращает 9точка9K`() {
        assertEquals("9.9K", formatCount(9999))
    }

    @Test
    fun `Если 10200, то возвращает 10K`() {
        assertEquals("10K", formatCount(10200))
    }

    @Test
    fun `Если 10000, то возвращает 10K`() {
        assertEquals("10K", formatCount(10000))
    }

    @Test
    fun `Если 11100, то возвращает 11K`() {
        assertEquals("11K", formatCount(11100))
    }

    @Test
    fun `Если 100000, то возвращает 99K`() {
        assertEquals("99K", formatCount(100000))
    }

    @Test
    fun `Если больше 100000, то возвращает 99K`() {
        assertEquals("99K", formatCount(100500))
    }
}