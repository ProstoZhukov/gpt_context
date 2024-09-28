package ru.tensor.sbis.communicator.themes_registry.utils.counter.navigation

import androidx.arch.core.util.Function
import io.reactivex.Observable
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import ru.tensor.sbis.design.navigation.view.model.FormatterType
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter
import ru.tensor.sbis.design.navigation.view.model.SIMPLE_FORMAT

/**
 * Счётчик непрочитанных сообщений в диалогах и чатах для компонентов навигации
 * @param provider поставщик данных о количестве непрочитанных сообщений
 *
 * @author vv.chekurda
 */
internal class NavConversationCounter(provider: CounterProvider<CommunicatorCounterModel>) : NavigationCounter {

    override val newCounter: Observable<Int> = provider.counterEventObservable.map { it.unreadTotal }
    override val totalCounter: Observable<Int> = Observable.empty()

    override fun getFormatter(type: FormatterType): Function<Int, String?> = SIMPLE_FORMAT
}