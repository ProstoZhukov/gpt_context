package ru.tensor.sbis.pushnotification.controller.command

import android.content.Context
import ru.tensor.sbis.pushnotification.controller.HandlingResult
import ru.tensor.sbis.pushnotification.repository.PushNotificationRepository
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.util.PushParserUtil.optStringNonEmpty
import ru.tensor.sbis.pushnotification_utils.PushPreferenceUtils

/**
 * Команда, отправляющая подтверждение получения пуш уведомлений.
 *
 * @author ev.grigoreva
 */
internal class ConfirmPushCommand(
    private val context: Context,
    private val repository: PushNotificationRepository
) : PushPostProcessCommand {

    override fun process(handleResult: HandlingResult) {
        val systemEnabled = PushPreferenceUtils.systemNotificationsEnabled(context)
        handleResult.notifyDisplayed.forEach { message ->
            process(message, systemEnabled)
        }
        handleResult.notifyHidden.forEach { message ->
            process(message, false)
        }
        handleResult.updateDisplayed.forEach { message ->
            process(message, systemEnabled)
        }
        handleResult.updateHidden.forEach { message ->
            process(message, false)
        }
    }

    private fun process(message: PushNotificationMessage, needConfirm: Boolean) {
        if (needConfirm) {
            message.data.optStringNonEmpty("confirm_link")?.let { confirmLink ->
                repository.confirmPushByLink(confirmLink)
            }
        } else {
            message.data.optStringNonEmpty("discard_link")?.let { discardLink ->
                repository.confirmPushByLink(discardLink)
            }
        }
    }
}