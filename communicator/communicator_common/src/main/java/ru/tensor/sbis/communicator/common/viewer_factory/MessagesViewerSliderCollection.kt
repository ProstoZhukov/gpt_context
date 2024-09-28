package ru.tensor.sbis.communicator.common.viewer_factory

import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tensor.sbis.communicator.common.analytics.CommunicatorAnalyticsUtil
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent
import ru.tensor.sbis.communicator.common.viewer_factory.data.DialogAttachmentViewerArgsFactory.createArgs
import ru.tensor.sbis.communicator.generated.CollectionObserverOfMessagesAttachmentViewModel
import ru.tensor.sbis.communicator.generated.CollectionOfMessagesAttachmentViewModel
import ru.tensor.sbis.communicator.generated.MessagesAttachmentAnchor
import ru.tensor.sbis.communicator.generated.MessagesAttachmentViewModel
import ru.tensor.sbis.communicator.generated.MessagesAttachmentCollectionProvider
import ru.tensor.sbis.communicator.generated.MessagesAttachmentFilter
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfMessagesAttachmentViewModel
import ru.tensor.sbis.communicator.generated.PaginationOfMessagesAttachmentAnchor
import ru.tensor.sbis.service.generated.DirectionType
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import ru.tensor.sbis.viewer.decl.slider.source.IndexedViewerArgs
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderCollection
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderCollectionFactory
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderCollectionObserver
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderDirection
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderViewPosition
import ru.tensor.sbis.viewer.decl.slider.source.logViewerCollection
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import java.util.UUID

@Parcelize
class MessagesViewerSliderCollectionFactory(
    private val themeUuid: UUID,
    private val attachmentUuid: UUID,
    private val analyticsUtil: @RawValue CommunicatorAnalyticsUtil?,
) : ViewerSliderCollectionFactory {

    override fun createCollection(
        anchor: ViewerArgs?,
        itemsOnPage: Long,
        direction: ViewerSliderDirection,
    ): ViewerSliderCollection =
        MessagesViewerSliderCollection(
            collectionProvider = lazy { MessagesAttachmentCollectionProvider.instance() },
            filter = MessagesAttachmentFilter(themeUuid),
            anchor = PaginationOfMessagesAttachmentAnchor(
                anchor = MessagesAttachmentAnchor(attachmentUuid),
                direction = DirectionType.BOTHWAY,
                pageSize = itemsOnPage
            ),
            analyticsUtil = analyticsUtil
        )
}

private class MessagesViewerSliderCollection(
    private val collectionProvider: Lazy<MessagesAttachmentCollectionProvider>,
    private val filter: MessagesAttachmentFilter,
    private val anchor: PaginationOfMessagesAttachmentAnchor,
    private val analyticsUtil: CommunicatorAnalyticsUtil?,
) : ViewerSliderCollection {

    private val collection: CollectionOfMessagesAttachmentViewModel by lazy {
        logViewerCollection("MessagesViewerCollection create: filter = $filter, anchor = $anchor")
        collectionProvider.value.get(filter, anchor)
    }

    override fun setObserver(observer: ViewerSliderCollectionObserver) {
        logViewerCollection("MessagesViewerCollection setObserver")
        collection.setObserver(
            object : CollectionObserverOfMessagesAttachmentViewModel() {
                override fun onReset(items: ArrayList<MessagesAttachmentViewModel>) {
                    logViewerCollection(
                        "MessagesViewerCollection onReset ${
                            items.map { "id = ${it.id}, attachmentId = ${it.fileInfoViewModel.id}" }
                        }"
                    )
                    observer.onReset(items.toViewerArgsList())
                }

                override fun onRemove(index: ArrayList<Long>) {
                    logViewerCollection("MessagesViewerCollection onRemove $index")
                    observer.onRemove(index)
                }

                override fun onMove(param: ArrayList<IndexPair>) {
                    logViewerCollection("MessagesViewerCollection onMove ${param.size}")
                    observer.onMove(param.map { it.firstIndex to it.secondIndex })
                }

                override fun onAdd(param: ArrayList<ItemWithIndexOfMessagesAttachmentViewModel>) {
                    logViewerCollection("MessagesViewerCollection onAdd ${param.size}")
                    observer.onAdd(param.toIndexedViewerArgsList())
                }

                override fun onReplace(param: ArrayList<ItemWithIndexOfMessagesAttachmentViewModel>) {
                    logViewerCollection("MessagesViewerCollection onReplace ${param.size}")
                    observer.onReplace(param.toIndexedViewerArgsList())
                }

                override fun onAddThrobber(position: ViewPosition) {
                    logViewerCollection("MessagesViewerCollection onAddThrobber $position")
                    observer.onAddThrobber(ViewerSliderViewPosition.HEADER)
                }

                override fun onRemoveThrobber() {
                    logViewerCollection("MessagesViewerCollection onRemoveThrobber")
                    observer.onRemoveThrobber()
                }

                override fun onAddStub(stubType: StubType, position: ViewPosition) {
                    logViewerCollection(
                        "MessagesViewerCollection onAddStub stubType = $stubType, position = $position"
                    )
                }

                override fun onRemoveStub() {
                    logViewerCollection("MessagesViewerCollection onRemoveStub")
                    observer.onRemoveStub()
                }

                override fun onBeginUpdate() = Unit
                override fun onEndUpdate() = Unit
            }
        )
    }

    override fun refresh() {
        logViewerCollection("MessagesViewerCollection refresh")
        collection.refresh()
    }

    override fun loadNext(anchorIndex: Long) {
        logViewerCollection("MessagesViewerCollection loadNext $anchorIndex")
        analyticsUtil?.sendAnalytics(
            ThemeAnalyticsEvent.ViewingAttachmentsInDialogCarousel(
                MessagesViewerSliderCollection::class.java.simpleName,
            ),
        )
        collection.next(anchorIndex)
    }

    override fun loadPrevious(anchorIndex: Long) {
        logViewerCollection("MessagesViewerCollection loadPrevious $anchorIndex")
        analyticsUtil?.sendAnalytics(
            ThemeAnalyticsEvent.ViewingAttachmentsInDialogCarousel(
                MessagesViewerSliderCollection::class.java.simpleName,
            ),
        )
        collection.prev(anchorIndex)
    }

    override fun dispose() {
        logViewerCollection("MessagesViewerCollection dispose")
        collection.dispose()
    }

    private fun List<MessagesAttachmentViewModel>.toViewerArgsList(): List<ViewerArgs> =
        map { it.fileInfoViewModel.createArgs(it.id) }

    private fun List<ItemWithIndexOfMessagesAttachmentViewModel>.toIndexedViewerArgsList(): List<IndexedViewerArgs> =
        map { IndexedValue(it.index.toInt(), it.item.fileInfoViewModel.createArgs(it.item.id)) }

    private fun ViewPosition.toSliderViewPosition(): ViewerSliderViewPosition =
        when (this) {
            ViewPosition.IN_PLACE -> ViewerSliderViewPosition.IN_PLACE
            ViewPosition.HEADER -> ViewerSliderViewPosition.HEADER
        }
}