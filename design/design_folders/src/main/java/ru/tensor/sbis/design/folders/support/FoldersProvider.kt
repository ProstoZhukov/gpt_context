package ru.tensor.sbis.design.folders.support

import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.toolbox_decl.Result
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.Folder

/**
 * Интерфейс для работы с данными папок
 *
 * @author ma.kolpakov
 */
interface FoldersProvider {

    /** Подписка на получение папок */
    fun getFolders(): Observable<List<Folder>>

    /**
     * Подписка на получение дополнительной команды.
     * При отсутствии команды публикуется объект [AdditionalCommand.EMPTY]
     */
    fun getAdditionalCommand(): Observable<AdditionalCommand>

    /** Создание новой папки */
    fun create(parentId: String, name: String): Single<Result>

    /** Переименование */
    fun rename(id: String, newName: String): Single<Result>

    /** Удаление */
    fun delete(id: String): Single<Result>

    /** Отмена шаринга */
    fun unshare(id: String): Single<Result>
}
