package ru.tensor.sbis.red_button.repository.mapper

import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.red_button.data.RedButtonActions

/**
 * @author ra.stepanov
 */
@RunWith(JUnitParamsRunner::class)
class RedButtonActionsMapperTest {

    private val mapper = RedButtonActionsMapper()

    @Test
    fun `Given 0 return HIDE_MANAGEMENT action`() {
        assertEquals(RedButtonActions.HIDE_MANAGEMENT, mapper.apply(0))
    }

    @Test
    fun `Given 1 return EMPTY_CABINET action`() {
        assertEquals(RedButtonActions.EMPTY_CABINET, mapper.apply(1))
    }
}