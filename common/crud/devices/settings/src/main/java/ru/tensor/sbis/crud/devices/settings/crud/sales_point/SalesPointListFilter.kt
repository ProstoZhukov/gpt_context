package ru.tensor.sbis.crud.devices.settings.crud.sales_point

import ru.tensor.devices.settings.generated.SalesPointFilter
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.crud.devices.settings.model.SettingsSyncStatus
import ru.tensor.sbis.crud.devices.settings.model.VisibilityType
import ru.tensor.sbis.crud.devices.settings.model.map
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/** Фильтр для получения информации о точке продажи */
class SalesPointListFilter : Serializable, ListFilter() {

    /** Выбор по идентификатору.*/
    var byId: String? = null

    /** Выбор по идентификаторам.*/
    private var byIds: ArrayList<String>? = null

    /**
     * Выбор по папке.
     * Если требуется корневая директория - передать пустую строку "".
     */
    var byFolder: String? = ""

    /** Выбор по признаку папки.*/
    var byIsFolder: Boolean? = null

    /** Выбор по имени/ИНН/КПП/коммерческому названию.*/
    var byName: String? = null

    /** Выбор по идентификатору компании.*/
    private var byCompany: Long? = null

    /** Выбор по идентификаторам компаний.*/
    private var companies: List<Long>? = null

    /** Выбор по региону.*/
    private var byRegion: String? = null

    /** Выбор по адресу.*/
    private var byAddress: String? = null

    /** Метка видимости точки продаж.*/
    var visibilityType: VisibilityType = VisibilityType.VISIBLE_ONLY

    /** Выбор по статусу синхронизации.*/
    var syncStatus: SettingsSyncStatus? = null

    /** Ограничение на количество извлекаемых записей.*/
    var limit: Int? = null

    /** Режим плоского списка.*/
    private var flatMode: Boolean? = null

    /** Смещение относительно начала в выборке записей.*/
    var offset: Int? = null

    /** Страничное смещение относительно начала в выборке записей.*/
    private var pageOffset: Int? = null

    /** Загрузить идентификатор склада.*/
    private var fetchWarehouse: Boolean = false

    /** Загрузить систему налогообложения.*/
    private var fetchMainTaxSystem: Boolean = false

    /** Используется для сброса фильта точек продаж, например при перелогине */
    fun clean() {
        byId = null
        byIds = null
        byFolder = ""
        byIsFolder = null
        byName = null
        byCompany = null
        byRegion = null
        byAddress = null
        visibilityType = VisibilityType.VISIBLE_ONLY
        syncStatus = null

        limit = null
        flatMode = null
        offset = null
        pageOffset = null

        fetchWarehouse = false
        fetchMainTaxSystem = false
    }

    override fun queryBuilder(): Builder<*, *> =
            SalesPointFilterBuilder(byId,
                    byIds,
                    byFolder,
                    byIsFolder,
                    byName,
                    byCompany,
                    companies,
                    byRegion,
                    byAddress,
                    visibilityType,
                    syncStatus,
                    fetchWarehouse,
                    fetchMainTaxSystem,
                    flatMode,
                    limit,
                    offset,
                    pageOffset)
                    .searchQuery(mSearchQuery)

    private class SalesPointFilterBuilder(private var byId: String?,
                                          private var byIds: ArrayList<String>?,
                                          private var byFolder: String?,
                                          private var byIsFolder: Boolean?,
                                          private var byName: String?,
                                          private var byCompany: Long?,
                                          private var companies: List<Long>?,
                                          private var byRegion: String?,
                                          private var byAddress: String?,
                                          private var visibilityType: VisibilityType,
                                          private var syncStatus: SettingsSyncStatus?,
                                          private var fetchWarehouse: Boolean,
                                          private var fetchMainTaxSystem: Boolean,
                                          private var flatMode: Boolean?,
                                          private var limit: Int?,
                                          private var offset: Int?,
                                          private var pageOffset: Int?) :
            AnchorPositionQueryBuilder<Any, SalesPointFilter>() {

        override fun build(): SalesPointFilter =
                SalesPointFilter(byId,
                        byIds,
                        byFolder,
                        byIsFolder,
                        byName,
                        byCompany,
                        companies?.let { ArrayList(it) },
                        byRegion,
                        byAddress,
                        visibilityType.map(),
                        syncStatus?.map(),
                        fetchWarehouse,
                        fetchMainTaxSystem,
                        flatMode,
                        limit,
                        offset,
                        pageOffset)
    }

    /**
     * Сравнение основных фильтров списка точек продаж (по папке и по видимости)
     * @param param [HashMap] с фильтрами
     * @return true, если фильтры в [param] соответствуют фильтрам в [SalesPointListFilter]
     */
    fun areMainParamsEqual(param: HashMap<String, String>): Boolean {
        val folderKey = "FOLDER"
        val showAllKey = "SHOW_ALL"
        val folder = param[folderKey]
        val visibilityType = if (param[showAllKey] == "true") VisibilityType.SHOW_ALL else VisibilityType.VISIBLE_ONLY
        val areFoldersEqual = byFolder == folder || (byFolder.isNullOrBlank() && folder.isNullOrBlank())
        val areVisibilityTypesEqual = this.visibilityType == visibilityType
        return areFoldersEqual && areVisibilityTypesEqual
    }
}