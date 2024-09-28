package ru.tensor.sbis.pushnotification

import androidx.annotation.CheckResult
import ru.tensor.sbis.pushnotification.center.PushCenter

/**
 * Класс подписки на пуш-уведомления, позволяющий осуществить отписку
 *
 * @author am.boldinov
 */
interface PushSubscription {
    fun unsubscribe()
}

/**
 * Класс подписчика на пуш-уведомления
 */
interface PushSubscriber {
    /**
     * Метод, в котором оформляется подписка на определенные пуш-уведомления
     *
     * @param pushCenter точка входа для подписки и отписки на пуши по конкретным типам
     * @return [PushSubscription] подписка, от которой в последствии можно будет отписаться
     */
    @CheckResult
    fun subscribe(pushCenter: PushCenter): PushSubscription
}