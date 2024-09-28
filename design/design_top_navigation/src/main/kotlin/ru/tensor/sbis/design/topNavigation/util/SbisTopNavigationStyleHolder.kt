package ru.tensor.sbis.design.topNavigation.util

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.Px
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.view_ext.UiUtils
import ru.tensor.sbis.design.R as RDesign

/**
 * Класс, содержаший ресурсные константы
 *
 * @author da.zolotarev
 */
internal class SbisTopNavigationStyleHolder {

    private val globalAttrs = intArrayOf(
        RDesign.attr.inlineHeight_l,
        RDesign.attr.fontSize_5xl_scaleOff,
        RDesign.attr.fontSize_xl_scaleOff,
        RDesign.attr.borderRadius_xs,
        RDesign.attr.offset_m,
        RDesign.attr.inlineHeight_s,
        RDesign.attr.borderColor,
        RDesign.attr.offset_xs
    )

    /** @SelfDocumented */
    var customTitleColor: SbisColor? = null

    /** @SelfDocumented */
    var customSubTitleColor: Int? = null

    /** @SelfDocumented */
    var customBackBtnTextColor: Int? = null

    /** @SelfDocumented */
    @Px
    var viewHeight = 0
        private set

    /** @SelfDocumented */
    @Px
    var oldToolbarHeight = 0
        private set

    /** @SelfDocumented */
    var largeTitleFontSize = 0f
        private set

    /** @SelfDocumented */
    var oldToolbarlargeTitleFontSize = 0f
        private set

    /** @SelfDocumented */
    var graphicBackgroundCornerRadius = 0f
        private set

    /** @SelfDocumented */
    @Px
    var searchInputHorizontalPadding = 0
        private set

    /** @SelfDocumented */
    @Px
    var footerSearchInputHeight = 0
        private set

    /** @SelfDocumented */
    @Px
    var backBtnRightPadding = 0
        private set

    /** @SelfDocumented */
    @Px
    var backBtnRightPaddingClickableContent = 0
        private set

    /** @SelfDocumented */
    @Px
    var backBtnLeftPadding = 0
        private set

    /** @SelfDocumented */
    @Px
    var backBtnWidth = 0
        private set

    /** @SelfDocumented */
    @Px
    var leftContentMinWidth = 0
        private set

    /** @SelfDocumented */
    @Px
    var leftContentWithSearchMinWidth = 0
        private set

    /** @SelfDocumented */
    @Px
    var dividerHeight = 0f
        private set

    /** @SelfDocumented */
    val dividerPaint = Paint()

    /** @SelfDocumented */
    @Px
    var titleMinTopMargin = 0
        private set

    /** @SelfDocumented */
    @Px
    var collapsedTopNavHeight: Int = 0

    /** @SelfDocumented */
    @Px
    var expandTopNavHeight: Int = 0

    /**
     * Установка значений ресурсов.
     */
    fun initResources(context: Context) {
        context.withStyledAttributes(
            attrs = globalAttrs
        ) {
            viewHeight = getDimenPx(RDesign.attr.inlineHeight_l, viewHeight)
            largeTitleFontSize = getDimen(RDesign.attr.fontSize_5xl_scaleOff, largeTitleFontSize)
            oldToolbarlargeTitleFontSize = getDimen(RDesign.attr.fontSize_xl_scaleOff, oldToolbarlargeTitleFontSize)
            graphicBackgroundCornerRadius = getDimen(RDesign.attr.borderRadius_xs, graphicBackgroundCornerRadius)
            searchInputHorizontalPadding = getDimenPx(RDesign.attr.offset_m, searchInputHorizontalPadding)
            leftContentMinWidth = getDimenPx(RDesign.attr.offset_m, leftContentMinWidth)
            leftContentWithSearchMinWidth = getDimenPx(RDesign.attr.offset_xs, leftContentWithSearchMinWidth)
            footerSearchInputHeight = getDimenPx(RDesign.attr.inlineHeight_s, footerSearchInputHeight)
            dividerPaint.color = getColor(globalAttrs.indexOf(RDesign.attr.borderColor), Color.MAGENTA)
            backBtnLeftPadding = getDimenPx(RDesign.attr.offset_xs, backBtnLeftPadding)
        }

        oldToolbarHeight = UiUtils.getToolBarHeight(context).coerceAtLeast(viewHeight)
        backBtnRightPadding = context.resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_back_btn_padding)
        backBtnRightPaddingClickableContent =
            context.resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_back_btn_padding_clickable_content)
        backBtnWidth =
            context.resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_back_btn_width_with_counter)
        dividerHeight = context.resources.getDimension(R.dimen.sbis_top_navigation_divider_height)
        titleMinTopMargin = context.resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_title_min_top_margin)
        collapsedTopNavHeight = InlineHeight.S.getDimenPx(context)
        expandTopNavHeight = InlineHeight.L.getDimenPx(context)
    }

    /** @SelfDocumented */
    fun setCollapsedHeight() {
        viewHeight = collapsedTopNavHeight
    }

    /** @SelfDocumented */
    fun setExpandHeight() {
        viewHeight = expandTopNavHeight
    }

    private fun TypedArray.getDimenPx(attr: Int, defValue: Int) =
        getDimensionPixelSize(globalAttrs.indexOf(attr), defValue)

    private fun TypedArray.getDimen(attr: Int, defValue: Float) = getDimension(globalAttrs.indexOf(attr), defValue)
}