package ru.tensor.sbis.pushnotification.util

import ru.tensor.sbis.pushnotification.util.PushLogger.error
import timber.log.Timber

/**
 * Хелпер для логирования событий модуля push-уведомлений.
 * https://wi.sbis.ru/docs/cpp/sbis/logging/typedefs/LogLevel?v=21.6200
 *
 * @author am.boldinov
 */
private const val TAG = "PushNotificationEvent"

internal object PushLogger {

    /**
     * Логирует любое пользовательское или функциональное событие.
     * Необходимо использовать в случае отслеживания истории и порядка вызовов функций
     * для последующего разбора узких проблем.
     *
     * Не отправляет в Crashlytics. Отправляет в LogDelivery в отладочном режиме.
     */
    @JvmStatic
    fun event(message: String) {
        Timber.tag(TAG).d(message)
    }

    /**
     * Логирует ошибку.
     * Необходимо использовать в случаях ошибочного поведения, при котором у пользователя нет доступа
     * к функционалу или работа функционала нарушена, а так же в случаях потенциальных падений.
     *
     * Отправляет в Crashlytics. Отправляет в LogDelivery всегда (минимальный режим).
     */
    @JvmStatic
    fun error(e: Throwable) {
        Timber.tag(TAG).e(e)
    }

    /**
     * Логирует ошибку.
     * @see [error]
     */
    @JvmStatic
    fun error(message: String) {
        Timber.tag(TAG).e(message)
    }

    /**
     * Логирует ошибку.
     * @see [error]
     */
    @JvmStatic
    fun error(e: Throwable, message: String) {
        Timber.tag(TAG).e(e, message)
    }

    /**
     * Логирует предупреждение.
     * Необходимо использовать в случае если поведение является ошибочным, но функционал продолжает работать.
     *
     * Отправляет в Crashlytics. Отправляет в LogDelivery в стандартном режиме.
     */
    @JvmStatic
    fun warning(message: String) {
        Timber.tag(TAG).w(message)
    }
}