package ru.tensor.sbis.design.topNavigation.internal_view.footer

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.util.SbisTopNavigationStyleHolder
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Элемент подвала - поиск.
 *
 * @author da.zolotarev
 */
class SbisTopNavigationTabsFooterItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes
    defStyleAttr: Int = R.attr.sbisTopNavigationTheme,
    @StyleRes
    defStyleRes: Int = R.style.SbisTopNavigationDefaultStyle
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    internal constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.sbisTopNavigationTheme,
        @StyleRes
        defStyleRes: Int = R.style.SbisTopNavigationDefaultStyle,
        styleHolder: SbisTopNavigationStyleHolder
    ) : this(context, attrs, defStyleAttr, defStyleRes) {
        this.styleHolder = styleHolder
        addView(
            tabsView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = styleHolder.searchInputHorizontalPadding
                marginEnd = styleHolder.searchInputHorizontalPadding
                gravity = Gravity.CENTER_VERTICAL
            }
        )
    }

    private var styleHolder = SbisTopNavigationStyleHolder()

    internal val tabsView = SbisTabsView(this.context).apply {
        id = R.id.top_navigation_tabs_footer
    }
}