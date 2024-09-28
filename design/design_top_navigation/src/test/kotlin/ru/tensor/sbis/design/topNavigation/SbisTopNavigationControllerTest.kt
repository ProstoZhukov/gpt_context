package ru.tensor.sbis.design.topNavigation

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.isVisible
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisLinkButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationPresentationContext
import ru.tensor.sbis.design.topNavigation.util.GraphicBackgroundManager
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationController
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.view.input.text.MultilineInputView
import java.util.LinkedList
import ru.tensor.sbis.design.topNavigation.R as RT


/**
 * Тесты [SbisTopNavigationController].
 *
 * @author da.zolotarev
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class SbisTopNavigationControllerTest {
    private val controller: SbisTopNavigationController = SbisTopNavigationController()
    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

    private lateinit var topNavView: SbisTopNavigationView

    @Before
    fun setup() {
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        topNavView = SbisTopNavigationView(activity).apply {
            layoutParams = MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT)
        }
        controller.attach(
            topNavView,
            GraphicBackgroundManager().apply {
                attach(topNavView)
            },
            null,
            RT.attr.sbisTopNavigationTheme,
            RT.style.SbisTopNavigationDefaultStyle
        )
    }

    @Test
    fun `When set LargeTitle content, then topNavView has large title configuration`() {
        controller.content = LARGE_TITLE_CONTENT
        topNavView.run {
            assertTrue(titleView != null && subtitleView != null && searchInput == null && tabsView == null)
        }

    }

    @Test
    fun `When SmallTitle content is set, then topNavView has small title configuration`() {
        controller.content = SMALL_TITLE_CONTENT
        topNavView.run {
            assertTrue(titleView != null && subtitleView != null && searchInput == null && tabsView == null)
        }
    }

    @Test
    fun `When SearchInput content is set, then topNavView has search input configuration`() {
        controller.content = SEARCH_INPUT_CONTENT
        topNavView.run {
            assertTrue(titleView == null && subtitleView == null && searchInput != null && tabsView == null)
        }
    }

    @Test
    fun `When Tab content is set, then topNavView has search input configuration`() {
        controller.content = TABS_CONTENT
        topNavView.run {
            assertTrue(titleView == null && subtitleView == null && searchInput == null && tabsView != null)
        }
    }

    @Test
    fun `When Empty content is set, then topNavView has empty configuration`() {
        controller.content = EMPTY_CONTENT
        topNavView.run {
            assertTrue(titleView == null && subtitleView == null && searchInput == null && tabsView == null)
        }
    }

    @Test
    fun `When content is not set, then topNavView has empty configuration`() {
        topNavView.run {
            assertTrue(titleView == null && subtitleView == null && searchInput == null && tabsView == null)
        }
    }

    @Test
    fun `When custom view is set, then it exists`() {
        controller.customView = View(activity).apply {
            background = ColorDrawable(Color.RED)
        }
        assertEquals(1, topNavView.customViewContainer?.childCount)
    }

    @Test
    fun `When custom view is set and in component another one already exists, then new replaces the old one`() {
        val customViewOld = View(activity)
        val customViewNew = View(activity)
        controller.customView = customViewOld
        controller.customView = customViewNew

        assertEquals(1, topNavView.customViewContainer?.childCount)
        assertEquals(customViewNew, topNavView.customViewContainer?.getChildAt(0))
    }

    @Test
    fun `When default presentation context is set, then left arrow back is shown`() {
        controller.showBackButton = true
        controller.presentationContext = SbisTopNavigationPresentationContext.DEFAULT

        assertTrue(topNavView.backBtn?.isVisible == true && topNavView.rightBackBtnContainer?.isVisible == false)
    }

    @Test
    fun `When modal presentation context is set, then right button back is shown`() {
        controller.showBackButton = true
        controller.presentationContext = SbisTopNavigationPresentationContext.MODAL

        assertTrue(topNavView.backBtn?.isVisible == false && topNavView.rightBackBtnContainer?.isVisible == true)
    }

    @Test
    fun `When edition is enabled and the title configuration is selected, then the title is editable`() {
        controller.content = LARGE_TITLE_CONTENT
        controller.isEditingEnabled = true

        assertFalse(topNavView.titleView?.readOnly ?: true)
    }

    @Test
    fun `When isEditingEnabled is not configured and configuration with title is chosen, then title is not editable`() {
        controller.content = LARGE_TITLE_CONTENT

        assertTrue(topNavView.titleView?.onHideKeyboard ?: false)
    }

    @Test
    fun `When smallTitleMaxLines is set and configuration with small title is chosen, then max number of lines in title is installed`() {
        val linesCount = 3
        controller.content = SMALL_TITLE_CONTENT
        controller.smallTitleMaxLines = linesCount

        assertEquals(linesCount, (topNavView.titleView as? MultilineInputView)?.maxLines)
    }

    @Test
    fun `When three buttons in rightButtons are set, then view have three buttons`() {
        controller.rightButtons = List(3) { SbisLinkButton(activity) }

        assertEquals(3, topNavView.rightBtnContainer?.childCount)
    }

    @Test
    fun `When SbisLinkButton or SbisRoundButton is set in rightButtons, then their minWidth's is installed correctly`() {
        val width = topNavView.resources.getDimensionPixelSize(RT.dimen.sbis_top_navigation_button_min_width)

        controller.rightButtons = listOf(
            SbisLinkButton(activity),
            SbisRoundButton(activity),
            SbisButton(activity)
        )

        topNavView.rightBtnContainer?.run {
            assertEquals(width, getChildAt(0).minimumWidth)
            assertEquals(width, getChildAt(1).minimumWidth)
            assertEquals(wrapContent, getChildAt(2)?.layoutParams?.width ?: 0)
        } ?: run { assert(false) { "rightBtnContainer not exist" } }
    }

    @Test
    fun `When SbisLinkButton or SbisRoundButton is set in rightButtons, then their minHeight's is installed correctly`() {
        val height = topNavView.resources.getDimensionPixelSize(RT.dimen.sbis_top_navigation_button_min_height)

        controller.rightButtons = listOf(
            SbisLinkButton(activity),
            SbisRoundButton(activity),
            SbisButton(activity)
        )

        topNavView.rightBtnContainer?.run {
            assertEquals(height, getChildAt(0).minimumHeight)
            assertEquals(height, getChildAt(1).minimumHeight)
            assertEquals(wrapContent, getChildAt(2)?.layoutParams?.height ?: 0)
        } ?: run { assert(false) { "rightBtnContainer not exist" } }
    }


    companion object {
        val LARGE_TITLE_CONTENT = SbisTopNavigationContent.LargeTitle(PlatformSbisString.Value("Title"))
        val SMALL_TITLE_CONTENT = SbisTopNavigationContent.SmallTitle(PlatformSbisString.Value("Title"))
        val SEARCH_INPUT_CONTENT = SbisTopNavigationContent.SearchInput
        val TABS_CONTENT = SbisTopNavigationContent.Tabs(LinkedList())
        val EMPTY_CONTENT = SbisTopNavigationContent.EmptyContent

        const val wrapContent = LayoutParams.WRAP_CONTENT
    }
}