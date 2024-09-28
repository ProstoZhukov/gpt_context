package ru.tensor.sbis.widget_player.widget.link

import android.view.ViewGroup
import android.widget.FrameLayout
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkType
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.WidgetRenderer
import ru.tensor.sbis.widget_player.widget.text.TextOptions

/**
 * @author am.boldinov
 */
internal class DecoratedLinkRenderer(
    private val context: WidgetContext,
    private val linkOptions: DecoratedLinkOptions,
    private val textOptions: TextOptions
) : WidgetRenderer<DecoratedLinkElement> {

    override val view = FrameLayout(context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
            ViewGroup.MarginLayoutParams.WRAP_CONTENT
        )
    }

    private val smallView by lazy(LazyThreadSafetyMode.NONE) {
        DecoratedLinkSmallView(
            context,
            linkOptions.linkOpener,
            linkOptions.linkStyle.small
        )
    }

    private val mediumView by lazy(LazyThreadSafetyMode.NONE) {
        DecoratedLinkMediumView(
            context,
            linkOptions.linkOpener,
            linkOptions.repository,
            linkOptions.linkStyle.medium
        )
    }

    override fun render(element: DecoratedLinkElement) {
        view.removeAllViews()
        val linkView = if (element.linkType == DecoratedLinkType.SMALL) {
            smallView.apply {
                setTextSize(element.style.fontSize.getValue(context))
            }
        } else {
            mediumView
        }
        linkView.apply {
            setLinksClickable(textOptions.linksClickable)
            setLinkData(element.linkData)
        }
        view.addView(linkView)
    }
}