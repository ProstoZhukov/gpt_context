package ru.tensor.sbis.push_cloud_messaging

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Реализация сервиса для получения новых пуш-сообщений от Firebase Cloud Messaging и
 * событий обновления токена для обеспечения подписки.
 *
 * @author ev.grigoreva
 */
internal class PushCloudMessagingService : FirebaseMessagingService(), EntryPointGuard.EntryPoint {

    override fun attachBaseContext(newBase: Context?) {
        EntryPointGuard.serviceAssistant
            .interceptAttachBaseContext(
                service = this,
                newBase,
                superMethod = { super.attachBaseContext(it) }
            )
    }

    override fun onCreate() {
        EntryPointGuard.serviceAssistant
            .interceptOnCreate(
                service = this,
                superMethod = { super.onCreate() },
                onReady = { }
            )
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        PushCloudMessaging.getMessagingProxy().onMessageReceived(remoteMessage.data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PushCloudMessaging.getMessagingProxy().onNewToken(token)
    }
}