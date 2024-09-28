package ru.tensor.sbis.design.counters.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.properties.Delegates

/**
 * Аналог [Delegates.notNull] с возможностью задать кастомный сеттер.
 *
 * @author ps.smirnyh
 */
internal inline fun <T> delegateNotNull(crossinline setter: (T) -> Unit): ReadWriteProperty<Any, T> =
    object : ReadWriteProperty<Any, T> {
        var value: T? = null
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return checkNotNull(value) {
                "Property ${property.name} should be initialized before get."
            }
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            if (this.value == value) return
            this.value = value
            setter(value)
        }
    }
