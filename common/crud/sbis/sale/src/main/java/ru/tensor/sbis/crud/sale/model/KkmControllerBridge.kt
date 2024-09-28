@file:Suppress("BooleanLiteralArgument")

package ru.tensor.sbis.crud.sale.model

import ru.tensor.devices.generic.generated.Connection
import ru.tensor.devices.generic.generated.UsbDeviceId
import ru.tensor.devices.generic.generated.UsbId
import ru.tensor.devices.settings.generated.DeviceDriver
import ru.tensor.sbis.crud.devices.settings.model.TaxSystem
import ru.tensor.sbis.common.util.date.DateParseTemplate
import ru.tensor.sbis.common.util.date.DateParseUtils
import ru.tensor.sbis.crud.devices.settings.model.DeviceCanUpdate
import ru.tensor.sbis.crud.devices.settings.model.RemoteWorkplaceInfo
import ru.tensor.sbis.crud.devices.settings.model.toControllerType
import ru.tensor.sbis.sale.mobile.generated.DateTimeCheckState
import ru.tensor.sbis.sale.mobile.generated.KkmInfo
import ru.tensor.sbis.sale.mobile.generated.KkmModel
import ru.tensor.sbis.sale.mobile.generated.OFDUntransmittedDocumentsInfo
import java.util.*
import kotlin.collections.ArrayList

private const val DEFAULT_VALUE_DATABITS = 8
private const val DEFAULT_VALUE_STOPBITS = 1

/**
 * метод, позволяющий создать [KkmModel]
 *
 * @param id id устройства
 * @param deviceName имя устройства
 * @param kkmName название кассы
 * @param serialNumber серийный номер
 * @param registrationNumber регистрационный номер
 * @param isActive активна или нет
 * @param companyId организация
 * @param typeUUID идентификатор типа устройства
 * @param missCheckRegnum нужно ли пропустить проверку регистрационного номера
 * @param printTicket печатать билет при регистрации продажи (для принтера-ККМ)
 *
 * @return модель KkmModel
 */
@Suppress("UNUSED_PARAMETER")
internal fun kkmFrom(
    id: Long,
    deviceName: String,
    kkmName: String,
    serialNumber: String,
    registrationNumber: String,
    isActive: Boolean,
    companyId: Long,
    workplaceId: Long,
    typeUUID: UUID,
    missCheckRegnum: Boolean,
    kkmId: Long?,
    remoteKkmId: Long?,
    printReceiptEnabled: Boolean,
    printTicket: Boolean,
    remoteWorkplaces: List<RemoteWorkplaceInfo>?
): KkmModel {
    return KkmModel().apply {
        this.id = id
        this.deviceName = deviceName
        this.kkmName = kkmName
        this.serialNumber = serialNumber
        this.registrationNumber = registrationNumber
        this.isActive = isActive
        company = companyId
        workplace = workplaceId
        deviceType = typeUUID
        this.missCheckRegnum = missCheckRegnum
        this.kkmId = kkmId
        this.remoteKkmId = remoteKkmId
        this.printTicket = printTicket
        this.remoteWorkplaces = remoteWorkplaces
            ?.map(RemoteWorkplaceInfo::toControllerType)
            ?.let(::ArrayList)
    }
}

/**
 * метод, позволяющий создать [Connection]
 *
 * @param deviceName имя устройства
 * @param macAddress MAC адрес
 * @param driver тип драйвера
 * @param connectionType тип соединения
 * @param vid идентификатор usb устройства
 * @param pid идентификатор производителя устройства
 * @param serialPort com порт
 * @param serialNumber серийный номер устройства
 * @param prefix префикс устройства
 * @param suffix суффикс устройства
 * @param timeout таймаут устройства
 *
 *
 * @return объект с информацией о соединении
 */
fun deviceConnectionFrom(
    deviceName: String,
    macAddress: String,
    driver: String,
    connectionType: Int,
    vid: Int = 0,
    pid: Int = 0,
    serialPort: String = "",
    serialNumber: String? = null,
    prefix: String = "",
    suffix: String = "",
    timeout: Int = 0,
    tcpIpAddress: String = "0.0.0.0",
    tcpIpPort: Int = 0,
    printerName: String = "",
    login: String = "",
    password: String = "",
    host: String? = null,
    countryCode: Int
): Connection =
    Connection(
        driver,
        connectionType,
        serialPort,
        null,
        DEFAULT_VALUE_DATABITS,
        0,
        DEFAULT_VALUE_STOPBITS,
        false,
        false,
        null,
        tcpIpAddress,
        tcpIpPort,
        UsbDeviceId(UsbId(vid, pid), null),
        serialNumber ?: "",
        prefix,
        suffix,
        timeout,
        login,
        password,
        "",
        "",
        "",
        macAddress,
        deviceName,
        null,
        printerName,
        host,
        null,
        null,
        null,
        null,
        "",
        null,
        countryCode
    )

/**
 * метод, позволяющий сконвертировать [CashRegister] в [Connection]
 */
