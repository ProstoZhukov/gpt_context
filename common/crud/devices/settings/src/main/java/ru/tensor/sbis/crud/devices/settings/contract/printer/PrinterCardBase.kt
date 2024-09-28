package ru.tensor.sbis.crud.devices.settings.contract.printer

import io.reactivex.Single
import ru.tensor.sbis.crud.devices.settings.contract.devices_common.DeviceCard
import ru.tensor.sbis.crud.devices.settings.contract.devices_common.DeviceCardConnectable
import ru.tensor.sbis.crud.devices.settings.contract.devices_common.DeviceCardWithDrivers
import ru.tensor.sbis.crud.devices.settings.contract.devices_common.DeviceCardNameable
import ru.tensor.sbis.crud.devices.settings.contract.devices_common.DeviceCardWithAdditionalInfo
import ru.tensor.sbis.crud.devices.settings.contract.devices_common.DeviceCardWithWorkplace
import ru.tensor.sbis.crud.devices.settings.model.DeviceCanUpdate
import ru.tensor.sbis.crud.devices.settings.model.DeviceCardAdditionalData
import ru.tensor.sbis.crud.devices.settings.model.DeviceConnectionInside
import ru.tensor.sbis.crud.devices.settings.model.DriverInfo
import ru.tensor.sbis.crud.devices.settings.model.PrintDensityItem
import ru.tensor.sbis.crud.devices.settings.model.TapeWidthItem
import java.util.UUID

/**
 * Интерфейс, описывающий основные возможности карточки принтера.
 * Эти возможности есть у любой реализации карточки принтера.
 * */
interface PrinterCardBase : DeviceCard,
    DeviceCardWithDrivers,
    DeviceCardConnectable,
    DeviceCardNameable,
    DeviceCardWithWorkplace,
    DeviceCardWithAdditionalInfo {

    override fun getName(): String

    override fun setName(name: String)

    /** Получить название модели оборудования. */
    fun getTitle(): String

    /** Получить изображение оборудования. */
    fun getImage(): String

    override fun getAdditionalInfo(): DeviceCardAdditionalData

    /** Получить серийный номер оборудования. */
    fun getSerialNumber(): String

    /** Установить серийный номер оборудования. */
    fun setSerialNumber(name: String)

    /** Получить идентификатор точки пролаж оборудования. */
    fun getCompanyID(): UUID

    override fun getWorkplaceID(): UUID

    override fun getConnection(): DeviceConnectionInside

    override fun setConnection(connection: DeviceConnectionInside)

    override fun getDrivers(): List<DriverInfo>

    /** Получить состояние активности оборудования. */
    fun getIsActive(): Boolean

    /** Установить состояние активности оборудования. */
    fun setIsActive(isActive: Boolean)

    /** Получить состояние настройки "Печатать чек" оборудования. */
    fun getIsPrintTicket(): Boolean

    /** Установить состояние настройки "Печатать чек" оборудования. */
    fun setIsPrintTicket(print: Boolean)

    /** Получить доступность настройки "Ширина ленты" оборудования. */
    fun isTapeWidthSupported() : Boolean

    /** Получить состояние настройки "Ширина ленты" оборудования. */
    fun getTapeWidthOptions() : List<TapeWidthItem>

    /** Установить состояние настройки "Ширина ленты" оборудования. */
    fun setTapeWidthOptions(options: List<TapeWidthItem>)

    /** Получить доступность настройки "Плотность печати" оборудования. */
    fun isPrintDensitySupported() : Boolean

    /** Получить состояние настройки "Плотность печати" оборудования. */
    fun getPrintDensityOptions() : List<PrintDensityItem>

    /** Получить состояние настройки "Плотность печати" оборудования. */
    fun setPrintDensityOptions(options: List<PrintDensityItem>)

    /** Напечатать тестовую страницу. */
    suspend fun printTestTicket(): Result<Unit>

    /**
     * Сохранить/обновить оборудование.
     * @param byUserRequest - является ли запрос прямым намерением пользователя(нажатие на кнопку).
     *                        Должен быть false для случая, когда метод вызывается в результате обработки ошибки, например.
     * */
    fun saveAsync(byUserRequest: Boolean): Single<DeviceCanUpdate>

    /**
     * Удалить оборудование.
     * @param byUserRequest - является ли запрос прямым намерением пользователя(нажатие на кнопку).
     *                        Должен быть false для случая, когда метод вызывается в результате обработки ошибки, например.
     * */
    fun removeAsync(byUserRequest: Boolean): Single<DeviceCanUpdate>
}