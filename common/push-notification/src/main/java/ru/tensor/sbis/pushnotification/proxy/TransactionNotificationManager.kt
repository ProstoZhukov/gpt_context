package ru.tensor.sbis.pushnotification.proxy

import android.content.Context
import ru.tensor.sbis.pushnotification.center.commands.CancelAllCommand
import ru.tensor.sbis.pushnotification.center.commands.CancelCommand
import ru.tensor.sbis.pushnotification.center.commands.NotifyCommand
import ru.tensor.sbis.pushnotification.center.commands.PushNotificationCommand
import ru.tensor.sbis.pushnotification.notification.PushNotification
import ru.tensor.sbis.pushnotification.notification.decorator.impl.ChannelDecorator
import ru.tensor.sbis.pushnotification.notification.decorator.impl.QuietDecorator
import ru.tensor.sbis.pushnotification.util.PushLogger
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Менеджер для обработки и показа уведомлений в рамках одной транзакции
 * для исключения пересечений при показе уведомлений и частых звуковых сигналов.
 *
 * По завершению транзакции публикует со звуком только первый пуш, а остальные,
 * которые были обработаны в рамках одной транзакции, отображает без звука.
 * Перед выполнением проверяет текущий статус авторизации пользователя.
 * В случае, если у пользователя произошел разлогин, все обработанные пуши
 * в рамках транзакции показаны не будут.
 *
 * @author am.boldinov
 */
internal class TransactionNotificationManager(
    context: Context,
    private val loginInterface: LoginInterface
) : NotificationManagerInterface {

    private val executor = NotificationManagerExecutor(context)
    private val transaction = mutableListOf<PushNotificationCommand>()
    private var inTransaction = false

    /**
     * Сигнализирует о начале транзакции по обработке и показу пуш-уведомлений
     */
    @Synchronized
    fun beginTransaction() {
        inTransaction = true
    }

    /**
     * Сигнализирует о завершении транзакции, по окончанию которой все обработанные события будут опубликованы разом
     */
    @Synchronized
    fun endTransaction() {
        inTransaction = false
        // Prepare transaction
        if (needToShow()) {
            prepareNotifyCommands()
        } else {
            evictNotifyCommands()
        }
        // Execute transaction
        for (command in transaction) {
            command.execute(executor)
        }
        transaction.clear()
    }

    override fun notify(tag: String?, id: Int, notification: PushNotification?) {
        require(!tag.isNullOrEmpty()) { "Empty push notification tag!" }
        apply(NotifyCommand(tag, id, notification))
    }

    override fun cancel(tag: String?, id: Int) {
        apply(CancelCommand(tag, id))
    }

    override fun cancelAll() {
        apply(CancelAllCommand())
    }

    @Synchronized
    private fun apply(command: PushNotificationCommand) {
        if (inTransaction) {
            PushLogger.event("TransactionNotificationManager.apply add command $command to transaction")
            transaction.add(command)
        } else {
            PushLogger.event("TransactionNotificationManager.apply execute command $command")
            command.execute(executor)
        }
    }

    private fun prepareNotifyCommands() {
        if (transaction.size > 1) {
            PushLogger.event("TransactionNotificationManager.prepareNotifyCommands ${transaction.size}")
            var isFirst = true
            // Mute all notifications except the first one
            val quietDecorator = QuietDecorator(false)
            // Publish all notifications except the first into update channel
            val updateChannelDecorator = ChannelDecorator(true)
            for (i in transaction.indices) {
                val command = transaction[i]
                if (command is NotifyCommand) {
                    if (isFirst) {
                        // Skip first and set flag
                        isFirst = false
                    } else {
                        // Decorate not first as quiet
                        command.notification
                            .decorate(quietDecorator)
                            .decorate(updateChannelDecorator)
                    }
                }
            }
        }
    }

    private fun evictNotifyCommands() {
        PushLogger.event("TransactionNotificationManager.evictNotifyCommands")
        var i = 0
        while (i < transaction.size) {
            val command = transaction[i]
            if (command is NotifyCommand && !command.isGuaranteed) {
                transaction.removeAt(i)
            } else {
                i++
            }
        }
    }

    private fun needToShow(): Boolean {
        return loginInterface.isAuthorized
    }
}