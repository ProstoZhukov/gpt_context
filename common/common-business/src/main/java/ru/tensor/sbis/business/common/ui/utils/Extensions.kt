package ru.tensor.sbis.business.common.ui.utils

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable

/**
 * Достать Parcelable из Bundle способом в зависимости от API.
 *
 * @param key ключ нужной записи в Bundle
 */
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

