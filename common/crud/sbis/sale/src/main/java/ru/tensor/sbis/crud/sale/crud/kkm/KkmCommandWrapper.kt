package ru.tensor.sbis.crud.sale.crud.kkm

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.devices.generic.generated.Connection
import ru.tensor.sbis.CXX.SbisException
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.crud.sale.model.*
import ru.tensor.sbis.crud.devices.settings.contract.ProductionAreaCard
import ru.tensor.sbis.crud.devices.settings.model.DeviceCanUpdate
import ru.tensor.sbis.crud.devices.settings.model.DeviceCardSaveArgs
import ru.tensor.sbis.crud.devices.settings.model.PrintingSettings
import ru.tensor.sbis.sale.mobile.generated.KkmFilter
import ru.tensor.sbis.sale.mobile.generated.KkmInfo
import ru.tensor.sbis.sale.mobile.generated.SaleMobileException
import java.util.*
import kotlin.jvm.Throws

/** Низкий заряд батареи ККТ. */
const val ERROR_CODE_KKM_LOW_BATTERY = -100075

/** Некорректные данные отправителя чека в отчете о регистрации */
const val ERROR_CODE_REGISTRATION_REPORT_INCORRECT_DATA = -100074

/**
 * Wrapper команд для контроллера
 */
interface KkmCommandWrapper {

    /**@SelfDocumented */
    val listCommand: ListObservableCommand<PagedListResult<CashRegister>, KkmFilter>

    /**
     * Функция для создания кассы
     */
    fun create(): Observable<CashRegister>

    /**
     * Функция для получения кассы по идентификатору
     *
     * @param id - идентификатор кассы
     */
    fun read(id: Long): Observable<CashRegister>

    /**
     * Функция для обновления данных о подключении кассы
     *
     * @param cashRegister - модель кассы
     * @param typeUuid - тип кассы
     * @param actions - список идентификаторов операций
     */
    fun update(cashRegister: CashRegister,
               typeUuid: UUID,
               actions: List<Int>): Completable

    /**
     * Функция для удаления кассы по идентификатору
     *
     * @param id - идентификатор кассы
     */
    fun delete(id: Long): Observable<Boolean>

    /**
     * Функция для сохранения кассы
     *
     * @param cashRegister - модель кассы
     * @param typeUuid - тип кассы
     */
    fun saveKkm(cashRegister: CashRegister,
                typeUuid: UUID): Completable

    /**
     * Функция для сохранения/обновления кассы, используется для Presto.
     *
     * @param prestoKkmCard - карточка ККТ.
     * @param args - аргументы, передаваемые контролллеру в сценариях сохранения/обновления оборудования.
     * @param cashRegister - основная информация о кассе.
     * @param typeUuid - тип кассы
     * @param isProductionEnabled - включена ли настройка "Производство"?
     */
    @Deprecated("Будет удалён: https://online.sbis.ru/opendoc.html?guid=dc32bf6b-6d56-47c3-b3e8-f89a7236bf3e")
    fun saveOrUpdateKkmPresto(
        prestoKkmCard: ProductionAreaCard,
        args: DeviceCardSaveArgs,
        cashRegister: CashRegister,
        typeUuid: UUID,
        isProductionEnabled: Boolean
    ) : Single<DeviceCanUpdate>

    /**
     * Функция проверки соединения с кассой
     *
     * @param deviceConnection - модель данных о подключении кассы
     */
    fun checkConnection(deviceConnection: Connection): Observable<Boolean>

    /**
     * Функция проверки соединения с кассой
     *
     * @param deviceConnection - модель данных о подключении кассы
     */
    suspend fun checkConnectionCoroutines(deviceConnection: Connection): Result<Boolean>

    /**
     * Проверить валидность регистрационного номера
     *
     * @param rnm - регистрационный номер
     */
    fun checkValidity(rnm: String): Observable<Boolean>

    /**
     * Напечатать тестовую страницу
     *
     */
    fun printTestTicket(connection: Connection, printingSettings: PrintingSettings): Completable

    /**
     * Функция для получения информации о непереданных в ОФД документах
     */
    fun getOFDUntransmittedDocumentsInfo(): Observable<OFDUntransmittedDocumentsInfoInside>

