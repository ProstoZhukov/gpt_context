package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.generic.generated.Connection
import ru.tensor.devices.generic.generated.ConnectionType
import ru.tensor.devices.generic.generated.DeviceKind
import ru.tensor.devices.settings.generated.Device
import ru.tensor.devices.settings.generated.DeviceImage
import ru.tensor.devices.settings.generated.DeviceMetainfo
import ru.tensor.devices.settings.generated.DeviceSettings
import ru.tensor.devices.settings.generated.RemoteWorkplaceInfo
import ru.tensor.devices.settings.generated.ScannerSettings
import ru.tensor.devices.settings.generated.SyncStatus
import ru.tensor.sbis.crud.devices.settings.exception.UnsupportedConnectionTypeException
import ru.tensor.sbis.retail_settings.generated.PrintingSettings
import java.util.UUID
import ru.tensor.devices.generic.generated.DeviceInfoBase as ControllerDeviceInfoBase
import ru.tensor.devices.generic.generated.UsbDevice as ControllerUsbDevice
import ru.tensor.devices.generic.generated.UsbDeviceId as ControllerUsbDeviceId
import ru.tensor.devices.generic.generated.UsbDeviceInfo as ControllerUsbDeviceInfo
import ru.tensor.devices.generic.generated.UsbId as ControllerUsbId
import ru.tensor.devices.pts.generated.DeviceInfo as ControllerPaymentTerminalDeviceInfo
import ru.tensor.devices.settings.generated.ConnectionTypeItem as ControllerConnectionTypeItem
import ru.tensor.devices.settings.generated.DeviceApplication as ControllerDeviceApplication
import ru.tensor.devices.settings.generated.DeviceCanUpdate as ControllerDeviceCanUpdate
import ru.tensor.devices.settings.generated.DriverInfo as ControllerDriverInfo
import ru.tensor.devices.settings.generated.Production as ControllerProductionArea
import ru.tensor.devices.settings.generated.SalesPointCabinet as ControllerOrganizationInfo
import ru.tensor.devices.settings.generated.ScannerSeparator as ControllerScannerSeparator
import ru.tensor.devices.settings.generated.ScannerTimeout as ControllerScannerTimeout
import ru.tensor.devices.settings.generated.SeparatorItem as ControllerSeparatorItem
import ru.tensor.devices.settings.generated.UsbPathItem as ControllerUsbPathItem

const val EMPTY_VALUE_TCP_PORT = 0

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun Device.toAndroidType(): DeviceInside =
    DeviceInside(
        id,
        kindToAndroidType(kind),
        type!!,
        serialNumber,
        name,
        modelName,
        company,
        workplace,
        visible,
        active,
        kkmId,
        connectionRaw,
        settingsRaw,
        metainfoRaw,
        syncStatus.toAndroidType(),
        connection?.toAndroidType(),
        settings?.toAndroidType(),
        metainfo?.toAndroidType(),
        image?.toAndroidType(),
        supportedConnectionTypes,
        editableConnectionTypes,
        isTensorOfd,
        missCheckRegnum ?: false,
        offlineModeFeature,
        sbisHelpLink.orEmpty(),
        scannerSettings?.toAndroidType(),
        supportedDrivers.map { it.toAndroidType() },
        port,
        remoteWorkplaces?.map(RemoteWorkplaceInfo::toAndroidType) ?: emptyList(),
        sharedWorkplace?.toAndroidType()
    )

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun DeviceImage.toAndroidType(): DeviceImageInside = DeviceImageInside(small, big)

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun SyncStatus.toAndroidType(): SyncStatusInside =
    when (this) {
        SyncStatus.NOT_REQUIRED -> SyncStatusInside.NOT_REQUIRED
        SyncStatus.REQUIRED     -> SyncStatusInside.REQUIRED
        SyncStatus.SYNCED       -> SyncStatusInside.SYNCED
        SyncStatus.DELETED      -> SyncStatusInside.DELETED
    }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun Connection.toAndroidType(): DeviceConnectionInside =
    DeviceConnectionInside(
        driver,
        driverDirectory,
        connectionType.toConnectionTypeInside(),
        serialPort,
        prefix,
        suffix,
        timeoutMs,
        usbDeviceId.toAndroidType(),
        deviceName,
        mACAddress,
        customIdentifier,
        iPAddress,
        port,
        login,
        password,
        baudrate,
        dataBits,
        parity,
        stopBits,
        additionalSettings,
        countryCode
    )