fun CashRegister.deviceConnection(): Connection =
    Connection(
        driver.toString(),
        connectionType?.toString()?.toInt() ?: 0,
        port ?: "",
        null,
        DEFAULT_VALUE_DATABITS,
        0,
        DEFAULT_VALUE_STOPBITS,
        false,
        false,
        null,
        ipAddress ?: "",
        tcpPort ?: 0,
        UsbDeviceId(UsbId(vid, pid), null),
        serialNumber ?: "",
        prefix ?: "",
        suffix ?: "",
        timeout ?: 0,
        login,
        password,
        "",
        "",
        "",
        macAddress,
        deviceName,
        null,
        "",
        host,
        passwordReadId,
        null,
        null,
        null,
        "",
        null,
        countryCode
    )

/**
 * метод, позволяющий сконвертировать [KkmModel] в [CashRegister]
 */
fun KkmModel.toAndroidType(): CashRegister {
    return CashRegister().also {
        it.id = id
        it.type = CashRegister.Type.CASH_REGISTER
        it.deviceName = this.deviceName
        it.modelName = this.kkmName
        it.serialNumber = this.serialNumber
        it.registerNumber = this.registrationNumber
        it.isActive = this.isActive
        it.isConnected = false
        it.imageUrl = null
        it.kkmId = kkmId
        it.remoteKkmId = remoteKkmId
        it.printTicket = printTicket ?: false
    }
}

/**
 * метод, позволяющий дополнить информацию о соединении в [CashRegister]
 *
 * @param connection информация о соединении
 */
fun CashRegister.fillConnection(connection: Connection) {
    macAddress = connection.mACAddress
    driver = connection.driver
    connectionType = connection.connectionType
    port = connection.serialPort
    vid = connection.usbDeviceId.id.vid
    pid = connection.usbDeviceId.id.pid
    ipAddress = connection.iPAddress
    tcpPort = connection.port
    passwordReadId = connection.passwordReadId
    countryCode = connection.countryCode
}

/**
 * метод, позволяющий преобразовать данные ККМ из контроллера в маппер
 *
 * @param kkmInfo информация о ККМ от контроллера
 */
fun convertKkmInfo(kkmInfo: KkmInfo): KkmInfoInside {
    with(kkmInfo) {
        return KkmInfoInside(
            fdLifeState = fdLifeState,
            model,
            firmware,
            isFiscalMode,
            FiscalVolumeTypeInside.values().getOrElse(fiscalVolumeType) { FiscalVolumeTypeInside.UNDEFINED },
            fiscalVolumeSerialNumber,
            serialNumber,
            rnm,
            inn,
            dateTime,
            password,
            sessionNumber,
            SessionStateInside.values().first { it.value == sessionState },
            TaxSystemsInside.getTaxSystemsFromBitMask(taxSystems),
            immediateReplacementCryptoCoProcessor,
            cryptoCoProcessorResourceExhaustion,
            fdMemoryOverflow,
            ofdWaitingTimeExceeded,
            fdCriticalError,
            fdExpiryDate?.let { DateParseUtils.parseDate(it, DateParseTemplate.ONLY_DATE) },
            onlinePaymentsReady,
            internetPaymentsFeature,
            offlineModeFeature,
            excisableGoodsFeature,
            isVat20Ready,
            fiscalDocumentFormatVer
        )
    }
}

/**
 * Маппер для OFDUntransmittedDocumentsInfo из контроллера
 */
fun OFDUntransmittedDocumentsInfo.toAndroidType(): OFDUntransmittedDocumentsInfoInside {
    return OFDUntransmittedDocumentsInfoInside(
        this.notTransmittedDocuments.notTransmittedDocumentsQuantity ?: 0,
        this.notTransmittedDocuments.notTransmittedDocumentsDateTime ?: Date(0)
    )
}

/**
 * Маппер для DateTimeCheckState из контроллера
 */
fun DateTimeCheckState.toAndroidType(): DateTimeCheckStatusInside {
    return DateTimeCheckStatusInside(
        DateTimeSyncTypeInside.valueOf(this.dateTimeSyncType.toString()),
        this.kkmDateTime,
        this.kkmLastFiscalDocumentDateTime,
        this.dateTimeReference,
        DateTimeSourceInside.valueOf(this.dateTimeReferenceSource.toString())
    )
}

/**
 * метод, позволяющий сконвертировать [Driver] в [DeviceDriver]
 */
fun Driver.toControllerType(): String {
    return when (this) {
        Driver.ATOL -> DeviceDriver.ATOL
        Driver.VIRTUAL -> DeviceDriver.VIRTUAL
        Driver.SHTRIH -> DeviceDriver.SHTRIH
        Driver.EVOTOR -> Driver.EVOTOR.title
        Driver.MSPOS -> Driver.MSPOS.title
        Driver.SHTRIKH -> DeviceDriver.SHTRIKH
        Driver.EMPTY -> ""
        else -> throw IllegalArgumentException("Не могу преобразовать драйвер $this")
    }
}

/** @SelfDocumented **/
fun TaxSystemsInside.toAndroidType(): TaxSystem = TaxSystem(name, code)

/**
 * Временный маппер из общей модели DeviceCanUpdate в локальную, будет убран после реализации:
 * https://online.sbis.ru/opendoc.html?guid=b8cf0169-ef03-463b-a715-5843fab505d8
 * */
fun DeviceCanUpdate.toKkmAndroidType() = KkmCanUpdate(
    result = result,
    message = message,
    deviceId = deviceId,
    metastate = metastate,
    actions = actions
)
