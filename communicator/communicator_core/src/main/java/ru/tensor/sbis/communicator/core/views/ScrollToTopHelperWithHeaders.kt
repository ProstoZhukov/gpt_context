package ru.tensor.sbis.communicator.core.views

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.scroll_to_top.ScrollToTop
import ru.tensor.sbis.design.scroll_to_top.ScrollToTopHelper
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.list_header.HeaderDateView

/**
 * Хелпер для компонента [ScrollToTop] с поддержкой вертикального положения для [HeaderDateView].
 *
 * @author vv.chekurda
 */
@Deprecated("Отказываемся от ScrollToTopHelper", ReplaceWith("ScrollToTopSubscriptionHolder"))
class ScrollToTopHelperWithHeaders(fragment: Fragment) : ScrollToTopHelper(fragment) {

    @JvmOverloads
    fun initViews(
        scrollToTop: ScrollToTop,
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        listView: AbstractListView<out View, Any>? = null,
        sbisToolbar: Toolbar,
        headerDateView: HeaderDateView,
        pinnedHeaderView: View? = null
    ) {
        this.initViews(
            scrollToTop,
            appBarLayout,
            collapsingToolbarLayout,
            listView,
            sbisToolbar,
            pinnedHeaderView = pinnedHeaderView,
            pinnedHeaderViewOffsetChangedListener = { pinnedViewOffset: Int ->
                headerDateView.translationY = pinnedViewOffset.toFloat()
            }
        )
    }

    override fun updateProgressAndInformationViewVerticalMargins() = Unit
}