package ru.tensor.sbis.red_button.repository.mapper

import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.red_button.data.RedButtonStubType

/**
 * @author ra.stepanov
 */
@RunWith(JUnitParamsRunner::class)
class RedButtonStubMapperTest {

    private val mapper = RedButtonStubMapper()

    @Test
    fun `Given 1 return CLOSE_STUB stub type`() {
        assertEquals(RedButtonStubType.CLOSE_STUB, mapper.apply(1))
    }

    @Test
    fun `Given 0 return OPEN_STUB stub type`() {
        assertEquals(RedButtonStubType.OPEN_STUB, mapper.apply(0))
    }

    @Test
    fun `Given -1 return OPEN_STUB stub type`() {
        assertEquals(RedButtonStubType.NO_STUB, mapper.apply(-1))
    }

}