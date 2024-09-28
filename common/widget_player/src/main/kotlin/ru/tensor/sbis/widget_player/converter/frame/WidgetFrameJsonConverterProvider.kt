package ru.tensor.sbis.widget_player.converter.frame

import androidx.collection.LruCache
import ru.tensor.sbis.widget_player.WidgetConverter
import ru.tensor.sbis.widget_player.WidgetConverterProvider
import ru.tensor.sbis.widget_player.config.WidgetConfiguration

/**
 * @author am.boldinov
 */
internal class WidgetFrameJsonConverterProvider(
    cachedConfigSize: Int = 0
) : WidgetConverterProvider {

    private val cache =
        if (cachedConfigSize > 0) object : LruCache<WidgetConfiguration, WidgetConverter>(cachedConfigSize) {
            override fun create(key: WidgetConfiguration): WidgetConverter {
                return createConverter(key)
            }
        } else null

    override fun provide(configuration: WidgetConfiguration): WidgetConverter {
        return cache?.get(configuration) ?: createConverter(configuration)
    }

    private fun createConverter(configuration: WidgetConfiguration): WidgetConverter {
        return WidgetFrameJsonConverter(configuration)
    }
}