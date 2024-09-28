package ru.tensor.sbis.design.stubview.layout_strategies

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.Px
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.R
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.IconMeasuringStrategy
import kotlin.math.max

/**
 * Стратегия компоновки заглушки для ландшафтного режима.
 *
 * Элементы располагаются следующим образом:
 *  * Слева иконка;
 *  * Справа заголовок и описание.
 * Описание находится под заголовков.
 *
 * Любой из элементов может отсутствовать. Если будут отсутствовать все, просто ничего не отобразится.
 *
 * @see LandscapeBlockStubViewComposer
 * @see PortraitStubViewComposer
 * @see PortraitBlockStubViewComposer
 *
 * @author ma.kolpakov
 */
internal open class LandscapeStubViewComposer(
    icon: View?,
    message: SbisTextView,
    details: TextView,
    iconMeasuringStrategy: IconMeasuringStrategy,
    context: Context,
) : BaseStubViewComposer(icon, message, details, iconMeasuringStrategy, context) {

    private companion object {
        const val PERCENT_55 = 0.55
    }

    private var textContainerWidth: Int = 0
    private var textsHeight: Int = 0
    private var iconWidth: Int = 0
    private var iconHeight: Int = 0
    private var contentHeight: Int = 0
    private var contentWidth: Int = 0
    private var iconVisible = false

    @Px
    private val minStubHeight = context.resources.getDimensionPixelSize(R.dimen.stub_view_min_height_landscape)

    private val displayIcon: Boolean
        get() = hasIcon && iconVisible

    override fun measure(@Px containerWidth: Int, @Px containerHeight: Int) {
        super.measure(containerWidth, containerHeight)

        if (hasAnyText) {
            measureTexts()
        }

        updateValues()
        updateIcon()
    }

    override fun layout(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
        icon?.layoutIcon(top)

        if (hasAnyText) {
            layoutTexts(top)
        }
    }

    /**
     * Для landscape не актуально (по крайней мере, пока что)
     */
    override fun maxHeight(): Int = 0

    /**
     * Подгонка размеров иконки и скрытие иконки, если для неё не хватает места по вертикали
     */
    private fun updateIcon() {
        if (icon == null) {
            return
        }

        val paddings =
            if (iconHeight < contentHeight) {
                (contentHeight - iconHeight) / 2 + stubViewTopPadding + stubViewPaddingVertical
            } else {
                stubViewTopPadding + stubViewPaddingVertical
            }

        val fullIconHeight = iconHeight + paddings

        if (fullIconHeight > containerHeight) {
            icon.measure(
                View.MeasureSpec.makeMeasureSpec(containerHeight - paddings, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(containerHeight - paddings, View.MeasureSpec.EXACTLY)
            )
        }

        if (containerHeight - paddings < minStubHeight) {
            icon.visibility = View.GONE
            iconVisible = false
        } else {
            icon.visibility = View.VISIBLE
            iconVisible = true
        }
        updateValues()
    }

    private fun updateValues() {
        textsHeight =
            when {
                hasMessage && hasDetails -> message.measuredHeight + messageBottomPadding + details.measuredHeight
                hasMessage               -> message.measuredHeight
                hasDetails               -> details.measuredHeight
                else                     -> 0
            }

        iconHeight =
            when {
                hasIcon -> max(icon!!.measuredHeight, iconMinSize)
                else    -> 0
            }

        iconWidth =
            when {
                hasIcon -> max(icon!!.measuredWidth, iconMinSize)
                else    -> 0
            }

        contentWidth =
            when {
                hasIcon && hasAnyText -> iconWidth + iconRightPadding + textContainerWidth
                hasIcon               -> iconWidth
                hasAnyText            -> textContainerWidth
                else                  -> 0
            }

        val maxContentHeight = max(textsHeight, iconHeight)
        contentHeight = max(maxContentHeight, minStubHeight)
    }

    private fun measureTexts() {
        textContainerWidth = (containerWidth * PERCENT_55).toInt()

        val textWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textContainerWidth, View.MeasureSpec.AT_MOST)

        // TODO: Будет доработано по (https://online.sbis.ru/opendoc.html?guid=c60f5791-5863-437d-a5a8-8ba0c994c1c4&client=3)
        // Безопасный фикс перед релизом
        message.measure(textWidthMeasureSpec, View.MeasureSpec.UNSPECIFIED)

        if (hasDetails) {
            details.measure(textWidthMeasureSpec, View.MeasureSpec.UNSPECIFIED)
        }
    }

    private fun View.layoutIcon(@Px paddingTop: Int) {
        val width = this.measuredWidth
        val height = this.measuredHeight
        val top =
            if (height < iconMinSize) {
                (contentHeight - iconMinSize) / 2 + (iconMinSize - height) / 2 + stubViewTopPadding
            } else {
                if (iconHeight > textsHeight) {
                    stubViewTopPadding
                } else {
                    (contentHeight - height) / 2 + stubViewTopPadding
                }
            } + paddingTop

        val left =
            if (width < iconMinSize) {
                (containerWidth - contentWidth) / 2 + (iconMinSize - width) / 2
            } else {
                (containerWidth - contentWidth) / 2
            }

        this.layoutByTopAndLeft(top, left)
    }

    private fun layoutTexts(@Px paddingTop: Int) {
        val top =
            if (displayIcon) {
                if (iconHeight > textsHeight) {
                    (iconHeight - textsHeight) / 2 + stubViewTopPadding
                } else {
                    (contentHeight - textsHeight) / 2 + stubViewTopPadding
                }
            } else {
                val tenPercentsOfContainerHeight = (containerHeight * PERCENT_10).toInt()
                (contentHeight - textsHeight) / 2 + tenPercentsOfContainerHeight.coerceAtLeast(stubViewTopPadding)
            } + paddingTop

        val leftShift =
            if (displayIcon) {
                (containerWidth - contentWidth) / 2 + iconWidth + iconRightPadding
            } else {
                (containerWidth - textContainerWidth) / 2
            }

        if (hasMessage) {
            val messageLeft = (textContainerWidth - message.measuredWidth) / 2 + leftShift
            message.layoutByTopAndLeft(top, messageLeft)
        }

        if (hasDetails) {
            val detailsLeft = (textContainerWidth - details.measuredWidth) / 2 + leftShift
            val detailsTop =
                if (hasMessage) {
                    message.bottom + messageBottomPadding
                } else {
                    top
                }
            details.layoutByTopAndLeft(detailsTop, detailsLeft)
        }
    }
}
