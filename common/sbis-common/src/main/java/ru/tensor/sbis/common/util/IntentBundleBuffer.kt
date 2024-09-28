package ru.tensor.sbis.common.util

import android.os.Bundle
import java.util.*

/**
 * Класс для хранения аргументов интента большого размера (> 1Мб).
 * Хранение такого объёма в самом интенте не поддерживается андроидом.
 *
 * @author sa.nikitin
 */
object IntentBundleBuffer {

    private var bundleStorage: MutableMap<UUID, Bundle>? = null

    private fun getStorage(): MutableMap<UUID, Bundle> =
        bundleStorage.let { currentBundleMap ->
            if (currentBundleMap == null) {
                val newBundleMap = mutableMapOf<UUID, Bundle>()
                bundleStorage = newBundleMap
                newBundleMap
            } else {
                currentBundleMap
            }
        }

    fun push(key: UUID, bundle: Bundle) {
        getStorage()[key] = bundle
    }

    fun pop(key: UUID): Bundle? =
        bundleStorage?.let { bundleMap ->
            val bundle = bundleMap.remove(key)
            if (bundleMap.isEmpty()) {
                bundleStorage = null
            }
            bundle
        }
}