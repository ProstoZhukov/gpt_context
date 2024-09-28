package ru.tensor.sbis.push_cloud_messaging

import android.content.Context
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Реализация сервиса для получения новых пуш-сообщений от Huawei Cloud Messaging и
 * событий обновления токена для обеспечения подписки.
 *
 * @author am.boldinov
 */
internal class PushCloudMessagingService : HmsMessageService(), EntryPointGuard.EntryPoint {

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

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        message?.dataOfMap?.let {
            PushCloudMessaging.getMessagingProxy().onMessageReceived(it)
        }
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        if (!token.isNullOrEmpty()) {
            PushCloudMessaging.getMessagingProxy().onNewToken(token)
        }
    }
}