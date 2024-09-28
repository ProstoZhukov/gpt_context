package ru.tensor.sbis.crud.sale.crud.kkm

import ru.tensor.devices.generic.generated.Connection
import ru.tensor.devices.generic.generated.SessionInfo
import ru.tensor.devices.kkmservice.generated.DeviceInfo
import ru.tensor.devices.kkmservice.generated.KkmWarning
import ru.tensor.devices.kkmservice.generated.SupportedOperations
import ru.tensor.devices.settings.generated.Device
import ru.tensor.sbis.crud.devices.settings.model.PrintingSettings
import ru.tensor.sbis.sale.mobile.generated.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Интерфейс для связи с контроллером.
 */
interface KkmRepository {

    /**
     * Функция для создания кассы
     *
     * @return KkmModel
     */
    fun create(): KkmModel

    /**
     * Функция для получения кассы по идентификатору
     *
     * @param id - идентификатор кассы
     *
     * @return KkmModel
     */
    fun read(id: Long): KkmModel?

    /**
     * Функция для обновления кассы
     *
     * @param model - модель кассы
     *
     * @return KkmModel
     */
    fun update(model: KkmModel): KkmModel

    /**
     * Функция для обновления данных о подключении кассы
     *
     * @param model - модель кассы
     * @param deviceConnection - модель данных о подключении кассы
     * @param actions - список идентификаторов операций
     *
     * @return KkmModel
     */
    fun update(model: KkmModel, deviceConnection: Connection, actions: ArrayList<Int>): KkmModel

    /**
     * Функция для удаления кассы по идентификатору
     *
     * @param id - идентификатор кассы
     *
     * @return Boolean
     */
    fun delete(id: Long): Boolean

    /**
     * Функция для получения списка касс (запускает синхронизацию с облаком)
     *
     * @param filter - модель фильтра по которому делаем запрос
     *
     * @return KkmListResult
     */
    fun list(filter: KkmFilter): KkmListResult

    /**
     * Функция для получения списка касс из кэша
     *
     * @param filter - модель фильтра по которому делаем запрос
     *
     * @return KkmListResult
     */
    fun refresh(filter: KkmFilter): KkmListResult

    /**
     * Функция для установки колбэка
     *
     * @param callback - модель колбэка
     *
     * @return KkmSubscription
     */
    fun setDataRefreshCallback(callback: KkmDataRefreshCallback): KkmSubscription

    /**
     * Функция для сохранения кассы
     *
     * @param model - модель кассы
     * @param deviceConnection - модель данных о подключении кассы
     *
     * @return Device
     */
    fun saveKkm(model: KkmModel, deviceConnection: Connection)

    /**
     * Функция проверки соединения с кассой
     *
     * @param deviceConnection - модель данных о подключении кассы
     *
     * @return Boolean
     */
    fun checkConnection(deviceConnection: Connection): Boolean

    /**
     * Проверить валидность регистрационного номера
     *
     * @param rnm - регистрационный номер
     *
     * @return Boolean
     */
    fun checkValidity(rnm: String): Boolean

    /**
     * Функция для получения информации о непереданных в ОФД документах
     *
     * @return OFDUntransmittedDocumentsInfo
     */
    fun getOFDUntransmittedDocumentsInfo(): OFDUntransmittedDocumentsInfo?

    /**
     * Функция для получения информации о непереданных в ОФД документах связанных с кассой
     *
     * @param deviceConnection - модель данных о подключении кассы
     *
     * @return OFDUntransmittedDocumentsInfo
     */
    fun getOFDUntransmittedDocumentsInfo(deviceConnection: Connection): OFDUntransmittedDocumentsInfo?

    /**
     * Функция для получения активной кассы
     *
     * @return KkmModel?
     */
    fun getActiveKkm(): KkmModel?

    /**
     * Функция для получения информации об активной кассе.
     *
     * @return KkmInfo?
     */
    fun getActiveKkmInfo(): KkmInfo?

    /**
     * Функция для получения данных о подключении кассы по его идентификатору
     *
     *  @param id - идентификатор кассы
     *
     * @return Connection
     */
    fun getDeviceSettings(id: Long): Connection?

    /**
     * Функция для получения данных о подключении кассы
     *
     * @param kkm - модель кассы
     *
     * @return Connection
     */
    fun getDeviceSettings(kkm: KkmModel): Connection?

