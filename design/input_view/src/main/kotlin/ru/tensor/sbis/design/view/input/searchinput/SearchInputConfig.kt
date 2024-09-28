package ru.tensor.sbis.design.view.input.searchinput

import android.content.Context
import android.graphics.Color.BLACK
import android.graphics.Color.MAGENTA
import android.graphics.Paint
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.view.input.R

/**
 * @author ma.kolpakov
 */
internal data class SearchInputConfig(
    val style: SearchInputStyle = SearchInputStyle(),
    val property: SearchInputProperty = SearchInputProperty()
) {
    class SearchInputStyle {
        //      colors
        var iconColor = MAGENTA
        var baseColor = MAGENTA
        var additionalColor = MAGENTA
        val bottomDividerPaint = Paint(BLACK)
        val separatorPaint = Paint(BLACK)
        var searchTextColor = MAGENTA

        //      dimens
        var touchPadding: Int = 0
        var searchTextSize = 0f
        var loupeSize = 0
        var minSearchSize = 0
        var celarSize = 0
        var leftPadding = 0
        var horizontalInputOffset = 0
        var horizontalClearOffset = 0
        var verticalDividerSize = 0
        var verticalDividerHeight = 0
        var bottomDividerHeight = 0
        var panelHeight = 0
        var clearIconSize: Int = 0
    }

    class SearchInputProperty {
        var inputType: Int = 0
        var imeOptions: Int = EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_SEARCH
        var hasFilter = true

        /**
         * Нужно ли скрывать клавиатуру при откреплении view.
         * Фиксит проблему, когда панель открепилась (например, при скролле) и не может скрыть клавиатуру
         */
        var hideKeyboardOnDetach = false
        var showCurrentFilters = true
        lateinit var loupeIconText: String
        lateinit var clearIconText: String
        var inputDelay = DEFAULT_SEARCH_DELAY
        var isVisibleLoupe = true
        lateinit var searchHint: String
        var additionalTextSpace = 0
        var searchColor = 0

        /** Радиус скругления панели поиска */
        var searchCornerRadius = 0f
        var searchInputSize = 0
        var bottomDividerVisible = false

    }

    fun initStyle(
        context: Context,
        attributeSet: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defaultStyle: Int
    ) {
        property.loupeIconText =
            context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_search)
        property.clearIconText =
            context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_toolbar_close)
        property.searchHint =
            context.getString(ru.tensor.sbis.design.R.string.design_search_panel_hint)
        property.additionalTextSpace =
            context.resources.getDimensionPixelSize(R.dimen.input_view_search_panel_additional_text_width)

        val styleRes = ThemeContextBuilder(context, defStyleAttr, defaultStyle).buildThemeRes()
        context.withStyledAttributes(
            attributeSet,
            R.styleable.SearchInput,
            defStyleAttr,
            styleRes
        ) {
            with(property) {
                searchColor = getInteger(R.styleable.SearchInput_searchInput_color, 1)
                searchCornerRadius = getDimension(R.styleable.SearchInput_searchInput_cornerRadius, 0f)
                searchInputSize = getInteger(R.styleable.SearchInput_searchInput_size, 1)
                hasFilter = getBoolean(R.styleable.SearchInput_hasFilter, true)
                hideKeyboardOnDetach =
                    getBoolean(R.styleable.SearchInput_hideKeyboardOnDetach, false)
                loupeIconText = getString(R.styleable.SearchInput_loupeIconText) ?: loupeIconText
                showCurrentFilters = getBoolean(R.styleable.SearchInput_showCurrentFilters, true)
                inputDelay = getInteger(
                    R.styleable.SearchInput_inputDelay,
                    DEFAULT_SEARCH_DELAY.toInt()
                ).toLong()
                isVisibleLoupe = getBoolean(R.styleable.SearchInput_isVisibleLoupe, true)
                imeOptions = getInt(
                    R.styleable.SearchInput_imeOptions,
                    EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_SEARCH
                )
                inputType = getInt(R.styleable.SearchInput_inputType, 0)
                val hint = getString(R.styleable.SearchInput_searchHint)
                if (hint != null) {
                    searchHint = hint
                }
                bottomDividerVisible =
                    getBoolean(R.styleable.SearchInput_bottomDividerVisible, false)
            }

            with(style) {
                iconColor = getColor(R.styleable.SearchInput_searchIconColor, MAGENTA)
                separatorPaint.color =
                    getColor(R.styleable.SearchInput_searchSeparatorColor, MAGENTA)
                baseColor = getColor(R.styleable.SearchInput_baseColor, MAGENTA)
                additionalColor = getColor(R.styleable.SearchInput_additionalColor, MAGENTA)
                searchTextColor = getColor(R.styleable.SearchInput_searchTextColor, MAGENTA)
                val maxTextSize =
                    context.getDimenPx(ru.tensor.sbis.design.R.attr.fontSize_2xl_scaleOff)
                loupeSize = getDimensionPixelSize(R.styleable.SearchInput_searchIconSize, 0)
                clearIconSize = context.getDimenPx(ru.tensor.sbis.design.R.attr.iconSize_xs)
                leftPadding = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_m)
                celarSize = context.getDimenPx(ru.tensor.sbis.design.R.attr.iconSize_xs)
                horizontalInputOffset = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_s)
                horizontalClearOffset = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_l)
                searchTextSize =
                    context.getDimenPx(ru.tensor.sbis.design.R.attr.fontSize_m_scaleOn)
                        .coerceAtMost(maxTextSize).toFloat()
                minSearchSize =
                    context.resources.getDimensionPixelSize(R.dimen.input_view_search_panel_min_size)
                verticalDividerSize =
                    context.getDimenPx(ru.tensor.sbis.design.R.attr.borderThickness_s)
                touchPadding =
                    context.resources.getDimensionPixelSize(R.dimen.input_view_search_panel_clear_touch_padding)

                verticalDividerHeight =
                    context.resources.getDimensionPixelSize(R.dimen.input_view_search_panel_separator_height)
                bottomDividerPaint.color =
                    getColor(R.styleable.SearchInput_bottomDividerColor, MAGENTA)
                bottomDividerHeight =
                    context.getDimenPx(ru.tensor.sbis.design.R.attr.borderThickness_s)

                panelHeight = context.getDimenPx(
                    if (property.searchInputSize == 1) {
                        ru.tensor.sbis.design.R.attr.inlineHeight_xs
                    } else {
                        ru.tensor.sbis.design.R.attr.inlineHeight_4xs
                    }
                )
            }
        }
    }
}