package ru.tensor.sbis.design.list_header

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * @author ra.petrov
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class DateViewDelegateViewViewModeTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val textView: SbisTextView = mock { on { context } doReturn context }

    /**
     * formattedDateTime который форматируем
     */
    private val formattedDateTime = FormattedDateTime("01.05.2021", "13:54")

    @Before
    fun setUp() {
        context.theme.applyStyle(R.style.BaseAppTheme, false)
    }

    @Test
    fun `When call setFormattedDateTime with text and DATE_TIME mode then set text to view`() {
        val viewMode = DateViewMode.DATE_TIME
        val expectedFormattedText = "01.05.2021 13:54"
        val delegate = DateViewDelegate(MODE_STYLEABLE_ATTR)

        delegate.init(textView, null, 0, 0)
        delegate.dateViewMode = viewMode
        delegate.setFormattedDateTime(formattedDateTime)

        val argument: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        verify(textView).text = argument.capture()
        Assert.assertEquals(expectedFormattedText, argument.value)
    }

    @Test
    fun `When call setFormattedDateTime with text and TIME_ONLY mode then set text to view`() {
        val viewMode = DateViewMode.TIME_ONLY
        val expectedFormattedText = "13:54"
        val delegate = DateViewDelegate(MODE_STYLEABLE_ATTR)

        delegate.init(textView, null, 0, 0)
        delegate.dateViewMode = viewMode
        delegate.setFormattedDateTime(formattedDateTime)

        val argument: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        verify(textView).text = argument.capture()
        Assert.assertEquals(expectedFormattedText, argument.value)
    }

    @Test
    fun `When call setFormattedDateTime with text and DATE_ONLY mode then set text to view`() {
        val viewMode = DateViewMode.DATE_ONLY
        val expectedFormattedText = "01.05.2021"
        val delegate = DateViewDelegate(MODE_STYLEABLE_ATTR)

        delegate.init(textView, null, 0, 0)
        delegate.dateViewMode = viewMode
        delegate.setFormattedDateTime(formattedDateTime)

        val argument: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        verify(textView).text = argument.capture()
        Assert.assertEquals(expectedFormattedText, argument.value)
    }

    companion object {
        const val MODE_STYLEABLE_ATTR = 1
    }
}