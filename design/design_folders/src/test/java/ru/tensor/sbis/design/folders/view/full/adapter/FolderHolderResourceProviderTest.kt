package ru.tensor.sbis.design.folders.view.full.adapter

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.folders.view.full.FolderListViewMode

/**
 * @author ma.kolpakov
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class FolderHolderResourceProviderTest {

    private companion object {
        const val iconSize = 34
        const val iconStartPadding = 6
        const val nestedHorizontalPadding = 18
        const val standaloneHorizontalPadding = 12
    }

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var resourceProvider: FolderHolderResourceProvider

    @Before
    fun setUp() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=935e706d-6954-4e43-81e0-d5eac0b0f5dd
    @Test
    fun `When folders view is nested, then first item padding should contain icon size`() {
        val expectedPadding = iconStartPadding + iconSize + nestedHorizontalPadding
        resourceProvider = FolderHolderResourceProvider(context, FolderListViewMode.NESTED)

        assertEquals(expectedPadding, resourceProvider.getFirstItemLeftPaddingPx())
    }

    @Test
    fun `When folders view is standalone, then first item padding should be without icon size`() {
        resourceProvider = FolderHolderResourceProvider(context, FolderListViewMode.STAND_ALONE)

        assertEquals(standaloneHorizontalPadding, resourceProvider.getFirstItemLeftPaddingPx())
    }
    //endregion
}