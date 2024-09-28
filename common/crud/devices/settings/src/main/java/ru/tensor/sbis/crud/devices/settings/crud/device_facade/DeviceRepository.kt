package ru.tensor.sbis.crud.devices.settings.crud.device_facade

import androidx.annotation.WorkerThread
import ru.tensor.devices.settings.generated.*
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository

/**
 * Интерфейс для связи с контроллером.
 */
interface DeviceRepository :
        CRUDRepository<Device>,
        BaseListRepository<ListResultOfDeviceMapOfStringString, DeviceFilter, DataRefreshedDeviceFacadeCallback> {

    /**
     * Функция для создания устройства
     *
     * @param model - модель устройства
     *
     * @return Device
     */
    fun create(model: Device): Device

    /**
     * Получает данные, необходимые для создания устройства.
     * Для того чтобы "досоздать" устройство, необходимо передать полученный результат в метод 'update'.
     * Метод [create] вызывает [update] под капотом, так что на UI можно использовать его.
    */
    fun createBaseDevice(model: Device): Device

    /**
     * Функция для получения устройства по идентификатору
     *
     * @param id - идентификатор устройства
     *
     * @return Device
     */
    fun read(id: Long): Device?

    /**
     * Функция для обновления данных о подключении устройства
     *
     * @param device - модель устройства
     * @param actions - список идентификаторов операций
     *
     * @return Device
     */
    fun update(device: Device, actions: ArrayList<Int>): Device

    /**
     * Функция для удаления устройства по идентификатору
     *
     * @param id - идентификатор устройства
     *
     * @return Boolean если true - удалено, иначе - нет
     */
    fun delete(id: Long): Boolean

    /**
     * Функция для получения активного устройства
     *
     * @return Device
     */
    fun getActiveDevice(): Device?

    /**
     * Функция для установки активного устройства
     *
     * @param model - модель устройства
     */
    fun setActiveDevice(model: Device)

    /**
     * Отключение СНО
     * @param deviceId - идентификатор устройства
     * @param taxSystems - список СНО, которые нужно отключить
     */
    fun setDisabledTaxSystems(deviceId: Long, taxSystems: ArrayList<TaxSystem>)

    /**
     * Функция для проверки возможности обновления устройства
     *
     * @param model - модель устройства
     *
     * @return DeviceCanUpdate
     */
    fun canUpdate(model: Device): DeviceCanUpdate

    /**
     * Функция для проверки возможности обновления устройства
     *
     * @param model - модель устройства
     * @param actions - идентификаторы операций
     *
     * @return DeviceCanUpdate
     */
    fun canUpdate(model: Device, actions: ArrayList<Int>): DeviceCanUpdate

    /**
     * Функция для деактивации устройства по идентификатору
     *
     * @param deviceId - идентификатор устройства
     */
    fun deactivate(deviceId: Long)

    /**
     * Функция для деактивации устройства по идентификатору
     *
     * @param deviceId - идентификатор устройства
     * @param actions - идентификаторы операций
     */
    fun deactivate(deviceId: Long, actions: ArrayList<Int>)

    /**
     * Возвращает текущий активный платежный терминал
     */
    fun getActivePaymentTerminal(): Device?

    /**
     * Возвращает текущие активные весы
     */
    fun getActiveScales(): Device?

    /**
     * Возвращает текущий активный дисплей
     */
    fun getActiveBuyerDisplay(): Device?

    /** Возвращает текущий активный принтер. */
    fun getActivePrinter(): Device?

    /**
     * Возвращает СНО, которые были отключены
     */
    fun getDisabledTaxSystems(deviceId: Long): List<TaxSystem>

    /**
     * Синхронизация настроек печати чека
     */
    @WorkerThread
    fun syncPrintingSettings(deviceIds: ArrayList<Long>)

    /** Установить серийник и регистрационный номер для кассы. */
    fun setSerialAndRegNumber(deviceID: Long, serialNumber: String, regNumber: String)

    /** Включена ли онлайн оплата. */
    suspend fun isOnlinePaymentEnabled(deviceID: Long): Boolean

    /** Включена ли оплата QR-кодом. */
    suspend fun isQrDestinationEnabled(deviceID: Long): Boolean
}
