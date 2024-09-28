package ru.tensor.sbis.dashboard_builder

import ru.tensor.sbis.widget_player.contract.WidgetPlayerStoreInitializer
import ru.tensor.sbis.widget_player.contract.WidgetStoreBuilder
import ru.tensor.sbis.widget_player.widget.root.layout.RootLayoutWidgetComponent

/**
 * Регистрирует базовый обязательный набор виджетов для дашбордов.
 *
 * @author am.boldinov
 */
internal class DashboardWidgetDefaultInitializer : WidgetPlayerStoreInitializer {

    override fun WidgetStoreBuilder.initialize() {
        widget("Dashboard/new/dashboard:View", component = RootLayoutWidgetComponent())
    }
}