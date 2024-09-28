package ru.tensor.sbis.widget_player.converter

import java.util.UUID

/**
 * @author am.boldinov
 */
@JvmInline
value class WidgetID(val value: String) {

    companion object {

        fun generate(): WidgetID {
            return WidgetID(UUID.randomUUID().toString())
        }
    }
}