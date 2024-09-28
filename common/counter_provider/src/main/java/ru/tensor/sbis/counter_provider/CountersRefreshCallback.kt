package ru.tensor.sbis.counter_provider

import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking
import ru.tensor.sbis.common.generated.BnpCounter
import ru.tensor.sbis.common.generated.DataRefreshedBnpCountersControllerCallback
import timber.log.Timber

/**
 * @author mb.kruglova
 */
internal class CountersRefreshCallback(
    private val channel: SendChannel<Map<String, BnpCounter>>
) : DataRefreshedBnpCountersControllerCallback() {

    override fun onEvent(counters: ArrayList<BnpCounter>) {
        val countersMap = counters.toMap()
        try {
            channel.trySendBlocking(countersMap)
        } catch (e: Exception) {
            if (e is ClosedSendChannelException) return
            Timber.e(e,"Unable to update counters $countersMap")
        }
    }

    companion object {

        /**
         * Преобразует список счётчиков в ассоциативный массив, где ключ - идентификатор счётчика
         */
        internal fun List<BnpCounter>.toMap(): Map<String, BnpCounter> {
            val map = HashMap<String, BnpCounter>(size)
            forEach { counter ->
                map[counter.name] = counter
            }
            return map
        }
    }
}