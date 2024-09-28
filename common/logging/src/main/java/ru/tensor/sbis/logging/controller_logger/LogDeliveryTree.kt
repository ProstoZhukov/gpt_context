package ru.tensor.sbis.logging.controller_logger

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Completable
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Логгер Timber, который перенаправляет все логи в [logDeliveryProvider].
 *
 * @param logDeliveryProvider обёртка над логгером контроллера, чтобы избежать прямой зависимости от контроллера.
 *
 * @author av.krymov
 */
internal class LogDeliveryTree(
    private val logDeliveryProvider: ControllerLogDeliveryProvider
) : Timber.Tree() {

    /**
     * Так как этот метод вызывается при вызове любого другого метода логирования(e(), w() и т.д.),
     * достаточно переопределить только его.
     */
    @SuppressLint("CheckResult")
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        Completable.fromAction {
            val formattedMessage = getFormattedMessage(tag, message)
            when (priority) {
                Log.WARN -> logDeliveryProvider.warning(formattedMessage)
                Log.ERROR -> logDeliveryProvider.error(formattedMessage)
                else -> logDeliveryProvider.message(formattedMessage, priority)
            }
        }
            .subscribeOn(Schedulers.single())
            .subscribe(Functions.EMPTY_ACTION) { cause ->
                Timber.w(
                    cause,
                    "Undelivered log (tag='%s', message='%s', priority='%d', exception='%s')",
                    tag,
                    message,
                    priority,
                    throwable.toString()
                )
            }
    }

    private fun getFormattedMessage(tag: String?, message: String): String {
        return tag?.let { "[$it] $message" } ?: message
    }
}