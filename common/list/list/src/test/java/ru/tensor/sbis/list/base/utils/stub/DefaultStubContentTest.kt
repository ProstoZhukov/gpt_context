package ru.tensor.sbis.list.base.utils.stub

import android.content.Context
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.design.stubview.StubViewCase

class DefaultStubContentTest {

    @Test
    fun invoke() {
        val context = mock<Context>()

        val defaultStubViewContent = DefaultStubContent()(context)

        verifyNoMoreInteractions(context)
        assertEquals(StubViewCase.NO_SEARCH_RESULTS.getContent(), defaultStubViewContent)
    }
}