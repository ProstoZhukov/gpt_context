package ru.tensor.sbis.design.stubview.layout_strategies

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.stubview.StubViewMode

/**
 * @author ma.kolpakov
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class StubViewComposerFactoryTest {

    private val mockContext: Context = ApplicationProvider.getApplicationContext<Context?>().apply {
        theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)
    }

    @Test
    fun `Given portrait + phone + not_block, when get composer(), then get PortraitStubViewComposer`() {
        val composer = getStubViewComposer(isLandscape = false, isTablet = false, displayMode = StubViewMode.BASE)

        assertTrue(composer is PortraitStubViewComposer)
        assertFalse(composer is PortraitBlockStubViewComposer)
    }

    @Test
    fun `Given portrait + phone + block, when get composer(), then get PortraitBlockStubViewComposer`() {
        val composer = getStubViewComposer(isLandscape = false, isTablet = false, displayMode = StubViewMode.BLOCK)

        assertTrue(composer is PortraitBlockStubViewComposer)
    }

    @Test
    fun `Given landscape + phone + not_block, when get composer(), then get LandscapeStubViewComposer`() {
        val composer = getStubViewComposer(isLandscape = true, isTablet = false, displayMode = StubViewMode.BASE)

        assertTrue(composer is LandscapeStubViewComposer)
        assertFalse(composer is LandscapeBlockStubViewComposer)
    }

    @Test
    fun `Given landscape + phone + block, when get composer(), then get PortraitBlockStubViewComposer`() {
        val composer = getStubViewComposer(isLandscape = true, isTablet = false, displayMode = StubViewMode.BLOCK)

        assertTrue(composer is LandscapeBlockStubViewComposer)
    }

    @Test
    fun `Given landscape + tablet + not_block, when get composer(), then get PortraitBlockStubViewComposer`() {
        val composer = getStubViewComposer(isLandscape = true, isTablet = true, displayMode = StubViewMode.BASE)

        assertTrue(composer is PortraitStubViewComposer)
        assertFalse(composer is PortraitBlockStubViewComposer)
    }

    @Test
    fun `Given landscape + tablet + block, when get composer(), then get PortraitBlockStubViewComposer`() {
        val composer = getStubViewComposer(isLandscape = true, isTablet = true, displayMode = StubViewMode.BLOCK)

        assertTrue(composer is PortraitBlockStubViewComposer)
    }

    private fun getStubViewComposer(
        isLandscape: Boolean,
        isTablet: Boolean,
        displayMode: StubViewMode,
    ): StubViewComposer =
        StubViewComposerFactory.createStubViewComposer(
            isLandscape = isLandscape,
            isTablet = isTablet,
            isDrawable = false,
            displayMode = displayMode,
            icon = mock(),
            message = mock(),
            details = mock(),
            mockContext,
        )
}
