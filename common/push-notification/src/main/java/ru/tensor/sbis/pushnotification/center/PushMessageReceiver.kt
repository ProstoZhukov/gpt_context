package ru.tensor.sbis.pushnotification.center

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.util.collections.ListUtils
import ru.tensor.sbis.common.util.collections.Predicate
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.buffer.Buffer
import ru.tensor.sbis.pushnotification.controller.PushActionController
import ru.tensor.sbis.pushnotification.controller.PushNotificationController
import ru.tensor.sbis.pushnotification.controller.command.PushPostProcessCommand
import ru.tensor.sbis.pushnotification.proxy.TransactionNotificationManager
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.util.PushLogger
import kotlin.coroutines.CoroutineContext

/**
 * Получатель пуш уведомлений
 *
 * @author ev.grigoreva
 */
internal class PushMessageReceiver(
    private val notificationManager: TransactionNotificationManager,
    private val getNotificationController: (PushType) -> PushNotificationController?,
    private val getActionController: (PushType) -> PushActionController?,
    coroutineContext: CoroutineContext,
    private vararg val postProcessCommands: PushPostProcessCommand,
) : Buffer.Receiver<PushNotificationMessage> {

    private val scope = CoroutineScope(coroutineContext)

    override fun receive(fromBuffer: MutableList<PushNotificationMessage>) {
        scope.launch {
            notificationManager.beginTransaction()
            while (fromBuffer.size > 0) {
                // Filter messages with same type
                val type = fromBuffer[0].type
                val sameTypeList = ListUtils.takeWithMutate(
                    fromBuffer,
                    object : Predicate<PushNotificationMessage> {
                        override fun apply(t: PushNotificationMessage): Boolean {
                            return t.type == type
                        }
                    }
                )

                // Handle notifications
                val controller = getNotificationController(type)
                if (controller != null) {
                    try {
                        PushLogger.event("Handle push notification controller: $controller, type: $type")
                        controller.handle(sameTypeList).let { result ->
                            postProcessCommands.forEach { it.process(result) }
                        }
                    } catch (e: Exception) {
                        PushLogger.error(e)
                    }
                }
                // Handle actions
                val action = getActionController(type)
                if (action != null) {
                    try {
                        PushLogger.event("Handle push notification action: $action, type: $type")
                        action.handle(sameTypeList)
                    } catch (e: Exception) {
                        PushLogger.error(e)
                    }
                }
            }
            notificationManager.endTransaction()
        }
    }
}