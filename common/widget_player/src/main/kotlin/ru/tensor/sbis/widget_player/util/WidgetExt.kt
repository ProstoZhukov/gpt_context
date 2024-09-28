package ru.tensor.sbis.widget_player.util

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import ru.tensor.sbis.widget_player.R
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.layout.widget.Widget

/**
 * @author am.boldinov
 */
internal fun View.setWidget(widget: Widget<*>) {
    setTag(R.id.widget_player_view_widget, widget)
}

internal fun View.getWidget(): Widget<*>? = getTag(R.id.widget_player_view_widget) as? Widget<*>

internal fun View.setDefaultWidgetLayoutParams(): MarginLayoutParams {
    return MarginLayoutParams(
        MarginLayoutParams.MATCH_PARENT,
        MarginLayoutParams.WRAP_CONTENT
    ).also {
        layoutParams = it
    }
}