package ru.tensor.sbis.communicator.themes_registry.utils.counter

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.communicator.generated.DataRefreshedUnreadCountersControllerCallback
import ru.tensor.sbis.communicator.generated.UnreadCountersController
import ru.tensor.sbis.counter_provider.CounterRepository
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Репозиторий счётчиков непрочитанных диалогов и чатов
 *
 * @param controller - контроллер счётчиков [UnreadCountersController]
 */
internal class CommunicatorCounterRepository(
    private val controller: DependencyProvider<UnreadCountersController>
) : CounterRepository<CommunicatorCounterModel> {

    /**@SelfDocumented */
    override fun getCounter(): CommunicatorCounterModel {
        val controller = controller.get()
        val unreadCounters = controller.get()
        return CommunicatorCounterModel(
            unreadDialogs = unreadCounters.unreadDialogs,
            unviewedDialogs = unreadCounters.unviewedDialogs,
            unreadChats = unreadCounters.unreadChats,
            unviewedChats = unreadCounters.unviewedChats,
            unreadTotal = unreadCounters.unreadTotal,
            unviewedTotal = unreadCounters.unviewedTotal
        )
    }

    /**@SelfDocumented */
    override fun subscribe(callback: DataRefreshCallback): Subscription {
        return controller.get().dataRefreshed().subscribe(
            object : DataRefreshedUnreadCountersControllerCallback() {
                override fun onEvent() {
                    callback.execute(hashMapOf())
                }
            }
        )
    }
}
