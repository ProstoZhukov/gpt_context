package ru.tensor.sbis.messages_main_screen_addon

import io.reactivex.Observable
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import ru.tensor.sbis.toolbox_decl.counters.UnreadAndTotalCounterModel
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter

/**
 * Счётчик новых сообщений и чатов для компонентов навигации
 *
 * @author ma.kolpakov
 */
internal class MessagesNavCounter(provider: CounterProvider<UnreadAndTotalCounterModel>) : NavigationCounter {

    override val newCounter: Observable<Int> = provider.counterEventObservable.map { it.unreadCount }
    override val totalCounter: Observable<Int> = provider.counterEventObservable.map { it.totalCount }

    constructor() : this(Provider())

    override fun useTotalCounterAsSecondary(): Boolean = true

    private class Provider : CounterProvider<UnreadAndTotalCounterModel> {
        private val communicatorProvider by lazy {
            MessagesMainScreenAddonPlugin.communicatorCounterProviderFactoryProvider.get().communicatorCounterProvider
        }

        override fun getCounter(): UnreadAndTotalCounterModel {
            throw UnsupportedOperationException("Synchronous request unsupported")
        }

        override fun getCounterEventObservable(): Observable<UnreadAndTotalCounterModel> {
            return communicatorProvider.counterEventObservable
                .map { counterModel ->
                    UnreadAndTotalCounterModel(counterModel.unviewedDialogs, counterModel.unreadTotal)
                }
        }
    }
}