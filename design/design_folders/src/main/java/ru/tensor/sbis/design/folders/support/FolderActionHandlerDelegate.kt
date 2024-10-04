package ru.tensor.sbis.design.folders.support

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.folders.FoldersView
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.FolderActionInfo
import ru.tensor.sbis.design.folders.data.model.FolderActionType

/**
 * Вспомогательный объект для публикации событий от [FoldersView] к подписчикам [folderAction]
 *
 * @author ma.kolpakov
 */
internal class FolderActionHandlerDelegate : FolderActionHandler {

    private val folderActionSubject = PublishSubject.create<FolderActionInfo>()

    val folderAction: Observable<FolderActionInfo> = folderActionSubject

    override fun handleAction(actionType: FolderActionType, folderId: String, folderName: String?) {
        folderActionSubject.onNext(FolderActionInfo(actionType, folderId, folderName))
    }
}