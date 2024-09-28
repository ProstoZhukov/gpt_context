package ru.tensor.sbis.communicator_support_channel_list.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.communicator.generated.ConsultationChatType
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.consultations.generated.SupportChatsService
import ru.tensor.sbis.consultations.generated.SupportChatsType
import ru.tensor.sbis.consultations.generated.SupportRegistryData
import java.util.UUID
import javax.inject.Inject


/**
 * Фабрика для SupportChatsService
 * @see SupportChannelCollectionInteractor
 */
internal interface SupportChatsServiceFactory {
    fun create(
        type: SupportChatsType,
    ): SupportChatsService
}

/**
 * Фабрика для SupportChannelCollectionInteractor
 * @see SupportChannelCollectionInteractor
 */
internal class SupportChannelCollectionInteractorFactory @Inject constructor(
    private val collectionProviderFactory: SupportChatsServiceFactory,
    private val consultationService: ConsultationService
) {
    fun create(
        type: SupportChatsType
    ): SupportChannelCollectionInteractor = SupportChannelCollectionInteractor(collectionProviderFactory.create(type), consultationService)
}

/**
 * Интерактор для взаимойдствия с CollectionOfSupportChatsViewModel
 * Так как для разных вариантов отображения используется разный SupportChatsType,
 * который необходим для создания SupportChatsService, для создания SupportChannelCollectionInteractor используется
 * SupportChannelCollectionInteractorFactory, в которую передается SupportChatsServiceFactory, внутри которой создается
 * SupportChatsService с нужным режимом
 * @see SupportChatsType
 */
internal class SupportChannelCollectionInteractor constructor(
    private val collectionProvider: SupportChatsService,
    private val consultationService: ConsultationService
) {

    /**
     * Получить режим открытия реестра службы поддержки
     */
    internal suspend fun getRegistryMode(): SupportRegistryData {
        return withContext(Dispatchers.IO) {
            collectionProvider.getRegistryMode()
        }
    }

    /**
     * Получить режим открытия для провала в источник консультаций
     * @return Режим открытия и данные необходимые для отображаемого реестра
     */
    internal suspend fun getRegistryMode(sourceId: UUID): SupportRegistryData {
        return withContext(Dispatchers.IO) {
            collectionProvider.getRegistryMode(sourceId)
        }
    }

    /**
     * Получить id канала / источника если всего один канал
     * @param consultationId id источника консультаций
     */
    internal suspend fun getSourceId(consultationId: UUID): UUID? {
        return withContext(Dispatchers.IO) {
            consultationService.getConversationData(null, consultationId, false, ConsultationChatType.CLIENT).consultation?.sourceId
        }
    }
}