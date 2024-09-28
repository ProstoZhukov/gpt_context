package ru.tensor.sbis.communicator_support_channel_list.data

import ru.tensor.sbis.communicator_support_channel_list.interactor.SupportChatsServiceFactory
import ru.tensor.sbis.consultations.generated.OnUnreadMsgCounterChangeCallback
import ru.tensor.sbis.consultations.generated.SupportChatsType
import ru.tensor.sbis.counter_provider.BaseCounterRepository
import ru.tensor.sbis.platform.generated.Subscription
import javax.inject.Inject


internal class SupportUnreadCounterRepositoryFactory @Inject constructor(
    private val supportChatsServiceFactory: SupportChatsServiceFactory
) {
    fun create(type: SupportChatsType) = SupportUnreadCounterRepository(type, supportChatsServiceFactory)
}

/**
 * Репозиторий, реализация BaseCounterRepository, для получения счетчика непрочитанных для
 * службы поддержки
 */
internal class SupportUnreadCounterRepository(
    private val type: SupportChatsType, private val supportChatsServiceFactory: SupportChatsServiceFactory
) : BaseCounterRepository<Int, OnUnreadMsgCounterChangeCallback> {

    private val supportChatsService by lazy {
        supportChatsServiceFactory.create(type)
    }

    override fun getCounter(): Int {
        return supportChatsService.unreadMsgCounter().toInt()
    }

    override fun subscribe(callback: OnUnreadMsgCounterChangeCallback): Subscription {
        return supportChatsService.onUnreadMsgCounterChange().subscribe(callback)
    }
}