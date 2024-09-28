package ru.tensor.sbis.android_ext_decl.args

import android.content.Intent
import android.os.Bundle

/** Интерфейс для спецификации ключа передачи данных посредством [Bundle] или [Intent]. */
interface KeySpec<T> {

    /** @SelfDocumented */
    val key: String?

    /** Положить [value] в [Bundle]. */
    fun put(bundle: Bundle, value: T)

    /** Прочитать данные из [Bundle]. */
    fun get(bundle: Bundle): T

    /** Положить [value] в [Intent]. */
    fun put(intent: Intent, value: T)

    /** Прочитать данные из [Intent]. */
    fun get(intent: Intent): T

    companion object
}
