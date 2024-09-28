package ru.tensor.sbis.pushnotification.center

import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.buffer.RenewalBuffer
import ru.tensor.sbis.pushnotification.controller.PushActionController
import ru.tensor.sbis.pushnotification.controller.PushHandler
import ru.tensor.sbis.pushnotification.controller.PushNotificationController
import ru.tensor.sbis.pushnotification.controller.base.GroupedNotificationController
import ru.tensor.sbis.pushnotification.controller.command.PushPostProcessCommand
import ru.tensor.sbis.pushnotification.proxy.TransactionNotificationManager
import ru.tensor.sbis.pushnotification.repository.PushNotificationRepository
import ru.tensor.sbis.pushnotification.util.PushLogger
import ru.tensor.sbis.pushnotification.util.SwipeOutHelper
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Время буферизации пуш уведомлений, в рамках которого обработка входящих пушей будет
 * объединяться во избежании тротлинга на устройстве и частых звуковых сигналов
 */
private const val BUFFER_RENEWAL_TIME = 800L

/**
 * Класс, отвечающий за обработку полученных пуш уведомлений
 *
 * @author ev.grigoreva
 */
internal class PushHandlingManagerImpl(
    private val notificationManager: TransactionNotificationManager,
    private val repository: PushNotificationRepository,
    vararg postProcessCommands: PushPostProcessCommand
) : PushHandlingManager {

    private val handlers = CopyOnWriteArrayList<PushHandler>()
    private val notificationControllers = ConcurrentHashMap<PushType, PushNotificationController>()
    private val actionControllers = ConcurrentHashMap<PushType, PushActionController>()
    private val instantHandlingTypes = HashSet<PushType>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val singleThreadDispatcher = Dispatchers.IO.limitedParallelism(1)
    private val scope = CoroutineScope(singleThreadDispatcher)

    private val instantMessageReceiver = PushMessageReceiver(
        notificationManager,
        { getNotificationController(it) },
        { getActionController(it) },
        singleThreadDispatcher,
        *postProcessCommands
    )

    private val messageBuffer = RenewalBuffer(instantMessageReceiver).apply {
        setRenewalTime(BUFFER_RENEWAL_TIME)
    }

    /**
     * Предоставляет обработчик пуш-уведомлений, соответствующий конкретному типу пуша
     */
    fun getNotificationController(type: PushType): PushNotificationController? {
        return notificationControllers[type]
    }

    /**
     * Предоставляет обработчик действий над пуш-уведомлением, соответствующий конкретному типу пуша
     */
    fun getActionController(type: PushType): PushActionController? {
        return actionControllers[type]
    }

    override fun setInstantHandlingForType(type: PushType) {
        instantHandlingTypes.add(type)
    }

    override fun resetInstantHandlingForType(type: PushType) {
        instantHandlingTypes.remove(type)
    }

    override fun registerPushHandler(handler: PushHandler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler)
        } else {
            PushLogger.error("Handler $handler already registered!")
        }
    }

    override fun unregisterPushHandler(handler: PushHandler) {
        handlers.remove(handler)
    }

    override fun registerNotificationController(type: PushType, controller: PushNotificationController) {
        notificationControllers[type] = controller
    }

    override fun unregisterNotificationController(type: PushType) {
        notificationControllers.remove(type)
    }

    override fun registerActionController(type: PushType, controller: PushActionController) {
        actionControllers[type] = controller
    }

    override fun unregisterActionController(type: PushType) {
        actionControllers.remove(type)
    }

    @Synchronized
    override fun handleMessage(message: Map<String, String>) {
        PushLogger.event("Start processing an incoming push message")
        val dataMessage = repository.createNotification(message)
        for (handler in handlers) {
            PushLogger.event("Start handler: ${handler.javaClass.simpleName}")
            handler.handle(message, dataMessage)
        }
        if (dataMessage != null) {
            PushLogger.event("Push message to buffer for processing, uuid: ${dataMessage.notificationUuid}, type: ${dataMessage.type}")
            if (instantHandlingTypes.contains(dataMessage.type)) {
                instantMessageReceiver.receive(mutableListOf(dataMessage))
            } else {
                messageBuffer.push(dataMessage)
            }
        }
    }

    override fun cancelAll() {
        performAsyncAction {
            notificationManager.cancelAll()
            repository.clearAll()
        }
    }

    override fun cancelAll(types: Set<PushType>) {
        performAsyncAction {
            val notProcessedTypes = HashSet<PushType>()
            repository.getNotifications(types).forEach { message ->
                val notifyMeta = message.notifyMeta
                if (notifyMeta != null) {
                    notificationManager.cancel(notifyMeta.tag, notifyMeta.notifyId)
                } else {
                    notProcessedTypes.add(message.type)
                }
            }
            notProcessedTypes.forEach { type ->
                getNotificationController(type)
                    // исключаем контроллеры, которые не публикуют события с notifyMeta == null
                    .takeIf { it !is GroupedNotificationController<*> }?.cancelAll(type)
            }
            repository.clearAll(types)
        }
    }

    override fun cancelAll(type: PushType) {
        performAsyncAction {
            getNotificationController(type)?.cancelAll(type)
            repository.clearAll(type)
        }
    }

    override fun cancel(type: PushType, params: Bundle) {
        performAsyncAction {
            PushLogger.event("Cancel push notification for type: $type, params: $params")
            getNotificationController(type)?.cancel(type, params)
        }
    }

    override fun onSwipeOut(intent: Intent) {
        performAsyncAction {
            val swipeOutTag = SwipeOutHelper.getSwipeOutTag(intent) ?: return@performAsyncAction
            val swipeOutId = SwipeOutHelper.getSwipeOutId(intent)
            val controllerSet = HashSet(notificationControllers.values)
            controllerSet.forEach { controller ->
                if (controller.getNotifyTag() != swipeOutTag) return@forEach
                val messages = repository.getPublishedNotifications(swipeOutTag)
                for (message in messages) {
                    val notifyMeta = message.notifyMeta
                    if (notifyMeta == null || notifyMeta.notifyId != swipeOutId) {
                        return@performAsyncAction
                    }
                    PushLogger.event("onSwipeOut performed for push message, uuid: ${message.notificationUuid}, type: ${message.type}")
                    repository.removeNotification(message)
                }
            }
        }
    }

    private fun performAsyncAction(action: () -> Unit) {
        scope.launch { action.invoke() }
    }
}