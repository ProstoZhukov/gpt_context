package ru.tensor.sbis.communicator.themes_registry.utils.counter.navigation

import ru.tensor.sbis.communicator.declaration.counter.nav_counters.CommunicatorNavCounters
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter

/**
 * Реализация счетчиков модуля коммуникатор для компонентов навигации
 * @see [NavConversationCounter]
 * @see [NavDialogsCounter]
 * @see [NavChatsCounter]
 *
 * @property counterProvider поставщик данных о количестве непрочитанных сообщений
 *
 * @author vv.chekurda
 */
internal class CommunicatorNavCountersImpl(
    private val counterProvider: CounterProvider<CommunicatorCounterModel>
) : CommunicatorNavCounters {

    override val messagesCounter: NavigationCounter by lazy {
        NavConversationCounter(counterProvider)
    }

    override val dialogsCounter: NavigationCounter by lazy {
        NavDialogsCounter(counterProvider)
    }

    override val chatsCounter: NavigationCounter by lazy {
        NavChatsCounter(counterProvider)
    }
}