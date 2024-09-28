package ru.tensor.sbis.folderspanel

import io.reactivex.Observable
import ru.tensor.sbis.toolbox_decl.Result

/**
 * Базовый интерактор компонента папок. Определяет основные use-case при работе с папкам (общие для всех модулей)
 */
interface FoldersInteractor<FOLDER> {

    /**
     * Создание папки
     * @param parentUuid uuid родительской папки (внутри которой происходит создание папки)
     * @param name имя новой папки
     * @return источник данных, возвращаюший пару: результат выполнения операции [Result] и модель созданной папки [FOLDER]
     */
    fun create(parentUuid: String, name: String): Observable<Pair<Result, FOLDER>>

    /**
     * Создание папки в корне
     * @param name имя новой папки
     * @return источник данных, возвращаюший пару: результат выполнения операции [Result] и модель созданной папки [FOLDER]
     */
    fun createInRoot(name: String): Observable<Pair<Result, FOLDER>>

    /**
     * Получение списка всех папок
     * @return источник данных, возвращаюший список моделей папок [FOLDER]
     */
    fun getFolders(): Observable<List<FOLDER>>

    /**
     * Получение списка всех папок, за исключением подпапок.
     * Используется для выбора целевой папки при перемещении
     * @param uuid uuid папки, для которой нужно исключить подпапки
     * @return источник данных, возвращаюший список моделей папок [FOLDER]
     */
    fun getFoldersWithoutSubFolders(uuid: String): Observable<List<FOLDER>>

    /**
     * Удаление папки
     * @param uuid uuid удаляемой папки
     * @return источник данных, возвращаюший результат выполнения операции [Result]
     */
    fun delete(uuid: String): Observable<Result>

    /**
     * Переименование папки
     * @param uuid uuid папки, которую нужно переименовать
     * @param name новое имя папки
     * @return источник данных, возвращаюший результат выполнения операции [Result]
     */
    fun rename(uuid: String, name: String): Observable<Result>
}