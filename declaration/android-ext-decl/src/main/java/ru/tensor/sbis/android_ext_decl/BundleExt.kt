package ru.tensor.sbis.android_ext_decl

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/** Получает Serializable по-новому для API 33+ и по-старому для предыдущих. */
inline fun <reified T : Serializable> Bundle.getSerializableUniversally(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getSerializable(key) as? T
    }
}

/** Получает Parcelable по-новому для API 33+ и по-старому для предыдущих. */
fun <T : Parcelable> Bundle.getParcelableUniversally(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }
}

/**
 * Получает Serializable по-новому для API 33+ и по-старому для предыдущих.
 * Поддержано выведение типа.
 */
inline fun <reified T : Serializable> Bundle.getSerializableUniversally(key: String): T? {
    return getSerializableUniversally(key, T::class.java)
}

/**
 * Получает Parcelable по-новому для API 33+ и по-старому для предыдущих.
 * Поддержано выведение типа.
 */
inline fun <reified T : Parcelable> Bundle.getParcelableUniversally(key: String): T? {
    return getParcelableUniversally(key, T::class.java)
}

/**
 * Получает Parcelable по-новому для API 33+ и по-старому для предыдущих.
 * Поддержано выведение типа.
 */
inline fun <reified T : Parcelable> Intent.getParcelableUniversally(key: String): T? {
    return extras?.getParcelableUniversally(key)
}

/**
 * Получает Serializable по-новому для API 33+ и по-старому для предыдущих.
 * Поддержано выведение типа.
 */
inline fun <reified T : Serializable> Intent.getSerializableUniversally(key: String): T? {
    return extras?.getSerializableUniversally(key)
}

/** Получает ParcelableArray по-новому для API 33+ и по-старому для предыдущих. */
fun <T : Parcelable> Bundle.getParcelableArrayListUniversally(key: String, clazz: Class<T>): List<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayList(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelableArrayList(key)
    }
}

inline fun <reified T : Parcelable> Bundle.getParcelableArrayListUniversally(key: String?): List<T> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelableArrayList(key, T::class.java).orEmpty()
    else
        @Suppress("DEPRECATION")
        getParcelableArrayList(key) ?: emptyList()
}

/** Получает ParcelableArray по-новому для API 33+ и по-старому для предыдущих. */
inline fun <reified T : Parcelable> Intent.getParcelableArrayListUniversally(key: String): List<T>? =
    getParcelableArrayListUniversally(key, T::class.java)

/** Получает ParcelableArray по-новому для API 33+ и по-старому для предыдущих. */
fun <T : Parcelable> Intent.getParcelableArrayListUniversally(key: String, clazz: Class<T>): List<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayListExtra(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelableArrayListExtra(key)
    }
}
