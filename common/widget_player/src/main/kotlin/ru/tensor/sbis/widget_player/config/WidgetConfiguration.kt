package ru.tensor.sbis.widget_player.config

import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.widget_player.WidgetPlayerPlugin
import ru.tensor.sbis.widget_player.contract.WidgetPlayerStoreInitializer
import ru.tensor.sbis.widget_player.contract.WidgetStoreBuilder

/**
 * @author am.boldinov
 */
class WidgetConfiguration internal constructor(
    internal val context: SbisThemedContext,
    internal val widgetStore: WidgetStore,
    val options: WidgetOptions
) {

    companion object {

        private val defaultConfiguration by lazy {
            create()
        }

        fun getDefault(): WidgetConfiguration {
            return defaultConfiguration
        }

        /**
         * Создает конфигурацию виджетов.
         *
         * @param context темизированный контекст.
         * Необходимо передать в случае если тема конкретной активити может отличаться от темы приложения.
         * @param init билдер для конфигурации виджетов: добавление прикладных виджетов, изменений опций и свойств базовых виждетов.
         */
        fun create(
            context: SbisThemedContext = WidgetPlayerPlugin.component.themedContext,
            init: (ConfigurationBuilder.() -> Unit) = {}
        ): WidgetConfiguration {
            return ConfigurationBuilder(
                context = context,
                initializers = WidgetPlayerPlugin.component.initializers
            ).run {
                init()
                build()
            }
        }
    }
}

class ConfigurationBuilder(
    private val context: SbisThemedContext,
    initializers: Set<WidgetPlayerStoreInitializer>
) : WidgetOptionsBuilder<WidgetConfiguration>() {

    private val widgets = DefaultWidgetStoreBuilder()
    private val options = RTWidgetOptionsBuilder(context)

    init {
        initializers.forEach {
            it.run {
                widgets.initialize()
            }
        }
    }

    fun widgets(init: WidgetStoreBuilder.() -> Unit) {
        widgets.apply(init)
    }

    fun options(init: RTWidgetOptionsBuilder.() -> Unit) {
        options.apply(init)
    }

    override fun build(): WidgetConfiguration {
        val options = options.build()
        return WidgetConfiguration(context, widgets.build(options), options)
    }

}

@WidgetOptionsDslBuilderMarker
abstract class WidgetOptionsBuilder<T> {

    internal abstract fun build(): T
}

@DslMarker
internal annotation class WidgetOptionsDslBuilderMarker