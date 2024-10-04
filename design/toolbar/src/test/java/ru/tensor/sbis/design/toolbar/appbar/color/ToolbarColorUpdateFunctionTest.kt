package ru.tensor.sbis.design.toolbar.appbar.color

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mikepenz.iconics.IconicsDrawable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * В тесте используется симулятор для снижения нагрузки по мокированию ресурсов и контекста
 *
 * @author ma.kolpakov
 * @since 12/12/2019
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ToolbarColorUpdateFunctionTest {

    private val view: Toolbar = mock()
    private val drawable: IconicsDrawable = mock()

    private val updateFunction = ToolbarColorUpdateFunction { drawable }

    @Before
    fun setUp() {
        with(Robolectric.buildActivity(AppCompatActivity::class.java).get()) {
            whenever(view.context).thenReturn(this)
        }
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=4b61cd0a-fc1f-4063-b5f5-2f9eec052905
     */
    @Test
    fun `Verify LAYER_TYPE_SOFTWARE used for toolbar when back icon generated as IconicsDrawable with shadow`() {
        updateFunction.updateColorModel(view, null)

        verify(view).setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * Иконка в шрифте имеет отступы, нужно это учитывать
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=ffd90d72-4c62-4809-9baf-fe60c4d38e79
     */
    @Test
    fun `Back arrow icon should respect font bound`() {
        updateFunction.updateColorModel(view, null)

        verify(drawable).respectFontBounds(true)
    }
}