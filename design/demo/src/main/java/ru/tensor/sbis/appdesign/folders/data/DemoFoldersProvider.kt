package ru.tensor.sbis.appdesign.folders.data

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.declaration.Result
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.AdditionalCommandType
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderType
import ru.tensor.sbis.design.folders.support.FoldersProvider

/**
 * @author ma.kolpakov
 */
class DemoFoldersProvider : FoldersProvider {

    private var folders: List<Folder> = FoldersData.moreThanTwenty.toMutableList()

    private val foldersSubject: Subject<List<Folder>> = BehaviorSubject.create()

    override fun getFolders(): Observable<List<Folder>> {
        foldersSubject.onNext(folders)
        return foldersSubject
    }

    override fun getAdditionalCommand(): Observable<AdditionalCommand> =
        Observable.just(AdditionalCommand("Command", AdditionalCommandType.SHARE))

    override fun create(parentId: String, name: String): Single<Result> {
        val parentFolder = folders.find { it.id == parentId }

        return if (parentFolder == null) {
            Single.just(Result.newInstanceForFail(""))
        } else {
            val newFolderIndex = folders.indexOf(parentFolder) + 1
            val newFolder = Folder(
                id = (folders.size + 1).toString(),
                title = name,
                type = FolderType.DEFAULT,
                depthLevel = parentFolder.depthLevel + 1,
                totalContentCount = 19,
                unreadContentCount = 10,
            )
            folders = folders.toMutableList().apply { add(newFolderIndex, newFolder) }

            foldersSubject.onNext(folders)
            Single.just(Result.SUCCESS)
        }
    }

    override fun rename(id: String, newName: String): Single<Result> {
        val renamedFolder = folders.find { it.id == id }
        return if (renamedFolder == null) {
            Single.just(Result.newInstanceForFail(""))
        } else {
            folders = folders.map {
                if (it.id == id) it.copy(title = newName) else it
            }
            foldersSubject.onNext(folders)
            Single.just(Result.SUCCESS)
        }
    }

    override fun delete(id: String): Single<Result> {
        val folderToDelete = folders.find { it.id == id }
        folders = folders.filterNot { it == folderToDelete }
        foldersSubject.onNext(folders)
        return Single.just(Result.SUCCESS)
    }

    override fun unshare(id: String): Single<Result> {
        val renamedFolder = folders.find { it.id == id }
        return if (renamedFolder == null) {
            Single.just(Result.newInstanceForFail(""))
        } else {
            folders = folders.map {
                if (it.id == id) it.copy(type = FolderType.DEFAULT) else it
            }
            foldersSubject.onNext(folders)
            Single.just(Result.SUCCESS)
        }
    }
}
