package ru.tensor.sbis.list.base.data.utils

import timber.log.Timber
import java.lang.reflect.Method

/**
 * Пустая подписка для работы с источниками данных без механизма обновления
 */
internal val EMPTY_SUBSCRIPTION_HOLDER = SubscriptionHolder(null)

/**
 * Держатель ссылки на подписку, чтобы избежать утечку памяти в RxJava при создании реактивного источника данных,
 * на основе CRUD репозитория микросервиса контроллера.
 *
 * @constructor Инициализируется [subscription], ссылка на который будет удерживаться до вызова методом [clear],
 * который произойдет при освобождении подписки RxJava.
 */
class SubscriptionHolder internal constructor(
    internal var subscription: Any?
) {
    /**
     * Очистить ссылку на переданный в конструктор объект.
     */
    fun clear() {
        try {
            subscription?.let {
                val method: Method = it::class.java.getMethod(DISABLE_METHOD_MANE)
                method.invoke(subscription)
            }
        } catch (t: Throwable) {
            Timber.d(t)
        }

        subscription = null
    }
}

private const val DISABLE_METHOD_MANE = "disable"