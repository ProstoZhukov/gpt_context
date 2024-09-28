package ru.tensor.sbis.design.scroll_to_top

import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.navigation.util.ScrollToTopSubscriptionHolder
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.utils.PinnedHeaderViewOffsetChangedListener

/**
 * Хелпер для работы с компонентом [ScrollToTop] при использовании [AbstractListView]
 *
 * @author du.bykov
 */
@Deprecated("Отказываемся от ScrollToTopHelper", ReplaceWith("ScrollToTopSubscriptionHolder"))
open class ScrollToTopHelper(fragment: Fragment) : AbstractScrollToTopHelper(fragment) {

    protected var listView: AbstractListView<out View, *>? = null
    private var customListView: View? = null
    private var sbisToolbar: Toolbar? = null

    @Suppress("MemberVisibilityCanBePrivate")
    protected var isKeyboardOpen: Boolean = false
    private var ignoreListTopMarginUpdates = false

    /**
     * Инициализация хелпера.
     */
    @JvmOverloads
    fun initViews(
        scrollToTop: ScrollToTop,
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        listView: AbstractListView<out View, *>? = null,
        sbisToolbar: Toolbar,
        customListView: View? = null,
        pinnedHeaderView: View? = null,
        pinnedHeaderViewOffsetChangedListener: PinnedHeaderViewOffsetChangedListener? = null
    ) {
        this.initViews(
            scrollToTop,
            appBarLayout,
            collapsingToolbarLayout,
            pinnedHeaderView,
            pinnedHeaderViewOffsetChangedListener
        )
        this.sbisToolbar = sbisToolbar
        this.listView = listView
        this.customListView = customListView
        /*
        Правка ScrollToTopHelper при использовании в нескольких вкладках, у которых есть host, держащий вьюшки, использующиеся в ScrollToTopHelper
        Если были на вкладке с прокручиваемым контентом и перешли на вкладку с непрокручиваемым, то скрытие тулбара всё равно активно
        https://online.sbis.ru/opendoc.html?guid=ae69046e-6b40-4c13-998b-dec1d304fdf9
        */
        disableScrollToTopDirectly(false)
        fragment?.apply {
            lifecycleScope.launchWhenResumed {
                ScrollToTopSubscriptionHolder.event.collect {
                    if (isScrollToTopEnabled()) scrollToTop.callOnClick()
                }
            }
        }
    }

    override fun updateProgressAndInformationViewVerticalMargins() {
        // Паддинги отчасти костыльные, будет переделано в задаче:
        // https://online.sbis.ru/doc/b808d21e-ec20-494f-b1c1-2dc9ce3aee12
        listView?.run {
            val appBarHeight = appBarLayout?.height ?: 0
            val padding = bottomNavBarHeight + appBarHeight
            if (ignoreAppBarOffsetChanges) {
                if (isKeyboardOpen) {
                    val landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    if (landscape) {
                        setInformationViewVerticalPadding(0, scrollToTopPanelHeight)
                    } else {
                        setInformationViewVerticalPadding(0, 0)
                    }
                    setProgressBarVerticalMargin(0, 0)
                } else {
                    setProgressBarVerticalMargin(appBarHeight / 2, padding / 2)
                    setInformationViewVerticalPadding(0, padding / 2)
                }
            } else {
                if (isKeyboardOpen) {
                    setProgressBarVerticalMargin(appBarHeight / 2, padding / 2)
                    setInformationViewVerticalPadding(padding * 2, 0)
                } else {
                    setProgressBarVerticalMargin(0, padding / 2)
                    setInformationViewVerticalPadding(0, padding)
                }
            }
        }
    }

    override fun updateListViewTopMargin(@Px topMargin: Int) {
        if (ignoreListTopMarginUpdates) return
        (listView ?: customListView)?.run {
            val listViewParams = layoutParams as ViewGroup.MarginLayoutParams
            if (listViewParams.topMargin != topMargin) {
                listViewParams.topMargin = topMargin
                requestLayout()
            }
        }
    }

    override fun setToolbarButtonsClickable(clickable: Boolean) {
        sbisToolbar?.rightIcon1?.isClickable
    }

    override fun setToolbarContentAlpha(alpha: Float) {
        sbisToolbar?.toolbarContainer?.alpha = alpha
    }

    /** @SelfDocumented */
    fun setIsKeyboardOpen(isOpen: Boolean) {
        isKeyboardOpen = isOpen
    }

    /** @SelfDocumented */
    @Suppress("unused")
    fun setIgnoreListTopMarginUpdates(ignore: Boolean) {
        ignoreListTopMarginUpdates = ignore
    }

    override fun dispose() {
        super.dispose()
        listView = null
        customListView = null
        sbisToolbar = null
    }
}