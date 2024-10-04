package ru.tensor.sbis.design.design_menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.design_menu.dividers.LineDivider
import ru.tensor.sbis.design.design_menu.model.MenuSelectionStyle
import ru.tensor.sbis.design.design_menu.utils.ContainerType
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.design_menu.view.MenuItemView
import ru.tensor.sbis.design.design_menu.viewholders.ItemViewHolder
import ru.tensor.sbis.design.design_menu.viewholders.MenuAdapter
import ru.tensor.sbis.design.theme.HorizontalPosition.RIGHT

/**
 * Тесты адаптера меню [MenuAdapter].
 *
 * @author ra.geraskin
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class MenuAdapterTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var styleHolder: SbisMenuStyleHolder

    private lateinit var view: View
    private val mockCustomView = mock<View>()

    private lateinit var adapter: MenuAdapter

    @Before
    fun setup() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)
        styleHolder = SbisMenuStyleHolder.createStyleHolderForContainer(context, MenuSelectionStyle.CHECKBOX)
        adapter = MenuAdapter(
            hideDefaultDividers = true,
            hasTitle = false,
            styleHolder = styleHolder,
            minItemWidth = 0,
            containerType = ContainerType.CONTAINER,
            selectionEnabled = false,
            twoLinesItemsTitle = false
        )
        view = LayoutInflater.from(context).inflate(
            R.layout.menu_in_container,
            null,
            false
        )
        whenever(mockCustomView.measuredWidth).thenReturn(CUSTOM_VIEW_WIDTH)
    }

    @Test
    fun `When set items in adapter, then max width equals max elem`() {
        val items = listOf(
            SbisMenuItem("Short"),
            LineDivider,
            SbisMenuItem("Meedium"),
            LineDivider,
            SbisMenuItem("Loooooong"),
        )
        adapter.setItems(items, context)
        val itemWidth = ItemViewHolder(
            MenuItemView(context, styleHolder, RIGHT, ContainerType.CONTAINER, false),
            styleHolder
        ).let {
            it.bind(items[BIGGEST_ELEM], selectionEnabled = false, hasMenuItems = false) {}
            it.itemView.measure(MenuAdapter.specs, MenuAdapter.specs)
            it.itemView.measuredWidth
        }
        // verify
        Assert.assertEquals(adapter.maxItemWidth, itemWidth)
    }

    @Test
    fun `When set item with custom item in adapter, then max width equals max elem`() {
        val items = listOf(
            SbisMenuItem("Short"),
            LineDivider,
            SbisMenuItem("Medium"),
            LineDivider,
            CustomViewMenuItem({ mockCustomView }),
        )
        adapter.setItems(items, context)
        // verify
        Assert.assertEquals(adapter.maxItemWidth, mockCustomView.measuredWidth)
    }

    @Test
    fun `When set nested menu, then adapter expand all nested children lists to one level list`() {
        val items = listOf(
            SbisMenuItem("Item 1"),
            SbisMenuItem("Item 2"),
            SbisMenuNested(
                title = "Nested menu 1",
                children = listOf(
                    SbisMenuItem("Item 3"),
                    SbisMenuItem("Item 4"),
                    SbisMenuNested(
                        title = "Nested menu 2",
                        children = listOf(
                            SbisMenuItem("Item 5"),
                            SbisMenuItem("Item 6"),
                            SbisMenuItem("Item 7")
                        )
                    ),
                    SbisMenuItem("Item 8")
                )
            ),
            SbisMenuItem("Item 9"),
            SbisMenuItem("Item 10"),
        )
        adapter.setItems(items, context)
        // verify
        Assert.assertEquals((10 + 2), adapter.itemCount)
    }

    @Test
    fun `When show default dividers flag set true, then menu add default dividers after each item except last`() {
        adapter = MenuAdapter(
            hideDefaultDividers = false,
            hasTitle = false,
            styleHolder = styleHolder,
            minItemWidth = 0,
            containerType = ContainerType.CONTAINER,
            selectionEnabled = false,
            twoLinesItemsTitle = false
        )
        val items = listOf(
            SbisMenuItem("Item 1"),
            SbisMenuItem("Item 2"),
            SbisMenuItem("Item 3"),
            SbisMenuItem("Item 4"),
        )
        adapter.setItems(items, context)
        // verify
        Assert.assertEquals((4 + 3), adapter.itemCount)
    }

    companion object {
        const val CUSTOM_VIEW_WIDTH = 5000
        const val BIGGEST_ELEM = 4
    }
}