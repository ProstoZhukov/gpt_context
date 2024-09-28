package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.crud.devices.settings.model.SalesPointInfo.Companion.ID_EMPTY_WAREHOUSE
import ru.tensor.devices.settings.generated.SalesPointInfo as ControllerSalesPointInfo

/**
 * Модель с информацией о точке продаж
 *
 * @param companyId String - Идентификатор точки продаж
 * @param youAreHere Boolean - Находится ли сотрудник в данной точке продаж
 * @param employeeCount Int - Количество сотрудников
 * @param branchCount Int - Количество филиалов
 * @param workplaceCount Int - Количество рабочих мест
 * @param hallCount Int - Количество залов
 * @param locationCount Int - Количество расположений
 * @param devicesRaw String - JSON-массив с информацией об устройствах
 * @param devices List<DeviceInfo> - Массив с информацией об устройствах
 * @param employeesRaw String - JSON-массив ссылок на фотографии сотрудников
 * @param employeesPhoto List<String> - Массив ссылок на фотографии сотрудников
 * @param warehouseId Int - Склад
 * @param mainTaxSystemList List<TaxSystem> - Список систем налогообложения
 * @param inn - ИНН организации
 * @param sppId - id СПП
 * @param parentFolders List<SalesPointShortInfo> - Путь по вложенным папкам
 *
 * @see DeviceInfo
 * @see SalesPointShortInfo
 */
@Parcelize
data class SalesPointInfo(
    val companyId: String,
    val youAreHere: Boolean,
    val employeeCount: Int,
    val branchCount: Int,
    val workplaceCount: Int,
    val hallCount: Int,
    val locationCount: Int,
    val devicesRaw: String,
    val devices: List<DeviceInfo>,
    val employeesRaw: String,
    val employeesPhoto: List<String>,
    val warehouseId: Long,
    val mainTaxSystemList: List<TaxSystem>,
    val inn: String?,
    val sppId: Int?,
    var parentFolders: List<SalesPointShortInfo>,
    val kpp: String?,
    val fsrar: String?,
    val spTypes: List<SalesPointSupportedType> = emptyList()
) : Parcelable{
    companion object {
        fun stub(): SalesPointInfo = SalesPointInfo(
            companyId = "",
            youAreHere = false,
            employeeCount = 0,
            branchCount = 0,
            workplaceCount = 0,
            hallCount = 0,
            locationCount = 0,
            devicesRaw = "",
            devices = listOf(),
            employeesRaw = "",
            employeesPhoto = listOf(),
            warehouseId = ID_EMPTY_WAREHOUSE,
            mainTaxSystemList = listOf(),
            inn = "",
            sppId = null,
            parentFolders = listOf(),
            kpp = null,
            fsrar = null
        )

        const val ID_EMPTY_WAREHOUSE: Long = 0
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerSalesPointInfo.map(): SalesPointInfo = SalesPointInfo(
    companyId = identifier,
    youAreHere = here,
    employeeCount = employeeCount ?: 0,
    branchCount = branchCount ?: 0,
    workplaceCount = workplaceCount ?: 0,
    hallCount = hallCount ?: 0,
    locationCount = locationCount ?: 0,
    devicesRaw = devicesRaw.orEmpty(),
    devices = devices?.run { map { it.map() } } ?: listOf(),
    employeesRaw = employeesRaw.orEmpty(),
    employeesPhoto = employees ?: listOf(),
    warehouseId = warehouse ?: ID_EMPTY_WAREHOUSE,
    mainTaxSystemList = taxSystems.map { it.map() },
    inn = inn,
    sppId = null,
    parentFolders = parentFolders.map { it.map() },
    kpp = kpp,
    fsrar = fsrar,
    spTypes = spTypes.map { it.toSupportedSalesPointTypeUI() }
)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SalesPointInfo.map(): ControllerSalesPointInfo = let { model ->
    ControllerSalesPointInfo().apply {
        identifier = model.companyId
        here = model.youAreHere
        employeeCount = model.employeeCount
        branchCount = model.branchCount
        workplaceCount = model.workplaceCount
        hallCount = model.hallCount
        locationCount = model.locationCount
        devicesRaw = model.devicesRaw
        devices = ArrayList(model.devices.map { it.map() })
        employeesRaw = model.employeesRaw
        employees = ArrayList(model.employeesPhoto)
        warehouse = model.warehouseId
        taxSystems = ArrayList(model.mainTaxSystemList.map { it.map() })
        inn = model.inn
        sppId = model.sppId
        parentFolders = ArrayList(model.parentFolders.map { it.map() })
        kpp = null
        fsrar = null
        spTypes = ArrayList(model.spTypes.map { it.toSpTypeNative() })
    }
}