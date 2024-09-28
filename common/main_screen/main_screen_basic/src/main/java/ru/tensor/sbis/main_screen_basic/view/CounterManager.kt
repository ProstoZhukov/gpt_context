package ru.tensor.sbis.main_screen_basic.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.counter.SbisButtonCounter
import ru.tensor.sbis.main_screen_decl.basic.data.CounterSource
import ru.tensor.sbis.toolbox_decl.counters.CountersSubscriptionProvider
import ru.tensor.sbis.toolbox_decl.counters.UnreadAndTotalCounterModel

/**
 * Отвечает за обновление счётчиков у иконок в шапке.
 *
 * @author us.bessonov
 */
internal class CounterManager(
    private val countersSubscriptionProvider: CountersSubscriptionProvider?,
    private val scope: CoroutineScope
) {
    private val counterViews = mutableMapOf<String, Pair<SbisButton, CounterSource>>()
    private var latestCounters = mapOf<String, UnreadAndTotalCounterModel>()

    init {
        observeCounters()
    }

    /**
     * Зарегистрировать [view], у которого должен обновляться счётчик, согласно значению с микросервиса.
     */
    fun registerServiceCounterView(view: SbisButton, counterName: String, source: CounterSource) {
        counterViews[counterName] = view to source
        updateCounterViews()
    }

    /**
     * Зарегистрировать [view], у которого должен обновляться счётчик, согласно прикладной логике.
     */
    fun registerCustomCounterView(view: SbisButton, counter: Flow<Int>) {
        scope.launch {
            counter.collect {
                view.updateCounter(it)
            }
        }
    }

    private fun observeCounters() {
        countersSubscriptionProvider?.let {
            scope.launch {
                it.counters.collect {
                    latestCounters = it
                    updateCounterViews()
                }
            }
        }
    }

    private fun updateCounterViews() {
        latestCounters.entries.forEach { (name, counter) ->
            counterViews[name]?.let { (view, source) ->
                val count = when (source) {
                    CounterSource.UNREAD -> counter.unreadCount
                    CounterSource.UNSEEN -> counter.unseenCount
                    CounterSource.TOTAL -> counter.totalCount
                }
                view.updateCounter(count)
            }
        }
    }

    private fun SbisButton.updateCounter(value: Int) {
        model = model.copy(counter = SbisButtonCounter(value))
    }
}