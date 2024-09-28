package ru.tensor.sbis.design.tabs

import android.app.Activity
import android.view.ViewGroup
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.tabs.util.SbisTabInternalStyle
import ru.tensor.sbis.design.tabs.util.tabs
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.tabs.view.SbisTabsViewController
import ru.tensor.sbis.design.R as RD


/**
 * Тесты [SbisTopNavigationController].
 *
 * @author da.zolotarev
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class SbisTabsControllerTest {
    private val controller: SbisTabsViewController = SbisTabsViewController()
    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

    private lateinit var tabsView: SbisTabsView

    @Before
    fun setup() {
        activity.theme.applyStyle(RD.style.BaseAppTheme, false)
        tabsView = SbisTabsView(activity).apply {
            layoutParams =
                ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
                )
        }
        controller.attach(tabsView, null, R.attr.sbisTabsView_Theme, R.style.SbisTabsViewDefaultTheme)
    }

    @Test
    fun `When two tabs is set, then the number of tabsContainer children is equals two`() {
        controller.tabs = tabs {
            tab { content { text("Tab 1") } }
            tab { content { text("Tab 2") } }
        }
        Assert.assertEquals(2, tabsView.tabsContainer.childCount)
    }

    @Test
    fun `When the tabs is not set, then the number of tabsContainer children is equals zero`() {
        Assert.assertEquals(0, tabsView.tabsContainer.childCount)
    }

    @Test
    fun `When isAccent is true, then accented style is chosen`() {
        controller.isAccent = true
        Assert.assertEquals(
            SbisTabInternalStyle.ACCENTED.getMarkerColor(activity),
            controller.styleHolder.markerPaint.color
        )
    }

    @Test
    fun `When tabClickListener is set and tab is clicked, then tabClickListener is called`() {
        var testProperty = 0
        controller.tabs = tabs {
            tab { content { text("Tab 1") } }
        }
        controller.setOnTabClickListener {
            testProperty = 1
        }
        tabsView.tabsContainer.getChildAt(0).callOnClick()

        Assert.assertEquals(1, testProperty)
    }

    @Test
    fun `When one tab is selected, then not throw exception`() {
        Assertions.assertDoesNotThrow {
            controller.tabs = tabs {
                tab {
                    content { text("Tab 1") }
                }
                tab {
                    content { text("Tab 2") }
                }
            }
            controller.selectedTabIndex = 1
        }
    }

    @Test
    fun `When only main tab is selected, then not throw exception`() {
        Assertions.assertDoesNotThrow {
            controller.tabs = tabs {
                tab {
                    isMain = true
                    content { text("Tab 1") }
                }
                tab {
                    content { text("Tab 2") }
                }
            }
            controller.selectedTabIndex = 1
        }
    }

    @Test
    fun `When select tab and tabsView is empty, then not throw exception`() {
        Assertions.assertDoesNotThrow {
            controller.selectedTabIndex = 1
            controller.selectedTabIndex = 0
            controller.selectedTabIndex = 3
        }
    }
}