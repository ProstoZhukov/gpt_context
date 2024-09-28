package ru.tensor.sbis.widget_player.converter

import android.view.View
import ru.tensor.sbis.widget_player.config.WidgetStore

/**
 * @author am.boldinov
 */
internal interface WidgetStoreOwner {
    val widgetStore: WidgetStore
}

internal fun View.findWidgetStoreOwner(): WidgetStoreOwner? {
    return if (this is WidgetStoreOwner) {
        this
    } else {
        (this.parent as? View)?.findWidgetStoreOwner()
    }
}