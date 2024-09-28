package ru.tensor.sbis.widget_player.config

import ru.tensor.sbis.widget_player.BuildConfig
import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.contract.WidgetStoreBuilder
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.converter.internal.ConverterParams

/**
 * @author am.boldinov
 */
class WidgetStore internal constructor(
    private val store: Map<String, WidgetComponent<WidgetElement>>
) {

    companion object {

        val EMPTY = WidgetStore(emptyMap())
    }

    fun get(widget: String): WidgetComponent<WidgetElement>? {
        return store[widget.lowercase()]
    }
}

internal class DefaultWidgetStoreBuilder : WidgetStoreBuilder {

    private val store = mutableMapOf<String, WidgetComponentFactory>()

    override fun widget(vararg tag: String, factory: WidgetComponentFactory) {
        tag.forEach {
            store[it.lowercase()] = factory
        }
    }

    fun build(options: WidgetOptions): WidgetStore {
        val widgets = store.mapValues {
            @Suppress("UNCHECKED_CAST")
            it.value.run { options.create() } as WidgetComponent<WidgetElement>
        }
        return WidgetStore(widgets).apply {
            validateStore()
        }
    }

    private fun WidgetStore.validateStore() {
        if (BuildConfig.DEBUG) {
            if (get(ConverterParams.ReservedTag.TEXT_WIDGET) == null) {
                error("Store must contains a text widget component, please register it")
            }
        }
    }
}

