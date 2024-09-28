package ru.tensor.sbis.communicator_support_channel_list.data

import ru.tensor.sbis.consultations.generated.OnUnreadMsgCounterChangeCallback
import ru.tensor.sbis.consultations.generated.SupportChatsType
import ru.tensor.sbis.counter_provider.BaseAbstractCounterProvider
import ru.tensor.sbis.platform.generated.Subscription
import javax.inject.Inject

/**
 * Провайдер для получения счетчика непрочитанных для
 * службы поддержки, реализация BaseAbstractCounterProvider
 */
internal class SupportUnreadCounterProvider @Inject constructor(supportUnreadCounterRepositoryFactory: SupportUnreadCounterRepositoryFactory) :
    BaseAbstractCounterProvider<Int, OnUnreadMsgCounterChangeCallback, SupportUnreadCounterRepository>(
        supportUnreadCounterRepositoryFactory.create(SupportChatsType.SABY_SUPPORT)
    ) {

    /**
     * Подписка на изменение счетчика
     */
    override val controllerSubscription: Subscription by lazy {
        repository.subscribe(object : OnUnreadMsgCounterChangeCallback() {
            override fun onEvent(newCounter: Long) {
                onDataRefreshed(HashMap())
            }
        })
    }
}