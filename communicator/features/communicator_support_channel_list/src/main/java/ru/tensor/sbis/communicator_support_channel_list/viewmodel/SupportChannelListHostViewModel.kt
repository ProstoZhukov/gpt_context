package ru.tensor.sbis.communicator_support_channel_list.viewmodel

import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.tensor.sbis.communicator_support_channel_list.R
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportComponentConfig
import ru.tensor.sbis.communicator_support_channel_list.feature.controllerSupportChatsType
import ru.tensor.sbis.communicator_support_channel_list.interactor.SupportChannelCollectionInteractorFactory
import ru.tensor.sbis.communicator_support_consultation_list.feature.SabyGetConfig
import ru.tensor.sbis.communicator_support_consultation_list.feature.SupportConsultationListFragmentFactory
import ru.tensor.sbis.consultations.generated.SupportChatsViewModel
import ru.tensor.sbis.consultations.generated.SupportRegistryData
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import java.util.UUID

/**
 * Тип источника открытия переписки
 */
internal enum class ConversationSourceType {
    /**
     * Из канала, если всего одна консультация
     */
    Channel,

    /**
     * Из консультации
     */
    Consultation,

    /**
     * При старте - если единственная консультация и единственный канал
     */
    Start
}

/**
 * Событие для открытие переписки
 * @param id id переписки
 * @param conversationSourceType источник открытие переписки. Необходим для навигации
 */
internal data class OpenConversationAction(
    val id: UUID,
    val conversationSourceType: ConversationSourceType,
    val needBackButton: Boolean = false
)

@AssistedFactory
internal interface SupportChannelListHostVtiewModelFactory {
    fun create(
        config: SupportComponentConfig,
        isTablet: Boolean,
        @Assisted("sourceId") sourceId: UUID?,
        @Assisted("conversationId") conversationId: UUID?,
    ): SupportChannelListHostViewModel
}

/**
 * Вью-Модель для SupportChatListRootFragment
 * @param config конфиг отображения
 * @param isTablet
 * @param conversationId id переписки, которую надо открыть сразу
 * @param supportChatsCollectionInteractorFactory Фабрика интерактора для взаимойдствия с CollectionOfSupportChatsViewModel
 * @param supportRequestsListFeature Фабрика хост фрагмента реестра обращений в поддержку
 */
