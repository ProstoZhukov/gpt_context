package ru.tensor.sbis.communicator_support_consultation_list.data

import ru.tensor.sbis.consultations.generated.CollectionOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationCollectionProvider
import ru.tensor.sbis.consultations.generated.ConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationListFilter
import ru.tensor.sbis.consultations.generated.ConsultationListMode
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.PaginationOfConsultationListAnchor
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType
import java.util.UUID

/**
 * Реализация ChannelCollectionCrud3ServiceWrapper
 */
internal class ConsultationsCollectionCrud3ServiceWrapperImpl(
    private val channelId: UUID?,
    private val isSabyGet: Boolean,
    private val consultationCollectionProvider: ConsultationCollectionProvider
) :
    ConsultationsCollectionCrud3ServiceWrapper {

    override fun createEmptyFilter(): ConsultationListFilter {
        return ConsultationListFilter().apply {
            mode = ConsultationListMode.SUPPORT_SERVICE_SOURCE
            val channelUuids = ArrayList<UUID>().also {
                if (channelId != null) {
                    it.add(channelId)
                }
            }
            if (isSabyGet) {
                salePointIds = channelUuids
            } else {
                sourceIds = channelUuids
            }
        }
    }

    override fun createPaginationAnchor(
        itemsOnPage: Long,
        directionType: DirectionType
    ): PaginationOfConsultationListAnchor {
        return PaginationOfConsultationListAnchor(null, DirectionType.FORWARD, INIT_PAGE_SIZE)
    }

    override fun createCollection(
        filter: ConsultationListFilter,
        anchor: PaginationOfConsultationListAnchor
    ): CollectionOfConsultationListElementModel {
        return consultationCollectionProvider.get(
            filter,
            anchor
        )
    }

    override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfConsultationListElementModel, ConsultationListElementModel>): SupportConsultationsListCollectionObserver {
        return SupportConsultationsListCollectionObserver(observer)
    }

    override fun setObserver(
        observer: SupportConsultationsListCollectionObserver,
        toCollection: CollectionOfConsultationListElementModel
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfConsultationListElementModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfConsultationListElementModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfConsultationListElementModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfConsultationListElementModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfConsultationListElementModel): Long = itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfConsultationListElementModel): ConsultationListElementModel =
        itemWithIndex.item

    companion object {
        /**
         * Количество элеметов, запрашиваемых изначально
         * Используется для создания PaginationOfConsultationListAnchor
         */
        private const val INIT_PAGE_SIZE = 40L
    }
}