package ru.tensor.sbis.android_ext_decl.args

import android.content.Intent
import android.os.Bundle

/**
 * Реализация [KeySpec] для упрощенного создания специфицированного ключа.
 *
 * @param key ключ.
 * @param bundlePutAction функция для сохранения в [Bundle].
 * @param bundleGetAction функция для чтения из [Bundle].
 * @param intentPutAction функция для сохранения в [Intent].
 * @param intentGetAction функция для чтения из [Intent].
 */
class KeySpecImpl<T>(
    override val key: String,
    private val bundlePutAction: Bundle.(String, T) -> Unit,
    private val bundleGetAction: Bundle.(String) -> T,
    private val intentPutAction: Intent.(String, T) -> Unit,
    private val intentGetAction: Intent.(String) -> T
) : KeySpec<T?> {

    override fun put(bundle: Bundle, value: T?) {
        if (value != null) {
            bundle.run { bundlePutAction(key, value) }
        }
    }

    override fun get(bundle: Bundle): T? {
        return if (bundle.containsKey(key)) {
            bundle.run { bundleGetAction(key) }
        } else {
            null
        }
    }

    override fun put(intent: Intent, value: T?) {
        if (value != null) {
            intent.run { intentPutAction(key, value) }
        }
    }

    override fun get(intent: Intent): T? {
        return if (intent.hasExtra(key)) {
            intent.run { intentGetAction(key) }
        } else {
            null
        }
    }
}
