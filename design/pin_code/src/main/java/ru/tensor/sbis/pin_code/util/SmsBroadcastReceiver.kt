package ru.tensor.sbis.pin_code.util

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver
import timber.log.Timber

/**
 * Ресивер для получения кода подтверждения из смс сообщения.
 * Когда устройство получает SMS-сообщение с хэшем, сервисы Google Play используют хэш приложения, чтобы определить,
 * что сообщение предназначено для нашего приложения, и делают текст сообщения доступным для нас.
 * Никаких сторонних сообщений не получим.
 *
 * @author mb.kruglova
 */
internal class SmsBroadcastReceiver(
    private val codeLength: Int,
    private val smsRetrieverLiveData: SmsRetrieverLiveData
) : EntryPointBroadcastReceiver() {

    override fun onReady(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras!!
            val status = extras.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)!!
                    val code = extractCode(message)
                    Timber.d("Код из смс успешно извлечен, код = %s", code)
                    smsRetrieverLiveData.value = code
                }

                CommonStatusCodes.TIMEOUT -> {
                    Timber.d("Таймаут ожидания смс с кодом истек")
                }
            }
        }
    }

    /**
     * Извлечение кода из сообщения.
     * @param message сообщение, содержащее код
     * Сообщение с кодом имеет следующий вид:
     *
     * <#> Ваш код подтверждения СБИС: 99509
     * C9wKqKxhZgq
     *
     * После первого двоеточия идет пробел, за которым следуют 5 представляющих интерес символов
     */
    private fun extractCode(message: String): String {
        val firstColonPos = message.indexOfFirst { it == ':' }
        return message.substring(
            firstColonPos + 2,
            firstColonPos + 2 + codeLength
        )
    }
}