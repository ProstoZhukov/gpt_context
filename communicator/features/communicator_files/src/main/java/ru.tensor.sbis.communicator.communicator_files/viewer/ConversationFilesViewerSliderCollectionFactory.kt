package ru.tensor.sbis.communicator.communicator_files.viewer

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communicator.common.viewer_factory.data.DialogAttachmentViewerArgsFactory.createArgs
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileActionData
import ru.tensor.sbis.communicator.generated.CollectionObserverOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.CollectionOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.PaginationOfThemeAttachmentAnchor
import ru.tensor.sbis.communicator.generated.ThemeAttachmentAnchor
import ru.tensor.sbis.communicator.generated.ThemeAttachmentCollectionProvider
import ru.tensor.sbis.communicator.generated.ThemeAttachmentFilter
import ru.tensor.sbis.communicator.generated.ThemeAttachmentViewModel
import ru.tensor.sbis.service.generated.DirectionType
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import ru.tensor.sbis.viewer.decl.slider.source.IndexedViewerArgs
import ru.tensor.sbis.viewer.decl.slider.source.ViewerArgsSource
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderCollection
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderCollectionFactory
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderCollectionObserver
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderDirection
import ru.tensor.sbis.viewer.decl.slider.source.ViewerSliderViewPosition
import ru.tensor.sbis.viewer.decl.slider.source.logViewerCollection
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import java.util.UUID

/**
 * Фабрика наблюдаемой коллекции [ViewerSliderCollection] для слайдера просмотрщика файлов переписки.
 *
 * @author da.zhukov
 */
@Parcelize
class ConversationFilesViewerSliderCollectionFactory(
    private val themeUuid: UUID,
    private val folderUuid: UUID?,
    private val fileUuid: UUID,
) : ViewerSliderCollectionFactory {

    override fun createCollection(
        anchor: ViewerArgs?,
        itemsOnPage: Long,
        direction: ViewerSliderDirection
    ): ViewerSliderCollection =
        ConversationFilesViewerSliderCollection(
            collectionProvider = lazy { ThemeAttachmentCollectionProvider.instance() },
            filter = ThemeAttachmentFilter(
                themeId = themeUuid
            ).apply {
                rowItemCount = 1
                folderId = folderUuid
            },
            anchor = PaginationOfThemeAttachmentAnchor(
                anchor = ThemeAttachmentAnchor(attachmentId = fileUuid),
                direction = DirectionType.BOTHWAY,
                pageSize = itemsOnPage
            )
        )
}

private class ConversationFilesViewerSliderCollection(
    private val collectionProvider: Lazy<ThemeAttachmentCollectionProvider>,
    private val filter: ThemeAttachmentFilter,
    private val anchor: PaginationOfThemeAttachmentAnchor
) : ViewerSliderCollection {

    private val collection: CollectionOfThemeAttachmentViewModel by lazy {
        logViewerCollection("ConversationFilesViewerCollection create: filter = $filter, anchor = $anchor")
        collectionProvider.value.get(filter, anchor)
    }

    override fun setObserver(observer: ViewerSliderCollectionObserver) {
        logViewerCollection("ConversationFilesViewerCollection setObserver")
        collection.setObserver(
            object : CollectionObserverOfThemeAttachmentViewModel() {
                override fun onReset(items: ArrayList<ThemeAttachmentViewModel>) {
                    logViewerCollection(
                        "ConversationFilesViewerCollection onReset ${
                            items.map { "id = ${it.id}, attachmentId = ${it.attachmentList.first().fileInfoViewModel.id}" }
                        }"
                    )
                    observer.onReset(items.toViewerArgsList())
                }

                override fun onRemove(index: ArrayList<Long>) {
                    logViewerCollection("ConversationFilesViewerCollection onRemove $index")
                    observer.onRemove(index)
                }

                override fun onMove(param: ArrayList<IndexPair>) {
                    logViewerCollection("ConversationFilesViewerCollection onMove ${param.size}")
                    observer.onMove(param.map { it.firstIndex to it.secondIndex })
                }

                override fun onAdd(param: ArrayList<ItemWithIndexOfThemeAttachmentViewModel>) {
                    logViewerCollection("ConversationFilesViewerCollection onAdd ${param.size}")
                    observer.onAdd(param.toIndexedViewerArgsList())
                }

                override fun onReplace(param: ArrayList<ItemWithIndexOfThemeAttachmentViewModel>) {
                    logViewerCollection("ConversationFilesViewerCollection onReplace ${param.size}")
                    observer.onReplace(param.toIndexedViewerArgsList())
                }

                override fun onAddThrobber(position: ViewPosition) {
                    logViewerCollection("ConversationFilesViewerCollection onAddThrobber $position")
                    observer.onAddThrobber(ViewerSliderViewPosition.HEADER)
                }

                override fun onRemoveThrobber() {
                    logViewerCollection("ConversationFilesViewerCollection onRemoveThrobber")
                    observer.onRemoveThrobber()
                }

                override fun onAddStub(stubType: StubType, position: ViewPosition) {
                    logViewerCollection(
                        "ConversationFilesViewerCollection onAddStub stubType = $stubType, position = $position"
                    )
                }

                override fun onRemoveStub() {
                    logViewerCollection("ConversationFilesViewerCollection onRemoveStub")
                    observer.onRemoveStub()
                }

                override fun onBeginUpdate() = Unit
                override fun onEndUpdate() = Unit
            }
        )
    }

    override fun refresh() {
        logViewerCollection("ConversationFilesViewerCollection refresh")
        collection.refresh()
    }

    override fun loadNext(anchorIndex: Long) {
        logViewerCollection("ConversationFilesViewerCollection loadNext $anchorIndex")
        collection.next(anchorIndex)
    }

    override fun loadPrevious(anchorIndex: Long) {
        logViewerCollection("ConversationFilesViewerCollection loadPrevious $anchorIndex")
        collection.prev(anchorIndex)
    }

    override fun dispose() {
        logViewerCollection("ConversationFilesViewerCollection dispose")
        collection.dispose()
    }

    private fun List<ItemWithIndexOfThemeAttachmentViewModel>.toIndexedViewerArgsList(): List<IndexedViewerArgs> =
        map {
            IndexedValue(
                it.index.toInt(),
                it.item.attachmentList.first().fileInfoViewModel.createArgs(it.item.id)
            )
        }

    private fun List<ThemeAttachmentViewModel>.toViewerArgsList(): List<ViewerArgs> =
        map { it.attachmentList.first().fileInfoViewModel.createArgs(it.id) }
}

/**
 * Создать аргументы для сквозного просмотрщика вложений диалога.
 *
 * @param themeUuid идентификатор переписки.
 * @param fileActionData данные для действия на кликнутое пользователем вложение из этого списка.
 */
internal fun createViewerSliderArgs(
    themeUuid: UUID,
    folderUuid: UUID?,
    fileActionData: CommunicatorFileActionData,
    filesActionData: List<CommunicatorFileActionData> = listOf(fileActionData),
): ViewerSliderArgs {
    return ViewerSliderArgs(
        ViewerArgsSource.Collection(
            filesActionData.map { it.fileInfoViewModel.createArgs(it.fileId) },
            filesActionData.indexOfFirst { it.fileId == fileActionData.fileId }.coerceAtLeast(0),
            ConversationFilesViewerSliderCollectionFactory(themeUuid, folderUuid, fileActionData.fileId)
        )
    )
}