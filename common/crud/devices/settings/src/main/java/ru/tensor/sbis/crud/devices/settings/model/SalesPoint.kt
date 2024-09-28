package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.devices.settings.generated.SalesPoint as ControllerSalesPoint

/**
 * Интерфейс для точек продаж, содержит в себе поле идентификатора и поле имени
 */
interface SalesItem {
    val id: String
    val name: String
}

/** Разделитель с текстом. */
data class SimpleTextSeparator(
    override val id: String,
    override val name: String,
    val text: String
) : SalesItem {

    constructor(text: String) : this(
        id = "",
        name = "",
        text = text
    )
}

/**
 * Модель точки продажи для хлебных крошек
 *
 * @param id String - Идентификатор точки продаж
 * @param name String - Наименование точки продаж
 * @param crumbs List<String> - хлебные крошки
 *
 * @see SalesItem
 */
data class BreadCrumb(
    override val id: String,
    override val name: String,
    val crumbs: List<String>
) : SalesItem

/**
 * Модель точки продажи
 *
 * @param id String - Идентификатор точки продаж
 * @param name String - Наименование точки продаж
 * @param address String - Адрес точки продаж
 * @param company Int - Идентификатор компании
 * @param region String - Регион точки продаж
 * @param isFolder Boolean - Является ли папкой
 * @param parentFolderId String? - идентификатор родительской папки
 * @param visible Boolean - Метка видимости точки продаж
 * @param syncStatus SettingsSyncStatus - Статус синхронизации
 * @param settings SalesPointSettings - Настройки точки продаж
 * @param info SalesPointInfo - Дополнительная информация
 *
 * @see SettingsSyncStatus
 * @see SalesPointSettings
 * @see SalesPointInfo
 * @see SalesItem
 */
@Parcelize
data class SalesPoint(
    override val id: String,
    override val name: String,
    val address: String,
    val company: Long,
    val region: String,
    val isFolder: Boolean,
    val parentFolderId: String?,
    val visible: Boolean,
    val syncStatus: SettingsSyncStatus,
    val settings: SalesPointSettings,
    val info: SalesPointInfo
) : SalesItem, Parcelable {

    constructor() : this(
        id = "",
        name = "",
        address = "",
        company = 0,
        region = "",
        isFolder = false,
        parentFolderId = null,
        visible = true,
        syncStatus = SettingsSyncStatus.NOT_REQUIRED,
        settings = SalesPointSettings.stub(),
        info = SalesPointInfo.stub()
    )

    companion object {
        fun stub(): SalesPoint = SalesPoint(
            id = "1",
            name = "Android STUB Sales Point",
            address = "",
            company = 1,
            region = "",
            isFolder = false,
            parentFolderId = null,
            visible = true,
            syncStatus = SettingsSyncStatus.NOT_REQUIRED,
            settings = SalesPointSettings.stub(),
            info = SalesPointInfo.stub()
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SalesPoint) return false
        if (company != other.company) return false
        if (isFolder != other.isFolder) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + company.hashCode()
        result = 31 * result + isFolder.hashCode()
        return result
    }
}

/**
 * Модель точки продажи
 *
 * @param id String - Идентификатор точки продаж
 * @param name String - Наименование точки продаж
 * @param isFolder Boolean - Является ли папкой
 */
@Parcelize
data class SalesPointShort(
    override val id: String,
    override val name: String,
    val isFolder: Boolean
) : SalesItem, Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SalesPointShort

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerSalesPoint.map(): SalesPoint = SalesPoint(
    id = identifier,
    name = name,
    address = address ?: "",
    company = company,
    region = region ?: "",
    isFolder = isFolder,
    parentFolderId = folder,
    visible = visible,
    syncStatus = syncStatus.map(),
    settings = settings.map(),
    info = info.map()
)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SalesPoint.map() = ControllerSalesPoint().apply {
    identifier = id
    folder = parentFolderId
    isFolder = this@map.isFolder
    name = this@map.name
    company = this@map.company
    region = this@map.region
    address = this@map.address
    visible = this@map.visible
    syncStatus = this@map.syncStatus.map()
    settings = this@map.settings.map()
    info = this@map.info.map()
}