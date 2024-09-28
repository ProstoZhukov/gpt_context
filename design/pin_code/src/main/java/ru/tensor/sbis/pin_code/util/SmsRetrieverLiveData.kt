package ru.tensor.sbis.pin_code.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient

/**
 * LiveData с прослушкой смс сообщения.
 * Включается прослушка смс при наличии хотябы одного наблюдателя и отключается при их отсутсвии.
 *
 * @param appContext контекст приложения
 * @param codeLength ожидаемая длина кода в смс сообщении
 *
 * @author mb.kruglova
 */
internal class SmsRetrieverLiveData(
    private val appContext: Context,
    private val codeLength: Int,
    private val clientProducer: () -> SmsRetrieverClient = { SmsRetriever.getClient(appContext) }
) : MutableLiveData<String>() {
    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onActive() {
        super.onActive()
        broadcastReceiver = SmsBroadcastReceiver(codeLength, this)
        ContextCompat.registerReceiver(
            appContext,
            broadcastReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        clientProducer().startSmsRetriever()
    }

    override fun onInactive() {
        super.onInactive()
        appContext.unregisterReceiver(broadcastReceiver)
        broadcastReceiver = null
    }
}