package ru.tensor.sbis.communicator_support_channel_list.viewmodel.accessibility

import io.reactivex.Observable
import ru.tensor.sbis.communicator_support_channel_list.interactor.SupportChatsServiceFactory
import ru.tensor.sbis.consultations.generated.OnSourcesEmptyChangeCallback
import ru.tensor.sbis.consultations.generated.SupportChatsType
import ru.tensor.sbis.mvp.interactor.BaseInteractor

/** Проверка доступности отображения службы поддержки */
internal interface RegistryAccessibilityService {

    /**
     * Доступны ли клиентские каналы
     * */
    suspend fun isSupportAvailable(): Boolean

    /**
     * Доступны ли клиентские каналы
     *
     */
    fun isClientSupportAvailableObservable(): Observable<Boolean>
}

/**
 * Фабрика для RegistryAccessibilityServiceImpl
 * используется для создания разных RegistryAccessibilityServiceImpl, с разным SupportChatsType
 */
internal interface RegistryAccessibilityServiceFactory {
    fun create(supportChatsType: SupportChatsType): RegistryAccessibilityServiceImpl
}

/** Реализация [RegistryAccessibilityService] */
internal class RegistryAccessibilityServiceImpl(
    private val supportChatsServiceFactory: SupportChatsServiceFactory,
    private val supportChatsType: SupportChatsType,
) : BaseInteractor(), RegistryAccessibilityService {

    private val supportChatsService by lazy {
        supportChatsServiceFactory.create(supportChatsType)
    }

    override suspend fun isSupportAvailable(): Boolean = !supportChatsService.isSourcesEmpty()

    override fun isClientSupportAvailableObservable(): Observable<Boolean> {
        return Observable.create { emitter ->
            emitter.onNext(!supportChatsService.isSourcesEmpty())
            val subscription =
                supportChatsService.onSourcesEmptyChange().subscribe(object : OnSourcesEmptyChangeCallback() {
                    override fun onEvent(sourcesEmpty: Boolean) {
                        emitter.onNext(!sourcesEmpty)
                    }
                })
            emitter.setCancellable(subscription::disable)
        }
    }
}