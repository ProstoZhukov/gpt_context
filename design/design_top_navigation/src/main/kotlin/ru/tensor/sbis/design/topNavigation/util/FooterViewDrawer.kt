package ru.tensor.sbis.design.topNavigation.util

import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.FrameLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.view.input.searchinput.SearchInput

/**
 * Утилита для формирования подвала.
 *
 * @author da.zolotarev
 */
internal sealed class FooterViewDrawer {
    /**
     * @SelfDocumented
     */
    abstract fun init(view: FrameLayout, styleHolder: SbisTopNavigationStyleHolder)

    /**
     * @SelfDocumented
     */
    abstract fun getHeightMeasureSpec(styleHolder: SbisTopNavigationStyleHolder): Int

    /**
     * @see [SbisTopNavigationContent.SearchInput]
     */
    object SearchInput : FooterViewDrawer() {

        override fun init(view: FrameLayout, styleHolder: SbisTopNavigationStyleHolder) {
            SearchInput(ContextThemeWrapper(view.context, R.style.SearchInputTheme)).apply {
                id = R.id.top_navigation_search_input_footer
                isRoundSearchInputBackground = true
            }.also {
                view.addView(
                    it,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = styleHolder.searchInputHorizontalPadding
                        marginEnd = styleHolder.searchInputHorizontalPadding
                        gravity = Gravity.CENTER_VERTICAL
                    }
                )
            }
        }

        override fun getHeightMeasureSpec(styleHolder: SbisTopNavigationStyleHolder) = MeasureSpecUtils.makeExactlySpec(
            styleHolder.footerSearchInputHeight
        )

    }

    /**
     * @see [SbisTopNavigationContent.EmptyContent]
     */
    object Empty : FooterViewDrawer() {
        override fun init(view: FrameLayout, styleHolder: SbisTopNavigationStyleHolder) = Unit
        override fun getHeightMeasureSpec(styleHolder: SbisTopNavigationStyleHolder) =
            MeasureSpecUtils.makeExactlySpec(0)
    }
}