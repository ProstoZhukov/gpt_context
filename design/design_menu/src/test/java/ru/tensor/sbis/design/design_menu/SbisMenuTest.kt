package ru.tensor.sbis.design.design_menu

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Тесты компонента меню [SbisMenu].
 *
 * @author ra.geraskin
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class SbisMenuTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val menu = SbisMenu(children = emptyList())

    @Before
    fun setup() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)
    }

    @Test
    fun `When menu close, then close listener invoke`() {
        val closeListener = mock<() -> Unit>()
        menu.createView(context, null)
        menu.addCloseListener(closeListener)
        menu.closeMenu()
        // verify
        verify(closeListener).invoke()
    }

}