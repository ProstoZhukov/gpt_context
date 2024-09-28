package ru.tensor.sbis.communicator.communicator_crm_chat_list.data

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.helper.CRMCollectionSynchronizeHelper
import ru.tensor.sbis.consultations.generated.CollectionOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationCollectionProvider
import ru.tensor.sbis.consultations.generated.ConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationListFilter
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.PaginationOfConsultationListAnchor
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Реализация CRMChatListCollectionWrapper.
 *
 * @author da.zhukov
 */
internal class CRMChatListCollectionWrapperImpl(
    private val consultationCollectionProvider: DependencyProvider<ConsultationCollectionProvider>,
    private val filterProvider: () -> ConsultationListFilter,
    private val collectionSynchronizeHelper: CRMCollectionSynchronizeHelper
) : CRMChatListCollectionWrapper {

    override fun createEmptyFilter(): ConsultationListFilter =
        filterProvider()

    override fun createPaginationAnchor(
        itemsOnPage: Long,
        directionType: DirectionType
    ): PaginationOfConsultationListAnchor {
        return PaginationOfConsultationListAnchor(null, DirectionType.FORWARD, itemsOnPage)
    }

    override fun createCollection(
        filter: ConsultationListFilter,
        anchor: PaginationOfConsultationListAnchor
    ): CollectionOfConsultationListElementModel {
        return consultationCollectionProvider.get().get(
            filter,
            anchor
        )
    }

    override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfConsultationListElementModel, ConsultationListElementModel>): CRMChatListCollectionObserver {
        return CRMChatListCollectionObserver(observer).also {
            collectionSynchronizeHelper.observer = it
        }
    }

    override fun setObserver(
        observer: CRMChatListCollectionObserver,
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

    override fun getIndex(itemWithIndex: ItemWithIndexOfConsultationListElementModel): Long =
        itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfConsultationListElementModel): ConsultationListElementModel =
        itemWithIndex.item
}