package ru.tensor.sbis.widget_player.contract

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory

/**
 * Компонент для регистрации виджетов в плеере.
 *
 * @author am.boldinov
 */
interface WidgetStoreBuilder {

    fun widget(vararg tag: String, component: WidgetComponentFactory)
}