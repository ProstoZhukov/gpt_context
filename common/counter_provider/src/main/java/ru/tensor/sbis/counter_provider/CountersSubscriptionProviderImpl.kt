package ru.tensor.sbis.counter_provider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.toolbox_decl.counters.CountersSubscriptionProvider
import ru.tensor.sbis.toolbox_decl.counters.UnreadAndTotalCounterModel

/**
 * Реализация поставщика счётчиков с контроллера в виде [UnreadAndTotalCounterModel].
 *
 * @author us.bessonov
 */
internal class CountersSubscriptionProviderImpl(
    private val repository: CountersRepository
) : CountersSubscriptionProvider {

    override val counters: Flow<Map<String, UnreadAndTotalCounterModel>>
        get() = repository.counters.map {
            it.mapValues { (_, counter) ->
                UnreadAndTotalCounterModel(counter.unreadCounter, counter.totalCounter, counter.unviewedCounter)
            }
        }
}