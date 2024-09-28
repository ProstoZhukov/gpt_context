package ru.tensor.sbis.design.stubview

import org.mockito.kotlin.mock
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class StubViewCaseTest {

    @Test
    @Parameters(source = StubViewCase::class)
    fun `getContent() without actions test`(case: StubViewCase) {
        val content = case.getContent()

        assertEquals(case.imageType, (content as ImageStubContent).imageType)
        assertEquals(case.messageRes, content.messageRes)
        assertEquals(case.detailsRes, content.detailsRes)
        assertTrue(content.actions.isEmpty())
    }

    @Test
    @Parameters(source = StubViewCase::class)
    fun `getContent() with actions test`(case: StubViewCase) {
        val testAction1: () -> Unit = mock()
        val testAction2: () -> Unit = mock()
        val testActions = mapOf(
            R.string.design_stub_view_no_events_details_clickable_1 to testAction1,
            R.string.design_stub_view_no_events_details_clickable_2 to testAction2
        )

        val content = case.getContent(testActions)

        assertEquals(case.imageType, (content as ImageStubContent).imageType)
        assertEquals(case.messageRes, content.messageRes)
        assertEquals(case.detailsRes, content.detailsRes)
    }
}
