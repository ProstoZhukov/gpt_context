package ru.tensor.sbis.dashboard_builder.internal

import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest
import ru.tensor.sbis.widget_player.converter.WidgetBodyStream

/**
 * @author am.boldinov
 */
internal interface DashboardBodyConverter {

    fun convert(request: DashboardRequest): WidgetBodyStream
}