package ru.tensor.sbis.platform_event_manager

import android.content.Context

/**
 * Поставщик [EventManagerComponent].
 * Позволяет получить [EventManagerComponent] из [EventManagerPlugin] приложения.
 *
 * @author unknown
 */
object EventManagerComponentProvider {

    fun get(context: Context): EventManagerComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return EventManagerPlugin.eventManagerComponent
    }

}