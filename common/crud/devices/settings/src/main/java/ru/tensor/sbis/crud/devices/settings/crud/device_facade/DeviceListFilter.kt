package ru.tensor.sbis.crud.devices.settings.crud.device_facade

import ru.tensor.devices.settings.generated.DeviceFilter
import ru.tensor.devices.settings.generated.SyncStatus
import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.crud.devices.settings.model.*
import java.io.Serializable
import java.util.*
import ru.tensor.devices.settings.generated.VisibilityType as ControllerVisibilityType

/** Фильтр по устройствам */
class DeviceListFilter : Serializable, ListFilter() {

    /** Выбор по идентификатору */
    var byId: Long? = null

    /** Выбор по типу */
    var byKind: DeviceKindInside? = null

    /** Выбор по идентификатору поддерживаемого устройства */
    var byType: UUID? = null
        set(value) {
            field = if (value == UUIDUtils.NIL_UUID) null else value
        }

    /** Выбор по серийному номеру */
    var bySerialNumber: String? = null
        set(value) {
            field = if (value.isNullOrBlank()) null else value
        }

    /** Выбор по имени */
    var byName: String? = null
        set(value) {
            field = if (value.isNullOrBlank()) null else value
        }

    /** Выбор по компаниям */
    var byCompanies: ArrayList<Long>? = null

    /** Выбор по рабочим местам (для получения devices с разными company_id из одной точки продажи) */
    var byWorkplaces: ArrayList<Long>? = null

    /** Выбор по рабочему месту */
    var byWorkplace: Long? = null

    /** Выбор по метке видимости. */
    var byVisibleMark: VisibilityType = VisibilityType.VISIBLE_ONLY

    /** Выбор по метке активности */
    var byActiveMark: Boolean? = null

    /** Выбор по статусу синхронизации */
    var bySyncStatus: SettingsSyncStatus? = null

    /** Выбор только по рабочему месту пользователя */
    var byMyWorkplace: Boolean? = null

    /** Выбор по регистрационному номеру. */
    private var byRegNumber: String? = null

    /** Выбор существующего оборудования. */
    var byRemotePlace: Boolean = false

    override fun queryBuilder(): Builder<*, *> =
            DeviceFilterBuilder(
                    byId,
                    byKind?.toControllerType(),
                    byType,
                    bySerialNumber,
                    byName,
                    byCompanies,
                    byWorkplaces,
                    byWorkplace,
                    byVisibleMark.map(),
                    byActiveMark,
                    bySyncStatus?.map(),
                    byMyWorkplace,
                    byRegNumber,
                    byRemotePlace)
                    .searchQuery(mSearchQuery)

    private class DeviceFilterBuilder(private val byId: Long?,
                                      private val byKind: Int?,
                                      private val byType: UUID?,
                                      private val bySerialNumber: String?,
                                      private val byName: String?,
                                      private val byCompanies: ArrayList<Long>?,
                                      private val byWorkplaces: ArrayList<Long>?,
                                      private val byWorkplace: Long?,
                                      private val byVisibleMark: ControllerVisibilityType,
                                      private val byActiveMark: Boolean?,
                                      private val bySyncStatus: SyncStatus?,
                                      private val byMyWorkplace: Boolean?,
                                      private val byRegNumber: String?,
                                      private val byRemotePlace: Boolean) :
            AnchorPositionQueryBuilder<Any, DeviceFilter>() {

        override fun build(): DeviceFilter {
            var offset = when (mDirection) {
                // двусторонняя пагинация не поддерживается контроллером,
                // обновление данных снизу вверх выполняем до самого начала
                QueryDirection.TO_NEWER -> 0

                // догрузка новых страниц
                QueryDirection.TO_OLDER -> mFromPosition

                QueryDirection.TO_BOTH -> throw IllegalStateException(
                    "Двунаправленный запрос списка устройств не поддерживается на контроллере"
                )

                null -> null
            }
            if (!mInclusive) {
                offset = offset?.plus(1) // на контроллере для данного фильтра смещение всегда включительно
            }
            return DeviceFilter().also {
                it.byId = byId
                it.byKind = byKind
                it.byType = byType
                it.bySerialNumber = bySerialNumber
                it.byName = byName
                it.byCompanies = byCompanies
                it.byWorkplaces = byWorkplaces
                it.byWorkplace = byWorkplace
                it.byVisibleMark = byVisibleMark
                it.byActiveMark = byActiveMark
                it.bySyncStatus = bySyncStatus
                it.byMyWorkplace = byMyWorkplace
                it.byRegNumber = byRegNumber
                it.byRemotePlace = byRemotePlace
                it.limit = mItemsCount.takeIf { mDirection != null }
                it.offset = offset
            }
        }
    }
}