/**
 * Маппер для преобразования значения во вью модель
 */
fun Int.toConnectionTypeInside(): ConnectionTypeInside =
    when (this) {
        ConnectionType.SERIAL_PORT -> ConnectionTypeInside.SERIAL_PORT
        ConnectionType.TCP_CLIENT -> ConnectionTypeInside.TCP_IP
        ConnectionType.USB_HID -> ConnectionTypeInside.USB_HID
        ConnectionType.KEYBOARD -> ConnectionTypeInside.KEYBOARD
        ConnectionType.BLUETOOTH -> ConnectionTypeInside.BLUETOOTH
        ConnectionType.USB_SERIAL -> ConnectionTypeInside.USB_SERIAL_CDC_ACM
        ConnectionType.TWO_CAN -> ConnectionTypeInside.NFC
        ConnectionType.VENDOR_SPECIFIC -> ConnectionTypeInside.VENDOR_SPECIFIC
        ConnectionType.PAYMENT_TERMINAL_OVER_LIB -> ConnectionTypeInside.PAYMENT_TERMINAL_OVER_LIB
        ConnectionType.INPAS_DUAL_CONNECTOR -> ConnectionTypeInside.INPAS_DUAL_CONNECTOR
        ConnectionType.USB_SERIAL_OVER_ADAPTER -> ConnectionTypeInside.USB_SERIAL_OVER_ADAPTER
        ConnectionType.EFTBASE -> ConnectionTypeInside.EFTBASE
        ConnectionType.USB_PRINTER -> ConnectionTypeInside.USB_PRINTER
        ConnectionType.WEBKASSA -> ConnectionTypeInside.WEBKASSA
        ConnectionType.EKASSA -> ConnectionTypeInside.EKASSA
        ConnectionType.EKASSA_ONLINE -> ConnectionTypeInside.EKASSA_ONLINE
        else -> throw UnsupportedConnectionTypeException("Тип подключения $this не поддерживается")
    }

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceKindInside.toControllerType(): Int =
    when (this) {
        DeviceKindInside.POS_SCANNER         -> DeviceKind.POS_SCANNER
        DeviceKindInside.POS_SCALES          -> DeviceKind.SCALES
        DeviceKindInside.PAYMENT_TERMINAL    -> DeviceKind.PAYMENT_TERMINAL
        DeviceKindInside.BUYER_DISPLAY       -> DeviceKind.BUYER_DISPLAY
        DeviceKindInside.KKM                 -> DeviceKind.KKT
        DeviceKindInside.SYSTEM_PRINTER      -> DeviceKind.PRINTER
        DeviceKindInside.REMOTE_KKM          -> DeviceKind.REMOTE_KKT
        DeviceKindInside.INFORMATION_SCREEN  -> DeviceKind.INFORMATION_SCREEN
        DeviceKindInside.QR_CODE_DISPLAY     -> DeviceKind.QR_CODE_DISPLAY
        DeviceKindInside.VIDEOCAM            -> DeviceKind.VIDEOCAM
        DeviceKindInside.SKUD                -> DeviceKind.SKUD
        DeviceKindInside.SCALES_WITH_PRINT   -> DeviceKind.SCALES_WITH_PRINT
        DeviceKindInside.FINGERPRINT_SCANNER -> DeviceKind.FINGERPRINT_SCANNER
        DeviceKindInside.IP_PHONE            -> DeviceKind.IP_PHONE
        DeviceKindInside.UNKNOWN             -> DeviceKind.NONE
    }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun kindToAndroidType(kind: Int): DeviceKindInside =
    when (kind) {
        DeviceKind.POS_SCANNER         -> DeviceKindInside.POS_SCANNER
        DeviceKind.SCALES              -> DeviceKindInside.POS_SCALES
        DeviceKind.PAYMENT_TERMINAL    -> DeviceKindInside.PAYMENT_TERMINAL
        DeviceKind.BUYER_DISPLAY       -> DeviceKindInside.BUYER_DISPLAY
        DeviceKind.KKT                 -> DeviceKindInside.KKM
        DeviceKind.PRINTER             -> DeviceKindInside.SYSTEM_PRINTER
        DeviceKind.REMOTE_KKT          -> DeviceKindInside.REMOTE_KKM
        DeviceKind.INFORMATION_SCREEN  -> DeviceKindInside.INFORMATION_SCREEN
        DeviceKind.QR_CODE_DISPLAY     -> DeviceKindInside.QR_CODE_DISPLAY
        DeviceKind.VIDEOCAM            -> DeviceKindInside.VIDEOCAM
        DeviceKind.SKUD                -> DeviceKindInside.SKUD
        DeviceKind.SCALES_WITH_PRINT   -> DeviceKindInside.SCALES_WITH_PRINT
        DeviceKind.FINGERPRINT_SCANNER -> DeviceKindInside.FINGERPRINT_SCANNER
        DeviceKind.IP_PHONE            -> DeviceKindInside.IP_PHONE
        else                           -> DeviceKindInside.UNKNOWN
    }

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceConnectionInside.toControllerType(): Connection =
    Connection(
        driver = driver,
        connectionType = connectionType.toInt(),
        serialPort = serialPort.orEmpty(),
        baudrate = baudrate,
        dataBits = dataBits,
        parity = parity,
        stopBits = stopBits,
        rTSEnable = false,
        dTREnable = false,
        additionalSettings = additionalSettings,
        iPAddress = ipAddress ?: "0.0.0.0",
        port = tcpPort ?: EMPTY_VALUE_TCP_PORT,
        usbDeviceId = usbDeviceId.toControllerType(),
        serialNumber = serialPort ?: "",
        prefix = prefix,
        suffix = suffix,
        timeoutMs = timeoutMs ?: 0,
        login = login,
        password = password,
        authToken = "",
        storeUuid = "",
        deviceUuid = "",
        mACAddress = macAddress,
        deviceName = deviceName,
        driverDirectory = driverDirectory,
        printerName = deviceName ?: "",
        host = null,
        configFilePath = null,
        customIdentifier = null,
        capsLockInvert = null,
        passwordReadId = null,
        kkmOfdId = "",
        cashCoreHost = null,
        countryCode = countryCode
    )

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceImageInside.toControllerType(): DeviceImage = DeviceImage(smallImage ?: "", bigImage ?: "")

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SyncStatusInside.toControllerType(): SyncStatus =
    when (this) {
        SyncStatusInside.NOT_REQUIRED -> SyncStatus.NOT_REQUIRED
        SyncStatusInside.REQUIRED     -> SyncStatus.REQUIRED
        SyncStatusInside.SYNCED       -> SyncStatus.SYNCED
        SyncStatusInside.DELETED      -> SyncStatus.DELETED
    }

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceSettingsInside.toControllerType(): DeviceSettings =
    DeviceSettings(
        cashDesk,
        company,
        companyName,
        contractor,
        deviceKind,
        editDialog,
        ffdVersion,
        printInternetPayment,
        regNumber,
        remoteConnectable,
        remoteDeviceId,
        serialNumber,
        testDialog,
        printZReport,
        slipPause,
        slipCheck,
        printTicket,
        printKitchenOrders,
        ArrayList(organizationsInfo.map { it.toControllerType() }),
        arrayListOf(),
        deviceApplication?.toControllerType(),
        PrintingSettings(),
        arrayListOf(),
        arrayListOf(),
        null,
        null,
        null,
        null,
    )

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceMetainfoInside.toControllerType(): DeviceMetainfo {
    return DeviceMetainfo(fetchedWithRemotes, multipleKkm, remoteKkm, workplaceName)
}

