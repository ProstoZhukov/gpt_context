package ru.tensor.sbis.widget_player.api

import android.graphics.Rect
import android.view.View
import ru.tensor.sbis.widget_player.converter.WidgetID

/**
 * @author am.boldinov
 */
interface WidgetPlayerViewApi : WidgetPlayerOffsetApi {

    var scrollingMode: ScrollingMode

    fun getVisibleWidgetRect(id: WidgetID): Rect?

    fun findViewByWidgetId(id: WidgetID): View?

    fun getVisibleWidgetRect(widget: View, rect: Rect)
}

enum class ScrollingMode {
    NONE,
    VERTICAL
}