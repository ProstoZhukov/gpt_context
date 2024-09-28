package ru.tensor.sbis.widget_player.widget.root.layout

import androidx.core.view.updatePadding
import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget
import ru.tensor.sbis.widget_player.layout.widget.viewGroupRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
class RootLayoutWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = { tag, attributes, environment ->
            RootLayoutElement(tag, attributes, environment.resources)
        },
        inflater = {
            GroupWidget(
                context = this,
                renderer = viewGroupRenderer {
                    VerticalBlockLayout(this).apply {
                        setDefaultWidgetLayoutParams()
                        gapSize = rootLayoutOptions.columnGap.getValuePx(context)
                        updatePadding(
                            left = rootLayoutOptions.paddingLeft.getValuePx(context),
                            top = rootLayoutOptions.paddingTop.getValuePx(context),
                            right = rootLayoutOptions.paddingRight.getValuePx(context),
                            bottom = rootLayoutOptions.paddingBottom.getValuePx(context)
                        )
                    }
                }
            )
        }
    )
}