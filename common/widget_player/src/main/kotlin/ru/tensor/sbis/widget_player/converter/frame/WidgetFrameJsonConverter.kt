package ru.tensor.sbis.widget_player.converter.frame

import ru.tensor.sbis.widget_player.WidgetConverter
import ru.tensor.sbis.widget_player.config.WidgetConfiguration
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.WidgetBodyJsonConverter

/**
 * @author am.boldinov
 */
internal class WidgetFrameJsonConverter(
    configuration: WidgetConfiguration
) : WidgetConverter {

    private val bodyConverter = WidgetBodyJsonConverter(configuration)

    override fun convert(frame: String): WidgetBody {
        return bodyConverter.convert(WidgetFrameJsonParser(frame))
    }

    override fun convertFromFile(filePath: String): WidgetBody {
        return bodyConverter.convert(WidgetFrameFilePathParser(filePath))
    }
}