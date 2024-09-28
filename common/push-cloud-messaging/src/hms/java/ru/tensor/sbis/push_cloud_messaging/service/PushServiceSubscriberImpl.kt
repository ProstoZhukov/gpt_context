package ru.tensor.sbis.push_cloud_messaging.service

import android.content.Context
import com.huawei.agconnect.AGConnectInstance
import com.huawei.agconnect.AGConnectOptionsBuilder
import com.huawei.hmf.tasks.TaskExecutors
import com.huawei.hmf.tasks.Tasks
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging
import ru.tensor.sbis.push_cloud_messaging.PushCloudMessaging
import timber.log.Timber
import java.lang.Exception

/**
 * Реализация подписчика для получения новых пуш-уведомлений от Huawei Cloud Messaging.
 *
 * @author am.boldinov
 */
internal class PushServiceSubscriberImpl : PushServiceSubscriber {

    override fun subscribe(context: Context, successHandler: (() -> Unit)?, errorHandler: ((Exception) -> Unit)?) {
        Tasks.callInBackground {
            val appId = getAppId(context)
            HmsInstanceId.getInstance(context).getToken(appId, HmsMessaging.DEFAULT_TOKEN_SCOPE)
        }.addOnSuccessListener(TaskExecutors.immediate()) { token ->
            if (!token.isNullOrEmpty()) {
                PushCloudMessaging.getMessagingProxy().onNewToken(token)
            }
            successHandler?.invoke()
        }.addOnFailureListener(TaskExecutors.immediate()) { exception ->
            Timber.e(exception)
            errorHandler?.invoke(exception)
        }
    }

    private fun getAppId(context: Context): String {
        val options = AGConnectInstance.getInstance()?.options ?: AGConnectOptionsBuilder().build(context)
        return options.getString("/client/app_id") ?: options.getString("client/app_id")
    }
}