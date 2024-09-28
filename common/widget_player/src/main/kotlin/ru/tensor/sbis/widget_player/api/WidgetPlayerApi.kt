package ru.tensor.sbis.widget_player.api

import ru.tensor.sbis.widget_player.converter.WidgetBody

/**
 * @author am.boldinov
 */
interface WidgetPlayerApi : WidgetPlayerViewApi, WidgetBodyApi {

    //var isAsyncMeasure: Boolean

    val body: WidgetBody?

    fun setWidgetSource(source: WidgetSource)
}