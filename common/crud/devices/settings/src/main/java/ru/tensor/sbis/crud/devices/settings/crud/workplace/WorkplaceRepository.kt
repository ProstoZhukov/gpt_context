package ru.tensor.sbis.crud.devices.settings.crud.workplace

import ru.tensor.devices.settings.generated.*
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository

/**
 * Интерфейс для связи с контроллером.
 */
interface WorkplaceRepository :
        CRUDRepository<Workplace>,
        BaseListRepository<ListResultOfWorkplaceMapOfStringString, WorkplaceFilter, DataRefreshedWorkplaceFacadeCallback> {

    /**
     * Попытка обновления рабочего места
     *
     * @param workplace - обновлённое рабочее место
     *
     * @return Workplace
     */
    fun updateTry(workplace: Workplace): Workplace

    /**
     * Функция для получения рабочего места по идентификатору
     *
     * @param workPlaceId - идентификатор рабочего места
     *
     * @return Workplace
     */
    fun readId(workPlaceId: Long): Workplace

    /**
     * Функция для получения рабочего места, если оно привязано к устройству на котором запущено приложение
     *
     * @return текущее рабочее место или заглушка
     */
    fun fetch(): Workplace

    /**
     * Функция для получения рабочего места, если оно привязано к устройству на котором запущено приложение
     *
     * @return текущее рабочее место или null
     */
    fun fetchNullable(): Workplace?

    /**
     * Функция для создания рабочего места
     *
     * @param name - имя рабочего места
     * @param companyId - идентификатор компании
     * @param deviceId - идентификатор устройства на котором запущено приложение
     * @param deviceName - имя устройства на котором запущено приложение
     *
     * @return Workplace
     */
    fun create(name: String, companyId: Long, deviceId: String?, deviceName: String?): Workplace

    /**
     * Функция для создания рабочего места c именем по умолчанию
     * и привязанного к текущей компании пользователя
     *
     * @param deviceId - идентификатор устройства на котором запущено приложение
     * @param deviceName - имя устройства на котором запущено приложение
     *
     * @return Workplace
     */
    fun create(deviceId: String?, deviceName: String?): Workplace

    /**
     * Функция для принудительного удаления рабочего места по идентификатору
     *
     * @param workPlaceId - идентификатор рабочего места
     *
     * @return Boolean
     */
    fun delete(workPlaceId: Long): Boolean

    /**
     * Попробовать удалить рабочее место
     *
     * @param workPlaceId - идентификатор рабочего места
     */
    fun deleteTry(workPlaceId: Long)

    /**
     * Функция для получения дефолтного имени для рабочего места по идентификатору компании
     *
     * @param companyId - идентификатор компании
     *
     * @return String
     */
    fun getDefaultName(companyId: Long): String

    /**
     * Попробовать сменить компанию рабочего места
     *
     * @param workplaceId - идентификатор рабочего места
     * @param companyId - идентификатор компании
     */
    fun setCompanyTry(workplaceId: Long, companyId: Long)

    /**
     * Принудительно сменить компанию рабочего места
     *
     * @param workplaceId - идентификатор рабочего места
     * @param companyId - идентификатор компании
     */
    fun setCompany(workplaceId: Long, companyId: Long)

    /**
     * Восстановить удалённое рабочее место
     *
     * @param workplaceId - идентификатор рабочего места
     */
    fun restore(workplaceId: Long)

    /**
     * Функция для получения рабочего места, если оно привязано к устройству, на котором запущено приложение.
     * В отличие от [fetch] получает только локальные данные и не производит синхронизацию с облаком
     *
     * @return текущее рабочее место или null
     */
    fun readCurrent(): Workplace?

    /**@SelfDocumented */
    val emptyWorkplace: Workplace
}
