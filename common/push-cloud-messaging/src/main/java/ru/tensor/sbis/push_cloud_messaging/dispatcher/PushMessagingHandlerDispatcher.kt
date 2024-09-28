package ru.tensor.sbis.push_cloud_messaging.dispatcher

import android.os.Looper
import java.util.*
import java.util.concurrent.Executors

/**
 * Прокси реализация для хранения внешних обработчиков пуш-уведомлений и передачи им управления при наступлении события.
 *
 * @author am.boldinov
 */
internal class PushMessagingHandlerDispatcher : PushMessagingServiceProxy, PushMessagingServiceRegistry {

    private val handlers = LinkedList<PushMessagingHandler>()
    private val executor by lazy { Executors.newSingleThreadExecutor() }

    override fun onMessageReceived(message: Map<String, String>) {
        if (isWorkerThread()) {
            synchronized(handlers) {
                handlers.forEach {
                    it.onMessageReceived(message)
                }
            }
        } else {
            executor.submit {
                onMessageReceived(message)
            }
        }
    }

    override fun onNewToken(token: String) {
        synchronized(handlers) {
            handlers.forEach {
                it.onNewToken(token)
            }
        }
    }

    override fun registerHandler(handler: PushMessagingHandler) {
        synchronized(handlers) {
            if (!handlers.contains(handler)) {
                handlers.add(handler)
            }
        }
    }

    override fun unregisterHandler(handler: PushMessagingHandler) {
        synchronized(handlers) {
            handlers.remove(handler)
        }
    }

    private fun isWorkerThread(): Boolean {
        return Looper.myLooper() != Looper.getMainLooper()
    }
}