package ru.tensor.sbis.widget_player.layout.internal

import ru.tensor.sbis.widget_player.converter.WidgetBody

/**
 * @author am.boldinov
 */
internal interface WidgetHostAccessor {

    fun accessTo(access: HostAccess)
}

internal sealed interface HostAccess {

    class Body(val action: (body: WidgetBody) -> Unit) : HostAccess
}