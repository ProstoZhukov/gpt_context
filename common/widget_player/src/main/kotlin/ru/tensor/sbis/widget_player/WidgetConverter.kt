package ru.tensor.sbis.widget_player

import androidx.annotation.WorkerThread
import ru.tensor.sbis.widget_player.config.WidgetConfiguration
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.frame.WidgetFrameJsonConverter

/**
 * @author am.boldinov
 */
interface WidgetConverter {

    @WorkerThread
    fun convert(frame: String): WidgetBody

    @WorkerThread
    fun convertFromFile(filePath: String): WidgetBody

    companion object {

        @JvmStatic
        fun jsonFrame(configuration: WidgetConfiguration = WidgetConfiguration.getDefault()): WidgetConverter {
            return WidgetFrameJsonConverter(configuration)
        }
    }
}

fun interface WidgetConverterProvider {

    fun provide(configuration: WidgetConfiguration): WidgetConverter
}