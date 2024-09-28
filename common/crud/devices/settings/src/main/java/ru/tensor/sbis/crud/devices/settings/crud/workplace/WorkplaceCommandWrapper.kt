package ru.tensor.sbis.crud.devices.settings.crud.workplace

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.devices.settings.generated.DataRefreshedWorkplaceFacadeCallback
import ru.tensor.devices.settings.generated.WorkplaceFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.crud.devices.settings.model.Workplace
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.devices.settings.generated.Workplace as ControllerWorkplace

/**
 * Wrapper команд для контроллера для работы с настройками.
 */
interface WorkplaceCommandWrapper {

    val createCommand: CreateObservableCommand<ControllerWorkplace>
    val readCommand: ReadObservableCommand<Workplace>
    val updateCommand: UpdateObservableCommand<ControllerWorkplace>
    val deleteCommand: DeleteRepositoryCommand<ControllerWorkplace>
    val listCommand: BaseListObservableCommand<PagedListResult<BaseItem<Workplace>>, WorkplaceFilter, DataRefreshedWorkplaceFacadeCallback>

    /**
     * Функция для получения рабочего места по идентификатору
     *
     * @param workPlaceId - идентификатор рабочего места
     */
    fun readId(workPlaceId: Long): Observable<Workplace>

    /**
     * Попытка обновления рабочего места
     *
     * @param workplace - обновлённое рабочее место
     */
    fun updateTry(workplace: Workplace): Single<Workplace>

    /**
     * Функция для получения рабочего места, если оно привязано к устройству на котором запущено приложение
     */
    fun fetch(): Observable<Workplace>

    /**
     * Функция для "честного" получения рабочего места, если оно привязано к устройству на котором запущено приложение.
     * В отличии от [fetch] не возвращает заглушку рабочего места, а кидает onComplete.
     * */
    fun fetchMaybe(): Maybe<Workplace>

    /**
     * Функция для создания рабочего места
     *
     * @param name - имя рабочего места
     * @param companyId - идентификатор компании
     * @param deviceId - идентификатор устройства на котором запущено приложение
     * @param deviceName - имя устройства на котором запущено приложение
     */
    fun create(name: String, companyId: Long, deviceId: String? = null, deviceName: String? = null): Observable<Workplace>

    /**
     * Функция для создания рабочего места с названием по умолчанию
     * и привязанного к текущей компании пользователя.
     * Не осуществляет переключение между потоками. Это обязанность клиентского кода
     *
     * @param deviceId - идентификатор устройства на котором запущено приложение
     * @param deviceName - имя устройства на котором запущено приложение
     *
     */
    fun create(deviceId: String?, deviceName: String?): Observable<Workplace>

    /**
     * Функция для принудительного удаления рабочего места по идентификатору
     *
     * @param workPlaceId - идентификатор рабочего места
     */
    fun delete(workPlaceId: Long): Observable<Boolean>

    /**
     * Попробовать удалить рабочее место
     *
     * @param workPlaceId - идентификатор рабочего места
     */
    fun deleteTry(workPlaceId: Long): Completable

    /**
     * Функция для получения дефолтного имени для рабочего места по идентификатору компании
     *
     * @param companyId - идентификатор компании
     */
    fun getDefaultName(companyId: Long): Observable<String>

    /**
     * Попробовать сменить компанию рабочего места
     *
     * @param workplaceId - идентификатор рабочего места
     * @param companyId - идентификатор компании
     */
    fun setCompanyTry(workplaceId: Long, companyId: Long): Completable

    /**
     * Принудительно сменить компанию рабочего места
     *
     * @param workplaceId - идентификатор рабочего места
     * @param companyId - идентификатор компании
     */
    fun setCompany(workplaceId: Long, companyId: Long): Completable

    /**
     * Восстановить удалённое рабочее место
     *
     * @param workplaceId - идентификатор рабочего места
     */
    fun restore(workplaceId: Long): Completable

    /**
     * Идентификатор устройства
     */
    fun getDeviceId(): Single<String>

    /**
     * Функция для получения рабочего места, если оно привязано к устройству, на котором запущено приложение.
     * В отличие от [fetch] получает только локальные данные и не производит синхронизацию с облаком
     */
    fun readCurrent(): Maybe<Workplace>

    /**@SelfDocumented */
    val emptyWorkplace: Workplace
}