/**
 * Маппер для преобразования модели контроллера в UI модель
 */
fun ScannerSettings.toAndroidType(): ScannerDeviceSettings = ScannerDeviceSettings(scannerTimeout?.toAndroidType(), prefix, suffix)

/**
 * Маппер для преобразования UI модели в модель контроллера
 */
fun ScannerDeviceSettings.toControllerType(): ScannerSettings = ScannerSettings(scannerTimeout?.toControllerType(), prefix, suffix)

/**
 * Маппер для преобразования модели контроллера в UI модель
 */
fun ControllerScannerTimeout.toAndroidType(): ScannerTimeout = ScannerTimeout(defaultValue, minValue, maxValue)

/**
 * Маппер для преобразования UI модели в модель контроллера
 */
fun ScannerTimeout.toControllerType(): ControllerScannerTimeout = ControllerScannerTimeout(defaultValue, minValue, maxValue)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceInside.toControllerType(): Device =
    Device(
        id,
        kind.toControllerType(),
        type,
        serialNumber,
        name,
        modelName,
        company,
        workplace,
        visible,
        isActive,
        kkmId,
        connectionRaw,
        settingsRaw,
        metainfoRaw,
        isTensorOfd,
        syncStatus.toControllerType(),
        isStandAloneEnabled,
        connection?.toControllerType(),
        settings?.toControllerType(),
        metaInfo?.toControllerType(),
        image?.toControllerType(),
        supportedConnectionTypes,
        editableConnectionTypes,
        missCheckRegnum,
        arrayListOf(),
        "",
        scannerDeviceSettings?.toControllerType(),
        port,
        ArrayList(remoteWorkplaces.map { it.toControllerType() }),
        sharedWorkplaceInfo?.toControllerType()
    )

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceType.toControllerType(workplace: Long) = Device().also {
    it.active = true
    it.name = name
    it.kind = kind.toControllerType()
    it.type = type
    it.workplace = workplace
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun DeviceSettings.toAndroidType(): DeviceSettingsInside =
    DeviceSettingsInside(
        cashDesk,
        company,
        companyName,
        contractor,
        deviceKind,
        editDialog,
        fFDVersion,
        printInternetPayment,
        regNumber,
        remoteConnectable,
        remoteDeviceId,
        serialNumber,
        testDialog,
        printZReport,
        slipPause,
        slipCheck,
        printKitchenOrders,
        salesPointCabinets?.map { it.toAndroidType() } ?: emptyList(),
        application?.toAndroidType(),
        printTicket,
        printingSettings.toAndroid()
    )

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun DeviceMetainfo.toAndroidType(): DeviceMetainfoInside {
    return DeviceMetainfoInside(fetchedWithRemotes, multipleKkm, remoteKkm, workplaceName)
}

/**
 * Маппер для преобразования модели терминала контроллера в модель терминала Android
 */
fun ControllerPaymentTerminalDeviceInfo.toAndroidType(): PaymentTerminalDeviceInfoInside =
    PaymentTerminalDeviceInfoInside(
        deviceInfoBase.toAndroidType(),
        terminalID,
        receiptBody,
        organizationIdList
    )

/**
 * Маппер для преобразования модели информации об устройстве контроллера в модель информации об устройстве Android
 */
fun ControllerDeviceInfoBase.toAndroidType(): DeviceInfoBaseInside =
    DeviceInfoBaseInside(
        model,
        serialNumber,
        firmwareVersion,
        dateTime
    )

/** @SelfDocumented */
fun ControllerDriverInfo.toAndroidType() = DriverInfo(isDefault, name, connections.map { it.toConnectionTypeInside() })

/** @SelfDocumented */
fun ControllerUsbDevice.toAndroidType() = UsbDevice(getId(), getUsbDeviceId().id.toAndroidType(), getUsbDeviceId().path.orEmpty())

/** @SelfDocumented */
fun ControllerUsbDeviceId.toAndroidType() = UsbDeviceId(id.toAndroidType(), path)

/** @SelfDocumented */
fun ControllerUsbDeviceInfo.toAndroidType() =
    UsbDevice(
        if (connection.deviceUuid.isBlank()) UUID.randomUUID() else UUID.fromString(connection.deviceUuid),
        connection.usbDeviceId.id.toAndroidType(),
        connection.usbDeviceId.path ?: ""
    )

/** @SelfDocumented */
fun UsbDeviceId.toControllerType() = ControllerUsbDeviceId(usbId.toControllerType(), devicePath)


/** @SelfDocumented */
fun ControllerUsbId.toAndroidType() = UsbId(vid, pid)

/** @SelfDocumented */
fun UsbId.toControllerType() = ControllerUsbId(vId, pId)


/** @SelfDocumented */
fun ControllerOrganizationInfo.toAndroidType() = OrganizationInfo(company, name, id, qrId, inn, use)

/** @SelfDocumented */
fun OrganizationInfo.toControllerType() = ControllerOrganizationInfo(companyId, organizationId.ifBlank { "" }, qrId, inn, companyName, isUse)


/** @SelfDocumented */
fun ControllerProductionArea.toAndroidType() = ProductionArea(productionSite, warehouseSale, id, name, productionName, marked, original, company)

/** @SelfDocumented */
fun ProductionArea.toControllerType() = ControllerProductionArea(productionSite, warehouseSale, id, isMarked, isOriginal, name, productionName, company)


/** @SelfDocumented */
fun ControllerDeviceApplication.toAndroidType() = when (this) {
    ControllerDeviceApplication.PRODUCTION_MONITOR -> DeviceApplication.PRODUCTION_MONITOR
    ControllerDeviceApplication.CLIENT_MONITOR -> DeviceApplication.CLIENT_MONITOR
}

/** @SelfDocumented */
fun DeviceApplication.toControllerType() = when (this) {
    DeviceApplication.PRODUCTION_MONITOR -> ControllerDeviceApplication.PRODUCTION_MONITOR
    DeviceApplication.CLIENT_MONITOR -> ControllerDeviceApplication.CLIENT_MONITOR
}

/** @SelfDocumented */
fun ControllerDeviceCanUpdate.toAndroidType() = DeviceCanUpdate(
    result = result,
    message = message,
    deviceId = device,
    metastate = metastate,
    actions = actions
)

/** @SelfDocumetned */
fun SeparatorItem.toControllerType(): ControllerSeparatorItem =
    ControllerSeparatorItem(
        value.toControllerType(),
        isActive
    )

/** @SelfDocumented */
fun ControllerSeparatorItem.toAndroidType(): SeparatorItem =
    SeparatorItem(
        value.toAndroidType(),
        isActive
    )

/** @SelfDocumented */
fun UsbPathItem.toControllerType(): ControllerUsbPathItem =
    ControllerUsbPathItem(
        value.toControllerType(),
        isActive
    )

/** @SelfDocumented */
fun ControllerUsbPathItem.toAndroidType(): UsbPathItem =
    UsbPathItem(
        value.toAndroidType(),
        isActive
    )

/** @SelfDocumented */
fun ConnectionTypeItem.toControllerType(): ControllerConnectionTypeItem =
    ControllerConnectionTypeItem(
        value.toInt(),
        driver,
        isActive
    )

/** @SelfDocumented */
fun ControllerConnectionTypeItem.toAndroidType(): ConnectionTypeItem =
    ConnectionTypeItem(
        value.toConnectionTypeInside(),
        driver,
        isActive
    )

/** @SelfDocumented */
fun ScannerSeparator.toControllerType(): ControllerScannerSeparator =
    when (this) {
        ScannerSeparator.CR_LF -> ControllerScannerSeparator.CR_LF
        ScannerSeparator.CR -> ControllerScannerSeparator.CR
        ScannerSeparator.LF -> ControllerScannerSeparator.LF
        ScannerSeparator.TAB -> ControllerScannerSeparator.TAB
        ScannerSeparator.STX_ETX -> ControllerScannerSeparator.STX_ETX
        ScannerSeparator.EOT -> ControllerScannerSeparator.EOT
    }

/** @SelfDocumented */
fun ControllerScannerSeparator.toAndroidType(): ScannerSeparator =
    when (this) {
        ControllerScannerSeparator.CR_LF -> ScannerSeparator.CR_LF
        ControllerScannerSeparator.CR -> ScannerSeparator.CR
        ControllerScannerSeparator.LF -> ScannerSeparator.LF
        ControllerScannerSeparator.TAB -> ScannerSeparator.TAB
        ControllerScannerSeparator.STX_ETX -> ScannerSeparator.STX_ETX
        ControllerScannerSeparator.EOT -> ScannerSeparator.EOT
    }
