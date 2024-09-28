package ru.tensor.sbis.push_cloud_messaging.service

import android.content.Context
import java.lang.Exception

/**
 * Интерфейс подписчика для получения новых пуш-уведомлений.
 *
 * @author ev.grigoreva
 */
interface PushServiceSubscriber {

    /**
     * Подписаться на пуши
     */
    fun subscribe(
        context: Context,
        successHandler: (() -> Unit)? = null,
        errorHandler: ((Exception) -> Unit)? = null
    )
}
