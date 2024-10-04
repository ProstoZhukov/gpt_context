package ru.tensor.sbis.design.context_menu

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
import ru.tensor.sbis.design.context_menu.dividers.SlimDivider
import ru.tensor.sbis.design.context_menu.utils.CheckboxIcon
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.context_menu.viewholders.ItemViewHolder

/**
 * Тесты адаптера [SbisMenu].
 *
 * @author da.zolotarev
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class MenuAdapterTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private var styleHolder = SbisMenuStyleHolder()
    private var adapter = MenuAdapter(
        hideDefaultDividers = false,
        hasTitle = false,
        styleHolder = styleHolder,
        minItemWidth = 0
    )
    private lateinit var view: View
    private val mockCustomView = mock<View>()

    @Before
    fun setup() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.AppTheme, false)
        styleHolder.loadStyle(context, CheckboxIcon.CHECK)
        view = LayoutInflater.from(context).inflate(
            R.layout.context_menu_item,
            null,
            false
        )
        whenever(mockCustomView.measuredWidth).thenReturn(CUSTOM_VIEW_WIDTH)
    }

    @Test
    fun `When set items in adapter then max width equals max elem`() {
        val items = listOf(
            MenuItem("Short"),
            SlimDivider,
            MenuItem("Meedium"),
            SlimDivider,
            MenuItem("Loooooong"),
        )

        adapter.setItems(items, context)

        val itemWidth = ItemViewHolder(view, styleHolder).let {
            it.bind(items[BIGGEST_ELEM]) {}
            it.itemView.measure(MenuAdapter.specs, MenuAdapter.specs)
            it.itemView.measuredWidth
        }

        Assert.assertEquals(adapter.maxItemWidth, itemWidth)
    }

    @Test
    fun `When set item with custom item in adapter then max width equals max elem`() {
        val items = listOf(
            MenuItem("Short"),
            SlimDivider,
            MenuItem("Medium"),
            SlimDivider,
            CustomViewItem({ _ -> mockCustomView }),
        )
        adapter.setItems(items, context)

        Assert.assertEquals(adapter.maxItemWidth, mockCustomView.measuredWidth)
    }

    companion object {
        const val CUSTOM_VIEW_WIDTH = 5000
        const val BIGGEST_ELEM = 4
    }
}