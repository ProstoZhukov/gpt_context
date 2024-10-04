package ru.tensor.sbis.design.design_menu.viewholders

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.design_menu.R
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.design_menu.databinding.MenuTextDividerBinding
import ru.tensor.sbis.design.design_menu.dividers.LineDivider
import ru.tensor.sbis.design.design_menu.dividers.TextDivider
import ru.tensor.sbis.design.design_menu.dividers.TextLineDivider
import ru.tensor.sbis.design.design_menu.model.MenuSelectionStyle
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.HorizontalAlignment.CENTER
import ru.tensor.sbis.design.theme.HorizontalAlignment.LEFT
import ru.tensor.sbis.design.theme.HorizontalAlignment.RIGHT

/**
 * Тесты ViewHolder разделителя [DividerViewHolder].
 *
 * @author ra.geraskin
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class DividerViewHolderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val mockRootView: LinearLayout = mock { on { context } doReturn context }
    private val mockResources: Resources = mock()
    private val titleViewLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    private val rootViewLayoutParams = RecyclerView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    private lateinit var binding: MenuTextDividerBinding

    private val leftLine = mock<View>()
    private val dividerTitle = mock<SbisTextView>()
    private val rightLine = mock<View>()

    private lateinit var styleHolder: SbisMenuStyleHolder
    private lateinit var viewHolder: DividerViewHolder

    @Before
    fun setup() {
        context.theme.applyStyle(RDesign.style.BaseAppTheme, false)

        whenever(mockRootView.childCount).thenReturn(1)
        whenever(mockRootView.getChildAt(0)).thenReturn(mockRootView)

        whenever(mockRootView.findViewById<View>(R.id.menu_divider_line_left)).thenReturn(leftLine)
        whenever(mockRootView.findViewById<View>(R.id.menu_divider_line_left)).thenReturn(leftLine)
        whenever(mockRootView.findViewById<SbisTextView>(R.id.menu_divider_text)).thenReturn(dividerTitle)
        whenever(mockRootView.findViewById<View>(R.id.menu_divider_line_right)).thenReturn(rightLine)
        whenever(mockRootView.layoutParams).thenReturn(rootViewLayoutParams)
        whenever(dividerTitle.layoutParams).thenReturn(titleViewLayoutParams)
        whenever(dividerTitle.context).thenReturn(context)

        whenever(mockRootView.resources).thenReturn(mockResources)

        binding = MenuTextDividerBinding.bind(mockRootView)

        styleHolder = SbisMenuStyleHolder.createStyleHolderForContainer(context, MenuSelectionStyle.CHECKBOX)
        viewHolder = DividerViewHolder(binding, styleHolder)
    }

    @Test
    fun `When set divider with line, then text in view holder is gone`() {
        val divider = LineDivider
        viewHolder.bind(divider, selectionEnabled = true, hasMenuItems = false, clickListener = null)
        // verify
        verify(dividerTitle).visibility = View.GONE
    }

    @Test
    fun `When set divider with text, then text in view holder has item text`() {
        val titleText = "TestTitle"
        val divider = TextDivider(text = titleText)
        viewHolder.bind(divider, selectionEnabled = true, hasMenuItems = false, clickListener = null)
        // verify
        verify(dividerTitle).text = titleText
    }

    @Test
    fun `When set divider with text and line, then text in view holder has item text`() {
        val titleText = "TestTitle"
        val divider = TextLineDivider(titleText)
        viewHolder.bind(divider, selectionEnabled = true, hasMenuItems = false, clickListener = null)
        // verify
        verify(dividerTitle).text = titleText
    }

    @Test
    fun `When set divider with line, then left and right lines are visible`() {
        val divider = LineDivider
        viewHolder.bind(divider, selectionEnabled = true, hasMenuItems = false, clickListener = null)
        // verify
        verify(leftLine).visibility = View.VISIBLE
        verify(rightLine).visibility = View.VISIBLE
    }

    @Test
    fun `When set divider with text, then left and right lines are gone`() {
        val divider = TextDivider("TestTitle")
        viewHolder.bind(divider, selectionEnabled = true, hasMenuItems = false, clickListener = null)
        // verify
        verify(leftLine).visibility = View.GONE
        verify(rightLine).visibility = View.INVISIBLE
    }

    @Test
    fun `When set divider with text and line, then left and right lines are visible`() {
        val divider = TextLineDivider("TestTitle", textAlignment = CENTER)
        viewHolder.bind(divider, selectionEnabled = true, hasMenuItems = false, clickListener = null)
        // verify
        verify(leftLine).visibility = View.VISIBLE
        verify(rightLine).visibility = View.VISIBLE
    }

    @Test
    fun `When set divider with text and line and set text alignment left, then left line is gone and right line is visible`() {
        val divider = TextLineDivider("TestTitle", textAlignment = LEFT)
        viewHolder.bind(divider, selectionEnabled = true, hasMenuItems = false, clickListener = null)
        // verify
        verify(leftLine).visibility = View.GONE
        verify(rightLine).visibility = View.VISIBLE
    }

    @Test
    fun `When set divider with text and line and set text alignment left, then left line is visible and right line is gone`() {
        val divider = TextLineDivider("TestTitle", textAlignment = RIGHT)
        viewHolder.bind(divider, selectionEnabled = true, hasMenuItems = false, clickListener = null)
        // verify
        verify(leftLine).visibility = View.VISIBLE
        verify(rightLine).visibility = View.GONE
    }

}
