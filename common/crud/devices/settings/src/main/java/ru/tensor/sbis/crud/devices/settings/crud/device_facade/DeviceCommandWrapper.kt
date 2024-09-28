package ru.tensor.sbis.crud.devices.settings.crud.device_facade

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.devices.settings.generated.DataRefreshedDeviceFacadeCallback
import ru.tensor.devices.settings.generated.DeviceFilter
import ru.tensor.sbis.crud.devices.settings.model.DeviceCanUpdate
import ru.tensor.sbis.crud.devices.settings.model.DeviceInside
import ru.tensor.sbis.crud.devices.settings.model.DeviceType
import ru.tensor.sbis.crud.devices.settings.model.TaxSystem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.devices.settings.generated.Device as ControllerDevice

/**
 * Wrapper команд для контроллера для работы с настройками.
 */
interface DeviceCommandWrapper {

    /**@SelfDocumented*/
    val createCommand: CreateObservableCommand<ControllerDevice>
    /**@SelfDocumented*/
    val readCommand: ReadObservableCommand<DeviceInside>
    /**@SelfDocumented*/
    val deleteCommand: DeleteRepositoryCommand<ControllerDevice>
    /**@SelfDocumented*/
    val listCommand: BaseListObservableCommand<PagedListResult<DeviceInside>, DeviceFilter, DataRefreshedDeviceFacadeCallback>

    /**
     * Функция для создания устройства
     *
     * @param model - модель устройства
     */
    fun create(model: DeviceInside): Observable<DeviceInside>

    /**
     * Получает данные, необходимые для создания устройства.
     * Для того чтобы "досоздать" устройство, необходимо передать полученный результат в метод 'update'.
     * Метод [create] вызывает [update] под капотом, так что на UI можно использовать его.
     */
    fun createBaseDevice(model: DeviceType, workplace: Long): Single<DeviceInside>

    /**
     * Функция для получения устройства по идентификатору
     *
     * @param id - идентификатор устройства
     */
    fun read(id: Long): Observable<DeviceInside>

    /**
     * Функция для обновления данных о подключении устройства
     *
     * @param deviceInside - модель устройства
     * @param actions - список идентификаторов операций
     */
    fun update(deviceInside: DeviceInside, actions: List<Int>): Observable<DeviceInside>

    /**
     * Функция для удаления устройства по идентификатору
     *
     * @param id - идентификатор устройства
     */
    fun delete(id: Long): Observable<Boolean>

    /**
     * Функция для получения активного устройства
     */
    fun getActiveDevice(): Observable<DeviceInside>

    /**
     * Функция для установки активного устройства
     *
     * @param model - модель устройства
     */
    fun setActiveDevice(model: DeviceInside): Completable

    /**
     * Функция для проверки возможности сделать устройство активным
     *
     * @param model - модель устройства
     */
    fun canUpdate(model: DeviceInside): Observable<DeviceCanUpdate>

    /**
     * Функция для проверки возможности сделать устройство активным
     *
     * @param model - модель устройства
     * @param actions - идентификаторы операций
     */
    fun canUpdate(model: DeviceInside, actions: List<Int>): Observable<DeviceCanUpdate>

    /**
     * Функция для деактивации устройства по идентификатору
     *
     * @param deviceId - идентификатор устройства
     */
    fun deactivate(deviceId: Long): Completable

    /**
     * Функция для деактивации устройства по идентификатору
     *
     * @param deviceId - идентификатор устройства
     * @param actions - идентификаторы операций
     */
    fun deactivate(deviceId: Long, actions: List<Int>): Completable

    /**
     * Возвращает Maybe который вернет активный терминал, если он доступен, или null (success), если его нет
     */
    fun getActivePaymentTerminal(): Maybe<ControllerDevice?>

    /**
     * Синхронно возвращает активный терминал
     */
    fun getActivePaymentTerminalSync(): ControllerDevice?

    /**
     * Возвращает Maybe который вернет активные весы, если они доступены, или null (success), если их нет
     */
    fun getActiveScales(): Maybe<ControllerDevice?>

    /**
     * Синхронно возвращает активные весы
     */
    fun getActiveScalesSync(): ControllerDevice?

    /**
     * Найти сканер штрихкодов среди списка устойств
     */
    fun getActiveScannersFromList(devices: List<DeviceInside>): List<DeviceInside>

    /**
     * Найти платежный терминал среди списка устойств
     */
    fun getActivePaymentTerminalFromList(devices: List<DeviceInside>): DeviceInside?

    /** Синхронно возвращает активный принтер. */
    fun getActivePrinterSync(): ControllerDevice?

    /** Синхронно возвращает активный дисплей. */
    fun getActiveQrDisplay(): ControllerDevice?

    /**
     * Найти принтер чеков среди списка устойств
     */
    fun getActivePrinterFromList(devices: List<DeviceInside>): DeviceInside?

    /**
     * Найти весы среди списка устойств
     */
    fun getActiveScalesFromList(devices: List<DeviceInside>): DeviceInside?

    /**
     * Найти дисплей Qr-кодов среди списка устойств
     */
    fun getActiveQrDisplayFromList(devices: List<DeviceInside>): DeviceInside?

    /**
     * Найти видеокамеры среди списка устройств.
     */
    fun getActiveVideocamsFromList(devices: List<DeviceInside>): List<DeviceInside>

    /**
     * Найти отключенные СНО
     */
    fun getDisabledTaxSystems(deviceId: Long): Observable<List<TaxSystem>>

    /**
     * Отключить СНО
     */
    fun setDisabledTaxSystems(deviceId: Long, taxSystems: ArrayList<TaxSystem>): Completable

    /**
     * Синхронизация настроек печати чека
     */
    fun syncPrintingSettings(deviceId: Long): Completable

    /** Установить серийник и регистрационный номер для кассы. */
    fun setSerialAndRegNumberSync(deviceID: Long, serialNumber: String, regNumber: String)
}
