package ru.tensor.sbis.widget_player.widget.paragraph

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.inline.InlineLayoutCompatFactory
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget
import ru.tensor.sbis.widget_player.layout.widget.viewGroupRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class ParagraphWidgetComponent(
    private val level: ParagraphLevel? = null
) : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = ParagraphElementFactory(paragraphOptions, level),
        inflater = {
            GroupWidget(
                context = this,
                renderer = viewGroupRenderer {
                    InlineLayoutCompatFactory.create(this).apply {
                        val verticalMargin = paragraphOptions.verticalMargin.getValuePx(context)
                        minimumHeight = paragraphOptions.minHeight.getValuePx(context)
                        setDefaultWidgetLayoutParams().apply {
                            topMargin = verticalMargin
                            bottomMargin = verticalMargin
                        }
                    }
                }
            )
        }
    )
}