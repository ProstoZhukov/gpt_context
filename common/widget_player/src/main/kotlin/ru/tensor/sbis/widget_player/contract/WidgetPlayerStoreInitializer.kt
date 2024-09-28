package ru.tensor.sbis.widget_player.contract

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для регистрации виджетов для проигрывания в [ru.tensor.sbis.widget_player.WidgetPlayer].
 * Регистрируется в плагинной системе в качестве api модуля, пример:
 * FeatureWrapper(WidgetPlayerStoreInitializer::class.java) { MotivationWidgetInitializer() }
 *
 * @author am.boldinov
 */
fun interface WidgetPlayerStoreInitializer : Feature {

    fun WidgetStoreBuilder.initialize()
}