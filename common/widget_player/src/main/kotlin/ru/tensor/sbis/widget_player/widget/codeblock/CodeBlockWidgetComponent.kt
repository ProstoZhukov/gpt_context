package ru.tensor.sbis.widget_player.widget.codeblock

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget

/**
 * @author am.boldinov
 */
internal class CodeBlockWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = CodeBlockElementFactory(codeBlockOptions),
        inflater = {
            GroupWidget(
                context = this,
                renderer = CodeBlockRenderer(
                    context = this,
                    options = codeBlockOptions
                )
            )
        }
    )
}