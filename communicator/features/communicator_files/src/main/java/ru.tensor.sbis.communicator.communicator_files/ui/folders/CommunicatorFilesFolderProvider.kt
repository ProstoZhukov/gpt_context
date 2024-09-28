package ru.tensor.sbis.communicator.communicator_files.ui.folders

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.base_folders.CommunicatorBaseFoldersProvider
import ru.tensor.sbis.communicator.base_folders.ResultCreateFolder
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFilesFilterHolder
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeAttachmentsFolderDatasourceCallback
import ru.tensor.sbis.communicator.generated.ThemeAttachmentController
import ru.tensor.sbis.communicator.generated.ThemeAttachmentsFolder
import ru.tensor.sbis.communicator.generated.ThemeAttachmentsFolderDatasource
import ru.tensor.sbis.communicator.generated.ThemeAttachmentsFolderFilter
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderType
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import timber.log.Timber
import java.util.UUID
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Функционал провайдера папок.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesFolderProvider(
    initialFolderListObservable: PublishSubject<List<Folder>>,
    filterHolder: CommunicatorFilesFilterHolder,
    private val context: SbisThemedContext
) : CommunicatorBaseFoldersProvider(initialFolderListObservable, true) {

    private val foldersDatasource by lazy { ThemeAttachmentsFolderDatasource.instance() }
    private val attachmentController by lazy { ThemeAttachmentController.instance() }

    private val folderFilter = ThemeAttachmentsFolderFilter(
        themeId = filterHolder().themeId
    ).apply {
        includeSubfolders = true
    }

    override val dataRefreshObservable: Observable<List<Folder>> = Observable.create { emitter ->
        val subscriptionHolder = foldersDatasource.dataRefreshed().subscribe(
            object : DataRefreshedThemeAttachmentsFolderDatasourceCallback() {
                override fun onEvent() {
                    try {
                        emitter.onNext(loadFoldersFromCache())
                    } catch (e: Throwable) {
                        Timber.e(e)
                    }
                }
            }
        )
        subscriptionHolder.enable()
        emitter.setCancellable { subscriptionHolder.disable() }
    }

    override fun loadFolders(): List<Folder> {
        return foldersDatasource.list(folderFilter).result.map { folder ->
            folder.mapToUIFolder()
        }
    }

    override fun loadFoldersFromCache(): List<Folder> {
        return foldersDatasource.refresh(folderFilter).result.map { folder ->
            folder.mapToUIFolder()
        }
    }

    override fun renameFolder(uuid: UUID, newName: String): CommandStatus {
        return attachmentController.renameFolder(folderFilter.themeId, uuid, newName)
    }

    override fun createFolder(parentUuid: UUID?, name: String): ResultCreateFolder {
        val result = attachmentController.createFolder(folderFilter.themeId, name, parentUuid)
        return ResultCreateFolder(result, null)
    }

    override fun deleteFolder(uuid: UUID): CommandStatus {
        return attachmentController.deleteFolder(folderFilter.themeId, uuid)
    }

    private fun ThemeAttachmentsFolder.mapToUIFolder(): Folder =
        Folder(
            id = id?.toString() ?: ROOT_FOLDER_ID,
            title =  id?.let { name } ?: context.getString(RCommunicatorDesign.string.communicator_folder_files_title),
            type = FolderType.EDITABLE,
            depthLevel = hierarchyLevel,
            totalContentCount = 0,
            unreadContentCount = 0
        )
}