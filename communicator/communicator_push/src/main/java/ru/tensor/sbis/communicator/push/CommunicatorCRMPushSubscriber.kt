package ru.tensor.sbis.communicator.push

import ru.tensor.sbis.pushnotification.ComplexPushSubscriber
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.controller.PushNotificationController

/**
 * Класс подписчика на пуш-уведомления для модуля "Коммуникатор".
 *
 * @param notificationController - обработчик входящих пуш уведомлений по сообщениям.
 */
class CommunicatorCRMPushSubscriber(
    private val notificationController: PushNotificationController
) : ComplexPushSubscriber() {

    /** @SelfDocumented */
    override fun prepareOperations(): List<Operation> =
        listOf<Operation>(
            RegisterNotificationOperation(
                PushType.OPERATORS_CONSULTATION_MESSAGE,
                notificationController
            ),
            RegisterNotificationOperation(
                PushType.OPERATORS_RATE,
                notificationController
            )
        )
}