internal class SupportChannelListHostViewModel @AssistedInject constructor(
    @Assisted val config: SupportComponentConfig,
    @Assisted private val isTablet: Boolean,
    @Assisted("sourceId") private val sourceId: UUID?,
    @Assisted("conversationId") private val conversationId: UUID?,
    private val supportChatsCollectionInteractorFactory: SupportChannelCollectionInteractorFactory,
    private val supportRequestsListFeature: SupportConsultationListFragmentFactory
) : ViewModel() {

    private val supportChatsCollectionInteractor by lazy {
        supportChatsCollectionInteractorFactory.create(config.controllerSupportChatsType())
    }

    private val selectedConsultationFromPush = MutableSharedFlow<UUID>()

    /**
     * Режим открытия реестра службы поддержки
     */
    private val supportRegistryMode: MutableSharedFlow<SupportRegistryData?> = MutableSharedFlow()

    /**
     * Выбран канал в списке каналов
     */
    val onChannelSelectedInMaster = MutableSharedFlow<SupportChatsViewModel>()

    /**
     * Выбран канал в списке каналов
     */
    private val selectChannelForDetail = MutableSharedFlow<UUID>()

    /**
     * Выбрана консультация как единственный фрагмент,
     * то есть supportRegistryMode?.storedType == SupportRegistryData.supportRegistryDataMessages
     */
    val selectConsultationForDetail = MutableSharedFlow<UUID>()

    /**
     * При старте определено, что имеется только один канал с одной консультацией, её и открываем
     */
    val openConversationOnStart = supportRegistryMode.filter { supportRegistryMode ->
        supportRegistryMode?.storedType == SupportRegistryData.supportRegistryDataMessages
                && supportRegistryMode.fieldSupportRegistryDataMessages != null
    }.map {
        it?.fieldSupportRegistryDataMessages?.consultationId
    }.filterNotNull()

    /**
     * По клику на пуш уведомляем подписчиков о том, что надо открыть переписку.
     * Делаем map на getRegistryMode, так как если storedType != SupportRegistryData.supportRegistryDataMessages
     * То сначала будет открыт список каналов, а потом - переписка, для поддержки навигации по кнопке "назад"
     */
    val openConversationOnPush = selectedConsultationFromPush.map {
        it to (supportChatsCollectionInteractor.getRegistryMode().storedType == SupportRegistryData.supportRegistryDataMessages)
    }

    /**
     * Событие показа списка каналов
     * Если канал всего один, сразу показываем список консультаций в канале, если нет - показываем список каналов
     */
    val showChannelListOnStart: Flow<SupportRegistryData?> =
        supportRegistryMode.filter { supportRegistryMode ->
            supportRegistryMode?.storedType == SupportRegistryData.supportRegistryDataSources
        }

    /**
     * true если сейчас первая инициализация иначе false.
     */
    private var isFirstInit = true

    /**
     * Событие показа списка каналов
     * Если канал всего один, сразу показываем список консультаций в канале, если нет - показываем список каналов
     */
    val showConsultationsListOnStart: Flow<UUID> =
        supportRegistryMode.filter { supportRegistryMode ->
            supportRegistryMode?.storedType == SupportRegistryData.supportRegistryDataConsultations
                    && supportRegistryMode.fieldSupportRegistryDataConsultations != null
        }
            .map {
                it?.fieldSupportRegistryDataConsultations!!.sourceId
            }

    /**
     * Показать заглушку
     */
    val showViewStub: Flow<StubViewContent?> =
        supportRegistryMode.map {
            if (it?.storedType == SupportRegistryData.supportRegistryDataNone) {
                if (config is SupportComponentConfig.SabyGet) {
                    ResourceImageStubContent(
                        icon = R.drawable.communicator_support_channel_sabyget_stub_empty,
                        messageRes = R.string.communicator_support_channel_sabyget_message,
                        detailsRes = R.string.communicator_support_channel_sabyget_stub_subtitle
                    )
                } else {
                    ImageStubContent(
                        imageType = StubViewImageType.EMPTY,
                        messageRes = R.string.communicator_support_channel_list_no_data,
                        detailsRes = ResourcesCompat.ID_NULL
                    )
                }

            } else null
        }

    /**
     * Сразу ли открылся список консультаций? При переходе в поддержку через ННП (только 1 канал).
     */
    suspend fun isOpenConsultationListOnStart() =
        supportChatsCollectionInteractor.getRegistryMode().storedType == SupportRegistryData.supportRegistryDataConsultations
                && config is SupportComponentConfig.ClientSupport

    /**
     * Необходимо ли показывать кнопку создания консультации в sabyget и brand.
     */
    suspend fun needShowCreationButtonInSabyGet(salePoint: UUID?): Boolean =
        salePoint?.let {
            val supportRegistryData: SupportRegistryData = supportChatsCollectionInteractor.getRegistryMode(it)
            val needShowCreationButton: Boolean =
                if (supportRegistryData.storedType == SupportRegistryData.supportRegistryDataConsultations) {
                    supportRegistryData.fieldSupportRegistryDataConsultations?.createEnabled == true
                } else false
            needShowCreationButton
        } ?: false


    /**
     * Будет ли показано ННП на фргменте, надо ли это учитывать при отображении
     */
    val shouldShowNavPadding =
        merge(
            selectChannelForDetail,
            selectConsultationForDetail,
            selectedConsultationFromPush
        ).map {
            if (config !is SupportComponentConfig.SabySupport) {
                supportChatsCollectionInteractor.getRegistryMode().storedType != SupportRegistryData.supportRegistryDataSources
            } else {
                false
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /**
     * При выборе канала требуется открыть консультацию
     */
    private val openConsultationsFromChannelContract =
        selectChannelForDetail
            .map { supportChatsCollectionInteractor.getRegistryMode(it) }
            .filter { it.storedType == SupportRegistryData.supportRegistryDataConsultations }
            .map { it.fieldSupportRegistryDataConsultations }
            .filterNotNull()
            .map {
                val isThereChannels =
                    supportChatsCollectionInteractor.getRegistryMode().storedType == SupportRegistryData.supportRegistryDataSources
                val isSabyGetConfig = config is SupportComponentConfig.SabyGet
                val showBackButtonInConsultation =
                    ((!isTablet && config is SupportComponentConfig.ClientSupport && isThereChannels)
                            || (!isTablet && config is SupportComponentConfig.SabySupport)
                            || (!isTablet && isSabyGetConfig)) && (config.showLeftPanelOnToolbar)
                            || (!isTablet && !isOpenConsultationListOnStart())
                it.sourceId to supportRequestsListFeature.getSupportRequestsListFeatureContract(
                    supportRegistryDataConsultations = it,
                    needBackBtn = showBackButtonInConsultation,
                    sabyGetConfig = if (config is SupportComponentConfig.SabyGet) SabyGetConfig(config.isBrand, config.salePoint, config.hasAccordion) else null,
                    isSingleChannel = isOpenConsultationListOnStart()
                )
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 0)

    /**
     * Получаем список консультаций для канала "поддержка СБИС"
     */
    private val openConsultationsFromSupportChannel =
        supportRegistryMode
            .filterNotNull()
            .filter { it.storedType == SupportRegistryData.supportRegistryDataConsultations }
            .map { it.fieldSupportRegistryDataConsultations }
            .filterNotNull()
            .map {
                it.sourceId to supportRequestsListFeature.getSupportRequestsListFeatureContract(it, !isTablet)
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 0)

    /**
     * Открыть список консультаций из обоих источников
     * @see openConsultationsFromChannelContract
     * @see openConsultationsFromSupportChannel
     */
    val openConsultationsFromChannel =
        merge(openConsultationsFromChannelContract, openConsultationsFromSupportChannel)
            .filterNotNull()


    /**
     * При выборе канала требуется сразу открыть переписку
     */
    private val openConversationFromChannel =
        selectChannelForDetail
            .map { supportChatsCollectionInteractor.getRegistryMode(it) }
            .filter { it.storedType == SupportRegistryData.supportRegistryDataMessages }
            .map { it.fieldSupportRegistryDataMessages }
            .filterNotNull()
            .map {
                it.consultationId
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 0)


    /**
     * Создать консультацию извне, например из переписки по консультации.
     * UUID - индентификатор источника.
     * Bolean - необходимо ли открывать поверх всего.
     */
    val createConsultation = MutableSharedFlow<Pair<UUID, Boolean>>()

    /**
     * Открыть карточку компании(sabyget/brand).
     */
    val openCompanyDetail = MutableSharedFlow<UUID>()

    /**
     * Открыть переписку из консультации
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val openConversationFromConsultation =
        merge(openConsultationsFromChannelContract, openConsultationsFromSupportChannel)
            .filterNotNull()
            .flatMapLatest {
                it.second.selectedConsultation()
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val openCompanyDetailsCard =
        openConsultationsFromChannelContract
            .filterNotNull()
            .flatMapLatest {
                it.second.onSabyGetTitleClick()
            }.shareIn(viewModelScope, SharingStarted.Eagerly, 0)

    /**
     * Открыть переписку (из источника или списка консультаций)
     */
    val openConversationInDetail =
        merge(openConversationFromChannel.map {
            OpenConversationAction(it, ConversationSourceType.Channel, (config is SupportComponentConfig.SabyGet && config.isBrand))
        }, openConversationFromConsultation.map {
            OpenConversationAction(it, ConversationSourceType.Consultation)
        }, openConversationOnStart.map {
            OpenConversationAction(it, ConversationSourceType.Start)
        }).shareIn(viewModelScope, SharingStarted.Eagerly, 0)

    suspend fun initViewModel() {
        conversationId?.let {
            selectedConsultationFromPush.emit(it)
            selectConsultationForDetail.emit(it)
        }
        sourceId?.let {
            if (isFirstInit) {
                selectChannelForDetail.emit(it)
                isFirstInit = false
            }
        }
        if (conversationId == null && sourceId == null) {
            loadRegistryMode()
        }
    }

    /**
     * Создать новую консультацию, если есть выбранный источник консультаций.
     * @param createParams - параметры для открытия новой консультации.
     * UUID - индентификатор источника.
     * Bolean - необходимо ли открывать поверх всего.
     */
    fun createConsultation(createParams: Pair<UUID, Boolean>) = viewModelScope.launch {
        createConsultation.emit(createParams)
    }

    /**
     * Открыть карточку компанни в sabyget/brand.
     */
    fun openCompanyDetailCard(companyId: UUID) = viewModelScope.launch {
        openCompanyDetail.emit(companyId)
    }

    private suspend fun loadRegistryMode() {
        supportChatsCollectionInteractor.getRegistryMode().apply {
            supportRegistryMode.emit(this)
        }
    }
}
