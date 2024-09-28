package ru.tensor.sbis.counter_provider

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import ru.tensor.sbis.common.generated.BnpCounter
import ru.tensor.sbis.common.generated.BnpCountersController
import ru.tensor.sbis.common.generated.DataRefreshedBnpCountersControllerCallback
import timber.log.Timber

/**
 * Поставщик информации о счётчиках
 *
 * @author mb.kruglova
 */
class CountersRepository(
    controller: Lazy<BnpCountersController> = lazy { BnpCountersController.instance() },
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Подписка на обновления счётчиков. При подписке присылает доступные значения из кэша, затем
     * работает по обновлениям из контроллера
     */
    val counters = callbackFlow<Map<String, BnpCounter>> {
        val service by controller
        // при подписке запросим разом актуальные состояния счётчиков
        channel.send(service.getCountersCached().toMap())
        val callback = object : DataRefreshedBnpCountersControllerCallback() {
            override fun onEvent(counters: ArrayList<BnpCounter>) {
                val countersMap = counters.toMap()
                try {
                    channel.trySendBlocking(countersMap)
                } catch (e: Exception) {
                    if (e is ClosedSendChannelException) return
                    Timber.e(e,"Unable to update counters $countersMap")
                }
            }
        }
        val subscription = service.dataRefreshed().subscribe(callback)
        awaitClose { subscription.disable() }
    }.flowOn(dispatcher)

    private fun List<BnpCounter>.toMap(): Map<String, BnpCounter> {
        val map = HashMap<String, BnpCounter>(size)
        forEach { counter ->
            map[counter.name] = counter
        }
        return map
    }
}