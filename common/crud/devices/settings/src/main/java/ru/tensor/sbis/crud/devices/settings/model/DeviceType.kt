package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.common.util.UUIDUtils
import java.util.*
import ru.tensor.devices.settings.generated.DeviceType as ControllerDeviceType

/**
 * Модель устройства в списке поддерживаемого оборудования
 *
 * @param identifier Int - идентификатор в облаке
 * @param name String - имя устройства
 * @param kind DeviceKindInside - тип устройства
 * @param type UUID - nип устройства из сервиса поддерживаемого оборудования (уникальный идентификатор конкретного устройства)
 * @param folder Int - Родительская папка
 * @param isFolder Boolean - Является ли папкой (true - да, иначе - нет)
 * @param options String - JSON-схема валидации опций подключенного устройства
 * @param mobileSupported Boolean - флаг, обозначающий поддерживается ли устройство в мобильном приложении (true - да, иначе - нет)
 * @param deviceImage DeviceImage - Ссылки на большую и маленькую картинки, готовые для использования на UI
 * @param photoRef String - ссылка на картинку устройства
 * @param supportedConnectionTypes ArrayList<Int> - список возможных типов подключения для данного устройства
 * @param editableConnectionTypes HashMap<Int, ArrayList<String>> - редактируемые типы подключений.
 * Ключ - тип подключения (SERIAL_PORT, BLUETOOTH и т.д.),
 * Значение - список платформ, на которых можно редактировать тип подключения ("online", "mobile", "presto" и т.д.)
 * @param supportedDrivers ArrayList<String> - список поддерживаемых драйверов
 * @param sbisHelpLink String - ссылка по которой нужно перейти при нажатии на кнопку "Как настроить"
 *
 * @see UUID
 * @see DeviceKindInside
 * @see DeviceImage
 */
@Parcelize
data class DeviceType(
    val identifier: Long,
    val name: String,
    val kind: DeviceKindInside,
    val type: UUID,
    val folder: Long,
    val isFolder: Boolean,
    val options: String,
    val mobileSupported: Boolean,
    val deviceImage: DeviceImage,
    val photoRef: String,
    val supportedConnectionTypes: ArrayList<Int>,
    val editableConnectionTypes: HashMap<Int, ArrayList<String>>,
    val supportedDrivers: List<DriverInfo>,
    val sbisHelpLink: String,
    val scannerDeviceSettings: ScannerDeviceSettings?,
    val port: Int?,
    val application: DeviceApplication? = null) : Parcelable

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerDeviceType.map(): DeviceType = DeviceType(
        identifier,
        name,
        kindToAndroidType(kind),
        type ?: UUIDUtils.NIL_UUID,
        folder ?: 0,
        isFolder,
        options,
        mobileSupported,
        image?.map() ?: DeviceImage.stub(),
        photoRef ?: "",
        supportedConnectionTypes,
        editableConnectionTypes,
        supportedDrivers.map { it.toAndroidType() },
        sbisHelpLink ?: "",
        scannerSettings?.toAndroidType(),
        port,
        application?.toAndroidType())