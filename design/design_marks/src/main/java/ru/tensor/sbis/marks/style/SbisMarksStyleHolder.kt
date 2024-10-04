package ru.tensor.sbis.marks.style

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.marks.R
import ru.tensor.sbis.marks.item.SbisMarksElementView

/**
 * Ресурсы для [SbisMarksElementView].
 *
 * @author ra.geraskin
 */
internal data class SbisMarksStyleHolder(

    /** Высота всех элементов шапки. */
    @Dimension
    var headerItemsHeight: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var headerCancelButtonEndPadding: Int = 0,

    /** Высота всего элемента пометки. */
    @Dimension
    var elementHeight: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var elementVerticalPadding: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var checkboxSpaceHeight: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var checkboxSpaceVerticalPadding: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var checkboxSpaceHorizontalPadding: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var iconSpaceStartPadding: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var iconSpaceHeight: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var iconSpaceWidth: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var titleSpaceStartPadding: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var titleSpaceEndPadding: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var titleSpaceHeight: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var titleFontSize: Int = 0,

    /** @SelfDocumented */
    @ColorInt
    var titleFontColor: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var iconFontSize: Int = 0,

    /** @SelfDocumented */
    @ColorInt
    var iconFontColor: Int = 0,

    /** Размер кружка иконки. */
    @Dimension
    var colorCircleSize: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var markerWidth: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var markerHeight: Int = 0,

    /** @SelfDocumented */
    @Dimension
    var markerBorderRadius: Int = 0,

    /** @SelfDocumented */
    @ColorInt
    var markerColor: Int = 0
) {

    companion object {

        /**
         * Получение темизированного контекста в зависимости от параметров (default/large) и (isCheckboxVisible)
         */
        fun getThemedContext(context: Context, isCheckboxVisible: Boolean): Context {
            val currentContext =
                ThemeContextBuilder(context, R.attr.marksComponentTheme, R.style.MarksComponentDefaultTheme).build()
            val requiredStyle = if (isCheckboxVisible) {
                currentContext.getDataFromAttrOrNull(R.attr.marksComponentHeaderCheckBoxTheme)
                    ?: R.style.MarksComponentDefaultHeaderThemeCheckBox
            } else {
                currentContext.getDataFromAttrOrNull(R.attr.marksComponentHeaderTheme)
                    ?: R.style.MarksComponentDefaultHeaderTheme
            }
            return ContextThemeWrapper(context, requiredStyle)
        }

        /**
         * Создать [SbisMarksStyleHolder] с параметрами текущей темы (default/large).
         */
        fun create(context: Context, isCheckboxVisible: Boolean): SbisMarksStyleHolder =
            SbisMarksStyleHolder().apply {
                with(getThemedContext(context, isCheckboxVisible)) {
                    headerItemsHeight = getDimenPx(R.attr.marksComponentHeaderItemsHeight)
                    headerCancelButtonEndPadding = getDimenPx(R.attr.marksComponentHeaderCancelButtonEndPadding)
                    elementHeight = getDimenPx(R.attr.marksComponentElementHeight)
                    elementVerticalPadding = getDimenPx(R.attr.marksComponentElementVerticalPadding)
                    iconSpaceStartPadding = getDimenPx(R.attr.marksComponentIconSpaceStartPadding)
                    iconSpaceHeight = getDimenPx(R.attr.marksComponentIconSpaceHeight)
                    iconSpaceWidth = getDimenPx(R.attr.marksComponentIconSpaceWidth)
                    titleSpaceStartPadding = getDimenPx(R.attr.marksComponentTitleSpaceStartPadding)
                    titleSpaceEndPadding = getDimenPx(R.attr.marksComponentTitleSpaceEndPadding)
                    titleSpaceHeight = getDimenPx(R.attr.marksComponentTitleSpaceHeight)
                    titleFontSize = getDimenPx(R.attr.marksComponentTitleFontSize)
                    titleFontColor = getThemeColorInt(R.attr.marksComponentTitleFontColor)
                    iconFontSize = getDimenPx(R.attr.marksComponentIconFontSize)
                    iconFontColor = getThemeColorInt(R.attr.marksComponentIconFontColor)
                    colorCircleSize = getDimenPx(R.attr.marksComponentColorCircleSize)
                    markerWidth = getDimenPx(R.attr.marksComponentMarkerWidth)
                    markerHeight = getDimenPx(R.attr.marksComponentMarkerHeight)
                    markerBorderRadius = getDimenPx(R.attr.marksComponentMarkerBorderRadius)
                    markerColor = getThemeColorInt(R.attr.marksComponentMarkerColor)
                    checkboxSpaceHeight = getDimenPx(R.attr.marksComponentCheckboxSpaceHeight)
                    checkboxSpaceVerticalPadding = getDimenPx(R.attr.marksComponentCheckboxSpaceVerticalPadding)
                    checkboxSpaceHorizontalPadding = getDimenPx(R.attr.marksComponentCheckboxSpaceHorizontalPadding)
                }
            }
    }
}
