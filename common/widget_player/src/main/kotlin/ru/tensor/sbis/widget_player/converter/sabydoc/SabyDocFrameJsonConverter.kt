package ru.tensor.sbis.widget_player.converter.sabydoc

import ru.tensor.sbis.widget_player.SabyDocFrameConverter
import ru.tensor.sbis.widget_player.config.WidgetConfiguration
import ru.tensor.sbis.widget_player.converter.WidgetBodyJsonConverter

/**
 * @author am.boldinov
 */
internal class SabyDocFrameJsonConverter(
    configuration: WidgetConfiguration
) : SabyDocFrameConverter {

    private val contentsBuilder = SabyDocContentsBuilder()
    private val bodyConverter = WidgetBodyJsonConverter(configuration)

    override fun convert(frame: String): SabyDocFrame {
        return SabyDocFrame(
            body = bodyConverter.convert(
                SabyDocFrameJsonParser(frame, contentsBuilder)
            ),
            tableOfContents = contentsBuilder.build()
        )
    }

    override fun convertFromFile(filePath: String): SabyDocFrame {
        return SabyDocFrame(
            body = bodyConverter.convert(
                SabyDocFrameFilePathParser(filePath, contentsBuilder)
            ),
            tableOfContents = contentsBuilder.build()
        )
    }
}