package ru.tensor.sbis.communicator.base_folders.folderfilter

import io.reactivex.Single
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Интерфейс интерактора для списка папок (контактов и диалогов)
 *
 * @author vv.chekurda
 */
interface CommunicatorFoldersInteractor<FOLDER> {

    /**
     * Получение списка всех папок из кэша с запросом в облако
     * @return источник данных, возвращаюший список моделей папок
     */
    fun list(): Single<List<FOLDER>>

    /**
     * Получение списка всех папок из кэша
     * @return источник данных, возвращаюший список моделей папок
     */
    fun refresh(): Single<List<FOLDER>>

    /**
     * Подписка на обновление списка папок
     * @param callback действие, которое будет выполняться по событию обновления
     * @return источник данных, возвращающий объект подписки
     */
    fun setDataRefreshCallback(callback: (HashMap<String,String>) -> Unit): Single<Subscription>
}
