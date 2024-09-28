package ru.tensor.sbis.design.context_menu

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.times
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.context_menu.utils.CheckboxIcon
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.context_menu.view.IconCheckBox
import ru.tensor.sbis.design.context_menu.viewholders.ItemViewHolder
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class ViewHoldersTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val view = mock<View>()
    private val styleHolderMock = mock<SbisMenuStyleHolder>()
    private val textView = mock<SbisTextView>()
    private val imageView = mock<ImageView>()
    private val checkbox = mock<IconCheckBox>()
    private val root = mock<LinearLayout>()

    @Before
    fun setup() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.BaseAppTheme, false)

        whenever(view.context).thenReturn(context)

        whenever(view.findViewById<View>(R.id.context_menu_item_text)).thenReturn(textView)
        whenever(view.findViewById<View>(R.id.context_menu_item_comment)).thenReturn(textView)
        whenever(view.findViewById<View>(R.id.context_menu_item_checkbox)).thenReturn(checkbox)
        whenever(view.findViewById<View>(R.id.context_menu_item_image_right)).thenReturn(imageView)
        whenever(view.findViewById<View>(R.id.context_menu_item_image_left)).thenReturn(imageView)
        whenever(view.findViewById<View>(R.id.context_menu_item_root)).thenReturn(root)
    }

    @Test
    fun `When ItemState is On, then checkbox is visible`() {
        val viewHolder = ItemViewHolder(view, styleHolderMock)
        val item = MenuItem("", state = MenuItemState.ON)
        viewHolder.bind(item, null)

        verify(checkbox).visibility = View.VISIBLE
    }

    @Test
    fun `When ItemState is Off, then checkbox is GONE`() {
        val viewHolder = ItemViewHolder(view, styleHolderMock)
        val item = MenuItem("", state = MenuItemState.OFF)
        viewHolder.bind(item, null)

        verify(checkbox).visibility = View.GONE
    }

    @Test
    fun `When ItemState is MIXED, then checkbox is INVISIBLE`() {
        val viewHolder = ItemViewHolder(view, styleHolderMock)
        val item = MenuItem("", state = MenuItemState.MIXED)
        viewHolder.bind(item, null)

        verify(checkbox).visibility = View.INVISIBLE
    }

    @Test
    fun `When ItemStateOnIcon - Marker then checkbox icon is marker`() {
        val styleHolder = SbisMenuStyleHolder().apply {
            loadStyle(context, CheckboxIcon.MARKER)
        }
        val viewHolder = ItemViewHolder(view, styleHolder)
        val item = MenuItem("", state = MenuItemState.ON)
        viewHolder.bind(item, null)

        verify(checkbox, times(1)).setOnIcon(CheckboxIcon.MARKER)
    }

    @Test
    fun `When ItemStateOnIcon - CHECK then checkbox icon is check`() {
        val styleHolder = SbisMenuStyleHolder().apply {
            loadStyle(context, CheckboxIcon.CHECK)
        }
        val viewHolder = ItemViewHolder(view, styleHolder)
        val item = MenuItem("", state = MenuItemState.ON)
        viewHolder.bind(item, null)

        verify(checkbox, times(1)).setOnIcon(CheckboxIcon.CHECK)
    }

}