    /**
     * Функция для получения информации о кассе
     *
     * @param model - модель кассы
     * @param deviceConnection - модель данных о подключении кассы
     */
    fun getKkmInfo(model: KkmModel, deviceConnection: Connection): KkmInfo

    /**
     * Проверить состояние синхронизации даты/времени между референсным источником и ККМ
     *
     * @return DateTimeCheckState
     */
    fun checkSettingDateTimeForKkm(): DateTimeCheckState

    /**
     * Функция для синхронизации даты/времени между референсным источником и ККМ
     */
    fun setDateTimeToKkm()

    /**
     * Функция для проверки возможности обновления кассы
     *
     * @param model - модель кассы
     * @param deviceConnection - модель данных о подключении кассы
     * @param actions - список идентификаторов операций
     *
     * @return KkmCanUpdate
     */
    fun canUpdate(model: KkmModel, deviceConnection: Connection, actions: List<Int>): KkmCanUpdate

    /**
     * Функция для деактивации кассы
     *
     * @param kkmId - идентификатор кассы
     * @param actions - список идентификаторов операций
     */
    fun deactivate(kkmId: Long, actions: List<Int>)

    /**
     * Функция для открытия кассы
     */
    fun openCashbox()

    /**
     * Получает информацию о сессии активной ККМ
     */
    fun getActiveKkmSessionInfo(): SessionInfo?

    /**
     * Метод позволяет проверить установлено ли соединение с KKM
     *
     * @param model - модель кассы
     * @param deviceConnection - модель данных о подключении кассы
     *
     * @return есть соединение с кассой или нет
     */
    fun isKKMConnected(model: KkmModel, deviceConnection: Connection): Boolean

    /**
     * Попытка напечатать копию чека
     * @param fiscalDocumentNumber Фискальный номер документа.
     * @param fiscalKkmNumber Номер фискального накопителя. Может быть null, в этом случае на контроллере предусмотрена
     * логика, которая сама определит целевое устройство для распечатки чека.
     */
    fun printDocumentCopy(fiscalDocumentNumber: Long, fiscalKkmNumber: String?)


    /**
     * Попытка напечатать страницу в свободном формате
     * @param args что печатать
     */
    fun printFreeContentPage(args: String)

    /**
     * Печать тестовой страницы
     * @param connection настройки подключения для принтера на котором печатаем
     */
    fun printTestTicket(connection: Connection, printingSettings: PrintingSettings)

    /**
     * Метод получения списка поддерживаемых операций KKM
     *
     * @param deviceConnection - модель данных о подключении кассы
     *
     * @return [SupportedOperations]
     */
    fun getSupportedOperations(deviceConnection: Connection): SupportedOperations

    /**
     * Метод получения списка предупреждений от контроллера при проверке KKM
     *
     * @param deviceConnection - модель данных о подключении кассы
     *
     * @return список [KkmWarning]
     */
    fun getKkmWarnings(deviceConnection: Connection): List<KkmWarning>

    /**
     * Печать нефискального чека (билета)
     * @param saleUuid UUID завершенной продажи
     */
    fun printTicketForSale(saleUuid: UUID)

    /**
     * Тестовое обращение к ККМ для запуска обменя с ОФД; необходимо вызывать после того, как будет запущено основное Activity приложения,
     * т.к. при работе с кассой по USB драйвер может запросить разрешение о доступе к устройству у пользователя, и если Activity в этот момент скроется,
     * то и запрос пропадет
     */
    fun initOfdExchangeAndRequestPermissionsIfRequired()

    /**
     * См. [initOfdExchangeAndRequestPermissionsIfRequired]
     */
    fun initOfdExchangeAndRequestPermissionsIfRequired(connection: Connection)

    /**
     * Преобразование [KkmModel] -> [Device].
     * Временное решение для Presto, должно быть убрано, когда для ККТ появится полноценная карточка:
     * https://online.sbis.ru/opendoc.html?guid=b8cf0169-ef03-463b-a715-5843fab505d8
     */
    fun kkmToDevice(model: KkmModel, deviceConnection: Connection) : Device

    /**@SelfDocumented*/
    fun fetchKKM(kkmID: Long) : Device

    /**@SelfDocumented*/
    fun getDeviceInfo(deviceConnection: Connection): DeviceInfo
}