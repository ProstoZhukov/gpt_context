package ru.tensor.sbis.pushnotification

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдер для получения названия приложения при подписки на пуш-уведомления.
 *
 * @author kv.martyshenko
 */
fun interface PushAppNameProvider : Feature {

    /**
     * Метод для получения названия приложения
     */
    fun getAppName(): String

}