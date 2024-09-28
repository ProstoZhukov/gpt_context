package ru.tensor.sbis.verification_decl.permission

import androidx.annotation.WorkerThread
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * Интерфейс объекта, который предоставляет методы проверки уровня доступа к областям [PermissionScope]
 */
interface PermissionChecker {

    /**
     * Выполняет запрос уровня доступа к областям [scopes].
     * Результат будет ассинхронно возвращён в [permissionObservable]
     *
     * @param onError обработчик ошибок
     * @param scopes области, информацию о доступе к которым нужно запросить
     */
    fun checkPermissions(onError: Consumer<in Throwable>, vararg scopes: PermissionScope)

    /**
     * Выполняет запрос уровня доступа к областям [scopes].
     * Результат будет ассинхронно возвращён в [permissionObservable]
     *
     * @param onError обработчик ошибок
     * @param scopes области, информацию о доступе к которым нужно запросить
     */
    fun checkPermissions(onError: Consumer<in Throwable>, scopes: Collection<PermissionScope>)

    /**
     * Подписка на обновление доступа к области. Так как результат возвращается ассинхронно,
     * может потребоваться явно указать необходимый поток для получения данных
     */
    fun permissionObservable(): Observable<PermissionInfo>

    /**
     * Высвободить занимаемые ресурсы, без запрета на новые подписки
     */
    fun clear()

    /**
     * Выполняет блокирующий запрос уровня доступа к области [scope].
     *
     * @param scope область, информацию о доступе к которой нужно запросить
     * @param defaultIfMissing уровень доступа по умолчанию (если по данной обсласти нет информации о разрешениях)
     *
     * @throws PermissionRequestException если не удалось получить информацию по указанной области
     *
     * @return информация по запрашиваемой области
     */
    @WorkerThread
    @Throws(PermissionRequestException::class)
    fun checkPermissionNow(
        scope: PermissionScope,
        defaultIfMissing: PermissionLevel = PermissionLevel.NONE
    ): PermissionInfo

    /**
     * Выполняет блокирующий запрос уровня доступа к областям [scopes].
     *
     * @param scopes области, информацию о доступе к которым нужно запросить
     * @param defaultIfMissing уровень доступа по умолчанию (если по данной обсласти нет информации о разрешениях)
     *
     * @throws PermissionRequestException если не удалось получить информацию по указанным областям
     *
     * @return информация по запрашиваемым областям
     * (порядок элементов соответствует порядку во входном параметре [scopes])
     */
    @WorkerThread
    @Throws(PermissionRequestException::class)
    fun checkPermissionsNow(
        scopes: List<PermissionScope>,
        defaultIfMissing: PermissionLevel = PermissionLevel.NONE
    ): List<PermissionInfo>
}