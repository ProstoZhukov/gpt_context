package ru.tensor.sbis.design.stubview

import android.os.Build
import android.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.stubview.hint.StubViewHintStyleHolder
import ru.tensor.sbis.design.util.dpToPx

/**
 * Тесты заглушки-подсказки [StubViewHint].
 *
 * @author ra.geraskin
 */

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class StubViewHintTest {

    private val context = ContextThemeWrapper(ApplicationProvider.getApplicationContext(), R.style.AppGlobalTheme)
    private val view = StubViewHint(context)
    private val measureSpecAtMost = MeasureSpecUtils.makeAtMostSpec(10000)
    private val measureSpecExactly = MeasureSpecUtils.makeExactlySpec(10000)
    private val hintTextShort = "Простой текст"
    private val hintTextLong =
        "Очень длинный текст который не влезет на доступные три строки и в конце " +
            "концов применится ellipsize и в конце появится троеточие."

    @Test
    fun `When very large text is inserted and the stub is not set to a certain size in the layout, then stub is not wider than the default width`() {
        view.hintText = hintTextLong

        // act
        view.measure(measureSpecAtMost, measureSpecAtMost)
        val measuredWidth = view.measuredWidth
        val defaultWidth = context.dpToPx(StubViewHintStyleHolder.DEFAULT_VIEW_WIDTH)

        // verify
        assert(defaultWidth >= measuredWidth)
    }

    @Test
    fun `When text of a smaller width is inserted, then width of the component decreases`() {
        view.hintText = hintTextLong

        // act
        view.measure(measureSpecAtMost, measureSpecAtMost)
        val widthLong = view.measuredWidth
        view.hintText = hintTextShort
        view.measure(measureSpecAtMost, measureSpecAtMost)
        val widthShot = view.measuredWidth

        // verify
        assert(widthShot < widthLong)
    }

    @Test
    fun `When stub is given a width in the layout greater than the default width, then the width of the component is greater than the default width`() {
        view.hintText = hintTextLong

        // act
        view.measure(measureSpecExactly, measureSpecExactly)
        val widthLong = view.measuredWidth
        val defaultWidth = context.dpToPx(StubViewHintStyleHolder.DEFAULT_VIEW_WIDTH)

        // verify
        assert(defaultWidth < widthLong)
    }

}