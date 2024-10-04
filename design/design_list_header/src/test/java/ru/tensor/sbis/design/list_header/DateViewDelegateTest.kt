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
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * @author ra.petrov
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class DateViewDelegateTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val textView: SbisTextView = mock { on { context } doReturn context }

    @Before
    fun setUp() {
        context.theme.applyStyle(R.style.BaseAppTheme, false)
    }

    @Test
    fun `When call setFormattedDateTime with null then set text to null`() {
        val delegate = DateViewDelegate(MODE_STYLEABLE_ATTR)

        delegate.init(textView, null, 0, 0)
        delegate.setFormattedDateTime(null)

        val argument: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        verify(textView).text = argument.capture()
        Assert.assertNull(argument.value)
    }

    companion object {
        const val MODE_STYLEABLE_ATTR = 1
    }
}