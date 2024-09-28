package ru.tensor.sbis.pushnotification

import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.controller.PushActionController
import ru.tensor.sbis.pushnotification.controller.PushHandler
import ru.tensor.sbis.pushnotification.controller.PushNotificationController

/**
 * Реализация [PushSubscriber], позволяющая подклассам гибко настроить подписку на нужные пуш-уведомления
 *
 * @author am.boldinov
 */
abstract class ComplexPushSubscriber : PushSubscriber {

    /**
     * Интерфейс, описывающий действия на для настройки подписки
     */
    interface Operation {
        /**
         * Реализует действие подписки на пуш-уведомления
         */
        fun execute(pushCenter: PushCenter)

        /**
         * Реализует действие отписки от пуш-уведомлений
         */
        fun rollback(pushCenter: PushCenter)
    }

    final override fun subscribe(pushCenter: PushCenter): PushSubscription {
        val operations = prepareOperations()

        if (operations.isEmpty()) {
            throw IllegalStateException("There are no active operations. In that case you shouldn't subscribe at all")
        }

        for (operation in operations) {
            operation.execute(pushCenter)
        }
        return object : PushSubscription {
            override fun unsubscribe() {
                for (operation in operations) {
                    operation.rollback(pushCenter)
                }
            }

        }
    }

    /**
     * Возвращает список действий, которые необходимо выполнить для настройки подписок на пуш-уведомления
     */
    protected abstract fun prepareOperations(): List<Operation>

    /**
     * Действие, описывающие настройку подписки для [PushActionController]
     */
    class RegisterActionOperation(
        private val pushType: PushType,
        private val actionController: PushActionController
    ) : Operation {
        override fun execute(pushCenter: PushCenter) {
            pushCenter.registerActionController(pushType, actionController)
        }

        override fun rollback(pushCenter: PushCenter) {
            pushCenter.unregisterActionController(pushType)
        }

    }

    /**
     * Действие, описывающие настройку подписки для [PushNotificationController]
     */
    class RegisterNotificationOperation(
        private val pushType: PushType,
        private val notificationController: PushNotificationController
    ) : Operation {
        override fun execute(pushCenter: PushCenter) {
            pushCenter.registerNotificationController(pushType, notificationController)
        }

        override fun rollback(pushCenter: PushCenter) {
            pushCenter.unregisterNotificationController(pushType)
        }

    }

    /**
     * Действие, описывающие настройку подписки для [PushHandler]
     */
    class RegisterHandlerOperation(
        private val handler: PushHandler
    ) : Operation {
        override fun execute(pushCenter: PushCenter) {
            pushCenter.registerPushHandler(handler)
        }

        override fun rollback(pushCenter: PushCenter) {
            pushCenter.unregisterPushHandler(handler)
        }

    }
}
