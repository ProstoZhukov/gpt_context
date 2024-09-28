package ru.tensor.sbis.push_cloud_messaging.service

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import ru.tensor.sbis.push_cloud_messaging.PushCloudMessaging

/**
 * Реализация подписчика для получения новых пуш-уведомлений от Firebase Cloud Messaging.
 *
 * @author ev.grigoreva
 */
internal class PushServiceSubscriberImpl : PushServiceSubscriber {

    override fun subscribe(
        context: Context,
        successHandler: (() -> Unit)?,
        errorHandler: ((Exception) -> Unit)?
    ) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (!token.isNullOrEmpty()) {
                    PushCloudMessaging.getMessagingProxy().onNewToken(token)
                }
                successHandler?.invoke()
            }
            .addOnFailureListener { exception ->
                errorHandler?.invoke(exception)
            }
    }
}