package ru.tensor.sbis.pushnotification

import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.controller.PushActionController
import ru.tensor.sbis.pushnotification.controller.PushHandler
import ru.tensor.sbis.pushnotification.controller.PushNotificationController

/**
 * Реализация [PushSubscriber], позволяющая зарегистрировать переданные
 * [PushNotificationController], [PushActionController], [PushHandler] для соответствующих [PushType].
 *
 * @property pushNotificationControllers контроллеры уведомления для соответствующих [PushType]
 * @property actionControllers обработчики действий для соответствующих [PushType]
 * @property pushHandlers обработчики
 *
 * @author am.boldinov
 */
open class SimplePushSubscriber @JvmOverloads constructor(
    private val pushNotificationControllers: Map<PushType, PushNotificationController>? = null,
    private val actionControllers: Map<PushType, PushActionController>? = null,
    private val pushHandlers: List<PushHandler>? = null
) : PushSubscriber {

    override fun subscribe(pushCenter: PushCenter): PushSubscription {
        pushNotificationControllers?.apply {
            for ((type, controller) in this) {
                pushCenter.registerNotificationController(type, controller)
            }
        }
        actionControllers?.apply {
            for ((type, controller) in this) {
                pushCenter.registerActionController(type, controller)
            }
        }
        pushHandlers?.apply {
            for (handler in this) {
                pushCenter.registerPushHandler(handler)
            }
        }
        return object : PushSubscription {
            override fun unsubscribe() {
                pushNotificationControllers?.apply {
                    for ((type, _) in this) {
                        pushCenter.unregisterNotificationController(type)
                    }
                }
                actionControllers?.apply {
                    for ((type, _) in this) {
                        pushCenter.unregisterActionController(type)
                    }
                }
                pushHandlers?.apply {
                    for (handler in this) {
                        pushCenter.unregisterPushHandler(handler)
                    }
                }
            }
        }
    }
}
