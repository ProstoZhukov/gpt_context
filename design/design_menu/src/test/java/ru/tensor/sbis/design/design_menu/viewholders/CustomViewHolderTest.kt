package ru.tensor.sbis.design.design_menu.viewholders

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import org.junit.Assert.*
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Before
import ru.tensor.sbis.design.R as RDesign
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.design_menu.CustomViewMenuItem
import ru.tensor.sbis.design.design_menu.databinding.MenuCustomItemBinding
import ru.tensor.sbis.design.design_menu.model.MenuSelectionStyle
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder

/**
 * Тесты ViewHolder с прикладным контентом [CustomViewHolder].
 *
 * @author ra.geraskin
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class CustomViewHolderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val mockRootView: FrameLayout = mock { on { context } doReturn context }
    private val mockResources: Resources = mock()

    private val customView = mock<View>()

    private lateinit var binding: MenuCustomItemBinding

    private lateinit var styleHolder: SbisMenuStyleHolder
    private lateinit var viewHolder: CustomViewHolder

    @Before
    fun setup() {
        context.theme.applyStyle(RDesign.style.BaseAppTheme, false)

        whenever(mockRootView.childCount).thenReturn(1)
        whenever(mockRootView.getChildAt(0)).thenReturn(mockRootView)

        whenever(mockRootView.resources).thenReturn(mockResources)

        binding = MenuCustomItemBinding.bind(mockRootView)

        styleHolder = SbisMenuStyleHolder.createStyleHolderForContainer(context, MenuSelectionStyle.CHECKBOX)
        viewHolder = CustomViewHolder(binding, styleHolder)
    }

    @Test
    fun `When custom view holder inflate custom item, then view holder set that view to layout`() {
        val item = CustomViewMenuItem(factory = { _ -> customView })
        viewHolder.bind(item, false, false, null)
        val captor = ArgumentCaptor.forClass(View::class.java)
        // verify
        verify(viewHolder.itemView as ViewGroup).addView(captor.capture())
        assertEquals(customView, captor.value)

    }

}