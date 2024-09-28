package ru.tensor.sbis.date_picker.di

import android.content.Context
import ru.tensor.sbis.date_picker.DatePickerPlugin

/**
 * Провайдер для [DatePickerComponent]
 *
 * @author kv.martyshenko
 */
internal object DatePickerComponentProvider {

    @JvmStatic
    fun get(context: Context): DatePickerComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return DatePickerPlugin.component
    }

    @JvmStatic
    fun getComponentHolder(): DatePickerComponentHolder {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return DatePickerPlugin
    }
}