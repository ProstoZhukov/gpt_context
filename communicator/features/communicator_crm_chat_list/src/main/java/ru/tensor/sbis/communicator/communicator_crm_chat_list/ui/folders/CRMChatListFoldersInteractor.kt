package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.folders

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.base_folders.CommunicatorBaseFoldersProvider
import ru.tensor.sbis.communicator.base_folders.ResultCreateFolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListFilterHolder
import ru.tensor.sbis.consultations.generated.ConsultationCounterService
import ru.tensor.sbis.consultations.generated.DataRefreshedCallback
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderType
import ru.tensor.sbis.toolbox_decl.Result
import timber.log.Timber
import java.util.UUID

/**
 * Функционал провайдера папок.
 *
 * @author da.zhukov
 */
internal class CRMChatListFoldersInteractor(
    private val crmChatListFilterHolder: CRMChatListFilterHolder,
    initialFolderListObservable: PublishSubject<List<Folder>>
) : CommunicatorBaseFoldersProvider(initialFolderListObservable, true) {

    private val service by lazy { ConsultationCounterService.instance() }

    override fun getAdditionalCommand(): Observable<AdditionalCommand>  = Observable.never()

    override fun create(parentId: String, name: String): Single<Result> = Single.never()

    override fun rename(id: String, newName: String): Single<Result> = Single.never()

    override fun delete(id: String): Single<Result> = Single.never()

    override fun unshare(id: String): Single<Result> = Single.never()

    override val dataRefreshObservable: Observable<List<Folder>> = Observable.create { emitter ->
        val subscriptionHolder = service.dataRefreshed().subscribe(
            object : DataRefreshedCallback() {
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
        return service.list(crmChatListFilterHolder.getCurrentFolderFilter()).result.map {
            Folder(
                id = it.consultationGroupType.name,
                title = it.label,
                type = FolderType.DEFAULT,
                depthLevel = 0,
                totalContentCount = it.all.toInt(),
                unreadContentCount = it.expired.toInt()
            )
        }
    }

    override fun loadFoldersFromCache(): List<Folder> {
        return service.refresh(crmChatListFilterHolder.getCurrentFolderFilter()).result.map {
            Folder(
                id = it.consultationGroupType.name,
                title = it.label,
                type = FolderType.DEFAULT,
                depthLevel = 0,
                totalContentCount = it.all.toInt(),
                unreadContentCount = it.expired.toInt()
            )
        }
    }

    override fun createFolder(parentUuid: UUID?, name: String): ResultCreateFolder {
        return ResultCreateFolder(CommandStatus(ErrorCode.NOT_IMPLEMENTED, StringUtils.EMPTY), null)
    }

    override fun renameFolder(uuid: UUID, newName: String): CommandStatus {
        return CommandStatus(ErrorCode.NOT_IMPLEMENTED, StringUtils.EMPTY)
    }

    override fun deleteFolder(uuid: UUID): CommandStatus {
        return CommandStatus(ErrorCode.NOT_IMPLEMENTED, StringUtils.EMPTY)
    }

    fun updateFolders() {
        loadFolders()
    }
}