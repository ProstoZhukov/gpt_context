package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.consultations.generated.CollectionOfOperatorViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfOperatorViewModel
import ru.tensor.sbis.consultations.generated.OperatorCollectionFilter
import ru.tensor.sbis.consultations.generated.OperatorCollectionProvider
import ru.tensor.sbis.consultations.generated.OperatorViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfOperatorAnchor
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Реализация CRMAnotherOperatorCollectionWrapper.
 *
 * @author da.zhukov
 */
internal class CRMAnotherOperatorCollectionWrapperImpl(
    private val operatorCollectionProvider: DependencyProvider<OperatorCollectionProvider>,
    private val filterProvider: () -> OperatorCollectionFilter
) : CRMAnotherOperatorCollectionWrapper {

    override fun createEmptyFilter(): OperatorCollectionFilter =
        filterProvider()

    override fun createPaginationAnchor(itemsOnPage: Long, directionType: DirectionType): PaginationOfOperatorAnchor {
        return PaginationOfOperatorAnchor(
            null,
            DirectionType.FORWARD,
            itemsOnPage
        )
    }

    override fun createCollection(
        filter: OperatorCollectionFilter,
        anchor: PaginationOfOperatorAnchor
    ): CollectionOfOperatorViewModel {
        return operatorCollectionProvider.get().get(
            filter,
            anchor
        )
    }

    override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfOperatorViewModel, OperatorViewModel>): CRMAnotherOperatorCollectionObserver {
        return CRMAnotherOperatorCollectionObserver(
            observer
        )
    }

    override fun setObserver(
        observer: CRMAnotherOperatorCollectionObserver,
        toCollection: CollectionOfOperatorViewModel
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfOperatorViewModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfOperatorViewModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfOperatorViewModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfOperatorViewModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfOperatorViewModel): Long =
        itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfOperatorViewModel): OperatorViewModel =
        itemWithIndex.item
}