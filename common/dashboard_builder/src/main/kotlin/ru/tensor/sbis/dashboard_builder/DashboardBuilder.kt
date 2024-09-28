package ru.tensor.sbis.dashboard_builder

import androidx.annotation.AnyThread
import ru.tensor.sbis.dashboard_builder.config.DashboardConfiguration
import ru.tensor.sbis.dashboard_builder.internal.DashboardBodyJsonConverter
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest
import ru.tensor.sbis.widget_player.converter.WidgetBodyStream

/**
 * Конструктор дашборда для отрисовки с помощью [ru.tensor.sbis.widget_player.WidgetPlayer].
 *
 * @author am.boldinov
 */
object DashboardBuilder {

    @AnyThread
    @JvmStatic
    fun build(
        request: DashboardRequest,
        configuration: DashboardConfiguration = DashboardConfiguration.getDefault()
    ): WidgetBodyStream {
        return DashboardBodyJsonConverter(configuration).convert(request)
    }
}