package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Модель устройства
 *
 * @param id Int - идентификатор устройства
 * @param kind DeviceKindInside - тип устройства
 * @param type UUID - уникальный идентификатор типа устройства
 * @param serialNumber String - серийный номер устройства
 * @param name String - имя устройства
 * @param modelName String - имя модели устройства
 * @param company Int? - идентификатор компании к которой привязано устройство (опционально)
 * @param workplace Int? - идентификатор рабочего места к которому привязано устройство (опционально)
 * @param visible Boolean - флаг обозначающий удалено устройство (false) или нет (true)
 * @param isActive Boolean - флаг обозначающий является ли устройство активным (true) или нет (false)
 * @param connectionRaw String? - иинформация о подключении устройства в формате JSON (опционально)
 * @param settingsRaw String? - информация о настройках устройства в формате JSON (опционально)
 * @param metainfoRaw String? - дополнительная иинформация об устройстве в формате JSON (опционально)
 * @param syncStatus SyncStatusInside - информация о состоянии синхронизации устройства
 * @param connection DeviceConnectionInside? - иинформация о подключении устройства (опционально)
 * @param settings DeviceSettingsInside? - иинформация о настройках устройства (опционально)
 * @param metaInfo DeviceMetainfoInside? - дополнительная иинформация об устройстве (опционально)
 * @param image DeviceImageInside? - модель с ссылками на картинки устройства (опционально)
 * @param supportedConnectionTypes ArrayList<Int> - список возможных типов подключения для данного устройства
 * @param editableConnectionTypes HashMap<Int, ArrayList<String>> - редактируемые типы подключений.
 * Ключ - тип подключения (SERIAL_PORT, BLUETOOTH и т.д.),
 * Значение - список платформ, на которых можно редактировать тип подключения ("online", "mobile", "presto" и т.д.)
 * @param missCheckRegnum Boolean - флаг обозначающий необходимость пропустить проверку регистрационного номера устройства (true), иначе (true)
 * @param sbisHelpLink String - ссылка по которой нужно перейти при нажатии на кнопку "Как настроить"
 *
 * @see DeviceKindInside
 * @see UUID
 * @see SyncStatusInside
 * @see DeviceConnectionInside
 * @see DeviceSettingsInside
 * @see DeviceMetainfoInside
 * @see DeviceImageInside
 */
@Parcelize
data class DeviceInside(
    val id: Long,
    val kind: DeviceKindInside,
    val type: UUID,
    val serialNumber: String,
    val name: String,
    val modelName: String,
    val company: Long?,
    val workplace: Long?,
    val visible: Boolean = true,
    val isActive: Boolean,
    val kkmId: Long? = null,
    val connectionRaw: String? = null,
    val settingsRaw: String? = null,
    val metainfoRaw: String? = null,
    val syncStatus: SyncStatusInside,
    val connection: DeviceConnectionInside?,
    val settings: DeviceSettingsInside?,
    val metaInfo: DeviceMetainfoInside? = null,
    val image: DeviceImageInside?,
    val supportedConnectionTypes: ArrayList<Int> = arrayListOf(),
    val editableConnectionTypes: HashMap<Int, ArrayList<String>> = hashMapOf(),
    val isTensorOfd: Boolean,
    val missCheckRegnum: Boolean = false,
    val isStandAloneEnabled: Boolean? = null,
    val sbisHelpLink: String = "",
    val scannerDeviceSettings: ScannerDeviceSettings? = null,
    val supportedDrivers: List<DriverInfo> = emptyList(),
    val port: Int? = null,
    val remoteWorkplaces: List<RemoteWorkplaceInfo>,
    val sharedWorkplaceInfo: SharedWorkplaceInfo? = null
) : Parcelable

/**
 * Метод позволяет проверить, является ли касса расшаренной
 * (установлена на другом рабочем месте, но доступная для работы)
 *
 * @return true если касса расшаренная
 */
fun DeviceInside.isRemoteKKM(): Boolean {
    return kind == DeviceKindInside.REMOTE_KKM
}

/**
 * Можно ли редактировать текущее соединение на Android?
 * Если текущее соединение редактировать нельзя, необходимо отобразить информацию о нём в Read-only режиме.
 * */
fun DeviceInside.isConnectionTypeEditable(): Boolean {
    return connection
            ?.connectionType?.toInt()
            ?.let { it in getEditableConnectionTypesForMobile() }
            ?: false
}

/**
 * Метод для получения названия рабочего места, с которого расшарено оборудование
 *
 * @return название рабочего удаленного места, если касса расшаренная
 */
fun DeviceInside.getRemoteWorkplaceTitle(): String? {
    return sharedWorkplaceInfo?.name
}

/**
 * Можно ли менять тип соединениния устройства с мобилки?
 * Если устройство создано на другой платформе и использует тип соединения, характерный только для неё, то мы не должны возможности его менять.
 */
fun DeviceInside.isCurrentConnectionTypeSupportedByMobile(): Boolean {
    if (editableConnectionTypes.isEmpty()) return true // По словам Мантрова Александра, отсутствие ограничения означает поддержку текущего типа соединения
    val supportedConnectionTypes = editableConnectionTypes.filter { it.value.contains("mobile") }.map { it.key }
    return connection?.let { return it.connectionType.toInt() in supportedConnectionTypes } ?: false
}