package ru.tensor.sbis.appdesign.container

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.container.*
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout

/**
 * Шапка для демо контейнера.
 *
 * @author ma.kolpakov
 */
class DemoHeaderViewContent : ViewContent, TabbedHeaderContent, AcceptableHeader {
    override var closeContainer: (() -> Unit)? = null
    override var showNewContent: ((ContentCreator<Content>) -> Unit)? = null
    override fun theme() = R.style.DemoContainerCustomTheme
    lateinit var contentView: View
    override fun onTabChanged(tabId: Int) {
        contentView.background = when (tabId) {
            R.id.demo_container_header_tab_green -> ColorDrawable(Color.GREEN)
            R.id.demo_container_header_tab_red   -> ColorDrawable(Color.RED)
            R.id.demo_container_header_tab_blue  -> ColorDrawable(Color.BLUE)
            else                                 -> ColorDrawable(Color.BLACK)
        }
    }

    override fun getTabs(): LinkedHashMap<Int, ToolbarTabLayout.ToolbarTab> {
        return linkedMapOf(
            R.id.demo_container_header_tab_green to ToolbarTabLayout.ToolbarTab(
                R.id.demo_container_header_tab_green,
                R.string.demo_container_header_green
            ),
            R.id.demo_container_header_tab_red to ToolbarTabLayout.ToolbarTab(
                R.id.demo_container_header_tab_red,
                R.string.demo_container_header_red
            ),
            R.id.demo_container_header_tab_blue to ToolbarTabLayout.ToolbarTab(
                R.id.demo_container_header_tab_blue,
                R.string.demo_container_header_blue
            )
        )
    }

    override fun getSelectedTab(): Int = R.id.demo_container_header_tab_red

    override fun getView(context: Context, container: ViewGroup): View {
        contentView = LayoutInflater.from(context).inflate(R.layout.view_container_content, container, false)
        return contentView
    }

    override fun onAccept() {
        Toast.makeText(contentView.context, "CONFIRM", Toast.LENGTH_SHORT).show()
        closeContainer?.invoke()
    }

    override fun onDismiss() {

        Toast.makeText(contentView.context, "dismiss from testFragment", Toast.LENGTH_SHORT).show()
    }
}

