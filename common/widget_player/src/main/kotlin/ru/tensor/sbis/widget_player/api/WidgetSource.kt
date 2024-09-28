package ru.tensor.sbis.widget_player.api

import ru.tensor.sbis.widget_player.config.WidgetConfiguration
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.WidgetBodyStream

/**
 * @author am.boldinov
 */
sealed interface WidgetSource {

    class Body(val body: WidgetBody?) : WidgetSource

    class BodyStream(val body: WidgetBodyStream) : WidgetSource

    class Frame(
        val frame: String,
        val configuration: WidgetConfiguration = WidgetConfiguration.getDefault()
    ) : WidgetSource

    class File(
        val filePath: String,
        val configuration: WidgetConfiguration = WidgetConfiguration.getDefault()
    ) : WidgetSource
}