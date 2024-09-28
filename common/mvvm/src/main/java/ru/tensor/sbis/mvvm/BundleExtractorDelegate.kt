package ru.tensor.sbis.mvvm

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified T> extra(
    key: String,
    defaultValue: T
): ReadWriteProperty<Activity, T> =
    BundleExtractorDelegate { thisRef ->
        extractFromBundle(
            bundle = thisRef.intent?.extras,
            key = key,
            defaultValue = defaultValue
        )
    }

@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified T> Bundle?.extract(
    key: String,
    defaultValue: T? = null,
    isNullable: Boolean = null is T
): T = extractFromBundle(this, key, defaultValue, isNullable)

/**
 * Извлечь аргумент по ключу [key]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified T> Fragment.getArgument(
    key: String,
    defaultValue: T? = null,
    isNullable: Boolean = null is T
): T = extractFromBundle(arguments, key, defaultValue, isNullable)

/**
 * Извлечь аргумент по ключу [key]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified T> argument(
    key: String,
    defaultValue: T? = null,
    isNullable: Boolean = null is T
): ReadWriteProperty<Fragment, T> =
    BundleExtractorDelegate { thisRef ->
        extractFromBundle(
            bundle = thisRef.arguments,
            key = key,
            defaultValue = defaultValue,
            isNullableValue = isNullable
        )
    }

@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified T> extractFromBundle(
    bundle: Bundle?,
    key: String,
    defaultValue: T?,
    isNullableValue: Boolean = false
): T {
    var result = bundle?.get(key)
    if (result == null) {
        result = if (defaultValue == null && isNullableValue.not()) {
            if (BuildConfig.DEBUG) {
                throw NullPointerException("Property $key can't be null")
            }
            defaultValue
        } else {
            defaultValue
        }
    }

    if (result != null && result !is T) {
        throw ClassCastException("Property $key has different class type")
    }
    return result as T
}

@Deprecated("Устаревший подход, переходим на mvi_extension")
class BundleExtractorDelegate<R, T>(private val initializer: (R) -> T) : ReadWriteProperty<R, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == EMPTY) {
            value = initializer(thisRef)
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}
