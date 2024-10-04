package ru.tensor.sbis.design.design_menu.viewholders

import android.content.Context
import android.graphics.Color
import org.junit.Assert.*
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import ru.tensor.sbis.design.R as RDesign
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.SbisMenuItem
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.model.ItemSelectionState
import ru.tensor.sbis.design.design_menu.model.MenuItemSettings
import ru.tensor.sbis.design.design_menu.model.MenuSelectionStyle
import ru.tensor.sbis.design.design_menu.utils.ContainerType
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.VIEW_START
import ru.tensor.sbis.design.design_menu.utils.ItemViewPaddings.TITLE_START
import ru.tensor.sbis.design.design_menu.view.MenuItemView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.HorizontalPosition.RIGHT
import ru.tensor.sbis.design.theme.HorizontalPosition.LEFT

/**
 * Тесты ViewHolder обычного элемента меню [DividerViewHolder].
 *
 * @author ra.geraskin
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class ItemViewHolderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val testIcon = SbisMobileIcon.Icon.smi_task
    private lateinit var styleHolder: SbisMenuStyleHolder
    private lateinit var viewHolder: ItemViewHolder
    private lateinit var itemView: MenuItemView

    @Before
    fun setup() {
        context.theme.applyStyle(RDesign.style.BaseAppTheme, false)
        styleHolder = SbisMenuStyleHolder.createStyleHolderForContainer(context, MenuSelectionStyle.CHECKBOX)
        itemView = MenuItemView(context, styleHolder, LEFT, ContainerType.CONTAINER, false)
        viewHolder = ItemViewHolder(itemView, styleHolder)
    }

    @Test
    fun `When item title was set, then TextView has text too`() {
        val titleText = "TestTitle"
        val item = SbisMenuItem(title = titleText)

        setItemAndLayoutView(item)
        // verify
        assertEquals(itemView.titleLayout.text, titleText)
    }

    @Test
    fun `When selection enabled, item selection state is checked, then left marker is visible`() {
        val item = SbisMenuItem(
            title = "Test",
            settings = MenuItemSettings(selectionState = ItemSelectionState.CHECKED)
        )
        setItemAndLayoutView(item, LEFT)

        // verify
        assertTrue(itemView.markerLayout.left < itemView.titleLayout.left)
        assertTrue(itemView.markerLayout.text.isNotEmpty())
        assertTrue(itemView.markerLayout.textPaint.color != Color.TRANSPARENT)
    }

    @Test
    fun `When selection enabled, item selection state is checked, marker alignment is right, then right marker is visible`() {
        val item = SbisMenuItem(
            title = "Test",
            settings = MenuItemSettings(selectionState = ItemSelectionState.CHECKED)
        )
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.titleLayout.left < itemView.markerLayout.left)
        assertTrue(itemView.markerLayout.text.isNotEmpty())
        assertTrue(itemView.markerLayout.textPaint.color != Color.TRANSPARENT)
    }

    @Test
    fun `When item is SbisMenu, then view holder has submenu arrow icon`() {
        val item = SbisMenu(
            children = listOf(SbisMenuItem("TestItem")),
            title = "TestMenu"
        )

        setItemAndLayoutView(item, hasMenuItems = true)

        // verify
        assertTrue(itemView.titleLayout.left < itemView.arrowIconLayout.left)
        assertTrue(itemView.arrowIconLayout.text.isNotEmpty())
    }

    @Test
    fun `When item is not SbisMenu, then view holder has not submenu arrow`() {
        val item = SbisMenuItem("TestItem")
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.arrowIconLayout.left == 0)
        assertTrue(itemView.arrowIconLayout.text.isEmpty())
    }

    @Test
    fun `When item has not comment, then view holder comment view is gone`() {
        val item = SbisMenuItem("TestItem")
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.commentLayout.left == 0)
        assertTrue(itemView.commentLayout.text.isEmpty())
    }

    @Test
    fun `When item has comment, then view holder comment view is visible`() {
        val commentText = "Comment"
        val item = SbisMenuItem("TestItem", subTitle = commentText)
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.commentLayout.left == itemView.titleLayout.left)
        assertTrue(itemView.commentLayout.text == commentText)
    }

    @Test
    fun `When item icon is null, then all viewholder's icon are gone`() {
        val item = SbisMenuItem("TestItem")
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.iconLayout.left == 0)
        assertTrue(itemView.commentLayout.text.isEmpty())
    }

    @Test
    fun `When item has icon and icon alignment is RIGHT, then left icon is gone and right icon is visible`() {
        val item = SbisMenuItem("TestItem", icon = testIcon, settings = MenuItemSettings(iconAlignment = RIGHT))
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.iconLayout.left > itemView.titleLayout.left)
        assertTrue(itemView.iconLayout.text.isNotEmpty())
    }

    @Test
    fun `When item has icon and icon alignment is LEFT, then left icon is visible and right icon is gone`() {
        val item = SbisMenuItem("TestItem", icon = testIcon, settings = MenuItemSettings(iconAlignment = LEFT))
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.iconLayout.left < itemView.titleLayout.left)
        assertTrue(itemView.iconLayout.text.isNotEmpty())
    }

    @Test
    fun `When item has not icon and empty icon space flag enable and icon alignment is RIGHT, then left icon is gone and right icon is invisible`() {
        val item = SbisMenuItem(
            title = "TestItem",
            settings = MenuItemSettings(iconAlignment = RIGHT, emptyIconSpaceEnabled = true)
        )
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.iconLayout.left > itemView.titleLayout.left)
        assertTrue(itemView.iconLayout.text.isNotEmpty())
        assertTrue(itemView.iconLayout.textPaint.color == Color.TRANSPARENT)
    }

    @Test
    fun `When item has not icon and empty icon space flag enable and icon alignment is LEFT, then left icon is invisible and right icon is gone`() {
        val item =
            SbisMenuItem(
                "TestItem",
                settings = MenuItemSettings(iconAlignment = LEFT, emptyIconSpaceEnabled = true)
            )
        setItemAndLayoutView(item)

        // verify
        assertTrue(itemView.iconLayout.left < itemView.titleLayout.left)
        assertTrue(itemView.iconLayout.text.isNotEmpty())
        assertTrue(itemView.iconLayout.textPaint.color == Color.TRANSPARENT)
    }

    @Test
    fun `When item hierarchy leve is equals 3, then item view holder increase hierarchy offset on that by the appropriate value`() {
        val item = SbisMenuItem("TestItem")
        item.hierarchyLevel = 3
        setItemAndLayoutView(item)

        // verify
        assertEquals(
            itemView.titleLayout.left,
            (VIEW_START.get(context) + styleHolder.hierarchyOffset * item.hierarchyLevel + TITLE_START.get(context))
        )
    }

    private fun setItemAndLayoutView(
        item: BaseMenuItem,
        markerAlignment: HorizontalPosition = RIGHT,
        hasMenuItems: Boolean = false
    ) {
        itemView = MenuItemView(context, styleHolder, markerAlignment, ContainerType.CONTAINER, false)
        viewHolder = ItemViewHolder(itemView, styleHolder)
        viewHolder.bind(item, selectionEnabled = true, hasMenuItems = hasMenuItems, clickListener = null)
        itemView.measure(MenuAdapter.specs, MenuAdapter.specs)
        itemView.layout(0, 0, 1000, 1000)
    }

}