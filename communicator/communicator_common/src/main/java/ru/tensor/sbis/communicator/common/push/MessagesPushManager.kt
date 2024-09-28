package ru.tensor.sbis.communicator.common.push

import io.reactivex.Observable
import java.util.UUID

/**
 * Менеджер для управления отображением/скрытием пушей.
 *
 * @author da.zhukov
 */
interface MessagesPushManager {

    /** @SelfDocumented */
    fun getObservable(): Observable<MessagesPushAction>

    /** @SelfDocumented */
    fun executeAction(action: MessagesPushAction)
}

/**
 * Интерфейс действия для отображения/скрытия пушей.
 * @property conversationUuid - UUID переписки от которой необходимо отписаться.
 *
 * @author da.zhukov
 */
interface MessagesPushAction {
    val conversationUuid: UUID?
}
/** Действие отписки от пуш уведомлений.*/
class UnsubscribeFromNotification(override val conversationUuid: UUID? = null) : MessagesPushAction

/** Действие подписки от пуш уведомлений.*/
class SubscribeOnNotification(override val conversationUuid: UUID? = null) : MessagesPushAction

/**
 * Интерфейс действия для отображения/скрытия пушей для диалогов/каналов.
 * @property isChannel - если true отписываемся от каналов иначе от диалогов.
 *
 * @author da.zhukov
 */
interface ThemeRegistryPushAction : MessagesPushAction {
    val isChannel: Boolean
}

/** Действие отписки от пуш уведомлений диалогов/каналов. */
class ThemeUnsubscribeFromNotification(override val isChannel: Boolean = false) : ThemeRegistryPushAction {
    override val conversationUuid: UUID? = null
}

/** Действие подписки от пуш уведомлений диалогов/каналов. */
class ThemeSubscribeFromNotification(
    override val isChannel: Boolean = false
) : ThemeRegistryPushAction {
    override val conversationUuid: UUID? = null
}