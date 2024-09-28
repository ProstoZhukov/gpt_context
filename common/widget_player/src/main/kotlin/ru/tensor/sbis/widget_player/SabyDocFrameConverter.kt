package ru.tensor.sbis.widget_player

import androidx.annotation.WorkerThread
import ru.tensor.sbis.widget_player.config.WidgetConfiguration
import ru.tensor.sbis.widget_player.converter.sabydoc.SabyDocFrame
import ru.tensor.sbis.widget_player.converter.sabydoc.SabyDocFrameJsonConverter

/**
 * @author am.boldinov
 */
interface SabyDocFrameConverter {

    @WorkerThread
    fun convert(frame: String): SabyDocFrame

    @WorkerThread
    fun convertFromFile(filePath: String): SabyDocFrame

    companion object {

        @JvmStatic
        fun create(configuration: WidgetConfiguration = WidgetConfiguration.getDefault()): SabyDocFrameConverter {
            return SabyDocFrameJsonConverter(configuration)
        }
    }
}