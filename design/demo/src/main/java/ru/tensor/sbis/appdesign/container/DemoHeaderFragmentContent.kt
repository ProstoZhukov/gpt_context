package ru.tensor.sbis.appdesign.container

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Parcelable
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.container.*
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout

/**
 * Шапка для демо контейнера.
 *
 * @author ma.kolpakov
 */
@Parcelize
class DemoHeaderFragmentContent : FragmentContent, TabbedHeaderContent, AcceptableHeader,Parcelable {
    override var closeContainer: (() -> Unit)? = null
    override var showNewContent: ((ContentCreator<Content>) -> Unit)? = null

    lateinit var testFragment: SbisContainerFragment
    override fun theme() = R.style.DemoContainerCustomTheme

    override fun customWidth() = R.dimen.container_demo_fragment_width

    override fun customHeight() = R.dimen.match_parent

    override fun onTabChanged(tabId: Int) {
        testFragment.view?.background = when (tabId) {
            R.id.demo_container_header_tab_green -> ColorDrawable(Color.GREEN)
            R.id.demo_container_header_tab_red   -> ColorDrawable(Color.RED)
            R.id.demo_container_header_tab_blue  -> ColorDrawable(Color.BLUE)
            else                                 -> ColorDrawable(Color.BLACK)
        }
        testFragment.randomSize()
    }

    override fun getTabs(): LinkedHashMap<Int, ToolbarTabLayout.ToolbarTab> {
        return linkedMapOf(
                R.id.demo_container_header_tab_green to ToolbarTabLayout.ToolbarTab(
                        R.id.demo_container_header_tab_green,
                        R.string.demo_container_header_green),
                R.id.demo_container_header_tab_red to ToolbarTabLayout.ToolbarTab(
                        R.id.demo_container_header_tab_red,
                        R.string.demo_container_header_red),
                R.id.demo_container_header_tab_blue to ToolbarTabLayout.ToolbarTab(
                        R.id.demo_container_header_tab_blue,
                        R.string.demo_container_header_blue)
        )
    }

    override fun getSelectedTab(): Int = R.id.demo_container_header_tab_red


    override fun getFragment(): SbisContainerFragment {
        testFragment = SbisContainerFragment()
        return testFragment
    }

    override fun onRestoreFragment(fragment: Fragment) {
        testFragment  = fragment as SbisContainerFragment
    }

    override fun onAccept() {
        testFragment.onAccept()
        closeContainer?.invoke()
    }

    override fun onCancel() {
        testFragment.onCancel()
    }
}

