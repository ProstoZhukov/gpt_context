package ru.tensor.sbis.pushnotification.util

import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.center.PushCenter

/**
 * Отладчик уведомлений.
 *
 * @author am.boldinov
 */
object PushDebug {

    const val BUG_INFO_KEY = "Bug info"

    private var mPushCenter: PushCenter? = null
    private var mEnabled = false

    /**
     * Включить отладчик уведомлений
     */
    @JvmStatic
    fun enable(pushCenter: PushCenter) {
        mPushCenter = pushCenter
        mEnabled = true
    }

    /**
     * Отключить отладчик уведомлений.
     */
    @JvmStatic
    fun disable() {
        mPushCenter = null
        mEnabled = false
    }

    /**
     * Отправить информацию о произошедшей ошибке.
     */
    @Suppress("unused")
    @JvmStatic
    fun sendBug(info: String) {
        if (!mEnabled) {
            return
        }

        // Отправляем информацию на bug tracker
        PushLogger.event(info)

        // Отправляем push-сообщение чтобы вывести информацию об ошибке в историю уведомлений
        // Пользователю уведомление отображено не будет
        val message = mapOf("type" to PushType.BUG.value, BUG_INFO_KEY to info)
        mPushCenter?.handleMessage(message)
    }

}