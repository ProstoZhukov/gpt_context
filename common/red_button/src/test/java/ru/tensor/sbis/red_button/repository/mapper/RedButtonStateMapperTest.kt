package ru.tensor.sbis.red_button.repository.mapper

import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button_service.generated.RedButtonTypeState

/**
 * @author ra.stepanov
 */
@RunWith(JUnitParamsRunner::class)
class RedButtonStateMapperTest {

    private val mapper = RedButtonStateMapper()

    @Test
    fun `Given RedButtonTypeState#ACCESS_DENIED return ACCESS_DENIED state`() {
        assertEquals(RedButtonState.ACCESS_DENIED, mapper.apply(RedButtonTypeState.ACCESS_DENIED))
    }

    @Test
    fun `Given RedButtonTypeState#ACCESS_LOCK return ACCESS_LOCK state`() {
        assertEquals(RedButtonState.ACCESS_LOCK, mapper.apply(RedButtonTypeState.ACCESS_LOCK))
    }

    @Test
    fun `Given RedButtonTypeState#CLICK return CLICK state`() {
        assertEquals(RedButtonState.CLICK, mapper.apply(RedButtonTypeState.CLICK))
    }

    @Test
    fun `Given RedButtonTypeState#NOT_CLICK return NOT_CLICK state`() {
        assertEquals(RedButtonState.NOT_CLICK, mapper.apply(RedButtonTypeState.NOT_CLICK))
    }

    @Test
    fun `Given RedButtonTypeState#CLOSES return CLOSE_IN_PROGRESS state`() {
        assertEquals(RedButtonState.CLOSE_IN_PROGRESS, mapper.apply(RedButtonTypeState.CLOSES))
    }

    @Test
    fun `Given RedButtonTypeState#OPENS return OPEN_IN_PROGRESS state`() {
        assertEquals(RedButtonState.OPEN_IN_PROGRESS, mapper.apply(RedButtonTypeState.OPENS))
    }
}