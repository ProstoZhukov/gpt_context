package ru.tensor.sbis.design.custom_view_tools.text_layout.modification

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig

/**
 * Тесты результата метода [TextLayout.buildLayout].
 *
 * @author vv.chekurda
 */
class TextLayoutBuildLayoutResultTest {

    private lateinit var mockTextLayout: TextLayout

    @Before
    fun setUp() {
        mockTextLayout = mock()
        whenever(mockTextLayout.buildLayout(any<TextLayoutConfig>())).thenCallRealMethod()
    }

    @Test
    fun `When config is null, then buildLayout(config) is returns false`() {
        val configureResult = true
        whenever(mockTextLayout.configure(any())).thenReturn(configureResult)

        val isChanged = mockTextLayout.buildLayout()

        assertFalse(isChanged)
    }

    @Test
    fun `When config != null, then buildLayout(config) is returns configure result`() {
        val config = mock<TextLayoutConfig>()
        whenever(mockTextLayout.configure(any())).thenReturn(true)

        val isChanged = mockTextLayout.buildLayout(config)

        assertTrue(isChanged)
        verify(mockTextLayout).configure(config)
    }
}