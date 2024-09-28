package ru.tensor.sbis.design.stubview.layout_strategies

import android.content.Context
import android.view.View
import android.view.View.*
import android.widget.TextView
import androidx.annotation.Px
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.R
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.IconMeasuringStrategy
import kotlin.math.max
import kotlin.math.min

/**
 * Стратегия компоновки заглушки для портретного режима.
 *
 * Элементы располагаются сверху вниз в следующем порядке:
 * * Иконка;
 * * Заголовок;
 * * Описание.
 *
 * Любой из элементов может отсутствовать. Если будут отсутствовать все, просто ничего не отобразится.
 *
 * @see PortraitBlockStubViewComposer
 * @see LandscapeStubViewComposer
 * @see LandscapeBlockStubViewComposer
 *
 * @author ma.kolpakov
 */
internal open class PortraitStubViewComposer(
    icon: View?,
    message: SbisTextView,
    details: TextView,
    iconMeasuringStrategy: IconMeasuringStrategy,
    context: Context,
) : BaseStubViewComposer(icon, message, details, iconMeasuringStrategy, context) {

    @Px
    private val minStubHeight = context.resources.getDimensionPixelSize(R.dimen.stub_view_min_height_portrait)

    private var maxContentHeightRequested: Boolean = false

    override fun measure(@Px containerWidth: Int, @Px containerHeight: Int) {
        super.measure(containerWidth, containerHeight)

        val textWidthMeasureSpec = MeasureSpec.makeMeasureSpec(containerWidthWithPadding, MeasureSpec.AT_MOST)

        // TODO: Будет доработано по (https://online.sbis.ru/opendoc.html?guid=c60f5791-5863-437d-a5a8-8ba0c994c1c4&client=3)
        // Безопасный фикс перед релизом
        message.measure(textWidthMeasureSpec, MeasureSpec.UNSPECIFIED)

        if (hasDetails) {
            details.measure(textWidthMeasureSpec, MeasureSpec.UNSPECIFIED)
        }
    }

    override fun layout(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
        // верхний отступ
        var topPadding =
            if (maxContentHeightRequested) {
                stubViewPaddingVertical
            } else {
                stubViewTopPadding
            } + top

        // влезает ли иконка по высоте в контейнер
        val isIconFit = getIsIconFit(topPadding)

        // высота контента с учётом наличия или отсутствия иконки
        val contentHeight = getContentHeight(isIconFit)

        topPadding = min((containerHeight - contentHeight) / 2, topPadding).coerceAtLeast(stubViewPaddingVertical)

        // дополнительный отступ сверху, если высота всего контента меньше 200dp
        val additionalTopPadding =
            if (contentHeight < minStubHeight) {
                (minStubHeight - contentHeight) / 2
            } else {
                0
            }

        // итоговый верхний отступ самого верхнего элемента
        val finalTopPadding = topPadding + additionalTopPadding

        if (isIconFit) {
            icon?.visibility = VISIBLE
            icon?.layoutIcon(finalTopPadding)
        } else {
            icon?.visibility = GONE
        }

        if (hasMessage) {
            layoutMessage(finalTopPadding, isIconFit)
        }

        if (hasDetails) {
            layoutDetails(finalTopPadding, isIconFit)
        }
        maxContentHeightRequested = false
    }

    override fun maxHeight(): Int {
        maxContentHeightRequested = true
        return getContentHeight(true) + stubViewPaddingVertical * 2
    }

    private fun layoutMessage(top: Int, iconPresent: Boolean) {
        val messageTop =
            when {
                !iconPresent -> top
                hasIcon      -> icon!!.bottom + iconBottomPadding
                else         -> top
            }
        message.layoutByTop(messageTop)
    }

    private fun layoutDetails(top: Int, iconPresent: Boolean) {
        val detailsTop =
            when {
                hasMessage   -> message.bottom + messageBottomPadding
                !iconPresent -> top
                hasIcon      -> icon!!.bottom + iconBottomPadding
                else         -> top
            }
        details.layoutByTop(detailsTop)
    }

    private fun View.layoutByTop(@Px top: Int) {
        val width = this.measuredWidth
        val left = (containerWidth - width) / 2
        val right = left + width
        val bottom = top + this.measuredHeight
        this.layout(left, top, right, bottom)
    }

    /**
     * Установка положения иконки.
     * Если высота иконки меньше [iconMinSize], она располагается внизу области высотой [iconMinSize]
     */
    private fun View.layoutIcon(@Px top: Int) {
        val iconHeight = this.measuredHeight
        val iconTop: Int =
            if (iconHeight < iconMinSize) {
                top + iconMinSize - iconHeight
            } else {
                top
            }
        layoutByTop(iconTop)
    }

    private fun getIsIconFit(topPadding: Int): Boolean {
        val fullContentHeight = getContentHeight() + topPadding + stubViewPaddingVertical

        val additionalTopPadding =
            if (fullContentHeight < minStubHeight) {
                (minStubHeight - fullContentHeight) / 2
            } else {
                0
            }

        return hasIcon && fullContentHeight + additionalTopPadding <= containerHeight
    }

    /**
     * Общая высота всех элементов контента (иконка, заголовок, описание).
     * Учитываются отступы между элементами, но не учитываются самый верхний и самый нижний отступы.
     *
     * @param countIcon учитывать ли иконку при подсчёте размера
     */
    private fun getContentHeight(countIcon: Boolean = true): Int {
        var totalHeight = 0

        // высота иконки или минимальная высота иконки
        if (countIcon && hasIcon) {
            totalHeight += max(icon!!.measuredHeight, iconMinSize)
        }

        // высота заголовка
        if (hasMessage) {
            totalHeight += message.measuredHeight
        }

        // высота описания
        if (hasDetails) {
            totalHeight += details.measuredHeight
        }

        // вертикальный отступ между иконкой и текстом
        if (countIcon && hasIcon && hasAnyText) {
            totalHeight += iconBottomPadding
        }

        // вертикальный отступ между заголовком и описанием
        if (hasMessage && hasDetails) {
            totalHeight += messageBottomPadding
        }

        return totalHeight
    }
}
