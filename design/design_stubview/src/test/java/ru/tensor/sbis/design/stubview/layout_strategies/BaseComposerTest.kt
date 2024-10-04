package ru.tensor.sbis.design.stubview.layout_strategies

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * @author ma.kolpakov
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
abstract class BaseComposerTest {

    internal companion object {
        const val STUB_VIEW_MIN_HEIGHT_PORTRAIT = 200
        const val STUB_VIEW_MIN_HEIGHT_LANDSCAPE = 100
        const val STUB_VIEW_PADDING = 12
        const val ICON_SIZE_MAX = 250
        const val ICON_SIZE_MIN = 100
        const val ICON_BOTTOM_PADDING = 16
        const val ICON_RIGHT_PADDING = 12
        const val MESSAGE_BOTTOM_PADDING = 8

        const val DEFAULT_DRAWABLE_WIDTH = 150
        const val DEFAULT_DRAWABLE_HEIGHT = 150
    }

    private val context: Context = ApplicationProvider.getApplicationContext()

    internal val mockContext: Context = context.apply {
        theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)
    }

    internal fun mockDrawable(width: Int = DEFAULT_DRAWABLE_WIDTH, height: Int = DEFAULT_DRAWABLE_HEIGHT): Drawable =
        mock {
            on { intrinsicWidth } doReturn width
            on { intrinsicHeight } doReturn height
        }

    internal fun drawableIcon(drawable: Drawable? = null) = ImageView(context).apply { setImageDrawable(drawable) }
    internal fun viewIcon(view: View? = null) = FrameLayout(context).apply { view?.let { addView(it) } }
    internal fun textView(text: String = "") = TextView(context).apply { this.text = text }
    internal fun sbisTextView(text: String = "") = SbisTextView(context).apply { this.text = text }

    protected fun String.repeat(times: Int) = buildString {
        repeat(times) {
            append(this@repeat)
        }
    }
}
