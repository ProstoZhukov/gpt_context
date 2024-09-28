package ru.tensor.sbis.logging

import android.content.Context

/**
 * Интерфейс объекта хранящего и предоставляющего доступ к экземпляру [LoggingComponent].
 *
 * @author av.krymov
 */
interface LoggingComponentProvider {

    companion object {
        @JvmStatic
        fun get(context: Context): LoggingComponent {
            /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
            return LoggingPlugin.loggingComponent
        }
    }
}