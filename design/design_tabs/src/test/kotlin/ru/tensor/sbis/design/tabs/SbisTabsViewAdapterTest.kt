package ru.tensor.sbis.design.tabs

import android.app.Activity
import androidx.core.view.isVisible
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.tabs.util.SbisTabsViewAdapter
import ru.tensor.sbis.design.tabs.util.tabs
import ru.tensor.sbis.design.tabs.view.SbisTabsView

/**
 * Тесты [SbisTabsViewAdapter].
 *
 * @author da.zolotarev
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
internal class SbisTabsViewAdapterTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

    private lateinit var tabsView: SbisTabsView
    private lateinit var adapter: SbisTabsViewAdapter

    @Before
    fun setup() {
        activity.theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)
        tabsView = SbisTabsView(activity)
        adapter = SbisTabsViewAdapter(tabsView)
    }

    @Test
    fun `When the tab navxIds are not passed, then all tabs are invisible`() {
        tabsView.tabs = tabs {
            tab { navxId = NavxTestId.FIRST }
            tab { navxId = NavxTestId.SECOND }
            tab { navxId = NavxTestId.THIRD }
        }

        adapter.updateTabsVisibility(setOf(NavxTestId.FOURTH))

        assertAllTabsIsNotVisible()
    }

    @Test
    fun `When first and second tab navxIds are passed, then third tab is invisible`() {
        tabsView.tabs = tabs {
            tab { navxId = NavxTestId.FIRST }
            tab { navxId = NavxTestId.SECOND }
            tab { navxId = NavxTestId.THIRD }
        }

        adapter.updateTabsVisibility(setOf(NavxTestId.FIRST, NavxTestId.SECOND))

        assertTabIsVisible(0)
        assertTabIsVisible(1)
        assertTabIsNotVisible(2)
    }

    @Test
    fun `When the first tab navxId is passed, then first tab is visible`() {
        tabsView.tabs = tabs {
            tab { navxId = NavxTestId.FIRST }
            tab { navxId = NavxTestId.SECOND }
            tab { navxId = NavxTestId.THIRD }
        }
        adapter.updateTabsVisibility(setOf(NavxTestId.FOURTH))

        adapter.updateTabsVisibility(setOf(NavxTestId.FIRST))

        assertTabIsVisible(0)
        assertTabIsNotVisible(1)
        assertTabIsNotVisible(2)
    }

    @Test
    fun `When all tabs navxIds are passed, then all tabs are visible`() {
        tabsView.tabs = tabs {
            tab { navxId = NavxTestId.FIRST }
            tab { navxId = NavxTestId.SECOND }
            tab { navxId = NavxTestId.THIRD }
        }
        adapter.updateTabsVisibility(setOf(NavxTestId.FOURTH))

        adapter.updateTabsVisibility(setOf(NavxTestId.FIRST, NavxTestId.SECOND, NavxTestId.THIRD))

        assertAllTabsIsVisible()
    }

    @Test
    fun `When the tab has an id and navxId, and navxId is passed, then tab is visible`() {
        tabsView.tabs = tabs {
            tab {
                navxId = NavxTestId.SECOND
                id = CUSTOM_ID
            }
        }
        adapter.updateTabsVisibility(setOf(NavxTestId.FOURTH))

        adapter.updateTabsVisibility(setOf(NavxTestId.SECOND))

        assertTabIsVisible(0)
    }

    @Test
    fun `When the second tab navxIds are passed, then second tab is selected`() {
        tabsView.tabs = tabs {
            tab { navxId = NavxTestId.FIRST }
            tab { navxId = NavxTestId.SECOND }
            tab { navxId = NavxTestId.THIRD }
        }

        adapter.setSelection( NavxTestId.SECOND )
        assertTabIsSelected(1)
    }

    @Test
    fun `When the third tab has an id and navxId, and navxId is passed, then third tab is selected`() {
        tabsView.tabs = tabs {
            tab { navxId = NavxTestId.FIRST }
            tab { navxId = NavxTestId.SECOND }
            tab {
                navxId = NavxTestId.THIRD
                id = CUSTOM_ID
            }
        }

        adapter.setSelection(NavxTestId.THIRD)

        assertTabIsSelected(2)
    }

    private fun assertTabIsSelected(tabIndex: Int) = Assert.assertEquals(tabIndex, tabsView.selectedTabIndex)
    private fun assertTabIsVisible(tabIndex: Int) = Assert.assertTrue(tabsView.itemViews[tabIndex].isVisible)
    private fun assertTabIsNotVisible(tabIndex: Int) = Assert.assertFalse(tabsView.itemViews[tabIndex].isVisible)
    private fun assertAllTabsIsVisible() = Assert.assertTrue(tabsView.itemViews.all { it.isVisible })
    private fun assertAllTabsIsNotVisible() = Assert.assertFalse(tabsView.itemViews.any { it.isVisible })

    companion object {
        const val CUSTOM_ID = "custom id"
    }
}