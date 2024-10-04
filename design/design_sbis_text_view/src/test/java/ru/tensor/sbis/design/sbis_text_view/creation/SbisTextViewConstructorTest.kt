package ru.tensor.sbis.design.sbis_text_view.creation

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils.TruncateAt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.sbis_text_view.R

/**
 * Тесты конструкторов [SbisTextView].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisTextViewConstructorTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `When use default view constructor, then fields is initialized from attr theme`() {
        val expectedTextSize = context.resources.dp(25).toFloat()
        val expectedTextColor = ContextCompat.getColor(context, RDesign.color.text_color_black_3)

        val sbisTextView = SbisTextView(ContextThemeWrapper(context, R.style.SbisTextViewTestAppTheme))

        assertEquals(expectedTextSize, sbisTextView.textSize)
        assertEquals(expectedTextColor, sbisTextView.textColor)
    }

    @Test
    fun `When use constructor with config, then fields is initialized from config`() {
        val expectedText = "1234124124"
        val expectedTextSize = 1455f
        val expectedTextColor = Color.BLUE
        val expectedEllipsize = TruncateAt.MIDDLE

        val sbisTextView = SbisTextView(context) {
            text = expectedText
            textSize = expectedTextSize
            setTextColor(expectedTextColor)
            ellipsize = expectedEllipsize
        }

        assertEquals(expectedText, sbisTextView.text)
        assertEquals(expectedTextSize, sbisTextView.textSize)
        assertEquals(expectedTextColor, sbisTextView.textColor)
        assertEquals(expectedEllipsize, sbisTextView.ellipsize)
    }

    @Test
    fun `When use constructor with styleRes, then fields is initialized from style`() {
        val expectedText = "12345"
        val expectedTextSize = context.resources.dp(25).toFloat()
        val expectedTextColor = ContextCompat.getColor(context, RDesign.color.text_color_black_3)

        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_StyleConstructor)

        assertEquals(expectedText, sbisTextView.text)
        assertEquals(expectedTextSize, sbisTextView.textSize)
        assertEquals(expectedTextColor, sbisTextView.textColor)
    }

    @Test
    fun `When use constructor with styleRes and config, then fields is initialized from config`() {
        val expectedText = "1"
        val expectedTextSize = context.resources.dp(10).toFloat()
        val expectedTextColor = ContextCompat.getColor(context, RDesign.color.text_color_black_1)

        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_StyleConstructor) {
            text = expectedText
            textSize = expectedTextSize
            setTextColor(expectedTextColor)
        }

        assertEquals(expectedText, sbisTextView.text)
        assertEquals(expectedTextSize, sbisTextView.textSize)
        assertEquals(expectedTextColor, sbisTextView.textColor)
    }
}