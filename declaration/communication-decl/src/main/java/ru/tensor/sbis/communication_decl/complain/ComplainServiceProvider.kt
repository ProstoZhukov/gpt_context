package ru.tensor.sbis.communication_decl.complain

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import io.reactivex.Observable
import ru.tensor.sbis.communication_decl.complain.data.ComplainParams
import ru.tensor.sbis.communication_decl.complain.data.ComplainResult
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Интерфейс сервиса "Пожаловаться".
 *
 * @author da.zhukov
 */
interface ComplainService {

    /**
     * Метод для отправки жалобы.
     * @param params параметры для жалобы.
     */
    @WorkerThread
    fun complain(params: ComplainParams): ComplainResult

    /**
     * Заблокировать персону.
     * @param uuid uuid персоны для блокировки.
     */
    @WorkerThread
    fun blockPerson(uuid: UUID)

    /**
     * Разблокировать персону.
     * @param uuid uuid персоны для разблокировки.
     */
    @WorkerThread
    fun unblockPerson(uuid: UUID)

    /**
     * Является ли данная персона заблокированной.
     * @param uuid uuid персоны для проверки.
     */
    @MainThread
    fun isPersonBlocked(uuid: UUID): Boolean

    /**
     * Возвращает [Observable] для подписки на изменения списка заблокированных персон.
     */
    @WorkerThread
    fun getBlockedListChangedObservable(): Observable<Unit>

    /**
     * Поставщик [ComplainService].
     */
    interface Provider : Feature {

        /**
         * Получить [ComplainService].
         */
        fun getComplainService(): ComplainService
    }
}