    /**
     * Функция для получения информации о непереданных в ОФД документах связанных с кассой
     *
     * @param macAddress - мак адресс кассы
     * @param name - имя кассы
     * @param driver - драйвер кассы
     * @param connectionType - тип подключения кассы
     */
    fun getOFDUntransmittedDocumentsInfo(macAddress: String,
                                         name: String?,
                                         driver: Driver,
                                         connectionType: Int): Observable<OFDUntransmittedDocumentsInfoInside>

    /**
     * Функция для получения активной кассы (синхронно)
     * @param fetchConnectionSettings - true, если нужно подтянуть настройки соединения (см. [Connection])
     */
    fun getActiveKkm(fetchConnectionSettings: Boolean = true): CashRegister?

    /** Функция для получения информации об активной кассе (синхронно). */
    fun getActiveKkmInfo(): KkmInfo?

    /**
     * Функция для получения активной кассы (асинхронно)
     */
    fun getActiveKkmAsync(): Observable<CashRegister>

    /**
     * Функция для получения информации о кассе асинхронно
     *
     * @param cashRegister - модель кассы
     * @throws SbisException
     * @throws SaleMobileException
     */
    @Throws(SbisException::class, SaleMobileException::class)
    fun getKkmInfoAsync(cashRegister: CashRegister): Single<KkmInfoInside>

    /**
     * Проверить состояние синхронизации даты/времени между референсным источником и ККМ
     */
    fun checkSettingDateTimeForKkm(): Observable<DateTimeCheckStatusInside>

    /**
     * Функция для синхронизации даты/времени между референсным источником и ККМ
     */
    fun setDateTimeToKkm(): Observable<Unit>

    /**
     * Функция для проверки возможности активировать кассу
     *
     * @param cashRegister - модель кассы
     * @param missCheckRegnum - флаг обозначающий необходимость пропустить проверку регистрационного номера, true - пропускаем, иначе - нет
     * @param typeUuid - тип кассы
     * @param actions - список идентификаторов операций
     */
    fun canUpdate(cashRegister: CashRegister,
                  missCheckRegnum: Boolean,
                  typeUuid: UUID,
                  actions: List<Int>): Observable<KkmCanUpdate>

    /**
     * Функция для деактивации кассы
     *
     * @param kkmId - идентификатор кассы
     * @param actions - список идентификаторов операций
     */
    fun deactivate(kkmId: Long, actions: List<Int>): Completable

    /**
     * Функция для открытия кассы
     */
    fun openCashbox(): Completable

    fun isActiveKkmShiftExpired(): Single<Boolean>

    /**
     * Метод позволяет проверить установлено ли соединение с KKM
     *
     * @param cashRegister - модель кассы
     *
     * @return реактивный источник со статусом есть связь с устройством или отсутствует
     */
    fun isKKMConnected(cashRegister: CashRegister): Single<Boolean>

    /**
     * Метод получения списка поддерживаемых операций KKM
     *
     * @param deviceConnection - модель данных о подключении кассы
     *
     * @return [SupportedOperationsInside] - список поддерживаемых операций KKM
     */
    fun getSupportedOperations(deviceConnection: Connection): SupportedOperationsInside

    /**
     * Метод получения списка предупреждений от контроллера при проверке KKM
     *
     * @param deviceConnection - модель данных о подключении кассы
     *
     * @return список [KkmWarningInside] - реактивный источник со списком предупреждений от контроллера при проверке KKM
     */
    fun getKkmWarnings(deviceConnection: Connection): Observable<List<KkmWarningInside>>

    /**
     * Печать нефискального чека (билета)
     *
     * @param saleUuid UUID завершенной продажи
     * @return Completable
     */
    fun printTicketForSale(saleUuid: UUID): Completable

    /**
     * Метод получения списка поддерживаемых операций для активной KKM
     *
     * @return [SupportedOperationsInside] - реактивный источник со списком поддерживаемых операций KKM
     */
    fun getActiveKkmSupportedOperations(): Maybe<SupportedOperationsInside>

    /**
     * Проверка на нефискальный режим
     */
    suspend fun isNonFiscal(cashRegister: CashRegister): Boolean
}