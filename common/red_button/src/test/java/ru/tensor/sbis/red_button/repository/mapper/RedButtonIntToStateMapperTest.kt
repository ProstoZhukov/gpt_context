package ru.tensor.sbis.red_button.repository.mapper

import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.red_button.data.RedButtonState

/**
 * @author ra.stepanov
 */
@RunWith(JUnitParamsRunner::class)
class RedButtonIntToStateMapperTest {

    private val mapper = RedButtonIntToStateMapper()

    @Test
    fun `Given 0 return ACCESS_DENIED state`() {
        assertEquals(RedButtonState.ACCESS_DENIED, mapper.apply(0))
    }

    @Test
    fun `Given 1 return ACCESS_LOCK state`() {
        assertEquals(RedButtonState.ACCESS_LOCK, mapper.apply(1))
    }

    @Test
    fun `Given 2 return CLICK state`() {
        assertEquals(RedButtonState.CLICK, mapper.apply(2))
    }

    @Test
    fun `Given 3 return NOT_CLICK state`() {
        assertEquals(RedButtonState.NOT_CLICK, mapper.apply(3))
    }

    @Test
    fun `Given 4 return CLOSE_IN_PROGRESS state`() {
        assertEquals(RedButtonState.CLOSE_IN_PROGRESS, mapper.apply(4))
    }

    @Test
    fun `Given 5 return OPEN_IN_PROGRESS state`() {
        assertEquals(RedButtonState.OPEN_IN_PROGRESS, mapper.apply(5))
    }

}