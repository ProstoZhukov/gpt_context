package ru.tensor.sbis.communicator.push

import ru.tensor.sbis.pushnotification.ComplexPushSubscriber
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.controller.PushNotificationController

/**
 * Класс подписчика на пуш-уведомления для модуля "Коммуникатор".
 *
 * @param notificationController - обработчик входящих пуш уведомлений по сообщениям.
 */
internal class CommunicatorPushSubscriber(
    private val notificationController: PushNotificationController,
    private val pushType: List<PushType>
) : ComplexPushSubscriber() {

    /** @SelfDocumented */
    override fun prepareOperations(): List<Operation> {

        val operations = mutableListOf<Operation>()

        pushType.forEach {
            operations.add(RegisterNotificationOperation(it, notificationController))
        }

        operations.add(RegisterNotificationOperation(PushType.WAITER_MESSAGE, notificationController))
        return operations
    }
}
