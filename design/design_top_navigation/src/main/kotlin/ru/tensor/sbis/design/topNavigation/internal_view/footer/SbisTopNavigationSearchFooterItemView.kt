package ru.tensor.sbis.design.topNavigation.internal_view.footer

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.util.SbisTopNavigationStyleHolder
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.view.input.searchinput.SearchInput

/**
 * Элемент подвала - поиск.
 *
 * @author da.zolotarev
 */
class SbisTopNavigationSearchFooterItemView @JvmOverloads constructor(
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
            searchView,
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

    val searchView = SearchInput(ContextThemeWrapper(this.context, R.style.SearchInputTheme)).apply {
        id = R.id.top_navigation_search_input_footer
        isRoundSearchInputBackground = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpecUtils.makeExactlySpec(styleHolder.footerSearchInputHeight))
    }
}