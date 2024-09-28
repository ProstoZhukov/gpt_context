package ru.tensor.sbis.sale_point_qr_binder.internal.push

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.pushnotification.controller.base.SinglePushNotificationController
import ru.tensor.sbis.pushnotification.model.PushData
import ru.tensor.sbis.pushnotification.notification.PushNotification
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.sale_point_qr_binder.R

/**
 * Реализация обработчика [SinglePushNotificationController] пуш-уведомлений для привязки QR-кода.
 *
 * @param context
 *
 * @author kv.martyshenko
 */
internal class SalePointQrNotificationController(
    context: Context,
    private val screenIntentFactory: (Context, BindQRPushData) -> Intent
) : SinglePushNotificationController(context) {

    override fun createNotification(message: PushNotificationMessage): PushNotification {
        val pushNotification = pushBuildingHelper.createSbisNotification(message).apply {
            isGuaranteed = true
        }
        return try {
            val model = parsePushData(message)
            pushNotification.apply {
                val screenIntent = pushIntentHelper.createIntentWithBackStack(
                    screenIntentFactory(context, model).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                    SalePointQrPushContentCategory(),
                    0
                )

                builder.setContentIntent(screenIntent)
            }
        } catch (e: Exception) {
            pushNotification.apply {
                builder.setContentText(
                    context.getString(R.string.spqrb_error_broken_push_format, message.type.toNotificationType().value)
                )
            }
        }
    }

    private fun parsePushData(message: PushNotificationMessage): BindQRPushData {
        val extraData = message.data
        return BindQRPushData(
            message,
            salePointId = extraData.getString("company"),
            bindUrl = extraData.getString("bind_url"),
            site = extraData.optString("site"),
            objectType = extraData.optString("hall_kind"),
            objectIdentifier = extraData.optString("object_id"),
            hall = extraData.optString("hall")
        )
    }

    internal class BindQRPushData(
        message: PushNotificationMessage,
        val salePointId: String,
        val bindUrl: String,
        val site: String?,
        val objectType: String?,
        val objectIdentifier: String?,
        val hall: String?
    ) : PushData(message)

}