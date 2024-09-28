package ru.tensor.sbis.application_tools.leak

import android.app.Application

/**
 * Устанавливает наблюдателя ссылок из LeakCanary для классов-компонентов Android для отладки утечек памяти
 * @param application Application
 */
fun deployLeakCanary(application: Application) {
    //ignore
}

fun disableLeakCanary() {
    //ignore
}