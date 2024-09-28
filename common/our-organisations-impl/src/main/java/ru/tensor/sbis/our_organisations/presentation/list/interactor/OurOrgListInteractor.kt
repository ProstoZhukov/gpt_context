package ru.tensor.sbis.our_organisations.presentation.list.interactor

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.our_organisations.data.OurOrgFilter
import ru.tensor.sbis.our_organisations.domain.OrganisationMapper
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.presentation.list.ui.ListResultWrapper
import ru.tensor.sbis.ourorg.generated.OurorgController
import ru.tensor.sbis.platform.sync.generated.AreaStatus
import ru.tensor.sbis.platform.sync.generated.AreaSyncInformer
import ru.tensor.sbis.platform.sync.generated.AreaSyncStatusChangedCallback
import ru.tensor.sbis.platform.sync.generated.SyncType
import java.util.UUID
import javax.inject.Inject

/**
 * Класс отвечает за получение и установку данных,
 * необходимых для работы модуля 'нашей организации'.
 *
 * @author mv.ilin
 */
internal class OurOrgListInteractor @Inject constructor(
    private val controller: Lazy<@JvmSuppressWildcards OurorgController>,
    areaSyncInformer: AreaSyncInformer
) {

    private companion object {
        const val OUR_ORG_AREA = "OURORG"
    }

    enum class DataEvent {
        REFRESH,
        NETWORK_ERROR,
        ERROR
    }

    private var dataRefreshSubscription: ru.tensor.sbis.platform.generated.Subscription? = null
    private val eventSubject = PublishSubject.create<DataEvent>()

    private val refreshCallback = object : OurorgController.DataRefreshedCallback() {
        override fun onEvent(param: HashMap<String, String>) {
            eventSubject.onNext(DataEvent.REFRESH)
        }
    }

    private val areaSyncInformerCallback = object : AreaSyncStatusChangedCallback() {
        override fun onEvent(status: AreaStatus) {
            val event = when (status) {
                AreaStatus.NETWORK_WAITING -> DataEvent.NETWORK_ERROR
                AreaStatus.ERROR -> DataEvent.ERROR
                AreaStatus.NOT_RUNNING,
                AreaStatus.RUNNING -> null
            }

            event?.let(eventSubject::onNext)
        }
    }

    init {
        dataRefreshSubscription = controller.value.dataRefreshed().subscribe(refreshCallback)
        areaSyncInformer.apply {
            areaSyncStatusChanged(OUR_ORG_AREA, SyncType.PARTIAL).subscribeUnmanaged(areaSyncInformerCallback)
            areaSyncStatusChanged(OUR_ORG_AREA, SyncType.INCREMENTAL).subscribeUnmanaged(areaSyncInformerCallback)
        }
    }

    fun subscribeDataRefreshEvents(): Observable<DataEvent> {
        return eventSubject
    }

    suspend fun listRx(filter: OurOrgFilter): ListResultWrapper<Organisation> =
        withContext(Dispatchers.IO) {
            controller.value.list(OrganisationMapper.toControllerFilter(filter)).let {
                OrganisationMapper.fromControllerList(it)
            }
        }

    suspend fun getHeadOrganisation(organisationUUID: UUID): Organisation? =
        withContext(Dispatchers.IO) {
            controller.value.read(organisationUUID).let { organisation ->
                if (organisation == null) return@let null
                organisation.parent
                    ?.let { getHeadOrganisation(it) }
                    ?: OrganisationMapper.fromController(organisation)
            }
        }

    suspend fun refreshRx(filter: OurOrgFilter): ListResultWrapper<Organisation> =
        withContext(Dispatchers.IO) {
            controller.value.refresh(OrganisationMapper.toControllerFilter(filter)).let {
                OrganisationMapper.fromControllerList(it)
            }
        }
}
