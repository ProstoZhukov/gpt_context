package ru.tensor.sbis.design.design_menu.quick_action_menu

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import com.mikepenz.iconics.IconicsDrawable
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.design_menu.QuickActionMenuItem
import ru.tensor.sbis.design.design_menu.R as Rmenu
import ru.tensor.sbis.design.design_menu.databinding.QuickActionMenuItemBinding
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class QuickActionMenuItemViewHolderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val mockRootView: LinearLayout = mock { on { context } doReturn context }
    private val mockResources: Resources = mock()
    private val mockIconLayoutParams: ViewGroup.MarginLayoutParams = mock()
    private val mockRootLayoutParams: ViewGroup.LayoutParams = mock()

    private lateinit var binding: QuickActionMenuItemBinding

    private val iconView = mock<ImageView>()
    private val titleView = mock<SbisTextView>()

    private lateinit var styleHolder: QuickActionMenuStyleHolder
    private lateinit var viewHolder: QuickActionMenuItemViewHolder

    @Before
    fun setup() {
        context.theme.applyStyle(R.style.BaseAppTheme, false)

        whenever(mockRootView.childCount).thenReturn(1)
        whenever(mockRootView.getChildAt(0)).thenReturn(mockRootView)

        whenever(mockRootView.findViewById<View>(Rmenu.id.quick_action_menu_root)).thenReturn(mockRootView)
        whenever(mockRootView.findViewById<View>(Rmenu.id.quick_action_menu_icon)).thenReturn(iconView)
        whenever(mockRootView.findViewById<SbisTextView>(Rmenu.id.quick_action_menu_title)).thenReturn(titleView)

        whenever(mockRootView.resources).thenReturn(mockResources)
        binding = QuickActionMenuItemBinding.bind(mockRootView)

        whenever(binding.quickActionMenuIcon.layoutParams).thenReturn(mockIconLayoutParams)
        whenever(binding.root.layoutParams).thenReturn(mockRootLayoutParams)
        styleHolder = QuickActionMenuStyleHolder().apply { loadStyle(context) }
        viewHolder = QuickActionMenuItemViewHolder(binding, styleHolder)
    }

    @Test
    fun `When set icon to item, then that icon show in menu item`() {
        val testIcon = SbisMobileIcon.Icon.smi_Google
        val testTitle = "TestTitle"
        val item = QuickActionMenuItem(title = testTitle, image = testIcon)
        viewHolder.bind(item, mockk())
        val captor = ArgumentCaptor.forClass(Drawable::class.java)

        verify(iconView).setImageDrawable(captor.capture())
        assertEquals((captor.value as? IconicsDrawable)?.icon, testIcon)
    }

    @Test
    fun `When set title to item, then that title show in menu item`() {
        val testIcon = SbisMobileIcon.Icon.smi_Google
        val testTitle = "TestTitle"
        val item = QuickActionMenuItem(title = testTitle, image = testIcon)
        viewHolder.bind(item, mockk())
        verify(titleView).text = testTitle
    }

    @Test
    fun `When load style without width resource of quick action menu item, then styleHolder return default value in field maxItemWidth`() {
        val styleHolder = QuickActionMenuStyleHolder().apply {
            loadStyle(context)
        }
        assertEquals(
            styleHolder.maxItemWidth,
            context.resources.getDimensionPixelSize(Rmenu.dimen.quick_action_menu_width)
        )
    }

    @Test
    fun `When load style with custom width resource of quick action menu item, then styleHolder return custom value in field maxItemWidth`() {
        val styleHolder = QuickActionMenuStyleHolder().apply {
            loadStyle(context, Rmenu.dimen.menu_width)
        }
        assertEquals(styleHolder.maxItemWidth, context.resources.getDimensionPixelSize(Rmenu.dimen.menu_width))
    }
}